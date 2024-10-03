package com.mss.mssassignment.application.service.rank

import com.mss.mssassignment.domain.rank.BrandRankRepository
import com.mss.mssassignment.infrastructure.exception.MssException
import com.mss.mssassignment.infrastructure.exception.MssExceptionType
import org.springframework.stereotype.Service

@Service
class BrandRankService(
    private val brandRankRepository: BrandRankRepository,
) {
    fun getLowestBrand(): BrandLowestRankResponse {
        val brandRank = brandRankRepository.findTopByOrderByTotalPrice()
        return brandRank?.let {
            BrandLowestRankResponse(
                totalPrice = it.totalPrice,
                brand = it.brand,
                products =
                    it.products.sortedBy { product -> product.category.ordinal }.map { product ->
                        BrandLowestRankResponse.BrandLowestRankProduct(
                            category = product.category,
                            categoryName = product.category.value,
                            price = product.price,
                        )
                    },
            )
        } ?: throw MssException(MssExceptionType.BRAND_LOWEST_NOT_FOUND)
    }
}
