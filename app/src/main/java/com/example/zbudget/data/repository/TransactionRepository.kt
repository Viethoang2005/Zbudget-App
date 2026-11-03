package com.example.zbudget.data.repository

import com.example.zbudget.data.mock.FakeTransactions
import com.example.zbudget.data.model.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor() {
    private val _transactions = MutableStateFlow(
        FakeTransactions.transactions
    )

    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()
    fun getTransactions() : List<Transaction> {
        return _transactions.value
    }
    fun getTransactionById(id: String): Transaction? {
        return _transactions.value.find { it.id == id }
    }

    fun addTransaction(transaction: Transaction) {
        _transactions.value = listOf(transaction) + _transactions.value
    }

    fun updateTransaction(transaction: Transaction) {
        _transactions.value = _transactions.value.map { current ->
            if(current.id == transaction.id) {
                transaction
            }else {
                current
            }
        }
    }
    fun deleteTransaction(id: String) {
        _transactions.value = _transactions.value.filterNot {
            it.id == id
        }
    }

}

