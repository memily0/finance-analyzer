package com.example.financeanalyzer.service

import com.example.financeanalyzer.dto.response.CategoryAnalyticsResponse
import com.example.financeanalyzer.dto.response.MonthlyAnalyticsResponse
import com.example.financeanalyzer.dto.response.SummaryResponse
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
class AnalyticsService(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository
) {

    @Transactional(readOnly = true)
    fun getSummary(userId: Long): SummaryResponse {
        ensureUserExists(userId)
        val transactions = transactionRepository.findAllByUser_Id(userId)

        val incomes = transactions.filter { it.type == TransactionType.INCOME }
        val expenses = transactions.filter { it.type == TransactionType.EXPENSE }

        val totalIncome = incomes.sumOf { it.amount }
        val totalExpense = expenses.sumOf { it.amount }
        val balance = totalIncome.subtract(totalExpense)

        val averageExpense = if (expenses.isNotEmpty()) {
            totalExpense.divide(
                BigDecimal(expenses.size),
                2,
                RoundingMode.HALF_UP
            )
        } else {
            BigDecimal.ZERO
        }

        val biggestExpense = expenses.maxByOrNull { it.amount }?.amount

        return SummaryResponse(
            totalIncome = totalIncome,
            totalExpense = totalExpense,
            balance = balance,
            transactionCount = transactions.size,
            averageExpense = averageExpense,
            biggestExpense = biggestExpense
        )
    }

    @Transactional(readOnly = true)
    fun getCategoryAnalytics(userId: Long): List<CategoryAnalyticsResponse> {
        ensureUserExists(userId)
        val expenses = transactionRepository.findAllByUser_Id(userId)
            .filter { it.type == TransactionType.EXPENSE }

        if (expenses.isEmpty()) return emptyList()

        val totalExpense = expenses.sumOf { it.amount }

        return expenses
            .groupBy { it.category }
            .map { (category, categoryTransactions) ->
                val amount = categoryTransactions.sumOf { it.amount }
                val percentage = if (totalExpense > BigDecimal.ZERO) {
                    amount.multiply(BigDecimal(100))
                        .divide(totalExpense, 2, RoundingMode.HALF_UP)
                } else {
                    BigDecimal.ZERO
                }

                CategoryAnalyticsResponse(
                    category = category,
                    amount = amount,
                    percentage = percentage
                )
            }
            .sortedByDescending { it.amount }
    }

    @Transactional(readOnly = true)
    fun getMonthlyAnalytics(userId: Long): List<MonthlyAnalyticsResponse> {
        ensureUserExists(userId)
        val transactions = transactionRepository.findAllByUser_Id(userId)

        return transactions
            .groupBy { YearMonth.from(it.timestamp) }
            .map { (yearMonth, monthTransactions) ->
                val income = monthTransactions
                    .filter { it.type == TransactionType.INCOME }
                    .sumOf { it.amount }

                val expense = monthTransactions
                    .filter { it.type == TransactionType.EXPENSE }
                    .sumOf { it.amount }

                MonthlyAnalyticsResponse(
                    month = yearMonth.toString(),
                    income = income,
                    expense = expense
                )
            }
            .sortedBy { it.month }
    }

    private fun ensureUserExists(userId: Long) {
        if (!userRepository.existsById(userId)) {
            throw NotFoundException("User with id=$userId not found")
        }
    }
}
