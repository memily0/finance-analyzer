package com.example.financeanalyzer.dto.response

import java.math.BigDecimal
import java.time.LocalDateTime

data class SubscriptionResponse(
    val description: String,
    val averageAmount: BigDecimal,
    val occurrences: Int,
    val lastChargedAt: LocalDateTime,
    val estimatedIntervalDays: Long
)
