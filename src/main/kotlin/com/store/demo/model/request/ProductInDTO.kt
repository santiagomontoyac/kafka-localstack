package com.store.demo.model.request

import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class ProductInDTO(
    @field:NotBlank(message = "Name may not be empty")
    val name: String,

    val description: String? = null,

    val stock: Double? = null,

    @field:DecimalMin(value = "0.0", message = "Price should be greater than 0")
    @field:NotNull(message = "Price may not be empty")
    val price: Double,

    @field:NotNull(message = "Id Category may not be empty")
    val idCategory: Long
)

