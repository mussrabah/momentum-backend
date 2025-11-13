package com.muss_coding.momentum_backend.analytics

import com.muss_coding.momentum_backend.analytics.dto.AnalyticsResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/analytics")
class AnalyticsController(
    private val analyticsService: AnalyticsService
) {

    @GetMapping("/summary")
    fun getAnalyticsSummary(
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<AnalyticsResponse> {
        val summary = analyticsService.getAnalyticsSummary(userDetails)
        return ResponseEntity.ok(summary)
    }
}