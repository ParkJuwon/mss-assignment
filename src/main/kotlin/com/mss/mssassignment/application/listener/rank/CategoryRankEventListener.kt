package com.mss.mssassignment.application.listener.rank

import com.mss.mssassignment.domain.product.Product
import com.mss.mssassignment.domain.product.event.ProductCreatedEvent
import com.mss.mssassignment.domain.product.event.ProductDeletedEvent
import com.mss.mssassignment.domain.product.event.ProductUpdatedEvent
import com.mss.mssassignment.domain.rank.CategoryRank
import com.mss.mssassignment.domain.rank.CategoryRankRepository
import com.mss.mssassignment.domain.rank.RankType
import org.slf4j.LoggerFactory
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CategoryRankEventListener(
    private val categoryRankRepository: CategoryRankRepository,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    @ServiceActivator(inputChannel = "ProductCreatedEvent")
    fun handle(event: ProductCreatedEvent) {
        // 상품이 생성 될 때마다 랭킹을 업데이트한다.
        logger.info("[CategoryRankEventListener] ProductCreatedEvent: ${event.product.id}")
        refreshCategoryRank(event.product)
    }

    @Transactional
    @ServiceActivator(inputChannel = "ProductUpdatedEvent")
    fun handle(event: ProductUpdatedEvent) {
        // 상품이 변경 될 때마다 랭킹을 업데이트한다.
        logger.info("[CategoryRankEventListener] ProductUpdatedEvent: ${event.product.id}")
        refreshCategoryRank(event.product)
    }

    @Transactional
    @ServiceActivator(inputChannel = "ProductDeletedEvent")
    fun handle(event: ProductDeletedEvent) {
        // 상품이 삭제 될 때마다 랭킹을 업데이트 한다.
        logger.info("[RankEventListener] ProductDeletedEvent: $event")
    }

    private fun refreshCategoryRank(product: Product) {
        val categoryRanks = categoryRankRepository.findAllByCategory(product.category)

        val lowestRank = categoryRanks.find { rank -> rank.type == RankType.LOWEST }
        lowestRank?.let { rank ->
            if ((rank.product?.price ?: Long.MAX_VALUE) >= product.price) {
                rank.product = product
            }
        } ?: categoryRankRepository.save(CategoryRank(category = product.category, type = RankType.LOWEST, product = product))

        val highestRank = categoryRanks.find { rank -> rank.type == RankType.HIGHEST }
        highestRank?.let { rank ->
            if ((rank.product?.price ?: Long.MIN_VALUE) <= product.price) {
                rank.product = product
            }
        } ?: categoryRankRepository.save(CategoryRank(category = product.category, type = RankType.HIGHEST, product = product))
    }
}
