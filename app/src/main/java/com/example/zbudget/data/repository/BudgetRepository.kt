
package com.example.zbudget.data.repository

import com.example.zbudget.data.model.Budget
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {
    private val _budgets = MutableStateFlow<List<Budget>>(emptyList())
    val budgets: StateFlow<List<Budget>> = _budgets.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null
    private var currentUid: String? = null

    init {
        firebaseAuth.addAuthStateListener { auth ->
            val uid = auth.currentUser?.uid

            if (uid != currentUid) {
                listenerRegistration?.remove()
                listenerRegistration = null
                currentUid = uid
                _budgets.value = emptyList()

                if (uid != null) {
                    listenToBudgets(uid)
                }
            }
        }
    }

    private fun budgetsCollection(uid: String) =
        firestore.collection("users")
            .document(uid)
            .collection("budgets")

    private fun listenToBudgets(uid: String) {
        listenerRegistration = budgetsCollection(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                _budgets.value = snapshot.documents.mapNotNull { document ->
                    document.toObject(Budget::class.java)?.copy(
                        id = document.id
                    )
                }
            }
    }

    fun getBudgetsByMonth(month: String): List<Budget> {
        return _budgets.value.filter { it.month == month }
    }

    fun addBudget(budget: Budget) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        val document = budgetsCollection(uid).document()

        val newBudget = budget.copy(id = document.id)

        document.set(newBudget)
    }

    fun updateBudget(budget: Budget) {
        val uid = firebaseAuth.currentUser?.uid ?: return

        budgetsCollection(uid)
            .document(budget.id)
            .set(budget)
    }

    fun deleteBudget(id: String) {
        val uid = firebaseAuth.currentUser?.uid ?: return

        budgetsCollection(uid)
            .document(id)
            .delete()
    }
}
