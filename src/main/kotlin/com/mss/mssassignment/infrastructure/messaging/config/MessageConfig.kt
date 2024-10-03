package com.mss.mssassignment.infrastructure.messaging.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
class MessageConfig {
    @Bean
    fun messageTaskExecutor(): ThreadPoolTaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 200
        executor.queueCapacity = 1000
        executor.setAllowCoreThreadTimeOut(true)
        executor.setThreadNamePrefix("integration-")
        executor.setWaitForTasksToCompleteOnShutdown(true)
        executor.setAwaitTerminationSeconds(30)
        executor.initialize()
        return executor
    }
}
