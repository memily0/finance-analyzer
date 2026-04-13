package com.example.financeanalyzer.controller

import com.example.financeanalyzer.dto.request.BulkCreateTransactionsRequest
import com.example.financeanalyzer.dto.request.CreateTransactionRequest
import com.example.financeanalyzer.dto.response.TransactionResponse
import com.example.financeanalyzer.service.TransactionService
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@Validated
@RequestMapping("/api/transactions")
class TransactionController(
    private val transactionService: TransactionService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createTransaction(
        @Valid @RequestBody request: CreateTransactionRequest
    ): TransactionResponse {
        return transactionService.createTransaction(request)
    }

    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    fun createTransactions(
        @Valid @RequestBody request: BulkCreateTransactionsRequest
    ): List<TransactionResponse> {
        return transactionService.createTransactions(request)
    }

    @GetMapping
    fun getTransactionsByUserId(
        @RequestParam @Positive userId: Long
    ): List<TransactionResponse> {
        return transactionService.getTransactionsByUserId(userId)
    }

    @GetMapping("/{id}")
    fun getTransactionById(
        @PathVariable @Positive id: Long
    ): TransactionResponse {
        return transactionService.getTransactionById(id)
    }
}
