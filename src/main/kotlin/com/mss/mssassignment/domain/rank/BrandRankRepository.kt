package com.mss.mssassignment.domain.rank

import org.springframework.data.jpa.repository.JpaRepository

interface BrandRankRepository : JpaRepository<BrandRank, String> {
    fun removeByBrand(brand: String)

    fun findByBrand(brand: String): BrandRank?

    fun findTopByOrderByTotalPrice(): BrandRank?
}
