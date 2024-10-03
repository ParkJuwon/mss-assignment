package com.mss.mssassignment.infrastructure.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.support.GenericMessage
import org.springframework.stereotype.Component

@Component
class MessagePublisherImpl(
    private val context: ApplicationContext,
    private val objectMapper: ObjectMapper,
) : MessagePublisher {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun publish(
        channel: String,
        payload: Message,
    ) {
        val c = context.getBean(channel) as MessageChannel

        logger.debug("message send: $channel : ${objectMapper.writeValueAsString(payload)}")
        c.send(GenericMessage<Any>(payload))
    }
}
