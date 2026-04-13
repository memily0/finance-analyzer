package com.example.financeanalyzer.mapper

import com.example.financeanalyzer.dto.request.CreateTransactionRequest
import com.example.financeanalyzer.dto.response.TransactionResponse
import com.example.financeanalyzer.entity.Transaction
import com.example.financeanalyzer.entity.User
import com.example.financeanalyzer.model.Category
import org.springframework.stereotype.Component

@Component
class TransactionMapper {

    fun toEntity(
        request: CreateTransactionRequest,
        user: User,
        category: Category
    ): Transaction {
        return Transaction(
            user = user,
            amount = request.amount,
            type = request.type,
            category = category,
            description = request.description.trim(),
            timestamp = request.timestamp
        )
    }

    fun toResponse(transaction: Transaction): TransactionResponse {
        return TransactionResponse(
            id = requireNotNull(transaction.id) { "Transaction id must be present" },
            userId = requireNotNull(transaction.user.id) { "User id must be present" },
            amount = transaction.amount,
            type = transaction.type,
            category = transaction.category,
            description = transaction.description,
            timestamp = transaction.timestamp
        )
    }
}
