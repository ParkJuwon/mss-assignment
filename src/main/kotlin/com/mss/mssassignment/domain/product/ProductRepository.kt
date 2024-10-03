package com.mss.mssassignment.domain.product

import com.mss.mssassignment.domain.Category
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, Long> {
    fun findByBrandAndCategory(
        brand: String,
        category: Category,
    ): Product?
}
