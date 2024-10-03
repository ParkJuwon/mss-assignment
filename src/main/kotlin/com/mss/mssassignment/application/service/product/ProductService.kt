package com.mss.mssassignment.application.service.product

import com.fasterxml.jackson.databind.ObjectMapper
import com.mss.mssassignment.domain.product.Product
import com.mss.mssassignment.domain.product.ProductRepository
import com.mss.mssassignment.domain.product.event.ProductCreatedEvent
import com.mss.mssassignment.infrastructure.exception.MssException
import com.mss.mssassignment.infrastructure.exception.MssExceptionType
import com.mss.mssassignment.infrastructure.messaging.MessagePublisher
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val repository: ProductRepository,
    private val objectMapper: ObjectMapper,
    private val messagePublisher: MessagePublisher,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun saveProduct(product: Product): Product {
        val findProduct = repository.findByBrandAndCategory(product.brand, product.category)
        val savedProduct =
            runCatching { findProduct?.let { findProduct.setProduct(product) } ?: repository.save(product) }
                .onSuccess {
                    logger.info("product save success. product: ${objectMapper.writeValueAsString(it)}")
                    messagePublisher.publish("ProductCreatedEvent", ProductCreatedEvent(it.id))
                }

        return savedProduct.getOrElse { throw MssException(MssExceptionType.PRODUCT_SAVE_FAILED) }
    }
}
