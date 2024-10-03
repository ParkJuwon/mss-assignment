package com.mss.mssassignment.domain.rank

import com.mss.mssassignment.domain.Category
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CategoryRankRepository : JpaRepository<CategoryRank, String> {
    fun findAllByCategory(category: Category): List<CategoryRank>

    @Query("SELECT r FROM CategoryRank r join fetch r.product WHERE r.type = :type")
    fun findAllByType(type: RankType): List<CategoryRank>
}
