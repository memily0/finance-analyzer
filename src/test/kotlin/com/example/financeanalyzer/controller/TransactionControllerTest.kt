package com.example.financeanalyzer.controller

import com.example.financeanalyzer.dto.request.BulkCreateTransactionsRequest
import com.example.financeanalyzer.dto.request.CreateTransactionRequest
import com.example.financeanalyzer.dto.response.TransactionResponse
import com.example.financeanalyzer.exception.GlobalExceptionHandler
import com.example.financeanalyzer.model.Category
import com.example.financeanalyzer.model.TransactionType
import com.example.financeanalyzer.service.TransactionService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.time.LocalDateTime

@ActiveProfiles("test")
@WebMvcTest(TransactionController::class)
@Import(GlobalExceptionHandler::class)
class TransactionControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper
) {

    @MockkBean
    private lateinit var transactionService: TransactionService

    @Test
    fun `creates transaction`() {
        val request = CreateTransactionRequest(
            userId = 1L,
            amount = BigDecimal("120.50"),
            type = TransactionType.EXPENSE,
            category = Category.TRANSPORT,
            description = "Uber trip",
            timestamp = LocalDateTime.of(2026, 4, 10, 19, 30)
        )
        every { transactionService.createTransaction(any()) } returns transactionResponse()

        mockMvc.perform(
            post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.category").value("TRANSPORT"))
    }

    @Test
    fun `gets all transactions by user id`() {
        every { transactionService.getTransactionsByUserId(1L) } returns listOf(transactionResponse())

        mockMvc.perform(get("/api/transactions").param("userId", "1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].description").value("Uber trip"))
    }

    @Test
    fun `gets transaction by id`() {
        every { transactionService.getTransactionById(1L) } returns transactionResponse()

        mockMvc.perform(get("/api/transactions/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
    }

    @Test
    fun `returns 400 for invalid body`() {
        val invalidRequest = mapOf(
            "userId" to 1,
            "amount" to -10,
            "type" to "EXPENSE",
            "description" to "",
            "timestamp" to "2026-04-10T19:30:00"
        )

        mockMvc.perform(
            post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("Validation failed"))
    }

    @Test
    fun `creates transactions in bulk`() {
        val request = BulkCreateTransactionsRequest(listOf(
            CreateTransactionRequest(
                userId = 1L,
                amount = BigDecimal("10.00"),
                type = TransactionType.EXPENSE,
                category = null,
                description = "Netflix",
                timestamp = LocalDateTime.of(2026, 4, 1, 10, 0)
            )
        ))
        every { transactionService.createTransactions(any()) } returns listOf(transactionResponse())

        mockMvc.perform(
            post("/api/transactions/bulk")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$[0].id").value(1))
    }

    private fun transactionResponse() = TransactionResponse(
        id = 1L,
        userId = 1L,
        amount = BigDecimal("120.50"),
        type = TransactionType.EXPENSE,
        category = Category.TRANSPORT,
        description = "Uber trip",
        timestamp = LocalDateTime.of(2026, 4, 10, 19, 30)
    )
}
