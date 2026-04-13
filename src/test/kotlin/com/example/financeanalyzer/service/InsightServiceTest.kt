package com.example.financeanalyzer.service

import com.example.financeanalyzer.entity.Transaction
import com.example.financeanalyzer.entity.User
import com.example.financeanalyzer.model.Category
import com.example.financeanalyzer.model.TransactionType
import com.example.financeanalyzer.repository.TransactionRepository
import com.example.financeanalyzer.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime

class InsightServiceTest {

    private val transactionRepository = mockk<TransactionRepository>()
    private val userRepository = mockk<UserRepository>()
    private val service = InsightService(transactionRepository, userRepository)

    @Test
    fun `detects biggest expense category`() {
        every { userRepository.existsById(1L) } returns true
        every { transactionRepository.findAllByUser_IdOrderByTimestampAsc(1L) } returns transactionsForInsights()

        val insights = service.getInsights(1L)

        assertTrue(insights.any { it.type == "TOP_CATEGORY" && it.message.contains("SHOPPING") })
    }

    @Test
    fun `detects unusually large transaction`() {
        every { userRepository.existsById(1L) } returns true
        every { transactionRepository.findAllByUser_IdOrderByTimestampAsc(1L) } returns transactionsForInsights()

        val insights = service.getInsights(1L)

        assertTrue(insights.any { it.type == "UNUSUAL_EXPENSE" && it.message.contains("Laptop") })
    }

    @Test
    fun `compares monthly spending`() {
        every { userRepository.existsById(1L) } returns true
        every { transactionRepository.findAllByUser_IdOrderByTimestampAsc(1L) } returns transactionsForInsights()

        val insights = service.getInsights(1L)

        assertTrue(insights.any { it.type == "MONTHLY_SPENDING_GROWTH" && it.message.contains("2026-04") })
    }

    @Test
    fun `returns empty list when there is no data`() {
        every { userRepository.existsById(1L) } returns true
        every { transactionRepository.findAllByUser_IdOrderByTimestampAsc(1L) } returns emptyList()

        val insights = service.getInsights(1L)

        assertEquals(emptyList<Any>(), insights)
    }

    private fun transactionsForInsights(): List<Transaction> {
        val user = User(id = 1L, email = "user@example.com")
        return listOf(
            Transaction(
                id = 1L,
                user = user,
                amount = BigDecimal("120.00"),
                type = TransactionType.EXPENSE,
                category = Category.FOOD,
                description = "Groceries",
                timestamp = LocalDateTime.of(2026, 3, 2, 10, 0)
            ),
            Transaction(
                id = 2L,
                user = user,
                amount = BigDecimal("180.00"),
                type = TransactionType.EXPENSE,
                category = Category.SHOPPING,
                description = "Shoes",
                timestamp = LocalDateTime.of(2026, 4, 2, 10, 0)
            ),
            Transaction(
                id = 3L,
                user = user,
                amount = BigDecimal("900.00"),
                type = TransactionType.EXPENSE,
                category = Category.SHOPPING,
                description = "Laptop",
                timestamp = LocalDateTime.of(2026, 4, 8, 10, 0)
            )
        )
    }
}
