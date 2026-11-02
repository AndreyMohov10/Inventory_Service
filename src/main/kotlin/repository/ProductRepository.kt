package org.example.repository

import org.example.entity.Product
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ProductRepository : JpaRepository<Product, UUID> {
    fun findByNameContaining(text: String, pageable: Pageable?): List<Product>
}