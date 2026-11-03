package org.example.controller

import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.example.dto.ProductRequest
import org.example.dto.ProductResponse
import org.example.service.ProductService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/products")
class ProductController {
    @Autowired
    private lateinit var productService: ProductService

    @PostMapping("/")
    @Operation(summary = "создает новый товар")
    fun createProduct(@Valid @RequestBody productDto: ProductRequest): ResponseEntity<ProductResponse> {
        val ans = productService.createProduct(productDto)
        return ResponseEntity.status(201).body(ans)
    }

    @GetMapping("/")
    @Operation(
        summary = "ищет товар по названию",
        description = "получает товары с пагинацией по вхождению в название." +
                "\n Если параметра name нет то не фильтрует. Если одного из page и size нет - то выводит все"
    )
    fun getProducts(
        @RequestParam("строка поиска по имени") name: String?,
        @RequestParam("страница") page: Int?,
        @RequestParam("размер одной страницы") size: Int?
    ): ResponseEntity<List<ProductResponse>> {
        val ans = productService.getByName(name, page, size)
        return ResponseEntity.status(200).body(ans)
    }

    @GetMapping("/{id}")
    @Operation(summary = "получает товар по id")
    fun getProduct(
        @PathVariable id: UUID
    ): ResponseEntity<ProductResponse> {
        val ans = productService.getById(id)
        return ResponseEntity.status(200).body(ans)
    }

    @Operation(summary = "обновляет товар по id")
    @PutMapping("/{id}")
    fun updateProduct(
        @PathVariable id: UUID,
        @Valid @RequestBody productDto: ProductRequest
    ): ResponseEntity<Void> {
        productService.update(id, productDto)
        return ResponseEntity.status(200).build()
    }

    @Operation(summary = "обновляет количество товара на delta")
    @PatchMapping("/{id}/quantity")
    fun updateQuantity(
        @PathVariable id: UUID,
        @RequestBody(required = true) delta: Int
    ): ResponseEntity<Void> {
        productService.changeQuantity(id, delta)
        return ResponseEntity.status(200).build()
    }

    @Operation(summary = "удаляет продукт по id")
    @DeleteMapping("/{id}")
    fun deleteProduct(
        @PathVariable id: UUID
    ): ResponseEntity<Void> {
        productService.deleteProduct(id)
        return ResponseEntity.status(204).build()
    }
}