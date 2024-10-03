package com.mss.mssassignment.domain.product.event

import com.mss.mssassignment.infrastructure.messaging.Message

data class ProductDeletedEvent(
    val id: Long,
) : Message
