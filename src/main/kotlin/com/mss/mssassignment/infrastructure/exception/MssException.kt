package com.mss.mssassignment.infrastructure.exception

import org.springframework.http.HttpStatus

class MssException(
    val type: MssExceptionType,
) : RuntimeException("${type.name}, ${type.code}, ${type.message}")

enum class MssExceptionType(
    val status: HttpStatus,
    val code: Int,
    val message: String,
) {
    PRODUCT_ALREADY_EXISTS(HttpStatus.CONFLICT, -10000, "Product already exists"),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, -10001, "Product not found"),
}
