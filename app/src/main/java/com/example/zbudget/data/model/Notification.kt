
package com.example.zbudget.data.model

data class Notification(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = NotificationType.TRANSACTION.name,
    val createdAt: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val relatedId: String = ""
)

enum class NotificationType {
    TRANSACTION,
    BUDGET_WARNING,
    REMINDER
}