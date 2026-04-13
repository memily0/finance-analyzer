package com.example.financeanalyzer.service

import com.example.financeanalyzer.dto.response.InsightResponse
import com.example.financeanalyzer.exception.NotFoundException
import com.example.financeanalyzer.model.TransactionType
import com.example.financeanalyzer.repository.TransactionRepository
import com.example.financeanalyzer.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.YearMonth

@Service
class InsightService(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository
) {

    @Transactional(readOnly = true)
    fun getInsights(userId: Long): List<InsightResponse> {
        ensureUserExists(userId)
        val transactions = transactionRepository.findAllByUser_IdOrderByTimestampAsc(userId)
        if (transactions.isEmpty()) {
            return emptyList()
        }

        val insights = mutableListOf<InsightResponse>()
        val expenses = transactions.filter { it.type == TransactionType.EXPENSE }

        if (expenses.isNotEmpty()) {
            val topCategory = expenses
                .groupBy { it.category }
                .maxByOrNull { (_, categoryTransactions) -> categoryTransactions.sumOf { it.amount } }

            if (topCategory != null) {
                insights += InsightResponse(
                    type = "TOP_CATEGORY",
                    message = "${topCategory.key} is your biggest expense category with total ${topCategory.value.sumOf { it.amount }}"
                )
            }

            val averageExpense = expenses.sumOf { it.amount }
                .divide(BigDecimal(expenses.size), 2, RoundingMode.HALF_UP)
            val unusualTransaction = expenses
                .filter { it.amount > averageExpense.multiply(BigDecimal("1.8")) }
                .maxByOrNull { it.amount }

            if (unusualTransaction != null) {
                insights += InsightResponse(
                    type = "UNUSUAL_EXPENSE",
                    message = "Large expense detected: ${unusualTransaction.description} for ${unusualTransaction.amount}"
                )
            }
        }

        val monthlyExpenses = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { YearMonth.from(it.timestamp) }
            .mapValues { (_, monthTransactions) -> monthTransactions.sumOf { it.amount } }
            .toSortedMap()

        if (monthlyExpenses.size >= 2) {
            val months = monthlyExpenses.keys.toList()
            val currentMonth = months.last()
            val previousMonth = months[months.lastIndex - 1]
            val currentExpense = monthlyExpenses.getValue(currentMonth)
            val previousExpense = monthlyExpenses.getValue(previousMonth)

            if (previousExpense > BigDecimal.ZERO && currentExpense > previousExpense) {
                val growthPercent = currentExpense.subtract(previousExpense)
                    .multiply(BigDecimal(100))
                    .divide(previousExpense, 2, RoundingMode.HALF_UP)

                insights += InsightResponse(
                    type = "MONTHLY_SPENDING_GROWTH",
                    message = "Expenses increased by $growthPercent% in $currentMonth compared to $previousMonth"
                )
            }
        }

        return insights
    }

    private fun ensureUserExists(userId: Long) {
        if (!userRepository.existsById(userId)) {
            throw NotFoundException("User with id=$userId not found")
        }
    }
}
