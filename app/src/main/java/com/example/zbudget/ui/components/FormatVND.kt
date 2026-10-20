package com.example.zbudget.ui.components

import java.text.NumberFormat
import java.util.Locale

fun formatVnd(amount: Long): String {
    val locale = Locale.Builder()
        .setLanguage("vi")
        .setRegion("VN")
        .build()

    val formatter = NumberFormat.getInstance(locale)
    return "${formatter.format(amount)} đ"
}