package org.example.dto

import org.example.entity.Product
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.UUID

data class ProductResponse(
    @field:Schema(description = "id товара", accessMode = Schema.AccessMode.READ_ONLY)
    val id: UUID,

    @field:Size(min = 3, max = 32, message = "артикул должен иметь от 3 до 32 символов")
    @field:Schema(description = "артикул товара")
    val sku: String,

    @field:Size(max = 255, message = "название товара должно быть не длиннее 255 символов")
    @field:NotBlank(message = "название товара не должно быть пустым")
    @field:Schema(description = "название товара")
    val name: String,

    @field:Size(max = 1000, message = "описание товара должно быть не длиннее 1000 символов")
    @field:Schema(description = "описание товара")
    val description: String?,

    @field:PositiveOrZero(message = "цена не должна быть отрицательной")
    @field:Schema(description = "цена товара")
    @field:Digits(
        fraction = 2,
        message = "цена должна быть записываема 2 цифрами в дробной и 100 в целой частях",
        integer = 100
    )
    val price: BigDecimal,

    @field:PositiveOrZero(message = "остаток товара должен быть неотрицательным")
    @field:Schema(description = "остаток товара на складе")
    val quantity: Int,

    @field:Schema(description = "время создания записи о товаре", accessMode = Schema.AccessMode.READ_ONLY)
    @field:PastOrPresent(message = "время создания не должно быть в будущем")
    val createdAt: ZonedDateTime,

    @field:Schema(description = "время обновления записи о товаре", accessMode = Schema.AccessMode.READ_ONLY)
    @field:PastOrPresent(message = "время обновления не должно быть в будущем")
    val updatedAt: ZonedDateTime
) {
    companion object {
        fun from(entity: Product): ProductResponse {
            return ProductResponse(
                entity.id,
                entity.sku,
                entity.name,
                entity.description,
                entity.price,
                entity.quantity,
                entity.createdAt,
                entity.updatedAt
            )
        }
    }
}