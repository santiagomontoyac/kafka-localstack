package com.store.demo.service.impl

import com.store.demo.mapper.IMapper
import com.store.demo.model.Product
import com.store.demo.model.request.ProductInDTO
import com.store.demo.service.ProductService
import com.store.demo.service.client.ProductClient
import org.springframework.stereotype.Service

@Service
class ProductServiceImpl(
    val mapper: IMapper<ProductInDTO, Product>,
    val productClient: ProductClient,
    val jwtGenerator: JwtGenerator
) : ProductService {

    override fun processProduct(productInDTO: ProductInDTO): Product {
        val token = jwtGenerator.generateToken()
        val product = mapper.map(productInDTO)
        productClient.saveProduct(productInDTO, token)  // Feign sends it to external REST API
        return product
    }

}