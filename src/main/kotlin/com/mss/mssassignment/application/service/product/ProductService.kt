package com.mss.mssassignment.application.service.product

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
    private val repository: ProductRepository,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun create(product: Product): Product {
        val findProduct = repository.findByBrandAndCategory(product.brand, product.category)
        return findProduct?.let { throw MssException(MssExceptionType.PRODUCT_ALREADY_EXISTS) } ?: repository.save(product)
    }

    @Transactional
    fun update(product: Product): Product {
        val findProduct = repository.findByIdOrNull(product.id)
        return findProduct?.setProduct(product) ?: throw MssException(MssExceptionType.PRODUCT_NOT_FOUND)
    }

    @Transactional
    fun delete(id: Long): Product {
        val findProduct = repository.findByIdOrNull(id)
        return findProduct?.let {
            repository.delete(it)
            it
        } ?: throw MssException(MssExceptionType.PRODUCT_NOT_FOUND)
    }
}
