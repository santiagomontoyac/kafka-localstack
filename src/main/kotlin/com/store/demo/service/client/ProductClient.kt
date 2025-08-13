package com.store.demo.service.client

import com.store.demo.model.request.ProductInDTO
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(name = "demo-store", url = "\${product.service.url}", path = "/api/v1")
interface ProductClient {

    @PostMapping("/products")
    fun saveProduct(
        @RequestBody product: ProductInDTO,
        @RequestHeader("Authorization") token: String?
    ): ResponseEntity<Void>
}