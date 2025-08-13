package com.store.demo.service.impl

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.opencsv.CSVReader
import com.store.demo.model.request.ProductInDTO
import com.store.demo.producer.DlqSender
import com.store.demo.producer.KafkaProducer
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

@Service
class SqsToKafkaService(
    private val sqsClient: SqsClient,
    private val s3Client: S3Client,
    private val kafkaProducer: KafkaProducer,
    private val dlqSender: DlqSender
) {
    private val queueUrl = "http://localstack:4566/000000000000/s3-events-queue"

    private val objectMapper = jacksonObjectMapper()

    @Scheduled(fixedDelay = 5000)
    fun pollSqs() {
        val request = ReceiveMessageRequest.builder()
            .queueUrl(queueUrl)
            .maxNumberOfMessages(5)
            .waitTimeSeconds(1)
            .build()

        val messages = sqsClient.receiveMessage(request).messages()
        messages.forEach { message ->
            println("📩 Received S3 event: ${message.body()}")

            // Parse message
            val s3Event = objectMapper.readTree(message.body())
            val record = s3Event["Records"]?.firstOrNull()
            val bucket = record?.get("s3")?.get("bucket")?.get("name")?.asText()
            val key = record?.get("s3")?.get("object")?.get("key")?.asText()

            if (bucket != null && key != null) {
                processFile(bucket, key)
            }

            // Delete the message (optional)
            sqsClient.deleteMessage { it.queueUrl(queueUrl).receiptHandle(message.receiptHandle()) }
        }
    }


    private fun processFile(bucket: String, key: String) {
        val request = GetObjectRequest.builder().bucket(bucket).key(key).build()
        val invalidRows = mutableListOf<String>()

        s3Client.getObject(request).use { s3Object ->
            val reader = CSVReader(InputStreamReader(s3Object, StandardCharsets.UTF_8))
            val lines = reader.readAll()

            val header = lines.firstOrNull()
            val rows = lines.drop(1)

            rows.forEach { row ->
                try {
                    if (row.size != 5) {
                        throw IllegalArgumentException("Expected 5 columns, found ${row.size}")
                    }
                    val product = ProductInDTO(
                        name = row[0],
                        description = row[1],
                        stock = row[2].toDouble(),
                        price = row[3].toDouble(),
                        idCategory = row[4].toLong()
                    )

                    val json = objectMapper.writeValueAsString(product)
                    kafkaProducer.sendMessage(json)

                } catch (ex: Exception) {
                    println("❌ Failed to parse row: ${row.joinToString()} — ${ex.message}")
                    invalidRows.add(row.joinToString(","))
                }
            }


            if (invalidRows.isNotEmpty()) {
                println("📦 Sending ${invalidRows.size} invalid rows to DLQ")
                invalidRows.forEach { line ->
                    dlqSender.sendToDlq(line)
                }
            } else {
                println("✅ All lines processed successfully.")
            }
        }
    }


    /*
    function to read .txt files from S3 bucket
     */
//    private fun processFile(bucket: String, key: String) {
//        val request = GetObjectRequest.builder()
//            .bucket(bucket)
//            .key(key)
//            .build()
//
//        s3Client.getObject(request).use { s3Object ->
//            val reader = BufferedReader(InputStreamReader(s3Object, StandardCharsets.UTF_8))
//
//            val lines = reader.readLines()
//
//            // First line = JWT token (no "Bearer")
//            // change this approach to dont send the token in the file, instead I will create a token with the secret key
//            // val jwtRaw = lines.firstOrNull()?.trim() ?: ""
//            // val jwtToken = "Bearer $jwtRaw"
//
//            // val dataLines = lines.drop(1)
//
//            lines.forEach { line ->
//                val parts = line.split(" ")
//
//                if (parts.size >= 6) {
//                    val product = mapOf(
//                        "name" to parts[1],
//                        "description" to parts[2],
//                        "stock" to parts[3].toInt(),
//                        "price" to parts[4].toDouble(),
//                        "idCategory" to parts[5].toInt()
//                    )
//
//                    val productJson = objectMapper.writeValueAsString(product)
//
//                    kafkaProducer.sendMessage(productJson)
//                } else {
//                    println("❗ Invalid line format: $line")
//                }
//            }
//        }
//
//        println("✅ Processed file from $bucket/$key and sent to Kafka with Authorization header.")
//    }


}