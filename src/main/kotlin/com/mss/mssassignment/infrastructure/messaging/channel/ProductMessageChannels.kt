package com.mss.mssassignment.infrastructure.messaging.channel

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.integration.channel.PublishSubscribeChannel
import org.springframework.messaging.MessageChannel
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
class ProductMessageChannels {
    @Bean("ProductCreatedEvent")
    fun productCreatedEventChannel(messageTaskExecutor: ThreadPoolTaskExecutor): MessageChannel =
        PublishSubscribeChannel(messageTaskExecutor)

    @Bean("ProductDeletedEvent")
    fun productDeletedEventChannel(messageTaskExecutor: ThreadPoolTaskExecutor): MessageChannel =
        PublishSubscribeChannel(messageTaskExecutor)
}
