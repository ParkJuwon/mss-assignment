package com.mss.mssassignment.presentation.controller.rank

import com.mss.mssassignment.application.service.rank.CategoryRankResponse
import com.mss.mssassignment.application.service.rank.CategoryRankService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/category")
class CategoryRankController(
    private val categoryRankService: CategoryRankService,
) {
    @GetMapping("rank")
    fun getCategoryLowestRank(): CategoryRankResponse = categoryRankService.getLowestRank()
}
