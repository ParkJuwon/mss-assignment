package com.mss.mssassignment.domain.rank

import com.mss.mssassignment.domain.Category
import com.mss.mssassignment.domain.product.Product
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.Comment

@Entity
@Table(
    name = "category_ranks",
    indexes = [
        Index(name = "idx_category_type", columnList = "category, type", unique = true),
    ],
)
class CategoryRank(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    @Comment("카테고리")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var category: Category,
    @Comment("랭크 타입(최저가, 최고가)")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var type: RankType,
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    var product: Product,
)
