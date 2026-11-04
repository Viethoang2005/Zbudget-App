package com.example.zbudget.data.mock

import com.example.zbudget.data.model.Budget

object FakeBudget {
    val budgets: List<Budget> = listOf(
        Budget(
            id = "1",
            category = "Ăn uống",
            limitAmount = 2000000L,
            month = "2026-11"
        ),
        Budget(
            id = "2",
            category = "Di chuyển",
            limitAmount = 500000L,
            month = "2026-11"
        ),
        Budget(
            id = "3",
            category = "Học tập",
            limitAmount = 1000000L,
            month = "2026-11"
        ),
        Budget(
            id = "4",
            category = "Giải trí",
            limitAmount = 500000L,
            month = "2026-11"
        ),
        Budget(
            id = "5",
            category = "Gia đình",
            limitAmount = 1500000L,
            month = "2026-11"
        )
    )
}