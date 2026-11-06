package com.example.zbudget.data.model

data class Transaction(
    val id: String = "",
    val title: String = "",
    val amount: Long = 0L,
    val type: TransactionType = TransactionType.EXPENSE,
    val category: String = "",
    val dateLabel: String = "",
    val timeLabel: String = "",
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

enum class TransactionType {
    INCOME,
    EXPENSE
}