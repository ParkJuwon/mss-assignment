package com.mss.mssassignment.presentation.controller.admin

import com.mss.mssassignment.application.service.product.ProductService
import com.mss.mssassignment.domain.Category
import com.mss.mssassignment.domain.product.Product
import com.mss.mssassignment.domain.product.event.ProductCreatedEvent
import com.mss.mssassignment.domain.product.event.ProductDeletedEvent
import com.mss.mssassignment.domain.product.event.ProductUpdatedEvent
import com.mss.mssassignment.infrastructure.messaging.MessagePublisher
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/product")
class AdminProductController(
    private val productService: ProductService,
    private val messagePublisher: MessagePublisher,
) {
    @GetMapping("/{id}")
    fun getProduct(
        @PathVariable id: Long,
    ): Product = productService.get(id)

    @GetMapping("/brand/{brand}")
    fun getProductByBrand(
        @PathVariable brand: String,
    ): List<Product> = productService.getByBrand(brand)

    @GetMapping("/category/{category}")
    fun getProductByCategory(
        @PathVariable category: String,
    ): List<Product> = productService.getByCategory(Category.findCategory(category))

    @PostMapping
    fun createProduct(
        @RequestBody product: Product,
    ): Product =
        runCatching { productService.create(product) }
            .onSuccess { createdProduct -> messagePublisher.publish("ProductCreatedEvent", ProductCreatedEvent(createdProduct)) }
            .getOrElse { e -> throw e }

    @PutMapping
    fun updateProduct(
        @RequestBody product: Product,
    ): Product =
        runCatching { productService.update(product) }
            .onSuccess { updatedProduct -> messagePublisher.publish("ProductUpdatedEvent", ProductUpdatedEvent(updatedProduct)) }
            .getOrElse { e -> throw e }

    @DeleteMapping("/{id}")
    fun deleteProduct(
        @PathVariable id: Long,
    ): Product =
        runCatching { productService.delete(id) }
            .onSuccess { deletedProduct -> messagePublisher.publish("ProductDeletedEvent", ProductDeletedEvent(deletedProduct)) }
            .getOrElse { e -> throw e }
}
