package com.example.financeanalyzer.dto.request

import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty

data class BulkCreateTransactionsRequest(
    @field:NotEmpty
    val transactions: List<@Valid CreateTransactionRequest>
)
