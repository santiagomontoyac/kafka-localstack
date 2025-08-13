package com.store.demo.service.impl

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse
import java.security.Key
import java.util.*


@Component
class JwtGenerator(
    private val secretManagerClient:
    SecretsManagerClient
) {

    private val issuer = "kafka-app"
    private val subject = "internal-client"

    fun generateToken(): String {
        val now = System.currentTimeMillis()
        val token = Jwts.builder()
            .setClaims(HashMap<String?, Any?>())
            .setIssuer(issuer)
            .setSubject(subject)
            .setIssuedAt(Date(now))
            .setExpiration(Date(now + 10 * 365 * 24 * 60 * 60 * 1000)) // ~10 years, non-expiring
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact()

        return token
    }

    private fun getSigningKey(): Key {
        val keyBytes = Decoders.BASE64.decode(getSecretKey())
        return Keys.hmacShaKeyFor(keyBytes)
    }

    fun getSecretKey(): String {
        val request = GetSecretValueRequest.builder()
            .secretId("secret-key")
            .build()

        val response: GetSecretValueResponse = secretManagerClient.getSecretValue(request)
        val secretJson = response.secretString()

        // Assuming the secret is stored as {"token":"your-secret-token-value"}
        return extractTokenFromJson(secretJson)
    }

    private fun extractTokenFromJson(json: String): String {
        val mapper = jacksonObjectMapper()
        val map = mapper.readValue<Map<String, String>>(json)
        return map["token"] ?: throw IllegalStateException("Missing 'token' in secret JSON")
    }


}