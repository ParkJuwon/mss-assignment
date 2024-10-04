package com.mss.mssassignment.domain.product

import com.mss.mssassignment.domain.Category
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import org.hibernate.annotations.Comment

@Entity
@Table(
    name = "products",
    indexes = [
        Index(name = "idx_brand_category", columnList = "brand, category", unique = true),
    ],
)
class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Comment("브랜드 명")
    @Column(nullable = false)
    var brand: String,
    @Comment("카테고리")
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var category: Category,
    @Comment("가격")
    @Column(nullable = false)
    var price: Long = 0L,
) {
    fun setProduct(product: Product): Product {
        this.brand = product.brand
        this.category = product.category
        this.price = product.price

        return this
    }
}
