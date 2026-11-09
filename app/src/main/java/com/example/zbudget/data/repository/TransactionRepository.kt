
package com.example.zbudget.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.example.zbudget.data.model.Transaction
import com.example.zbudget.data.model.TransactionType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Source
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

@Singleton
class TransactionRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var snapshotListener: ListenerRegistration? = null
    private var currentUserId: String? = null
    private val repositoryScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Volatile
    private var hasInternet = hasInternetConnection()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            val connected =
                networkCapabilities.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_INTERNET
                ) &&
                        networkCapabilities.hasCapability(
                            NetworkCapabilities.NET_CAPABILITY_VALIDATED
                        )

            updateInternetStatus(connected)
        }

        override fun onLost(network: Network) {
            updateInternetStatus(false)
        }
    }

    private fun registerNetworkCallback() {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        try {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } catch (e: Exception) {
            hasInternet = hasInternetConnection()
        }
    }

    private fun updateInternetStatus(connected: Boolean) {
        if (hasInternet == connected) return

        hasInternet = connected

        if (!connected) {
            if (firebaseAuth.currentUser != null) {
                _isLoading.value = false
                _isRefreshing.value = false
                _error.value = "Không có kết nối mạng. Vui lòng kiểm tra lại."
            }
        } else {
            _error.value = null

            if (firebaseAuth.currentUser != null) {
                repositoryScope.launch {
                    retryLoad()
                }
            }
        }
    }
    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        val userId = auth.currentUser?.uid

        if (userId == currentUserId) {
            return@AuthStateListener
        }

        currentUserId = userId

        snapshotListener?.remove()
        snapshotListener = null

        _transactions.value = emptyList()
        _error.value = null
        _isLoading.value = userId != null
        _isRefreshing.value = false

        if (userId != null) {
            observeTransactions(userId)
        }
    }

    init {
        registerNetworkCallback()
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    private fun observeTransactions(userId: String) {
        snapshotListener = firestore.collection("users")
            .document(userId)
            .collection("transactions")
            .addSnapshotListener { snapshot, error ->

                if (firebaseAuth.currentUser?.uid != userId) {
                    return@addSnapshotListener
                }

                if (!hasInternet) {
                    _isLoading.value = false
                    _error.value = "Không có kết nối mạng. Vui lòng kiểm tra lại."
                    return@addSnapshotListener
                }

                if (error != null) {
                    _isLoading.value = false
                    _error.value = "Không thể tải dữ liệu giao dịch."
                    return@addSnapshotListener
                }

                val transactions = snapshot?.documents
                    ?.mapNotNull { document ->
                        mapTransaction(document)
                    } ?: emptyList()

                _transactions.value = transactions
                _isLoading.value = false
                if (hasInternet) {
                    _error.value = null
                }
            }
    }

    private fun mapTransaction(
        document: com.google.firebase.firestore.DocumentSnapshot
    ): Transaction? {
        return runCatching {
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
    }

    suspend fun retryLoad() {
        val userId = firebaseAuth.currentUser?.uid

        if (userId == null) {
            _error.value = "Vui lòng đăng nhập để tải giao dịch."
            return
        }
        if (!hasInternetConnection()) {
            _isRefreshing.value = false
            _error.value = "Không có kết nối mạng. Vui lòng kiểm tra lại."
            return
        }

        if (_isRefreshing.value) {
            return
        }

        _isRefreshing.value = true
        _error.value = null

        try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("transactions")
                .get(Source.SERVER)
                .await()

            if (firebaseAuth.currentUser?.uid != userId) {
                return
            }

            val transactions = snapshot.documents
                .mapNotNull { document ->
                    mapTransaction(document)
                }

            _transactions.value = transactions
            _error.value = null

        }catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (firebaseAuth.currentUser?.uid == userId) {
                _error.value = e.localizedMessage
                    ?: "Không thể làm mới dữ liệu giao dịch."
            }
        } finally {
            _isRefreshing.value = false
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

    @SuppressLint("ServiceCast")
    private fun hasInternetConnection(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false

        val capabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}
