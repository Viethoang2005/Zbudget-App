package com.example.zbudget.data.repository

import com.example.zbudget.data.model.Transaction
import com.example.zbudget.data.model.TransactionType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private var snapshotListener: ListenerRegistration? = null
    private var currentUserId: String? = null

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        val userId = auth.currentUser?.uid

        if (userId == currentUserId) {
            return@AuthStateListener
        }

        currentUserId = userId

        snapshotListener?.remove()
        snapshotListener = null

        _transactions.value = emptyList()

        if (userId != null) {
            observeTransactions(userId)
        }
    }

    init {
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    private fun observeTransactions(userId: String) {
        snapshotListener = firestore.collection("users")
            .document(userId)
            .collection("transactions")
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    return@addSnapshotListener
                }

                if (firebaseAuth.currentUser?.uid != userId) {
                    return@addSnapshotListener
                }

                val transactions = snapshot?.documents?.mapNotNull { document ->
                    runCatching {
                        Transaction(
                            id = document.id,
                            title = document.getString("title") ?: "",
                            amount = document.getLong("amount") ?: 0L,
                            type = TransactionType.valueOf(
                                document.getString("type") ?: "EXPENSE"
                            ),
                            category = document.getString("category") ?: "",
                            dateLabel = document.getString("dateLabel") ?: "",
                            timeLabel = document.getString("timeLabel") ?: "",
                            note = document.getString("note") ?: "",
                            createdAt = document.getLong("createdAt")
                                ?: System.currentTimeMillis()
                        )
                    }.getOrNull()
                } ?: emptyList()

                _transactions.value = transactions
            }
    }

    private fun getTransactionsCollection() =
        firestore.collection("users")
            .document(
                firebaseAuth.currentUser?.uid
                    ?: throw Exception("Vui lòng đăng nhập")
            )
            .collection("transactions")

    fun getTransactions(): List<Transaction> {
        return _transactions.value
    }

    fun getTransactionById(id: String): Transaction? {
        return _transactions.value.find { it.id == id }
    }

    suspend fun addTransaction(transaction: Transaction): String {
        val document = getTransactionsCollection().document()

        val data = hashMapOf(
            "title" to transaction.title,
            "amount" to transaction.amount,
            "type" to transaction.type.name,
            "category" to transaction.category,
            "dateLabel" to transaction.dateLabel,
            "timeLabel" to transaction.timeLabel,
            "note" to transaction.note,
            "createdAt" to transaction.createdAt
        )

        document.set(data).await()

        return document.id
    }

    suspend fun updateTransaction(transaction: Transaction) {
        require(transaction.id.isNotBlank()) {
            "ID giao dịch không hợp lệ"
        }

        val data = hashMapOf(
            "title" to transaction.title,
            "amount" to transaction.amount,
            "type" to transaction.type.name,
            "category" to transaction.category,
            "dateLabel" to transaction.dateLabel,
            "timeLabel" to transaction.timeLabel,
            "note" to transaction.note,
            "createdAt" to transaction.createdAt
        )

        getTransactionsCollection()
            .document(transaction.id)
            .update(data as Map<String, Any>)
            .await()
    }

    suspend fun deleteTransaction(id: String) {
        getTransactionsCollection()
            .document(id)
            .delete()
            .await()
    }
}