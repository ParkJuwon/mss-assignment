package com.mss.mssassignment.domain.product.event

import com.mss.mssassignment.infrastructure.messaging.Message

data class ProductCreatedEvent(
    val id: Long,
) : Message
