package org.example.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class ProductRequest(
    @field:Size(min = 3, max = 32, message = "артикул должен иметь от 3 до 32 символов")
    @field:Schema(description = "артикул товара")
    @field:NotNull
    val sku: String?,

    @field:Size(max = 255, message = "название товара должно быть не длиннее 255 символов")
    @field:NotBlank(message = "название товара не должно быть пустым")
    @field:Schema(description = "название товара")
    @field:NotNull
    val name: String?,

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
    @field:NotNull
    val price: BigDecimal?,

    @field:PositiveOrZero(message = "остаток товара должен быть неотрицательным")
    @field:Schema(description = "остаток товара на складе")
    @field:NotNull
    val quantity: Int?,
)