package com.example.zbudget.ui.transactions

enum class TransactionTab(val label: String) {
    ALL("Tất cả"),
    INCOME("Thu"),
    EXPENSE("Chi")
}

enum class TransactionSort(val label: String) {
    NEWEST("Mới nhất"),
    OLDEST("Cũ nhất")
}

enum class TransactionDateFilter(val label: String) {
    ALL("Tất cả thời gian"),
    LAST_7_DAYS("7 ngày qua"),
    THIS_MONTH("Tháng này"),
    LAST_MONTH("Tháng trước")
}

data class TransactionFilter(
    val dateFilter: TransactionDateFilter =
        TransactionDateFilter.ALL,
    val category: String = "Tất cả danh mục",
    val minAmount: String = "",
    val maxAmount: String = ""
)
