package com.store.demo.producer

import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaProducer(private val kafkaTemplate: KafkaTemplate<String, String>) {

    fun sendMessage(message: String) {
        val topic = "demo-topic"
        val record = ProducerRecord<String, String>(topic, message)
        // record.headers().add(RecordHeader("Authorization", "$jwt".toByteArray()))

        kafkaTemplate.send(record)
        println("✅ Sent message with JWT header")
    }
}