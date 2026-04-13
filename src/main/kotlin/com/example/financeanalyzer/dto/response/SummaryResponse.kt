package  com.example.financeanalyzer.dto.response

import java.math.BigDecimal

data class SummaryResponse(
    val totalIncome: BigDecimal,
    val totalExpense: BigDecimal,
    val balance: BigDecimal,
    val transactionCount: Int,
    val averageExpense: BigDecimal,
    val biggestExpense: BigDecimal?

)

