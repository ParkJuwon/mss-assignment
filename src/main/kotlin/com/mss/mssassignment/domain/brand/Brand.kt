package com.mss.mssassignment.domain.brand

import com.mss.mssassignment.domain.product.Product
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.Comment

@Entity
@Table(
    name = "brands",
    indexes = [
        Index(name = "idx_brand", columnList = "brand", unique = true),
        Index(name = "idx_total_price", columnList = "totalPrice"),
    ],
)
class Brand(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,
    @Comment("브랜드 명")
    @Column(nullable = false)
    var brand: String,
    @Comment("브랜드 상품 총 가격")
    @Column(nullable = false)
    var totalPrice: Long = 0L,
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_rank_id")
    val products: List<Product> = mutableListOf(),
)
