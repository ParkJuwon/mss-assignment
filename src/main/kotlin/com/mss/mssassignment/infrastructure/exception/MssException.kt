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
    PRODUCT_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, -10000, "Product save failed"),
}
