package com.example.zbudget.data.repository

import com.example.zbudget.data.mock.FakeTransactions
import com.example.zbudget.data.model.Transaction
import javax.inject.Inject

class TransactionRepository @Inject constructor() {
    fun getTransactions() : List<Transaction> {
        return FakeTransactions.transactions
    }
}