package com.example.financeanalyzer.repository

import com.example.financeanalyzer.entity.Transaction
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface TransactionRepository : JpaRepository<Transaction, Long> {
    fun findAllByUser_IdOrderByTimestampDesc(userId: Long): List<Transaction>
    fun findAllByUser_IdOrderByTimestampAsc(userId: Long): List<Transaction>
    fun findAllByUser_Id(userId: Long): List<Transaction>
    fun findAllByUser_IdAndTimestampBetweenOrderByTimestampAsc(
        userId: Long,
        from: LocalDateTime,
        to: LocalDateTime
    ): List<Transaction>
}
