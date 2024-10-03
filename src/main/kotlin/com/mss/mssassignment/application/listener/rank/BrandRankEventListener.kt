package com.mss.mssassignment.application.listener.rank

import com.mss.mssassignment.domain.Category
import com.mss.mssassignment.domain.product.ProductRepository
import com.mss.mssassignment.domain.product.event.ProductCreatedEvent
import com.mss.mssassignment.domain.product.event.ProductDeletedEvent
import com.mss.mssassignment.domain.product.event.ProductUpdatedEvent
import com.mss.mssassignment.domain.rank.BrandRank
import com.mss.mssassignment.domain.rank.BrandRankRepository
import org.slf4j.LoggerFactory
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class BrandRankEventListener(
    private val productRepository: ProductRepository,
    private val brandRankRepository: BrandRankRepository,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    @ServiceActivator(inputChannel = "ProductCreatedEvent")
    fun handle(event: ProductCreatedEvent) {
        // 상품이 생성 할때마다 브랜드의 정보를 업데이트 한다.
        logger.info("[BrandRankEventListener] ProductCreatedEvent: $event")
        // 브랜드 정보 업데이트
        refreshBrandRank(event.product.brand)
    }

    @Transactional
    @ServiceActivator(inputChannel = "ProductUpdatedEvent")
    fun handle(event: ProductUpdatedEvent) {
        // 상품이 변경 할때마다 브랜드의 정보를 업데이트 한다.
        logger.info("[BrandRankEventListener] ProductUpdatedEvent: $event")
        // 브랜드 정보 업데이트
        refreshBrandRank(event.product.brand)
    }

    @Transactional
    @ServiceActivator(inputChannel = "ProductDeletedEvent")
    fun handle(event: ProductDeletedEvent) {
        // 상품이 삭제 할때마다 브랜드의 정보를 업데이트 한다.
        logger.info("[RankEventListener] ProductDeletedEvent: $event")
        // 브랜드 정보 업데이트
        refreshBrandRank(event.product.brand)
    }

    private fun refreshBrandRank(brand: String) {
        val allCategories = Category.entries.toSet()

        val brandProducts = productRepository.findAllByBrand(brand)
        val savedProductCategories = brandProducts.map { it.category }.toSet()

        if (allCategories != savedProductCategories) {
            // 모든 카테고리가 없으므로 브랜드 랭킹에서 제거
            brandRankRepository.removeByBrand(brand)
        } else {
            // 모든 카테고리가 있으므로 브랜드 랭킹 업데이트
            val brandRank = brandRankRepository.findByBrand(brand)
            brandRank?.let {
                it.totalPrice = brandProducts.sumOf { product -> product.price }
            } ?: brandRankRepository.save(
                BrandRank(
                    brand = brand,
                    products = brandProducts,
                    totalPrice =
                        brandProducts.sumOf { product ->
                            product.price
                        },
                ),
            )
        }
    }
}
