package org.example.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.*

@Entity
@Table(name = "products")
open class Product {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    open lateinit var id: UUID

    @Column(name = "sku", nullable = false)
    open lateinit var sku: String

    @Column(name = "name", nullable = false)
    open lateinit var name: String

    @Column(name = "description", nullable = true)
    open var description: String? = null

    @Column(name = "price", nullable = false)
    open lateinit var price: BigDecimal

    @Column(name = "quantity", nullable = false)
    open var quantity: Int = 0

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    open lateinit var createdAt: ZonedDateTime

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    open lateinit var updatedAt: ZonedDateTime

    @Version
    private val version: Long? = null
}