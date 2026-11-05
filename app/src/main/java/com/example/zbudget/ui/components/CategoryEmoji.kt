package com.example.zbudget.ui.components

fun CategoryEmoji(category: String): String {
    return when (category) {
        "Ăn uống" -> "🍜"
        "Di chuyển" -> "🚗"
        "Mua sắm" -> "🛍️"
        "Học tập" -> "📚"
        "Giải trí" -> "🎮"
        "Gia đình" -> "🏠"
        "Sức khỏe" -> "💊"
        "Làm đẹp" -> "💄"
        "Du lịch" -> "✈️"
        "Hóa đơn" -> "🧾"
        else -> "📦"
    }
}
