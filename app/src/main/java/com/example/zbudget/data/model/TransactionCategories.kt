package com.example.zbudget.data.model

import com.example.zbudget.ui.components.CategoryOption

object TransactionCategories {
    val expense = listOf(
        CategoryOption("Ăn uống", "🍜"),
        CategoryOption("Di chuyển", "🚗"),
        CategoryOption("Mua sắm", "🛍️"),
        CategoryOption("Học tập", "📚"),
        CategoryOption("Giải trí", "🎮"),
        CategoryOption("Gia đình", "🏠"),
        CategoryOption("Sức khỏe", "💊"),
        CategoryOption("Làm đẹp", "💄"),
        CategoryOption("Du lịch", "✈️"),
        CategoryOption("Hóa đơn", "🧾"),
        CategoryOption("Nhà cửa", "🛋️"),
        CategoryOption("Thể thao", "⚽"),
        CategoryOption("Thú cưng", "🐶"),
        CategoryOption("Quà tặng", "🎁"),
        CategoryOption("Khác", "📦")
    )

    val income = listOf(
        CategoryOption("Lương", "💰"),
        CategoryOption("Thưởng", "🎁"),
        CategoryOption("Đầu tư", "📈"),
        CategoryOption("Kinh doanh", "🏪"),
        CategoryOption("Bán hàng", "🛒"),
        CategoryOption("Làm thêm", "💻"),
        CategoryOption("Trợ cấp", "🤝"),
        CategoryOption("Quà tặng", "🎀"),
        CategoryOption("Tiền lãi", "🏦"),
        CategoryOption("Hoàn tiền", "💳"),
        CategoryOption("Khác", "📦")
    )

    fun getCategories(type: TransactionType): List<CategoryOption> {
        return when (type) {
            TransactionType.EXPENSE -> expense
            TransactionType.INCOME -> income
        }
    }
}