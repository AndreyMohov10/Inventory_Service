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
    open var quantity: Int = 0

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    open var createdAt: ZonedDateTime? = null

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    open var updatedAt: ZonedDateTime? = null

    @Version
    private val version: Long? = null
}