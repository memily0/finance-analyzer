package com.example.financeanalyzer.dto.response

import java.math.BigDecimal

data class MonthlyAnalyticsResponse(
    val month: String,
    val income: BigDecimal,
    val expense: BigDecimal
)

