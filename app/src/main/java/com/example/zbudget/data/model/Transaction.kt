package com.example.zbudget.data.model

data class Transaction(
    val id: String,
    val title: String,
    val amount: Long,
    val type: TransactionType,
    val category: String,
    val dateLabel: String
)

enum class TransactionType {
    INCOME, EXPENSE
}
