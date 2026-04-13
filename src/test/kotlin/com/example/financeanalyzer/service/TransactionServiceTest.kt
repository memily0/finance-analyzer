package com.example.financeanalyzer.service

import com.example.financeanalyzer.dto.request.BulkCreateTransactionsRequest
import com.example.financeanalyzer.dto.request.CreateTransactionRequest
import com.example.financeanalyzer.entity.Transaction
import com.example.financeanalyzer.entity.User
import com.example.financeanalyzer.exception.NotFoundException
import com.example.financeanalyzer.mapper.TransactionMapper
import com.example.financeanalyzer.model.Category
import com.example.financeanalyzer.model.TransactionType
import com.example.financeanalyzer.repository.TransactionRepository
import com.example.financeanalyzer.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.Optional

class TransactionServiceTest {

    private val transactionRepository = mockk<TransactionRepository>()
    private val userRepository = mockk<UserRepository>()
    private val categoryDetectionService = CategoryDetectionService()
    private val mapper = TransactionMapper()

    private val service = TransactionService(
        transactionRepository = transactionRepository,
        userRepository = userRepository,
        transactionMapper = mapper,
        categoryDetectionService = categoryDetectionService
    )

    @Test
    fun `creates transaction`() {
        val user = User(id = 1L, email = "user@example.com")
        val request = createRequest()

        every { userRepository.findById(1L) } returns Optional.of(user)
        every { transactionRepository.save(any()) } answers {
            firstArg<Transaction>().copy(id = 10L)
        }

        val response = service.createTransaction(request)

        assertEquals(10L, response.id)
        assertEquals(1L, response.userId)
        assertEquals(Category.TRANSPORT, response.category)
        assertEquals(BigDecimal("50.00"), response.amount)
    }

    @Test
    fun `bulk insert creates several transactions`() {
        val user = User(id = 1L, email = "user@example.com")
        val request = BulkCreateTransactionsRequest(
            listOf(
                createRequest(description = "Uber trip"),
                createRequest(description = "Netflix subscription", timestamp = LocalDateTime.of(2026, 4, 2, 10, 0))
            )
        )

        every { userRepository.findAllById(any<Iterable<Long>>()) } returns listOf(user)
        every { transactionRepository.saveAll(any<Iterable<Transaction>>()) } answers {
            firstArg<Iterable<Transaction>>().mapIndexed { index, transaction ->
                transaction.copy(id = index + 1L)
            }
        }

        val responses = service.createTransactions(request)

        assertEquals(2, responses.size)
        assertEquals(Category.TRANSPORT, responses[0].category)
        assertEquals(Category.ENTERTAINMENT, responses[1].category)
    }

    @Test
    fun `throws when user does not exist`() {
        every { userRepository.findById(1L) } returns Optional.empty()

        assertThrows(NotFoundException::class.java) {
            service.createTransaction(createRequest())
        }
    }

    @Test
    fun `gets transactions by user`() {
        val user = User(id = 1L, email = "user@example.com")
        every { userRepository.existsById(1L) } returns true
        every { transactionRepository.findAllByUser_IdOrderByTimestampDesc(1L) } returns listOf(
            Transaction(
                id = 2L,
                user = user,
                amount = BigDecimal("20.00"),
                type = TransactionType.EXPENSE,
                category = Category.FOOD,
                description = "Lunch",
                timestamp = LocalDateTime.of(2026, 4, 5, 13, 0)
            ),
            Transaction(
                id = 1L,
                user = user,
                amount = BigDecimal("1000.00"),
                type = TransactionType.INCOME,
                category = Category.SALARY,
                description = "Payroll",
                timestamp = LocalDateTime.of(2026, 4, 1, 9, 0)
            )
        )

        val responses = service.getTransactionsByUserId(1L)

        assertEquals(2, responses.size)
        assertEquals("Lunch", responses.first().description)
        assertEquals("Payroll", responses.last().description)
    }

    private fun createRequest(
        description: String = "Uber trip",
        timestamp: LocalDateTime = LocalDateTime.of(2026, 4, 1, 10, 0)
    ) = CreateTransactionRequest(
        userId = 1L,
        amount = BigDecimal("50.00"),
        type = TransactionType.EXPENSE,
        category = null,
        description = description,
        timestamp = timestamp
    )
}
