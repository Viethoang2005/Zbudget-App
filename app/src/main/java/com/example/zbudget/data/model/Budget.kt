package com.example.zbudget.data.model

data class Budget(
    val id: String,
    val category: String,
    val limitAmount: Long,
    val month: String
)
