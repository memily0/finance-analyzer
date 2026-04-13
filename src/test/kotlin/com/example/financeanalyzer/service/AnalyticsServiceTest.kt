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
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime

class AnalyticsServiceTest {

    private val transactionRepository = mockk<TransactionRepository>()
    private val userRepository = mockk<UserRepository>()
    private val service = AnalyticsService(transactionRepository, userRepository)

    @Test
    fun `calculates summary correctly`() {
        every { userRepository.existsById(1L) } returns true
        every { transactionRepository.findAllByUser_Id(1L) } returns sampleTransactions()

        val summary = service.getSummary(1L)

        assertEquals(BigDecimal("3000.00"), summary.totalIncome)
        assertEquals(BigDecimal("800.00"), summary.totalExpense)
        assertEquals(BigDecimal("2200.00"), summary.balance)
        assertEquals(4, summary.transactionCount)
        assertEquals(BigDecimal("400.00"), summary.averageExpense)
        assertEquals(BigDecimal("650.00"), summary.biggestExpense)
    }

    @Test
    fun `calculates category analytics correctly`() {
        every { userRepository.existsById(1L) } returns true
        every { transactionRepository.findAllByUser_Id(1L) } returns sampleTransactions()

        val analytics = service.getCategoryAnalytics(1L)

        assertEquals(2, analytics.size)
        assertEquals(Category.SHOPPING, analytics[0].category)
        assertEquals(BigDecimal("650.00"), analytics[0].amount)
        assertEquals(BigDecimal("81.25"), analytics[0].percentage)
        assertEquals(Category.FOOD, analytics[1].category)
        assertEquals(BigDecimal("150.00"), analytics[1].amount)
    }

    @Test
    fun `calculates monthly analytics correctly`() {
        every { userRepository.existsById(1L) } returns true
        every { transactionRepository.findAllByUser_Id(1L) } returns sampleTransactions()

        val monthly = service.getMonthlyAnalytics(1L)

        assertEquals(2, monthly.size)
        assertEquals("2026-03", monthly[0].month)
        assertEquals(BigDecimal("2000.00"), monthly[0].income)
        assertEquals(BigDecimal("150.00"), monthly[0].expense)
        assertEquals("2026-04", monthly[1].month)
        assertEquals(BigDecimal("1000.00"), monthly[1].income)
        assertEquals(BigDecimal("650.00"), monthly[1].expense)
    }

    private fun sampleTransactions(): List<Transaction> {
        val user = User(id = 1L, email = "user@example.com")
        return listOf(
            Transaction(
                id = 1L,
                user = user,
                amount = BigDecimal("2000.00"),
                type = TransactionType.INCOME,
                category = Category.SALARY,
                description = "Salary March",
                timestamp = LocalDateTime.of(2026, 3, 1, 9, 0)
            ),
            Transaction(
                id = 2L,
                user = user,
                amount = BigDecimal("150.00"),
                type = TransactionType.EXPENSE,
                category = Category.FOOD,
                description = "Groceries",
                timestamp = LocalDateTime.of(2026, 3, 15, 18, 0)
            ),
            Transaction(
                id = 3L,
                user = user,
                amount = BigDecimal("1000.00"),
                type = TransactionType.INCOME,
                category = Category.SALARY,
                description = "Salary April",
                timestamp = LocalDateTime.of(2026, 4, 1, 9, 0)
            ),
            Transaction(
                id = 4L,
                user = user,
                amount = BigDecimal("650.00"),
                type = TransactionType.EXPENSE,
                category = Category.SHOPPING,
                description = "Laptop accessories",
                timestamp = LocalDateTime.of(2026, 4, 10, 18, 0)
            )
        )
    }
}
