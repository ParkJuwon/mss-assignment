package com.mss.mssassignment

import com.mss.mssassignment.application.service.InitialService
import com.mss.mssassignment.application.service.product.ProductsResponse
import com.mss.mssassignment.application.service.rank.BrandLowestRankResponse
import com.mss.mssassignment.application.service.rank.CategoryLowestRankResponse
import com.mss.mssassignment.application.service.rank.CategoryNameRankResponse
import com.mss.mssassignment.domain.Category
import com.mss.mssassignment.domain.product.Product
import com.mss.mssassignment.domain.product.ProductRepository
import com.mss.mssassignment.infrastructure.exception.MssExceptionType
import com.mss.mssassignment.presentation.config.ExceptionBody
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MssAssignmentApplicationTests
    @Autowired
    constructor(
        private val restTemplate: TestRestTemplate,
        private val productRepository: ProductRepository,
        private val brandRepository: ProductRepository,
        private val categoryRepository: ProductRepository,
        private val initialService: InitialService,
    ) {
        @BeforeEach
        fun tearDown() {
            productRepository.deleteAll()
            brandRepository.deleteAll()
            categoryRepository.deleteAll()
            initialService.initialProduct()
        }

        @Test
        @DisplayName("상품 조회시 id 로 조회 가능")
        fun productGetByIdTest() {
            val lastProduct = productRepository.findTopByOrderByIdDesc()
            val response = restTemplate.getForEntity("/admin/product/${lastProduct.id}", Product::class.java)

            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            with(response.body!!) {
                assertThat(this.id).isEqualTo(lastProduct.id)
                assertThat(this.brand).isEqualTo(lastProduct.brand)
                assertThat(this.category).isEqualTo(lastProduct.category)
                assertThat(this.price).isEqualTo(lastProduct.price)
            }
        }

        @Test
        @DisplayName("상품 조회시 id 로 조회 실패시 404 반환")
        fun productGetByIdFailTest() {
            val lastProduct = productRepository.findTopByOrderByIdDesc()
            val notFoundId = lastProduct.id!! + 1L
            val response = restTemplate.getForEntity("/admin/product/$notFoundId", ExceptionBody::class.java)

            assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
            with(response.body!!) {
                assertThat(this.code).isEqualTo(MssExceptionType.PRODUCT_NOT_FOUND.code)
                assertThat(this.message).isEqualTo(MssExceptionType.PRODUCT_NOT_FOUND.message)
            }
        }

        @Test
        @DisplayName("상품 생성 시 해당 브랜드와 카테고리가 없으면 생성 성공")
        fun productCreateTest() {
            val newProduct = Product(brand = "X", category = Category.PANTS, price = 1000)
            val response = restTemplate.postForEntity("/admin/product", newProduct, Product::class.java)

            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            with(response.body!!) {
                assertThat(this.id).isNotNull()
                assertThat(this.brand).isEqualTo(newProduct.brand)
                assertThat(this.category).isEqualTo(newProduct.category)
                assertThat(this.price).isEqualTo(newProduct.price)
            }
        }

        @Test
        @DisplayName("상품 생성 시 해당 브랜드와 카테고리가 중복이 있으면 실패한다")
        fun productCreateFailTest() {
            val lastProduct = productRepository.findTopByOrderByIdDesc()
            val newProduct = Product(brand = lastProduct.brand, category = lastProduct.category, price = 1000)
            val response = restTemplate.postForEntity("/admin/product", newProduct, ExceptionBody::class.java)

            assertThat(response.statusCode).isEqualTo(HttpStatus.CONFLICT)
            with(response.body!!) {
                assertThat(this.code).isEqualTo(MssExceptionType.PRODUCT_ALREADY_EXISTS.code)
                assertThat(this.message).isEqualTo(MssExceptionType.PRODUCT_ALREADY_EXISTS.message)
            }
        }

        @Test
        @DisplayName("상품 변경 시 저장 되어 있는 상품이면 성공한다")
        fun productUpdateTest() {
            val lastProduct = productRepository.findTopByOrderByIdDesc()
            val updateProduct = Product(id = lastProduct.id, brand = lastProduct.brand, category = lastProduct.category, price = 1000)

            val header =
                HttpHeaders().apply {
                    contentType = MediaType.APPLICATION_JSON
                }
            val entity = HttpEntity(updateProduct, header)
            val response = restTemplate.exchange("/admin/product", HttpMethod.PUT, entity, Product::class.java)
            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            with(response.body!!) {
                assertThat(this.id).isEqualTo(updateProduct.id)
                assertThat(this.brand).isEqualTo(updateProduct.brand)
                assertThat(this.category).isEqualTo(updateProduct.category)
                assertThat(this.price).isEqualTo(updateProduct.price)
            }
        }

        @Test
        @DisplayName("상품 변경시 저장 되어 있지 않은 상품이면 실패한다")
        fun productUpdateNotFoundTest() {
            val lastProduct = productRepository.findTopByOrderByIdDesc()
            val updateProduct = Product(id = lastProduct.id!! + 1, brand = lastProduct.brand, category = lastProduct.category, price = 1000)

            val header =
                HttpHeaders().apply {
                    contentType = MediaType.APPLICATION_JSON
                }
            val entity = HttpEntity(updateProduct, header)
            val response = restTemplate.exchange("/admin/product", HttpMethod.PUT, entity, ExceptionBody::class.java)
            assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
            with(response.body!!) {
                assertThat(this.code).isEqualTo(MssExceptionType.PRODUCT_NOT_FOUND.code)
                assertThat(this.message).isEqualTo(MssExceptionType.PRODUCT_NOT_FOUND.message)
            }
        }

        @Test
        @DisplayName("상품 변경시 중복 브랜드 카테고리 상품이면 실패한다")
        fun productUpdateAlreadyTest() {
            val lastProduct = productRepository.findTopByOrderByIdDesc()
            val updateProduct =
                Product(
                    id = lastProduct.id,
                    brand = lastProduct.brand,
                    category =
                        Category.entries.first {
                            it !=
                                lastProduct.category
                        },
                    price = 1000,
                )

            val header =
                HttpHeaders().apply {
                    contentType = MediaType.APPLICATION_JSON
                }
            val entity = HttpEntity(updateProduct, header)
            val response = restTemplate.exchange("/admin/product", HttpMethod.PUT, entity, ExceptionBody::class.java)
            assertThat(response.statusCode).isEqualTo(HttpStatus.CONFLICT)
            with(response.body!!) {
                assertThat(this.code).isEqualTo(MssExceptionType.PRODUCT_ALREADY_EXISTS.code)
                assertThat(this.message).isEqualTo(MssExceptionType.PRODUCT_ALREADY_EXISTS.message)
            }
        }

        @Test
        @DisplayName("상품 삭제시 존재 하는 id 호출시 성공 한다")
        fun productDeleteTest() {
            val deleteProduct = productRepository.findTopByOrderByIdDesc()
            val header =
                HttpHeaders().apply {
                    contentType = MediaType.APPLICATION_JSON
                }
            val entity = HttpEntity(null, header)
            val response = restTemplate.exchange("/admin/product/${deleteProduct.id!!}", HttpMethod.DELETE, entity, Product::class.java)
            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            with(response.body!!) {
                assertThat(this.id).isEqualTo(deleteProduct.id)
                assertThat(this.brand).isEqualTo(deleteProduct.brand)
                assertThat(this.category).isEqualTo(deleteProduct.category)
                assertThat(this.price).isEqualTo(deleteProduct.price)
            }
        }

        @Test
        @DisplayName("상품 삭제시 존재 하지 않는 id 호출시 실패 한다")
        fun productDeleteFailTest() {
            val deleteProduct = productRepository.findTopByOrderByIdDesc()
            val header =
                HttpHeaders().apply {
                    contentType = MediaType.APPLICATION_JSON
                }
            val entity = HttpEntity(null, header)
            val response =
                restTemplate.exchange(
                    "/admin/product/${deleteProduct.id!! + 1}",
                    HttpMethod.DELETE,
                    entity,
                    ExceptionBody::class.java,
                )
            assertThat(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
            with(response.body!!) {
                assertThat(this.code).isEqualTo(MssExceptionType.PRODUCT_NOT_FOUND.code)
                assertThat(this.message).isEqualTo(MssExceptionType.PRODUCT_NOT_FOUND.message)
            }
        }

        @Test
        @DisplayName("카테고리 별 최저가 조회시 성공 한다")
        fun categoryLowestTest() {
            val response = restTemplate.getForEntity("/category/lowest", CategoryLowestRankResponse::class.java)

            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            with(response.body!!) {
                val productCategories = this.products.map { it.category }.toSet()
                assertThat(productCategories).isEqualTo(Category.entries.toSet())

                val totalPrice = this.products.sumOf { it.price }
                assertThat(totalPrice).isEqualTo(this.totalPrice)
            }
        }

        @Test
        @DisplayName("전체 카테고리 최저가 변경 시 변경된 값으로 갱신된다")
        fun categoryLowestUpdateTest() {
            val firstResponse = restTemplate.getForEntity("/category/lowest", CategoryLowestRankResponse::class.java)
            assertThat(firstResponse.statusCode).isEqualTo(HttpStatus.OK)

            val randomProduct = firstResponse.body!!.products.random()
            val allProductByCategory = productRepository.findAllByCategory(randomProduct.category)
            val anotherProduct = allProductByCategory.first { it.brand != randomProduct.brand }
            anotherProduct.price = randomProduct.price - 1

            val header =
                HttpHeaders().apply {
                    contentType = MediaType.APPLICATION_JSON
                }
            val entity = HttpEntity(anotherProduct, header)
            val updateResponse = restTemplate.exchange("/admin/product", HttpMethod.PUT, entity, Product::class.java)
            assertThat(updateResponse.statusCode).isEqualTo(HttpStatus.OK)

            Thread.sleep(100) // 이벤트 처리를 위한 대기
            val response = restTemplate.getForEntity("/category/lowest", CategoryLowestRankResponse::class.java)
            with(response.body!!) {
                val productCategories = this.products.map { it.category }.toSet()
                assertThat(productCategories).isEqualTo(Category.entries.toSet())

                val totalPrice = this.products.sumOf { it.price }
                assertThat(totalPrice).isEqualTo(this.totalPrice)

                val changedProduct = this.products.find { it.category == anotherProduct.category }!!
                assertThat(changedProduct.price).isEqualTo(anotherProduct.price)
                assertThat(changedProduct.brand).isEqualTo(anotherProduct.brand)
            }
        }

        @Test
        @DisplayName("전체 카테고리 최저가 삭제 시 변경된 값으로 갱신된다")
        fun categoryLowestDeleteTest() {
            val firstResponse = restTemplate.getForEntity("/category/lowest", CategoryLowestRankResponse::class.java)
            assertThat(firstResponse.statusCode).isEqualTo(HttpStatus.OK)

            val randomProduct = firstResponse.body!!.products.random()
            val allProductByCategory = productRepository.findAllByCategory(randomProduct.category)
            val deleteProduct = allProductByCategory.first { it.brand == randomProduct.brand }

            val header =
                HttpHeaders().apply {
                    contentType = MediaType.APPLICATION_JSON
                }
            val entity = HttpEntity(null, header)
            val deleteResponse =
                restTemplate.exchange(
                    "/admin/product/${deleteProduct.id!!}",
                    HttpMethod.DELETE,
                    entity,
                    Product::class.java,
                )
            assertThat(deleteResponse.statusCode).isEqualTo(HttpStatus.OK)

            Thread.sleep(100) // 이벤트 처리를 위한 대기
            val response = restTemplate.getForEntity("/category/lowest", CategoryLowestRankResponse::class.java)
            with(response.body!!) {
                val productCategories = this.products.map { it.category }.toSet()
                assertThat(productCategories).isEqualTo(Category.entries.toSet())

                val totalPrice = this.products.sumOf { it.price }
                assertThat(totalPrice).isEqualTo(this.totalPrice)

                val changedProduct = this.products.find { it.category == deleteProduct.category }!!
                assertThat(changedProduct.brand).isNotEqualTo(deleteProduct.brand)
            }
        }

        @Test
        @DisplayName("전체 카테고리 최저가 생성 시 변경된 값으로 갱신된다")
        fun categoryLowestCreateTest() {
            val firstResponse = restTemplate.getForEntity("/category/lowest", CategoryLowestRankResponse::class.java)
            assertThat(firstResponse.statusCode).isEqualTo(HttpStatus.OK)

            val randomProduct = firstResponse.body!!.products.random()

            val newProduct = Product(brand = "X", category = randomProduct.category, price = 1)
            val createResponse = restTemplate.postForEntity("/admin/product", newProduct, Product::class.java)
            assertThat(createResponse.statusCode).isEqualTo(HttpStatus.OK)

            Thread.sleep(100) // 이벤트 처리를 위한 대기
            val response = restTemplate.getForEntity("/category/lowest", CategoryLowestRankResponse::class.java)
            with(response.body!!) {
                val productCategories = this.products.map { it.category }.toSet()
                assertThat(productCategories).isEqualTo(Category.entries.toSet())

                val totalPrice = this.products.sumOf { it.price }
                assertThat(totalPrice).isEqualTo(this.totalPrice)

                val changedProduct = this.products.find { it.category == newProduct.category }!!
                assertThat(changedProduct.brand).isNotEqualTo(randomProduct.brand)
                assertThat(changedProduct.brand).isEqualTo(newProduct.brand)
            }
        }

        @Test
        @DisplayName("카테고리 이름별 조회시 조회가 잘 이루어 진다")
        fun categoryNameTest() {
            val categoryName = "바지"
            val response = restTemplate.getForEntity("/category/rank?name=$categoryName", CategoryNameRankResponse::class.java)
            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            with(response.body!!) {
                assertThat(this.categoryName).isEqualTo(categoryName)
                assertThat(this.lowest).isNotNull()
                assertThat(this.highest).isNotNull()
            }
        }

        @Test
        @DisplayName("카테고리 이름별 조회시 최저가 상품 생성 시 변경된 값으로 갱신된다")
        fun categoryNameLowestCreateTest() {
            val category = Category.TOP
            val categoryName = category.value
            val rankFirstResponse = restTemplate.getForEntity("/category/rank?name=$categoryName", CategoryNameRankResponse::class.java)
            assertThat(rankFirstResponse.statusCode).isEqualTo(HttpStatus.OK)

            val newProduct = Product(brand = "X", category = category, price = rankFirstResponse.body!!.lowest.price - 1)
            val createResponse = restTemplate.postForEntity("/admin/product", newProduct, Product::class.java)
            assertThat(createResponse.statusCode).isEqualTo(HttpStatus.OK)

            Thread.sleep(100) // 이벤트 처리를 위한 대기
            val rankSecondResponse = restTemplate.getForEntity("/category/rank?name=$categoryName", CategoryNameRankResponse::class.java)
            assertThat(rankSecondResponse.statusCode).isEqualTo(HttpStatus.OK)
            with(rankSecondResponse.body!!.lowest) {
                assertThat(this.brand).isEqualTo(newProduct.brand)
                assertThat(this.price).isEqualTo(newProduct.price)
            }
        }

        @Test
        @DisplayName("카테고리 이름별 조회시 최고가 상품 생성 시 변경된 값으로 갱신된다")
        fun categoryNameHighestCreateTest() {
            val category = Category.OUTER
            val categoryName = category.value
            val rankFirstResponse = restTemplate.getForEntity("/category/rank?name=$categoryName", CategoryNameRankResponse::class.java)
            assertThat(rankFirstResponse.statusCode).isEqualTo(HttpStatus.OK)

            val newProduct = Product(brand = "X", category = category, price = rankFirstResponse.body!!.highest.price + 1)
            val createResponse = restTemplate.postForEntity("/admin/product", newProduct, Product::class.java)
            assertThat(createResponse.statusCode).isEqualTo(HttpStatus.OK)

            Thread.sleep(100) // 이벤트 처리를 위한 대기
            val rankSecondResponse = restTemplate.getForEntity("/category/rank?name=$categoryName", CategoryNameRankResponse::class.java)
            assertThat(rankSecondResponse.statusCode).isEqualTo(HttpStatus.OK)
            with(rankSecondResponse.body!!.highest) {
                assertThat(this.brand).isEqualTo(newProduct.brand)
                assertThat(this.price).isEqualTo(newProduct.price)
            }
        }

        @Test
        @DisplayName("카테고리 이름별 조회시 최저가 상품 변경 시 변경된 값으로 갱신된다")
        fun categoryNameLowestUpdateTest() {
            val category = Category.PANTS
            val categoryName = category.value
            val rankFirstResponse = restTemplate.getForEntity("/category/rank?name=$categoryName", CategoryNameRankResponse::class.java)
            assertThat(rankFirstResponse.statusCode).isEqualTo(HttpStatus.OK)

            val allProductByCategory = productRepository.findAllByCategory(category)
            val anotherProduct = allProductByCategory.first { it.brand != rankFirstResponse.body!!.lowest.brand }
            anotherProduct.price = rankFirstResponse.body!!.lowest.price - 1

            val header =
                HttpHeaders().apply {
                    contentType = MediaType.APPLICATION_JSON
                }
            val entity = HttpEntity(anotherProduct, header)
            val response = restTemplate.exchange("/admin/product", HttpMethod.PUT, entity, Product::class.java)
            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

            Thread.sleep(100) // 이벤트 처리를 위한 대기
            val rankSecondResponse = restTemplate.getForEntity("/category/rank?name=$categoryName", CategoryNameRankResponse::class.java)
            assertThat(rankSecondResponse.statusCode).isEqualTo(HttpStatus.OK)
            with(rankSecondResponse.body!!.lowest) {
                assertThat(this.brand).isEqualTo(anotherProduct.brand)
                assertThat(this.price).isEqualTo(anotherProduct.price)
            }
        }

        @Test
        @DisplayName("카테고리 이름별 조회시 최고가 상품 변경 시 변경된 값으로 갱신된다")
        fun categoryNameHighestUpdateTest() {
            val category = Category.SNEAKERS
            val categoryName = category.value
            val rankFirstResponse = restTemplate.getForEntity("/category/rank?name=$categoryName", CategoryNameRankResponse::class.java)
            assertThat(rankFirstResponse.statusCode).isEqualTo(HttpStatus.OK)

            val allProductByCategory = productRepository.findAllByCategory(category)
            val anotherProduct = allProductByCategory.first { it.brand != rankFirstResponse.body!!.highest.brand }
            anotherProduct.price = rankFirstResponse.body!!.highest.price + 1

            val header =
                HttpHeaders().apply {
                    contentType = MediaType.APPLICATION_JSON
                }
            val entity = HttpEntity(anotherProduct, header)
            val response = restTemplate.exchange("/admin/product", HttpMethod.PUT, entity, Product::class.java)
            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

            Thread.sleep(100) // 이벤트 처리를 위한 대기
            val rankSecondResponse = restTemplate.getForEntity("/category/rank?name=$categoryName", CategoryNameRankResponse::class.java)
            assertThat(rankSecondResponse.statusCode).isEqualTo(HttpStatus.OK)
            with(rankSecondResponse.body!!.highest) {
                assertThat(this.brand).isEqualTo(anotherProduct.brand)
                assertThat(this.price).isEqualTo(anotherProduct.price)
            }
        }

        @Test
        @DisplayName("카테고리 이름별 조회시 최저가 상품 삭제 시 변경된 값으로 갱신된다")
        fun categoryNameLowestDeleteTest() {
            val category = Category.BAG
            val categoryName = category.value
            val rankFirstResponse = restTemplate.getForEntity("/category/rank?name=$categoryName", CategoryNameRankResponse::class.java)
            assertThat(rankFirstResponse.statusCode).isEqualTo(HttpStatus.OK)

            val allProductByCategory = productRepository.findAllByCategory(category)
            val lowestProduct = allProductByCategory.first { it.brand == rankFirstResponse.body!!.lowest.brand }

            val header =
                HttpHeaders().apply {
                    contentType = MediaType.APPLICATION_JSON
                }
            val entity = HttpEntity(null, header)
            val response = restTemplate.exchange("/admin/product/${lowestProduct.id!!}", HttpMethod.DELETE, entity, Product::class.java)
            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

            Thread.sleep(100) // 이벤트 처리를 위한 대기
            val rankSecondResponse = restTemplate.getForEntity("/category/rank?name=$categoryName", CategoryNameRankResponse::class.java)
            assertThat(rankSecondResponse.statusCode).isEqualTo(HttpStatus.OK)
            with(rankSecondResponse.body!!.lowest) {
                assertThat(this.brand).isNotEqualTo(lowestProduct.brand)
                assertThat(this.price).isNotEqualTo(lowestProduct.price)
            }
        }

        @Test
        @DisplayName("카테고리 이름별 조회시 최고가 상품 삭제 시 변경된 값으로 갱신된다")
        fun categoryNameHighestDeleteTest() {
            val category = Category.CAP
            val categoryName = category.value
            val rankFirstResponse = restTemplate.getForEntity("/category/rank?name=$categoryName", CategoryNameRankResponse::class.java)
            assertThat(rankFirstResponse.statusCode).isEqualTo(HttpStatus.OK)

            val allProductByCategory = productRepository.findAllByCategory(category)
            val highestProduct = allProductByCategory.first { it.brand == rankFirstResponse.body!!.highest.brand }

            val header =
                HttpHeaders().apply {
                    contentType = MediaType.APPLICATION_JSON
                }
            val entity = HttpEntity(null, header)
            val response = restTemplate.exchange("/admin/product/${highestProduct.id!!}", HttpMethod.DELETE, entity, Product::class.java)
            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

            Thread.sleep(100) // 이벤트 처리를 위한 대기
            val rankSecondResponse = restTemplate.getForEntity("/category/rank?name=$categoryName", CategoryNameRankResponse::class.java)
            assertThat(rankSecondResponse.statusCode).isEqualTo(HttpStatus.OK)
            with(rankSecondResponse.body!!.lowest) {
                assertThat(this.brand).isNotEqualTo(highestProduct.brand)
                assertThat(this.price).isNotEqualTo(highestProduct.price)
            }
        }

        @Test
        @DisplayName("브랜드 최저가 조회가 잘 된다")
        fun brandLowestTest() {
            val response = restTemplate.getForEntity("/brand/lowest", BrandLowestRankResponse::class.java)
            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)

            with(response.body!!) {
                assertThat(this.totalPrice).isNotNull()
                assertThat(this.brand).isNotNull()
                assertThat(this.totalPrice).isEqualTo(products.sumOf { it.price })
                assertThat(Category.entries.toSet()).isEqualTo(products.map { it.category }.toSet())
            }
        }

        @Test
        @DisplayName("브랜드 최저가 조회시 카테고리가 하나라도 지워지면 다른 브랜드로 변경된다")
        fun brandLowestDeleteTest() {
            val lowestBrandResponse = restTemplate.getForEntity("/brand/lowest", BrandLowestRankResponse::class.java)
            assertThat(lowestBrandResponse.statusCode).isEqualTo(HttpStatus.OK)

            val randomProduct = lowestBrandResponse.body!!.products.random()
            val allProductByCategory = productRepository.findAllByCategory(randomProduct.category)
            val deleteProduct = allProductByCategory.first { it.brand == lowestBrandResponse.body!!.brand }

            val header =
                HttpHeaders().apply {
                    contentType = MediaType.APPLICATION_JSON
                }
            val entity = HttpEntity(null, header)
            val deleteResponse =
                restTemplate.exchange(
                    "/admin/product/${deleteProduct.id!!}",
                    HttpMethod.DELETE,
                    entity,
                    Product::class.java,
                )
            assertThat(deleteResponse.statusCode).isEqualTo(HttpStatus.OK)

            Thread.sleep(100) // 이벤트 처리를 위한 대기

            val response = restTemplate.getForEntity("/brand/lowest", BrandLowestRankResponse::class.java)
            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(response.body!!.brand).isNotEqualTo(lowestBrandResponse.body!!.brand)
        }

        @Test
        @DisplayName("브랜드 최저가 조회시 다른 브랜드 상품을 가격을 최저가로 변경하면 브랜드가 변경된다")
        fun brandLowestUpdateTest() {
            val lowestBrandResponse = restTemplate.getForEntity("/brand/lowest", BrandLowestRankResponse::class.java)
            assertThat(lowestBrandResponse.statusCode).isEqualTo(HttpStatus.OK)

            val randomProduct = lowestBrandResponse.body!!.products.random()
            val allProductByCategory = productRepository.findAllByCategory(randomProduct.category)
            val anotherProduct = allProductByCategory.first { it.brand != lowestBrandResponse.body!!.brand }
            val anotherBrand = anotherProduct.brand

            val brandProductResponse = restTemplate.getForEntity("/admin/product/brand/$anotherBrand", ProductsResponse::class.java)
            val lowestProductMap = lowestBrandResponse.body!!.products.associateBy({ it.category }, { it.price })
            brandProductResponse.body!!
                .products
                .map {
                    it.price = lowestProductMap[it.category]!! - 1
                    it
                }.forEach {
                    val header =
                        HttpHeaders().apply {
                            contentType = MediaType.APPLICATION_JSON
                        }
                    val entity = HttpEntity(it, header)
                    val response = restTemplate.exchange("/admin/product", HttpMethod.PUT, entity, Product::class.java)
                    assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
                    Thread.sleep(100) // 이벤트 처리를 위한 대기
                }

            val response = restTemplate.getForEntity("/brand/lowest", BrandLowestRankResponse::class.java)
            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(response.body!!.brand).isEqualTo(anotherBrand)
        }

        @Test
        @DisplayName("브랜드 최저가 조회시 신규 브랜드 상품을 추가하면 브랜드가 추가된다")
        fun brandLowestCreateTest() {
            val newBrand = "X"
            val lowestBrandResponse = restTemplate.getForEntity("/brand/lowest", BrandLowestRankResponse::class.java)
            assertThat(lowestBrandResponse.statusCode).isEqualTo(HttpStatus.OK)

            val products = lowestBrandResponse.body!!.products
            products.forEach {
                val newProduct = Product(brand = newBrand, category = it.category, price = it.price - 1)
                val response = restTemplate.postForEntity("/admin/product", newProduct, Product::class.java)

                assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
                Thread.sleep(100) // 이벤트 처리를 위한 대기
            }

            val response = restTemplate.getForEntity("/brand/lowest", BrandLowestRankResponse::class.java)
            assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(response.body!!.brand).isEqualTo(newBrand)
        }
    }
