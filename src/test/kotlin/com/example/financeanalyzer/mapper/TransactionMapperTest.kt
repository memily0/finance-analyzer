package com.example.financeanalyzer.mapper

import com.example.financeanalyzer.entity.Transaction
import com.example.financeanalyzer.entity.User
import com.example.financeanalyzer.model.Category
import com.example.financeanalyzer.model.TransactionType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime

class TransactionMapperTest {

    private val mapper = TransactionMapper()

    @Test
    fun `maps entity to response`() {
        val user = User(id = 7L, email = "user@example.com")
        val timestamp = LocalDateTime.of(2026, 4, 10, 12, 30)
        val transaction = Transaction(
            id = 15L,
            user = user,
            amount = BigDecimal("499.99"),
            type = TransactionType.EXPENSE,
            category = Category.SHOPPING,
            description = "Headphones",
            timestamp = timestamp
        )

        val response = mapper.toResponse(transaction)

        assertEquals(15L, response.id)
        assertEquals(7L, response.userId)
        assertEquals(BigDecimal("499.99"), response.amount)
        assertEquals(TransactionType.EXPENSE, response.type)
        assertEquals(Category.SHOPPING, response.category)
        assertEquals("Headphones", response.description)
        assertEquals(timestamp, response.timestamp)
    }
}
