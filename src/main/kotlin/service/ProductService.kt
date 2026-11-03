package org.example.service

import org.example.dto.ProductRequest
import org.example.dto.ProductResponse
import org.example.entity.Product
import org.example.exception.NegativeQuantityException
import org.example.exception.NotFoundException
import org.example.repository.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.resilience.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
open class ProductService {
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

    @Transactional
    fun update(id: UUID, productDto: ProductRequest) {
        val product = repository.findByIdOrNull(id) ?: throw NotFoundException("не существет товара с таким id")
        product.sku = productDto.sku!!
        product.name = productDto.name!!
        product.description = productDto.description
        product.price = productDto.price!!
        product.quantity = productDto.quantity!!
        repository.saveAndFlush(product)
    }

    @Retryable(
        value = [ObjectOptimisticLockingFailureException::class],
        maxRetries = 3,
        maxDelay = 500,
        delay = 50,
        multiplier = 2.0,
    )
    @Transactional
    fun changeQuantity(id: UUID, delta: Int): ProductResponse {
        val product = repository.findByIdOrNull(id)
            ?: throw NotFoundException("не существует товара с id = $id")
        product.quantity += delta
        if (product.quantity < 0) {
            throw NegativeQuantityException("Невалидное обновление: остаток не должен быть отрицательным")
        }
        return ProductResponse.from(
            repository.saveAndFlush(product)
        )
    }

    fun deleteProduct(id: UUID) {
        val product = repository.findByIdOrNull(id) ?: throw NotFoundException("не существет товара с таким id")
        repository.delete(product)
    }
}