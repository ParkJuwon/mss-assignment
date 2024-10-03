package com.mss.mssassignment.presentation.controller.rank

import com.mss.mssassignment.application.service.brand.BrandService
import com.mss.mssassignment.application.service.rank.BrandLowestRankResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/brand")
class BrandController(
    private val brandService: BrandService,
) {
    @GetMapping("/lowest")
    fun getLowestBrand(): BrandLowestRankResponse = brandService.getLowestBrand()
}
