package com.mss.mssassignment.application.service

import com.mss.mssassignment.application.service.product.ProductService
import com.mss.mssassignment.domain.Category
import com.mss.mssassignment.domain.product.Product
import com.mss.mssassignment.domain.product.event.ProductCreatedEvent
import com.mss.mssassignment.infrastructure.messaging.MessagePublisher
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class InitialService(
    private val productService: ProductService,
    private val messagePublisher: MessagePublisher,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun initialProduct() {
        val initProducts =
            listOf(
                Product(brand = "A", category = Category.TOP, price = 11200L),
                Product(brand = "A", category = Category.OUTER, price = 5500L),
                Product(brand = "A", category = Category.PANTS, price = 4200L),
                Product(brand = "A", category = Category.SNEAKERS, price = 9000L),
                Product(brand = "A", category = Category.BAG, price = 2000L),
                Product(brand = "A", category = Category.CAP, price = 1700L),
                Product(brand = "A", category = Category.SOCKS, price = 1800L),
                Product(brand = "A", category = Category.ACCESSORY, price = 2300L),
                Product(brand = "B", category = Category.TOP, price = 10500L),
                Product(brand = "B", category = Category.OUTER, price = 5900L),
                Product(brand = "B", category = Category.PANTS, price = 3800L),
                Product(brand = "B", category = Category.SNEAKERS, price = 9100L),
                Product(brand = "B", category = Category.BAG, price = 2100L),
                Product(brand = "B", category = Category.CAP, price = 2000L),
                Product(brand = "B", category = Category.SOCKS, price = 2000L),
                Product(brand = "B", category = Category.ACCESSORY, price = 2200L),
                Product(brand = "C", category = Category.TOP, price = 10000L),
                Product(brand = "C", category = Category.OUTER, price = 6200L),
                Product(brand = "C", category = Category.PANTS, price = 3300L),
                Product(brand = "C", category = Category.SNEAKERS, price = 9200L),
                Product(brand = "C", category = Category.BAG, price = 2200L),
                Product(brand = "C", category = Category.CAP, price = 1900L),
                Product(brand = "C", category = Category.SOCKS, price = 2200L),
                Product(brand = "C", category = Category.ACCESSORY, price = 2100L),
                Product(brand = "D", category = Category.TOP, price = 10100L),
                Product(brand = "D", category = Category.OUTER, price = 5100L),
                Product(brand = "D", category = Category.PANTS, price = 3000L),
                Product(brand = "D", category = Category.SNEAKERS, price = 9500L),
                Product(brand = "D", category = Category.BAG, price = 2500L),
                Product(brand = "D", category = Category.CAP, price = 1500L),
                Product(brand = "D", category = Category.SOCKS, price = 2400L),
                Product(brand = "D", category = Category.ACCESSORY, price = 2000L),
                Product(brand = "E", category = Category.TOP, price = 10700L),
                Product(brand = "E", category = Category.OUTER, price = 5000L),
                Product(brand = "E", category = Category.PANTS, price = 3800L),
                Product(brand = "E", category = Category.SNEAKERS, price = 9900L),
                Product(brand = "E", category = Category.BAG, price = 2300L),
                Product(brand = "E", category = Category.CAP, price = 1800L),
                Product(brand = "E", category = Category.SOCKS, price = 2100L),
                Product(brand = "E", category = Category.ACCESSORY, price = 2100L),
                Product(brand = "F", category = Category.TOP, price = 11200L),
                Product(brand = "F", category = Category.OUTER, price = 7200L),
                Product(brand = "F", category = Category.PANTS, price = 4000L),
                Product(brand = "F", category = Category.SNEAKERS, price = 9300L),
                Product(brand = "F", category = Category.BAG, price = 2100L),
                Product(brand = "F", category = Category.CAP, price = 1600L),
                Product(brand = "F", category = Category.SOCKS, price = 2300L),
                Product(brand = "F", category = Category.ACCESSORY, price = 1900L),
                Product(brand = "G", category = Category.TOP, price = 10500L),
                Product(brand = "G", category = Category.OUTER, price = 5800L),
                Product(brand = "G", category = Category.PANTS, price = 3900L),
                Product(brand = "G", category = Category.SNEAKERS, price = 9000L),
                Product(brand = "G", category = Category.BAG, price = 2200L),
                Product(brand = "G", category = Category.CAP, price = 1700L),
                Product(brand = "G", category = Category.SOCKS, price = 2100L),
                Product(brand = "G", category = Category.ACCESSORY, price = 2000L),
                Product(brand = "H", category = Category.TOP, price = 10800L),
                Product(brand = "H", category = Category.OUTER, price = 6300L),
                Product(brand = "H", category = Category.PANTS, price = 3100L),
                Product(brand = "H", category = Category.SNEAKERS, price = 9700L),
                Product(brand = "H", category = Category.BAG, price = 2100L),
                Product(brand = "H", category = Category.CAP, price = 1600L),
                Product(brand = "H", category = Category.SOCKS, price = 2000L),
                Product(brand = "H", category = Category.ACCESSORY, price = 2000L),
                Product(brand = "I", category = Category.TOP, price = 11400L),
                Product(brand = "I", category = Category.OUTER, price = 6700L),
                Product(brand = "I", category = Category.PANTS, price = 3200L),
                Product(brand = "I", category = Category.SNEAKERS, price = 9500L),
                Product(brand = "I", category = Category.BAG, price = 2400L),
                Product(brand = "I", category = Category.CAP, price = 1700L),
                Product(brand = "I", category = Category.SOCKS, price = 1700L),
                Product(brand = "I", category = Category.ACCESSORY, price = 2400L),
            )

        initProducts.forEach {
            runCatching { productService.create(it) }
                .onSuccess { savedProduct -> messagePublisher.publish("ProductCreatedEvent", ProductCreatedEvent(savedProduct.id)) }
                .getOrElse { e -> throw e }
        }
    }
}
