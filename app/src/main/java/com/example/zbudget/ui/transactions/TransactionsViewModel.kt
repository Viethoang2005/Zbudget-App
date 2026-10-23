
package com.example.zbudget.ui.transactions

import androidx.lifecycle.ViewModel
import com.example.zbudget.data.model.Transaction
import com.example.zbudget.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {

    fun getTransactions(): List<Transaction> {
        return repository.getTransactions()
    }

    fun getTransactionById(id: String): Transaction? {
        return repository.getTransactionById(id)
    }
}
