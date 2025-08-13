package com.store.demo.service

import com.store.demo.model.Product
import com.store.demo.model.request.ProductInDTO

interface ProductService {
    fun processProduct(productInDTO: ProductInDTO): Product
}