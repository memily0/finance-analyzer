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
class TransactionFlowIntegrationTest(
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
    fun `creates transactions and returns list`() {
        val user = userRepository.save(User(email = "integration-user@example.com"))

        mockMvc.perform(
            post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        mapOf(
                            "userId" to user.id,
                            "amount" to "50.00",
                            "type" to "EXPENSE",
                            "description" to "Uber ride",
                            "timestamp" to "2026-04-10T10:15:00"
                        )
                    )
                )
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.category").value("TRANSPORT"))

        mockMvc.perform(
            post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        mapOf(
                            "userId" to user.id,
                            "amount" to "1500.00",
                            "type" to "INCOME",
                            "description" to "Payroll April",
                            "timestamp" to "2026-04-01T09:00:00"
                        )
                    )
                )
        )
            .andExpect(status().isCreated)

        mockMvc.perform(get("/api/transactions").param("userId", user.id.toString()))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].description").value("Uber ride"))
    }
}
