package com.mss.mssassignment.presentation.controller.rank

import com.mss.mssassignment.application.service.rank.BrandLowestRankResponse
import com.mss.mssassignment.application.service.rank.BrandRankService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/brand")
class BrandRankController(
    private val brandRankService: BrandRankService,
) {
    @GetMapping("/lowest")
    fun getLowestBrand(): BrandLowestRankResponse = brandRankService.getLowestBrand()
}
