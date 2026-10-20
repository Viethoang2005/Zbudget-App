package com.example.zbudget.data.mock

import com.example.zbudget.data.model.Transaction
import com.example.zbudget.data.model.TransactionType

object FakeTransactions {
    val transactions = listOf(
        Transaction(
            id = "1",
            title = "Ăn trưa",
            amount = 45000L,
            type = TransactionType.EXPENSE,
            category = "Ăn uống",
            dateLabel = "Hôm nay"
        ),
        Transaction(
            id = "2",
            title = "Lương làm thêm",
            amount = 2500000L,
            type = TransactionType.INCOME,
            category = "Thu nhập",
            dateLabel = "Hôm nay"
        ),
        Transaction(
            id = "3",
            title = "Gửi xe",
            amount = 5000L,
            type = TransactionType.EXPENSE,
            category = "Di chuyển",
            dateLabel = "Hôm nay"
        ),
        Transaction(
            id = "4",
            title = "Mua sách lập trình",
            amount = 120000L,
            type = TransactionType.EXPENSE,
            category = "Học tập",
            dateLabel = "Hôm qua"
        ),
        Transaction(
            id = "5",
            title = "Tiền gia đình gửi",
            amount = 1500000L,
            type = TransactionType.INCOME,
            category = "Gia đình",
            dateLabel = "18/10/2026"
        ),
        Transaction(
            id = "6",
            title = "Cà phê",
            amount = 35000L,
            type = TransactionType.EXPENSE,
            category = "Ăn uống",
            dateLabel = "17/10/2026"
        )
    )
}