package com.mss.mssassignment.application.service.rank

import com.mss.mssassignment.application.service.product.ProductResponse

data class CategoryRankResponse(
    val totalPrice: Long,
    val products: List<ProductResponse>,
)
