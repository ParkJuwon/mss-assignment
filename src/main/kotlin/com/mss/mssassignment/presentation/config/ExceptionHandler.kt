package com.mss.mssassignment.presentation.config

import com.mss.mssassignment.infrastructure.exception.MssException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler {
    @ExceptionHandler(value = [MssException::class])
    fun handleException(e: MssException): ResponseEntity<ExceptionBody> =
        ResponseEntity.status(e.type.status).body(ExceptionBody(code = e.type.code, message = e.type.message))
}
