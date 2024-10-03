package com.mss.mssassignment.infrastructure.messaging

interface MessagePublisher {
    fun publish(
        channel: String,
        payload: Message,
    )
}
