package com.example.financeanalyzer.repository

import com.example.financeanalyzer.entity.Transaction
import com.example.financeanalyzer.entity.User
import com.example.financeanalyzer.model.Category
import com.example.financeanalyzer.model.TransactionType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDateTime

@ActiveProfiles("test")
@DataJpaTest
class TransactionRepositoryTest(
    @Autowired private val transactionRepository: TransactionRepository,
    @Autowired private val userRepository: UserRepository
) {

    @BeforeEach
    fun setUp() {
        transactionRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    fun `finds all transactions by user ordered by timestamp desc`() {
        val user = userRepository.save(User(email = "user@example.com"))
        transactionRepository.save(
            Transaction(
                user = user,
                amount = BigDecimal("10.00"),
                type = TransactionType.EXPENSE,
                category = Category.FOOD,
                description = "Breakfast",
                timestamp = LocalDateTime.of(2026, 4, 1, 8, 0)
            )
        )
        transactionRepository.save(
            Transaction(
                user = user,
                amount = BigDecimal("20.00"),
                type = TransactionType.EXPENSE,
                category = Category.TRANSPORT,
                description = "Taxi",
                timestamp = LocalDateTime.of(2026, 4, 2, 8, 0)
            )
        )

        val result = transactionRepository.findAllByUser_IdOrderByTimestampDesc(user.id!!)

        assertEquals(2, result.size)
        assertEquals("Taxi", result[0].description)
        assertEquals("Breakfast", result[1].description)
    }
}
