package com.example.financeanalyzer.controller

import com.example.financeanalyzer.dto.response.CategoryAnalyticsResponse
import com.example.financeanalyzer.dto.response.InsightResponse
import com.example.financeanalyzer.dto.response.MonthlyAnalyticsResponse
import com.example.financeanalyzer.dto.response.SubscriptionResponse
import com.example.financeanalyzer.dto.response.SummaryResponse
import com.example.financeanalyzer.service.AnalyticsService
import com.example.financeanalyzer.service.InsightService
import com.example.financeanalyzer.service.SubscriptionDetectionService
import jakarta.validation.constraints.Positive
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@Validated
@RequestMapping("/api/analytics")
class AnalyticsController(
    private val analyticsService: AnalyticsService,
    private val insightService: InsightService,
    private val subscriptionDetectionService: SubscriptionDetectionService
) {

    @GetMapping("/summary")
    fun getSummary(
        @RequestParam @Positive userId: Long
    ): SummaryResponse {
        return analyticsService.getSummary(userId)
    }

    @GetMapping("/categories")
    fun getCategoryAnalytics(
        @RequestParam @Positive userId: Long
    ): List<CategoryAnalyticsResponse> {
        return analyticsService.getCategoryAnalytics(userId)
    }

    @GetMapping("/monthly")
    fun getMonthlyAnalytics(
        @RequestParam @Positive userId: Long
    ): List<MonthlyAnalyticsResponse> {
        return analyticsService.getMonthlyAnalytics(userId)
    }

    @GetMapping("/insights")
    fun getInsights(
        @RequestParam @Positive userId: Long
    ): List<InsightResponse> {
        return insightService.getInsights(userId)
    }

    @GetMapping("/subscriptions")
    fun getSubscriptions(
        @RequestParam @Positive userId: Long
    ): List<SubscriptionResponse> {
        return subscriptionDetectionService.findSubscriptions(userId)
    }
}
