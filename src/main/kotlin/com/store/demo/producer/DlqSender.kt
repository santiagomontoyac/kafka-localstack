package com.store.demo.producer

import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class DlqSender(private val kafkaTemplate: KafkaTemplate<String, String>) {

    fun sendToDlq(message: String) {
        val record = ProducerRecord<String, String>("demo-topic.DLQ", message)
        kafkaTemplate.send(record)
        println("📤 Message sent to DLQ: $message")
    }
}
