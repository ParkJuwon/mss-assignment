package com.mss.mssassignment.application.service.rank

import com.mss.mssassignment.domain.Category

data class CategoryLowestRankResponse(
    val totalPrice: Long,
    val products: List<CategoryLowestRankProduct>,
) {
    data class CategoryLowestRankProduct(
        val category: Category,
        val categoryName: String,
        val brand: String,
        val price: Long,
    )
}

data class CategoryNameRankResponse(
    val categoryName: String,
    val lowest: CategoryNameRankProduct,
    val highest: CategoryNameRankProduct,
) {
    data class CategoryNameRankProduct(
        val brand: String,
        val price: Long,
    )
}
