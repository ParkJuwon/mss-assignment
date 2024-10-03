package com.mss.mssassignment

import com.mss.mssassignment.application.service.InitialService
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Component

@SpringBootApplication
class MssAssignmentApplication

fun main(args: Array<String>) {
    runApplication<MssAssignmentApplication>(*args)
}

@Component
class ApplicationStartup(
    private val initialService: InitialService,
) : ApplicationListener<ApplicationReadyEvent?> {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        // 미리 준비된 상품을 초기화 합니다.
        initialService.initialProduct()
    }
}
