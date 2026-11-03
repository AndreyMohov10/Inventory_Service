package org.example.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.kotlinModule
import org.example.dto.ProductRequest
import org.example.dto.ProductResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.math.BigDecimal

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ProductControllerIntegrationTest {
    @Autowired
    private lateinit var context: WebApplicationContext

    private val objectMapper: ObjectMapper = ObjectMapper().apply {
        registerModule(kotlinModule())
        registerModule(JavaTimeModule())
    }

    private lateinit var mockMvc: MockMvc

    private val baseProduct = ProductRequest(
        sku = "base",
        name = "base",
        description = "base",
        price = BigDecimal(0),
        quantity = 0
    )

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .build()
    }


    companion object {
        @Container
        @JvmField
        val postgresContainer = PostgreSQLContainer("postgres:15-alpine").apply {
            withDatabaseName("test_db")
            withUsername("test_user")
            withPassword("test_password")
        }

        @JvmStatic
        @DynamicPropertySource
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { postgresContainer.getJdbcUrl() }
            registry.add("spring.datasource.username") { postgresContainer.username }
            registry.add("spring.datasource.password") { postgresContainer.password }
            registry.add("spring.flyway.enabled") { "true" }
            registry.add("spring.datasource.hikari.maximum-pool-size") { "25" }
            registry.add("spring.datasource.hikari.connection-timeout") { "10000" }
        }
    }

    private val baseUrl = "/api/v1/products"

    @Test
    @DisplayName("Создание продукта должно возвращать статус 201 и созданный объект")
    fun `should create product`() {
        val request = ProductRequest(
            sku = "abc",
            name = "notebook",
            description = "paper",
            price = BigDecimal(10),
            quantity = 10
        )

        mockMvc.post("$baseUrl/") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isCreated() }
            jsonPath("$.id") { exists() }
            jsonPath("$.sku") { equals(request.sku) }
            jsonPath("$.name") { equals(request.name) }
            jsonPath("$.description") { equals(request.description) }
            jsonPath("$.price") { equals(request.price) }
            jsonPath("$.quantity") { equals(request.quantity) }
            jsonPath("$.createdAt") { exists() }
            jsonPath("$.updatedAt") { exists() }
        }
    }

    @Test
    @DisplayName("Получение списка продуктов с фильтрацией по кириллическому параметру и пагинацией")
    fun `should get products with search and pagination`() {
        mockMvc.get("$baseUrl/") {
            param("строка поиска по имени", "Laptop")
            param("страница", "0")
            param("размер одной страницы", "10")
        }.andExpect {
            status { isOk() }
            jsonPath("$") { isArray() }
        }


    }

    @Test
    @DisplayName("Получение продукта по ID")
    fun `should get product by id`() {
        val response = mockMvc.post("$baseUrl/") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(baseProduct)
        }.andReturn().response.contentAsString
        val createdProduct = objectMapper.readValue(response, ProductResponse::class.java)
        val id = createdProduct.id

        mockMvc.get("$baseUrl/$id")
            .andExpect {
                status { isOk() }
                jsonPath("$.id") { equals(createdProduct.id) }
                jsonPath("$.sku") { equals(createdProduct.sku) }
                jsonPath("$.name") { equals(createdProduct.name) }
                jsonPath("$.description") { equals(createdProduct.description) }
                jsonPath("$.price") { equals(createdProduct.price) }
                jsonPath("$.quantity") { equals(createdProduct.quantity) }
                jsonPath("$.createdAt") { equals(createdProduct.createdAt) }
                jsonPath("$.updatedAt") { equals(createdProduct.updatedAt) }
            }
        mockMvc.delete("$baseUrl/$id").andExpect {
            status { isNoContent() }
        }
    }

    @Test
    @DisplayName("Обновление продукта по ID")
    fun `should update product`() {
        val response = mockMvc.post("$baseUrl/") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(baseProduct)
        }.andReturn().response.contentAsString
        val createdProduct = objectMapper.readValue(response, ProductResponse::class.java)
        val id = createdProduct.id

        val request = ProductRequest(
            sku = "abc",
            name = "notebook",
            description = "paper",
            price = BigDecimal(10),
            quantity = 10
        )

        mockMvc.put("$baseUrl/$id") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
        }
        mockMvc.delete("$baseUrl/$id").andExpect {
            status { isNoContent() }
        }
    }

    @Test
    @DisplayName("Обновление количества товара (delta)")
    fun `should update quantity`() {
        val response = mockMvc.post("$baseUrl/") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(baseProduct)
        }.andReturn().response.contentAsString
        val createdProduct = objectMapper.readValue(response, ProductResponse::class.java)
        val id = createdProduct.id
        val delta = 5

        mockMvc.patch("$baseUrl/$id/quantity") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(delta)
        }.andExpect {
            status { isOk() }
        }

        mockMvc.delete("$baseUrl/$id").andExpect {
            status { isNoContent() }
        }
    }

    @Test
    @DisplayName("Удаление продукта по ID")
    fun `should delete product`() {
        val response = mockMvc.post("$baseUrl/") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(baseProduct)
        }.andReturn().response.contentAsString
        val createdProduct = objectMapper.readValue(response, ProductResponse::class.java)
        val id = createdProduct.id


        mockMvc.delete("$baseUrl/$id").andExpect {
            status { isNoContent() }
        }
    }
}