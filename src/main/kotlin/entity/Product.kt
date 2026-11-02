package org.example.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID
import java.time.Instant

@Entity
@Table(name = "products")
open class Product {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    open var id: UUID? = null

    @Column(name = "sku", nullable = false)
    open var sku: String? = null

    @Column(name = "name", nullable = false)
    open var name: String? = null

    @Column(name = "description", nullable = true)
    open var description: String? = null

    @Column(name = "price", nullable = false)
    open var price: BigDecimal? = null

    @Column(name = "quantity", nullable = false)
    open var quantity: Int? = null

    @Column(name = "created_at", nullable = false)
    open var createdAt: Instant? = null

    @Column(name = "updated_at", nullable = false)
    open var updatedAt: Instant? = null
}