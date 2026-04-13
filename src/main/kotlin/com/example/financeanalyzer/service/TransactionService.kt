package com.example.financeanalyzer.service

import com.example.financeanalyzer.dto.request.BulkCreateTransactionsRequest
import com.example.financeanalyzer.dto.request.CreateTransactionRequest
import com.example.financeanalyzer.dto.response.TransactionResponse
import com.example.financeanalyzer.exception.NotFoundException
import com.example.financeanalyzer.mapper.TransactionMapper
import com.example.financeanalyzer.repository.TransactionRepository
import com.example.financeanalyzer.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    private val transactionMapper: TransactionMapper,
    private val categoryDetectionService: CategoryDetectionService
) {

    @Transactional
    fun createTransaction(request: CreateTransactionRequest): TransactionResponse {
        val user = userRepository.findById(request.userId)
            .orElseThrow { NotFoundException("User with id=${request.userId} not found") }
        val category = request.category ?: categoryDetectionService.detectCategory(request.description)
        val transaction = transactionMapper.toEntity(request, user, category)
        return transactionMapper.toResponse(transactionRepository.save(transaction))
    }

    @Transactional
    fun createTransactions(request: BulkCreateTransactionsRequest): List<TransactionResponse> {
        if (request.transactions.isEmpty()) {
            return emptyList()
        }

        val userIds = request.transactions.map { it.userId }.distinct()
        val users = userRepository.findAllById(userIds).associateBy { requireNotNull(it.id) }
        val missingUserId = userIds.firstOrNull { it !in users.keys }
        if (missingUserId != null) {
            throw NotFoundException("User with id=$missingUserId not found")
        }

        val transactions = request.transactions.map { transactionRequest ->
            val user = requireNotNull(users[transactionRequest.userId])
            val category = transactionRequest.category
                ?: categoryDetectionService.detectCategory(transactionRequest.description)
            transactionMapper.toEntity(transactionRequest, user, category)
        }

        return transactionRepository.saveAll(transactions)
            .map(transactionMapper::toResponse)
    }

    @Transactional(readOnly = true)
    fun getTransactionsByUserId(userId: Long): List<TransactionResponse> {
        ensureUserExists(userId)
        return transactionRepository.findAllByUser_IdOrderByTimestampDesc(userId)
            .map(transactionMapper::toResponse)
    }

    @Transactional(readOnly = true)
    fun getTransactionById(id: Long): TransactionResponse {
        val transaction = transactionRepository.findById(id)
            .orElseThrow { NotFoundException("Transaction with id=$id not found") }
        return transactionMapper.toResponse(transaction)
    }

    private fun ensureUserExists(userId: Long) {
        if (!userRepository.existsById(userId)) {
            throw NotFoundException("User with id=$userId not found")
        }
    }
}
