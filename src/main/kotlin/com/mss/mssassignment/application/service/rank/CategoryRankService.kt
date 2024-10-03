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
        val totalPrice: Long = categoryProducts.sumOf { it.product?.price ?: 0 }
        return CategoryLowestRankResponse(
            totalPrice = totalPrice,
            products =
                categoryProducts.sortedBy { it.category.ordinal }.mapNotNull {
                    it.product?.let { product ->
                        CategoryLowestRankResponse.CategoryLowestRankProduct(
                            category = product.category,
                            categoryName = product.category.value,
                            brand = product.brand,
                            price = product.price,
                        )
                    }
                },
        )
    }

    fun getCategoryRankByName(categoryName: String): CategoryNameRankResponse {
        val category = Category.findCategory(categoryName)
        val categoryRanks = categoryRankRepository.findAllByCategory(category)

        val lowestRankProduct =
            categoryRanks.find { rank -> rank.type == RankType.LOWEST }?.product
                ?: throw MssException(MssExceptionType.CATEGORY_LOWEST_PRODUCT_NOT_FOUND)
        val highestRankProduct =
            categoryRanks.find { rank -> rank.type == RankType.HIGHEST }?.product
                ?: throw MssException(MssExceptionType.CATEGORY_HIGHEST_PRODUCT_NOT_FOUND)

        return CategoryNameRankResponse(
            categoryName = categoryName,
            lowest =
                CategoryNameRankResponse.CategoryNameRankProduct(
                    brand = lowestRankProduct.brand,
                    price = lowestRankProduct.price,
                ),
            highest =
                CategoryNameRankResponse.CategoryNameRankProduct(
                    brand = highestRankProduct.brand,
                    price = highestRankProduct.price,
                ),
        )
    }
}
