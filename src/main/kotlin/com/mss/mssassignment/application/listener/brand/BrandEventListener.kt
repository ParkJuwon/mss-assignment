package com.mss.mssassignment.application.listener.brand

import com.mss.mssassignment.domain.product.event.ProductCreatedEvent
import com.mss.mssassignment.domain.product.event.ProductDeletedEvent
import org.slf4j.LoggerFactory
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.stereotype.Component

@Component
class BrandEventListener {
    private val logger = LoggerFactory.getLogger(javaClass)

    @ServiceActivator(inputChannel = "ProductCreatedEvent")
    fun handle(event: ProductCreatedEvent) {
        // 상품이 생성/변경 할때마다 브랜드의 정보를 업데이트 한다.
        logger.info("[BrandEventListener] ProductCreatedEvent: $event")
    }

    @ServiceActivator(inputChannel = "ProductDeletedEvent")
    fun handle(event: ProductDeletedEvent) {
        // 상품이 삭제 할때마다 브랜드의 정보를 업데이트 한다.
        logger.info("[RankEventListener] ProductDeletedEvent: $event")
    }
}
