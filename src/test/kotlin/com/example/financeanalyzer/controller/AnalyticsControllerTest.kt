package com.example.financeanalyzer.controller

import com.example.financeanalyzer.dto.response.CategoryAnalyticsResponse
import com.example.financeanalyzer.dto.response.InsightResponse
import com.example.financeanalyzer.dto.response.MonthlyAnalyticsResponse
import com.example.financeanalyzer.dto.response.SubscriptionResponse
import com.example.financeanalyzer.dto.response.SummaryResponse
import com.example.financeanalyzer.exception.GlobalExceptionHandler
import com.example.financeanalyzer.model.Category
import com.example.financeanalyzer.service.AnalyticsService
import com.example.financeanalyzer.service.InsightService
import com.example.financeanalyzer.service.SubscriptionDetectionService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.time.LocalDateTime

@ActiveProfiles("test")
@WebMvcTest(AnalyticsController::class)
@Import(GlobalExceptionHandler::class)
class AnalyticsControllerTest(
    @Autowired private val mockMvc: MockMvc
) {

    @MockkBean
    private lateinit var analyticsService: AnalyticsService

    @MockkBean
    private lateinit var insightService: InsightService

    @MockkBean
    private lateinit var subscriptionDetectionService: SubscriptionDetectionService

    @Test
    fun `returns summary`() {
        every { analyticsService.getSummary(1L) } returns SummaryResponse(
            totalIncome = BigDecimal("5000.00"),
            totalExpense = BigDecimal("1500.00"),
            balance = BigDecimal("3500.00"),
            transactionCount = 5,
            averageExpense = BigDecimal("300.00"),
            biggestExpense = BigDecimal("700.00")
        )

        mockMvc.perform(get("/api/analytics/summary").param("userId", "1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.balance").value(3500.00))
    }

    @Test
    fun `returns category analytics`() {
        every { analyticsService.getCategoryAnalytics(1L) } returns listOf(
            CategoryAnalyticsResponse(Category.FOOD, BigDecimal("200.00"), BigDecimal("50.00"))
        )

        mockMvc.perform(get("/api/analytics/categories").param("userId", "1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].category").value("FOOD"))
    }

    @Test
    fun `returns monthly analytics`() {
        every { analyticsService.getMonthlyAnalytics(1L) } returns listOf(
            MonthlyAnalyticsResponse("2026-04", BigDecimal("1000.00"), BigDecimal("200.00"))
        )

        mockMvc.perform(get("/api/analytics/monthly").param("userId", "1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].month").value("2026-04"))
    }

    @Test
    fun `returns insights`() {
        every { insightService.getInsights(1L) } returns listOf(
            InsightResponse(type = "TOP_CATEGORY", message = "SHOPPING is your biggest category")
        )

        mockMvc.perform(get("/api/analytics/insights").param("userId", "1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].type").value("TOP_CATEGORY"))
    }

    @Test
    fun `returns subscriptions`() {
        every { subscriptionDetectionService.findSubscriptions(1L) } returns listOf(
            SubscriptionResponse(
                description = "Netflix",
                averageAmount = BigDecimal("15.99"),
                occurrences = 3,
                lastChargedAt = LocalDateTime.of(2026, 3, 5, 9, 0),
                estimatedIntervalDays = 29L
            )
        )

        mockMvc.perform(get("/api/analytics/subscriptions").param("userId", "1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].description").value("Netflix"))
    }
}
