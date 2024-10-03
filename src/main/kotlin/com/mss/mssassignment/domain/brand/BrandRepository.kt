package com.mss.mssassignment.domain.brand

import org.springframework.data.jpa.repository.JpaRepository

interface BrandRepository : JpaRepository<Brand, String> {
    fun removeByBrand(brand: String)

    fun findByBrand(brand: String): Brand?

    fun findTopByOrderByTotalPrice(): Brand?
}
