package com.example.financeanalyzer.dto.response

import com.example.financeanalyzer.model.Category
import java.math.BigDecimal

data class CategoryAnalyticsResponse(
    val category: Category,
    val amount: BigDecimal,
    val percentage: BigDecimal

)