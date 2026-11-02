package org.example.service

import org.example.dto.ProductRequest
import org.example.dto.ProductResponse
import org.example.entity.Product
import org.example.exception.NotFoundException
import org.example.repository.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ProductService {
    @Autowired
    private lateinit var repository: ProductRepository

    fun createProduct(productDto: ProductRequest): ProductResponse {
        val product = Product()
        product.sku = productDto.sku!!
        product.name = productDto.name!!
        product.description = productDto.description
        product.price = productDto.price!!
        product.quantity = productDto.quantity!!

        return ProductResponse.from(
            repository.saveAndFlush(
                product
            )
        )
    }

    fun getByName(name: String?, page: Int?, size: Int?): List<ProductResponse> {
        val pageable: Pageable? = if (page == null || size == null) null
        else PageRequest.of(page, size)
        return if (name == null) {
            pageable?.run {
                repository.findAll(this).toList()
            } ?: repository.findAll()
        } else {
            repository.findByNameContaining(
                name,
                pageable
            )
        }.map { ProductResponse.from(it) }.toList()

    }

    fun getById(id: UUID): ProductResponse {
        val product = repository.findByIdOrNull(id) ?: throw NotFoundException("не существет товара с таким id")
        return ProductResponse.from(
            product
        )
    }

    fun update(id: UUID, productDto: ProductRequest) {
        val product = repository.findByIdOrNull(id) ?: throw NotFoundException("не существет товара с таким id")
        product.sku = productDto.sku!!
        product.name = productDto.name!!
        product.description = productDto.description
        product.price = productDto.price!!
        product.quantity = productDto.quantity!!
        repository.saveAndFlush(product)
    }

    fun changeQuantity(id: UUID, delta: Int): ProductResponse {
        val product = repository.findByIdOrNull(id) ?: throw NotFoundException("не существет товара с таким id")
        product.quantity += delta
        return ProductResponse.from(
            repository.saveAndFlush(product)
        )
    }

    fun deleteProduct(id: UUID) {
        val product = repository.findByIdOrNull(id) ?: throw NotFoundException("не существет товара с таким id")
        repository.delete(product)
    }
}