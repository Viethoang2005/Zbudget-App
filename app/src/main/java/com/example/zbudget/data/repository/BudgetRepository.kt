package com.example.zbudget.data.repository

import com.example.zbudget.data.mock.FakeBudget
import com.example.zbudget.data.model.Budget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepository @Inject constructor() {
    private val _budgets = MutableStateFlow(FakeBudget.budgets)
    val budgets: StateFlow<List<Budget>> = _budgets.asStateFlow()

    fun getBudgetsByMonth(month: String) : List<Budget> {
        return _budgets.value.filter { it.month == month }
    }

    fun addBudget(budget: Budget) {
        _budgets.value = listOf(budget) + _budgets.value
    }

    fun updateBudget(budget: Budget) {
        _budgets.value = _budgets.value.map { current ->
            if(current.id == budget.id) budget else current
        }
    }
    fun deleteBudget(id: String) {
        _budgets.value = _budgets.value.filterNot { it.id == id }
    }
}