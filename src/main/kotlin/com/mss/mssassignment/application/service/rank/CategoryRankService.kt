package com.mss.mssassignment.application.service.rank

import com.mss.mssassignment.application.service.product.ProductResponse
import com.mss.mssassignment.domain.rank.CategoryRankRepository
import com.mss.mssassignment.domain.rank.RankType
import org.springframework.stereotype.Service

@Service
class CategoryRankService(
    private val categoryRankRepository: CategoryRankRepository,
) {
    fun getLowestRank(): CategoryRankResponse {
        val categoryProducts = categoryRankRepository.findAllByType(RankType.LOWEST)
        val totalPrice: Long = categoryProducts.sumOf { it.product.price }
        return CategoryRankResponse(
            totalPrice = totalPrice,
            products =
                categoryProducts.sortedBy { it.category.ordinal }.map {
                    ProductResponse(
                        category = it.product.category.value,
                        brand = it.product.brand,
                        price = it.product.price,
                    )
                },
        )
    }
}
