package com.mss.mssassignment.application.service.brand

import com.mss.mssassignment.application.service.rank.BrandLowestRankResponse
import com.mss.mssassignment.domain.brand.BrandRepository
import com.mss.mssassignment.infrastructure.exception.MssException
import com.mss.mssassignment.infrastructure.exception.MssExceptionType
import org.springframework.stereotype.Service

@Service
class BrandService(
    private val brandRepository: BrandRepository,
) {
    fun getLowestBrand(): BrandLowestRankResponse {
        val brandRank = brandRepository.findTopByOrderByTotalPrice()
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
