package com.example.financeanalyzer.dto.response

import com.example.financeanalyzer.model.Category
import com.example.financeanalyzer.model.TransactionType
import java.math.BigDecimal
import java.time.LocalDateTime

data class TransactionResponse(
    val id: Long,
    val userId: Long,
    val amount: BigDecimal,
    val type: TransactionType,
    val category: Category,
    val description: String,
    val timestamp: LocalDateTime
)
