package com.mss.mssassignment.application.service.product

import com.mss.mssassignment.domain.Category
import com.mss.mssassignment.domain.product.Product
import com.mss.mssassignment.domain.product.ProductRepository
import com.mss.mssassignment.infrastructure.exception.MssException
import com.mss.mssassignment.infrastructure.exception.MssExceptionType
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val productRepository: ProductRepository,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional(readOnly = true) // read db 분리는 되어있지 않지만 명시적 표기
    fun get(id: Long): Product = productRepository.findByIdOrNull(id) ?: throw MssException(MssExceptionType.PRODUCT_NOT_FOUND)

    @Transactional(readOnly = true)
    fun getByBrand(brand: String): List<Product> = productRepository.findAllByBrand(brand).sortedBy { it.category.ordinal }

    @Transactional(readOnly = true)
    fun getByCategory(category: Category): List<Product> = productRepository.findAllByCategory(category).sortedBy { it.category.ordinal }

    @Transactional
    fun create(product: Product): Product {
        val findProduct = productRepository.findByBrandAndCategory(product.brand, product.category)
        return findProduct?.let { throw MssException(MssExceptionType.PRODUCT_ALREADY_EXISTS) } ?: productRepository.save(product)
    }

    @Transactional
    fun update(product: Product): Product {
        val findProductById = productRepository.findByIdOrNull(product.id)
        val findProductByBrandAndCategory = productRepository.findByBrandAndCategory(product.brand, product.category)

        return findProductById?.let {
            if (findProductByBrandAndCategory != null && it.id != findProductByBrandAndCategory.id) {
                // 이미 존재하는 브랜드와 카테고리의 상품이 존재할 경우
                throw MssException(MssExceptionType.PRODUCT_ALREADY_EXISTS)
            }
            it.setProduct(product)
        } ?: throw MssException(MssExceptionType.PRODUCT_NOT_FOUND)
    }

    @Transactional
    fun delete(id: Long): Product {
        val findProduct = productRepository.findByIdOrNull(id)
        return findProduct?.let {
            productRepository.delete(it)
            it
        } ?: throw MssException(MssExceptionType.PRODUCT_NOT_FOUND)
    }
}
