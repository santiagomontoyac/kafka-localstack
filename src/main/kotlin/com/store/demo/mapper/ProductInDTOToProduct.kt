package com.store.demo.mapper


import com.store.demo.model.Category
import com.store.demo.model.Product
import com.store.demo.model.ProductStatus
import com.store.demo.model.request.ProductInDTO
import org.springframework.stereotype.Component
import java.util.*

@Component
class ProductInDTOToProduct(
) : IMapper<ProductInDTO, Product> {

    override fun map(input: ProductInDTO): Product {
        return Product(
            name = input.name,
            description = input.description,
            stock = 0.0,
            price = input.price,
            status = ProductStatus.CREATED,
            createAt = Date(),
            category = Category(
                id = 1,
                name = "Default Category" // Assuming a default name, you might want to fetch the actual category
            )
        )
    }

    override fun update(target: Product, input: ProductInDTO) {
        input.name.let { target.name = it }
        input.description?.let { target.description = it }
        input.price.let { target.price = it }
        input.stock?.let { target.stock = it }
        input.idCategory.let {
            target.category = Category(
                id = it,
                name = "Default Category" // Assuming a default name, you might want to fetch the actual category
            )
        }
    }
}