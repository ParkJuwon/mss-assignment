package com.mss.mssassignment.domain

import com.mss.mssassignment.infrastructure.exception.MssException
import com.mss.mssassignment.infrastructure.exception.MssExceptionType

enum class Category(
    val value: String,
) {
    TOP("상의"),
    OUTER("아우터"),
    PANTS("바지"),
    SNEAKERS("스니커즈"),
    BAG("가방"),
    CAP("모자"),
    SOCKS("양말"),
    ACCESSORY("악세사리"),
    ;

    companion object {
        fun findCategory(value: String): Category =
            entries.find { it.value == value } ?: throw MssException(MssExceptionType.CATEGORY_NOT_FOUND)
    }
}
