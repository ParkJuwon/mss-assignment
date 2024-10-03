package com.mss.mssassignment.application.listener.rank

import com.mss.mssassignment.domain.product.ProductRepository
import com.mss.mssassignment.domain.product.event.ProductCreatedEvent
import com.mss.mssassignment.domain.product.event.ProductDeletedEvent
import com.mss.mssassignment.domain.rank.CategoryRank
import com.mss.mssassignment.domain.rank.CategoryRankRepository
import com.mss.mssassignment.domain.rank.RankType
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CategoryRankEventListener(
    private val categoryRankRepository: CategoryRankRepository,
    private val productRepository: ProductRepository,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    @ServiceActivator(inputChannel = "ProductCreatedEvent")
    fun handle(event: ProductCreatedEvent) {
        // 상품이 생성/변경 될 때마다 랭킹을 업데이트한다.
        logger.info("[RankEventListener] ProductCreatedEvent: $event")

        val product = productRepository.findByIdOrNull(event.id)
        product?.let {
            val categoryRanks = categoryRankRepository.findAllByCategory(it.category)

            val lowestRank = categoryRanks.find { rank -> rank.type == RankType.LOWEST }
            lowestRank?.let { rank ->
                if (rank.product.price >= it.price) {
                    rank.product = it
                }
            } ?: categoryRankRepository.save(CategoryRank(category = it.category, type = RankType.LOWEST, product = it))

            val highestRank = categoryRanks.find { rank -> rank.type == RankType.HIGHEST }
            highestRank?.let { rank ->
                if (rank.product.price <= it.price) {
                    rank.product = it
                }
            } ?: categoryRankRepository.save(CategoryRank(category = it.category, type = RankType.HIGHEST, product = it))
        }
    }

    @ServiceActivator(inputChannel = "ProductDeletedEvent")
    fun handle(event: ProductDeletedEvent) {
        // 상품이 삭제 될 때마다 랭킹을 업데이트 한다.
        logger.info("[RankEventListener] ProductDeletedEvent: $event")
    }
}
