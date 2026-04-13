package com.example.financeanalyzer.service

import com.example.financeanalyzer.dto.response.SubscriptionResponse
import com.example.financeanalyzer.exception.NotFoundException
import com.example.financeanalyzer.model.TransactionType
import com.example.financeanalyzer.repository.TransactionRepository
import com.example.financeanalyzer.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.temporal.ChronoUnit
import kotlin.math.roundToLong

@Service
class SubscriptionDetectionService(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository
) {

    @Transactional(readOnly = true)
    fun findSubscriptions(userId: Long): List<SubscriptionResponse> {
        ensureUserExists(userId)
        val expenses = transactionRepository.findAllByUser_IdOrderByTimestampAsc(userId)
            .filter { it.type == TransactionType.EXPENSE }

        return expenses
            .groupBy { normalizeDescription(it.description) }
            .mapNotNull { (_, transactions) ->
                if (transactions.size < 2) {
                    return@mapNotNull null
                }

                val sortedTransactions = transactions.sortedBy { it.timestamp }
                val amounts = sortedTransactions.map { it.amount }
                val minAmount = amounts.minOrNull() ?: BigDecimal.ZERO
                val maxAmount = amounts.maxOrNull() ?: BigDecimal.ZERO
                val averageAmount = amounts.fold(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal(amounts.size), 2, RoundingMode.HALF_UP)

                val intervals = sortedTransactions.zipWithNext { first, second ->
                    ChronoUnit.DAYS.between(first.timestamp.toLocalDate(), second.timestamp.toLocalDate())
                }

                val isMonthly = intervals.isNotEmpty() && intervals.all { it in 25..35 }
                val hasStableAmounts = averageAmount > BigDecimal.ZERO &&
                    maxAmount.subtract(minAmount).abs() <= averageAmount.multiply(BigDecimal("0.15"))

                if (isMonthly && hasStableAmounts) {
                    SubscriptionResponse(
                        description = sortedTransactions.last().description,
                        averageAmount = averageAmount,
                        occurrences = sortedTransactions.size,
                        lastChargedAt = sortedTransactions.last().timestamp,
                        estimatedIntervalDays = intervals.average().roundToLong()
                    )
                } else {
                    null
                }
            }
            .sortedByDescending { it.occurrences }
    }

    private fun normalizeDescription(description: String): String {
        return description.lowercase()
            .replace(Regex("\\d+"), "")
            .replace(Regex("[^a-z ]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun ensureUserExists(userId: Long) {
        if (!userRepository.existsById(userId)) {
            throw NotFoundException("User with id=$userId not found")
        }
    }
}
