package service

import org.example.dto.ProductRequest
import org.example.entity.Product
import org.example.exception.NegativeQuantityException
import org.example.exception.NotFoundException
import org.example.repository.ProductRepository
import org.example.service.ProductService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.kotlin.whenever
import org.mockito.kotlin.any
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.anyOrNull
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


@ExtendWith(MockitoExtension::class)
class ProductServiceTest {

    @Mock
    private lateinit var productRepository: ProductRepository

    @InjectMocks
    private lateinit var productService: ProductService

    private val map = mutableMapOf<UUID, Product>()

    private var i: Long = 0

    private fun generateUUID(): UUID {
        val hex1 = String.format("%016x", i)
        val hex2 = hex1.take(4) + "-" + hex1.drop(4)
        i++
        return UUID.fromString("00000000-0000-0000-$hex2")
    }

    private fun createNewProductOrUpdate(product: Product): Product {
        var id = product.id
        if (id != null) {
            product.updatedAt = ZonedDateTime.now()
            map[id] = product
            return product
        }
        id = generateUUID()
        product.id = id
        product.createdAt = ZonedDateTime.now()
        product.updatedAt = ZonedDateTime.now()
        map[id] = product
        return product
    }

    @BeforeEach
    fun setup() {
        map.clear()
        whenever(productRepository.saveAndFlush(any<Product>()))
            .thenAnswer { invocationOnMock ->
                val product = invocationOnMock.getArgument<Product>(0)
                return@thenAnswer createNewProductOrUpdate(product)
            }

    }

    @Test
    fun crudProductTest() {
        whenever(
            productRepository.findById(
                any<UUID>()
            )
        ).thenAnswer { invocationOnMock ->
            val id = invocationOnMock.getArgument<UUID>(0)
            return@thenAnswer Optional.ofNullable(map.getOrDefault(id, null))
        }
        whenever(productRepository.delete(any<Product>()))
            .then { invocationOnMock ->
                val product = invocationOnMock.getArgument<Product>(0)
                val id = product.id
                map.remove(id)
                return@then Void.TYPE
            }


        val product = ProductRequest(
            sku = "abc",
            name = "notebook",
            description = "paper",
            price = BigDecimal(12.0),
            quantity = 10
        )
        val gotProduct = productService.createProduct(product)
        val id = gotProduct.id
        assertEquals(gotProduct.name, product.name)
        assertEquals(gotProduct.sku, product.sku)
        assertEquals(gotProduct.price, product.price)
        assertEquals(gotProduct.quantity, product.quantity)
        assertEquals(gotProduct.description, product.description)

        val anotherProduct = productService.getById(gotProduct.id)
        assertEquals(gotProduct, anotherProduct)

        val newName = "тетрадь"
        val updateProduct = product.copy(name = newName)
        productService.update(id, updateProduct)
        val updatedProduct = productService.getById(id)

        assertEquals(
            updatedProduct.copy(name = gotProduct.name, updatedAt = gotProduct.updatedAt),
            gotProduct
        )
        assertEquals(updatedProduct.name, newName)

        productService.deleteProduct(id)

        assertThrows<NotFoundException> {
            productService.getById(id)
        }
    }

    @Test
    fun updateQuantityTest() {
        whenever(
            productRepository.findById(
                any<UUID>()
            )
        ).thenAnswer { invocationOnMock ->
            val id = invocationOnMock.getArgument<UUID>(0)
            return@thenAnswer Optional.ofNullable(map.getOrDefault(id, null))
        }
        val productDto = ProductRequest(
            sku = "abc",
            name = "notebook",
            description = "paper",
            price = BigDecimal(1.0),
            quantity = 10
        )
        val id = productService.createProduct(productDto).id
        productService.changeQuantity(id, 10)
        var curProduct = productService.getById(id)
        assertEquals(20, curProduct.quantity)

        productService.changeQuantity(id, -20)
        curProduct = productService.getById(id)
        assertEquals(0, curProduct.quantity)

        assertThrows<NegativeQuantityException> {
            productService.changeQuantity(id, -10)
        }
    }

    @Test
    fun searchCheck() {
        whenever(
            productRepository.findByNameContaining(
                any(),
                anyOrNull()
            )
        ).thenAnswer { invocationOnMock ->
            val text = invocationOnMock.getArgument<String>(0)
            val pageable = invocationOnMock.getArgument<Pageable?>(1)

            if (pageable != null) {
                return@thenAnswer map
                    .map { it.value }
                    .filter { it.name?.contains(text) ?: false }
                    .drop(pageable.offset.toInt())
                    .take(pageable.pageSize)
                    .toList()
            } else {
                return@thenAnswer map
                    .map { it.value }
                    .filter { it.name?.contains(text) ?: false }
                    .toList()
            }
        }

        whenever {
            productRepository.findAll(
                any<Pageable>()
            )
        }.thenAnswer { invocationOnMock ->
            val pageable = invocationOnMock.getArgument<Pageable>(0)
            return@thenAnswer PageImpl(
                map
                    .map { it.value }
                    .drop(pageable.offset.toInt())
                    .take(pageable.pageSize),
                pageable,
                map.size.toLong())
        }

        whenever {
            productRepository.findAll()
        }.thenAnswer { invocationOnMock ->
            return@thenAnswer map
                .map { it.value }

        }

        val productDto = ProductRequest(
            sku = "abc",
            name = "notebook",
            description = "paper",
            price = BigDecimal(1.0),
            quantity = 10
        )
        productService.createProduct(productDto)
        productService.createProduct(productDto.copy(name = "333", sku = "cba"))
        val x = productService.getByName(productDto.name, null, null)
        assertEquals(1, x.size)
        val y = productService.getByName(null, 0, 1)
        assertEquals(1, y.size)

        val z = productService.getByName("1223", 0, 10)
        assertEquals(0, z.size)

        val u = productService.getByName(null, null, null)
        assertEquals(2, u.size)
    }
}