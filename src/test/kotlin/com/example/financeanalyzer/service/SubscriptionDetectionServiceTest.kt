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

class SubscriptionDetectionServiceTest {

    private val transactionRepository = mockk<TransactionRepository>()
    private val userRepository = mockk<UserRepository>()
    private val service = SubscriptionDetectionService(transactionRepository, userRepository)

    @Test
    fun `finds recurring subscriptions`() {
        every { userRepository.existsById(1L) } returns true
        every { transactionRepository.findAllByUser_IdOrderByTimestampAsc(1L) } returns recurringTransactions()

        val subscriptions = service.findSubscriptions(1L)

        assertEquals(1, subscriptions.size)
        assertEquals("Netflix Premium", subscriptions.first().description)
        assertEquals(3, subscriptions.first().occurrences)
    }

    @Test
    fun `does not classify random payments as subscriptions`() {
        every { userRepository.existsById(1L) } returns true
        every { transactionRepository.findAllByUser_IdOrderByTimestampAsc(1L) } returns randomTransactions()

        val subscriptions = service.findSubscriptions(1L)

        assertTrue(subscriptions.isEmpty())
    }

    private fun recurringTransactions(): List<Transaction> {
        val user = User(id = 1L, email = "user@example.com")
        return listOf(
            Transaction(1L, user, BigDecimal("15.99"), TransactionType.EXPENSE, Category.ENTERTAINMENT, "Netflix Premium", LocalDateTime.of(2026, 1, 5, 9, 0)),
            Transaction(2L, user, BigDecimal("15.99"), TransactionType.EXPENSE, Category.ENTERTAINMENT, "Netflix Premium", LocalDateTime.of(2026, 2, 5, 9, 0)),
            Transaction(3L, user, BigDecimal("16.10"), TransactionType.EXPENSE, Category.ENTERTAINMENT, "Netflix Premium", LocalDateTime.of(2026, 3, 5, 9, 0)),
            Transaction(4L, user, BigDecimal("42.00"), TransactionType.EXPENSE, Category.FOOD, "Restaurant", LocalDateTime.of(2026, 3, 12, 19, 0))
        )
    }

    private fun randomTransactions(): List<Transaction> {
        val user = User(id = 1L, email = "user@example.com")
        return listOf(
            Transaction(1L, user, BigDecimal("10.00"), TransactionType.EXPENSE, Category.FOOD, "Coffee", LocalDateTime.of(2026, 1, 5, 9, 0)),
            Transaction(2L, user, BigDecimal("35.00"), TransactionType.EXPENSE, Category.FOOD, "Lunch", LocalDateTime.of(2026, 1, 17, 13, 0)),
            Transaction(3L, user, BigDecimal("80.00"), TransactionType.EXPENSE, Category.TRANSPORT, "Taxi", LocalDateTime.of(2026, 2, 2, 9, 0))
        )
    }
}
