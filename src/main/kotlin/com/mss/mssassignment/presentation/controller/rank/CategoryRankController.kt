package com.mss.mssassignment.presentation.controller.rank

import com.mss.mssassignment.application.service.rank.CategoryLowestRankResponse
import com.mss.mssassignment.application.service.rank.CategoryNameRankResponse
import com.mss.mssassignment.application.service.rank.CategoryRankService
import com.mss.mssassignment.infrastructure.exception.MssException
import com.mss.mssassignment.infrastructure.exception.MssExceptionType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/category")
class CategoryRankController(
    private val categoryRankService: CategoryRankService,
) {
    @GetMapping("/lowest")
    fun getCategoryLowestRank(): CategoryLowestRankResponse = categoryRankService.getLowestRank()

    @GetMapping("/name")
    fun getCategoryRankByName(
        @RequestParam categoryName: String?,
    ): CategoryNameRankResponse =
        categoryName?.let { categoryRankService.getCategoryRankByName(it) } ?: throw MssException(MssExceptionType.CATEGORY_NAME_REQUIRED)
}
