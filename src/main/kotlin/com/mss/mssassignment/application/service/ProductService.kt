package com.mss.mssassignment.application.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.mss.mssassignment.domain.product.Product
import com.mss.mssassignment.domain.product.ProductRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val repository: ProductRepository,
    private val objectMapper: ObjectMapper,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun saveProduct(product: Product): Product {
        val findProduct = repository.findByBrandAndCategory(product.brand, product.category)
        val savedProduct =
            runCatching { findProduct?.let { findProduct.setProduct(product) } ?: repository.save(product) }
                .onSuccess {
                    logger.info("product save success. product: ${objectMapper.writeValueAsString(it)}")
                    // TODO: 랭킹 및 최저가 변경
                }

        return savedProduct.getOrElse { throw RuntimeException("상품 저장에 실패했습니다.") }
    }
}
