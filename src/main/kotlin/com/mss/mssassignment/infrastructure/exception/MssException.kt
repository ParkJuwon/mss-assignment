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

    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, -20000, "Category not found"),
    CATEGORY_NAME_REQUIRED(HttpStatus.BAD_REQUEST, -20001, "Category name required"),
    CATEGORY_LOWEST_PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, -20002, "Category lowest product not found"),
    CATEGORY_HIGHEST_PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, -20003, "Category highest product not found"),

    BRAND_LOWEST_NOT_FOUND(HttpStatus.NOT_FOUND, -30000, "Brand lowest not found"),
}
