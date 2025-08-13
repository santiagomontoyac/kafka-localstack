package com.store.demo.model

import java.util.*

data class Product(

    val id: Long? = null,

    var name: String,

    var description: String? = null,

    var stock: Double = 0.0,

    var price: Double,

    var status: ProductStatus = ProductStatus.CREATED,

    val createAt: Date = Date(),

    var category: Category
)
