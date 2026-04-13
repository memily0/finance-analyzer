package com.example.financeanalyzer.dto.request

import com.example.financeanalyzer.model.Category
import com.example.financeanalyzer.model.TransactionType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.LocalDateTime

data class CreateTransactionRequest(
    @field:NotNull
    val userId: Long,

    @field:NotNull
    @field:Positive
    val amount: BigDecimal,

    @field:NotNull
    val type: TransactionType,

    val category: Category? = null,

    @field:NotBlank
    val description: String,

    @field:NotNull
    val timestamp: LocalDateTime
)
