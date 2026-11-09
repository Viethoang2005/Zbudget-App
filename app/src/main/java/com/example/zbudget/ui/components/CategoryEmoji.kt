
package com.example.zbudget.ui.components

import com.example.zbudget.data.model.TransactionCategories
import com.example.zbudget.data.model.TransactionType

fun CategoryEmoji(
    category: String,
    type: TransactionType? = null
): String {
    val categories = if (type != null) {
        TransactionCategories.getCategories(type)
    } else {
        TransactionCategories.expense + TransactionCategories.income
    }

    return categories
        .firstOrNull { it.name == category }
        ?.icon ?: "📦"
}
