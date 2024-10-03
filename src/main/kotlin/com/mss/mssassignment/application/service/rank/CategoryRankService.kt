package com.mss.mssassignment.application.service.rank

import com.mss.mssassignment.domain.Category
import com.mss.mssassignment.domain.rank.CategoryRankRepository
import com.mss.mssassignment.domain.rank.RankType
import com.mss.mssassignment.infrastructure.exception.MssException
import com.mss.mssassignment.infrastructure.exception.MssExceptionType
import org.springframework.stereotype.Service

@Service
class CategoryRankService(
    private val categoryRankRepository: CategoryRankRepository,
) {
    fun getLowestRank(): CategoryLowestRankResponse {
        val categoryProducts = categoryRankRepository.findAllByType(RankType.LOWEST)
        val totalPrice: Long = categoryProducts.sumOf { it.product.price }
        return CategoryLowestRankResponse(
            totalPrice = totalPrice,
            products =
                categoryProducts.sortedBy { it.category.ordinal }.map {
                    CategoryLowestRankResponse.CategoryLowestRankProduct(
                        category = it.product.category,
                        categoryName = it.product.category.value,
                        brand = it.product.brand,
                        price = it.product.price,
                    )
                },
        )
    }

    fun getCategoryRankByName(categoryName: String): CategoryNameRankResponse {
        val category = Category.findCategory(categoryName)
        val categoryRanks = categoryRankRepository.findAllByCategory(category)

        val lowestRank =
            categoryRanks.find { rank -> rank.type == RankType.LOWEST }
                ?: throw MssException(MssExceptionType.CATEGORY_LOWEST_PRODUCT_NOT_FOUND)
        val highestRank =
            categoryRanks.find { rank -> rank.type == RankType.HIGHEST }
                ?: throw MssException(MssExceptionType.CATEGORY_HIGHEST_PRODUCT_NOT_FOUND)

        return CategoryNameRankResponse(
            categoryName = categoryName,
            lowest =
                CategoryNameRankResponse.CategoryNameRankProduct(
                    brand = lowestRank.product.brand,
                    price = lowestRank.product.price,
                ),
            highest =
                CategoryNameRankResponse.CategoryNameRankProduct(
                    brand = highestRank.product.brand,
                    price = highestRank.product.price,
                ),
        )
    }
}
