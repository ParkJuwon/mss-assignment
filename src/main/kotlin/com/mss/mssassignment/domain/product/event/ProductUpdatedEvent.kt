package com.mss.mssassignment.domain.product.event

import com.mss.mssassignment.domain.product.Product
import com.mss.mssassignment.infrastructure.messaging.Message

data class ProductUpdatedEvent(
    val product: Product,
) : Message
