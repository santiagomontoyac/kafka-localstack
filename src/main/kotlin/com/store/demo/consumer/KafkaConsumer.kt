package com.store.demo.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.store.demo.model.request.ProductInDTO
import com.store.demo.producer.DlqSender
import com.store.demo.service.ProductService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class KafkaConsumer(
    val productService: ProductService, private val objectMapper: ObjectMapper,
    private val dlqSender: DlqSender
) {


    @KafkaListener(topics = ["demo-topic"], groupId = "demo-group")
    fun consume(@Payload jsonMessage: String) {
        //println("🧾 JWT Token from Kafka Header: $token")
        try {
            val dto = objectMapper.readValue(jsonMessage, ProductInDTO::class.java)
            println("📦 Received product: ${dto.name}")
            productService.processProduct(dto)
        } catch (ex: Exception) {
            println("❌ Failed to parse JSON message: $jsonMessage")
            ex.printStackTrace()
            // Forward to DLQ
            dlqSender.sendToDlq(jsonMessage)
        }
    }

    @KafkaListener(topics = ["demo-topic.DLQ"], groupId = "dlq-group")
    fun handleDlq(message: String) {
        println("📥 Received DLQ message: $message")
        // Log to file, alert system, or retry later manually
    }


}