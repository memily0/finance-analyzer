package com.example.financeanalyzer.integration

import com.example.financeanalyzer.entity.User
import com.example.financeanalyzer.repository.TransactionRepository
import com.example.financeanalyzer.repository.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class AnalyticsFlowIntegrationTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val transactionRepository: TransactionRepository
) {

    @BeforeEach
    fun setUp() {
        transactionRepository.deleteAll()
        userRepository.deleteAll()
    }

    @Test
    fun `returns summary categories and monthly analytics`() {
        val user = userRepository.save(User(email = "analytics-user@example.com"))

        createTransaction(user.id!!, "2000.00", "INCOME", "Payroll March", "2026-03-01T09:00:00")
        createTransaction(user.id, "150.00", "EXPENSE", "Perekrestok groceries", "2026-03-10T19:00:00")
        createTransaction(user.id, "2200.00", "INCOME", "Payroll April", "2026-04-01T09:00:00")
        createTransaction(user.id, "16.00", "EXPENSE", "Netflix", "2026-04-05T10:00:00")

        mockMvc.perform(get("/api/analytics/summary").param("userId", user.id.toString()))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.totalIncome").value(4200.00))
            .andExpect(jsonPath("$.totalExpense").value(166.00))

        mockMvc.perform(get("/api/analytics/categories").param("userId", user.id.toString()))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].category").value("FOOD"))

        mockMvc.perform(get("/api/analytics/monthly").param("userId", user.id.toString()))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].month").value("2026-03"))
            .andExpect(jsonPath("$[1].month").value("2026-04"))
    }

    private fun createTransaction(
        userId: Long,
        amount: String,
        type: String,
        description: String,
        timestamp: String
    ) {
        mockMvc.perform(
            post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        mapOf(
                            "userId" to userId,
                            "amount" to amount,
                            "type" to type,
                            "description" to description,
                            "timestamp" to timestamp
                        )
                    )
                )
        )
            .andExpect(status().isCreated)
    }
}
