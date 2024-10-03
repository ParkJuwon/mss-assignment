package com.mss.mssassignment.application.listener.rank

import com.mss.mssassignment.domain.product.event.ProductCreatedEvent
import com.mss.mssassignment.domain.product.event.ProductDeletedEvent
import org.slf4j.LoggerFactory
import org.springframework.integration.annotation.ServiceActivator
import org.springframework.stereotype.Component

@Component
class RankEventListener {
    private val logger = LoggerFactory.getLogger(javaClass)

    @ServiceActivator(inputChannel = "ProductCreatedEvent")
    fun handle(event: ProductCreatedEvent) {
        // 상품이 생성/변경 될 때마다 랭킹을 업데이트한다.
        logger.info("[RankEventListener] ProductCreatedEvent: $event")
    }

    @ServiceActivator(inputChannel = "ProductDeletedEvent")
    fun handle(event: ProductDeletedEvent) {
        // 상품이 삭제 될 때마다 랭킹을 업데이트 한다.
        logger.info("[RankEventListener] ProductDeletedEvent: $event")
    }
}
