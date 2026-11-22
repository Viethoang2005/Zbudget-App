
package com.example.zbudget.data.repository

import com.example.zbudget.data.model.Notification
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.isNotEmpty

@Singleton
class NotificationRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private var listenerRegistration: ListenerRegistration? = null
    private var currentUserId: String? = null

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        val userId = auth.currentUser?.uid

        if (userId != currentUserId) {
            currentUserId = userId
            listenerRegistration?.remove()
            listenerRegistration = null
            _notifications.value = emptyList()
            _error.value = null

            if (userId != null) {
                observeNotifications(userId)
            } else {
                _isLoading.value = false
            }
        }
    }

    init {
        firebaseAuth.addAuthStateListener(authStateListener)
    }

    private fun getNotificationsCollection() =
        firestore.collection("users")
            .document(
                firebaseAuth.currentUser?.uid
                    ?: throw Exception("Vui lòng đăng nhập")
            )
            .collection("notifications")

    private fun observeNotifications(userId: String) {
        _isLoading.value = true

        listenerRegistration = firestore.collection("users")
            .document(userId)
            .collection("notifications")
            .orderBy(
                "createdAt",
                com.google.firebase.firestore.Query.Direction.DESCENDING
            )
            .addSnapshotListener { snapshot, exception ->
                if (firebaseAuth.currentUser?.uid != userId) return@addSnapshotListener

                if (exception != null) {
                    _error.value = exception.message ?: "Không thể tải thông báo"
                    _isLoading.value = false
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    _notifications.value = snapshot.documents.mapNotNull { document ->
                        document.toObject(Notification::class.java)
                            ?.copy(id = document.id)
                    }
                    _error.value = null
                }

                _isLoading.value = false
            }
    }

    suspend fun addNotification(notification: Notification): String {
        val document = getNotificationsCollection().document()

        val data = mapOf(
            "title" to notification.title,
            "message" to notification.message,
            "type" to notification.type,
            "createdAt" to notification.createdAt,
            "isRead" to notification.isRead,
            "relatedId" to notification.relatedId
        )

        document.set(data).await()
        return document.id
    }

    suspend fun markAsRead(notificationId: String) {
        require(notificationId.isNotBlank()) {
            "ID thông báo không hợp lệ"
        }

        getNotificationsCollection()
            .document(notificationId)
            .update("isRead", true)
            .await()
    }

    suspend fun markAllAsRead() {
        val unreadNotifications = _notifications.value.filter { !it.isRead }
        val collection = getNotificationsCollection()
        val batch = firestore.batch()

        unreadNotifications.forEach { notification ->
            batch.update(
                collection.document(notification.id),
                "isRead",
                true
            )
        }

        if (unreadNotifications.isNotEmpty()) {
            batch.commit().await()
        }
    }

    suspend fun deleteNotification(notificationId: String) {
        require(notificationId.isNotBlank()) {
            "ID thông báo không hợp lệ"
        }

        getNotificationsCollection()
            .document(notificationId)
            .delete()
            .await()
    }

    suspend fun deleteAllNotifications() {
        val collection = getNotificationsCollection()
        val batch = firestore.batch()

        _notifications.value.forEach { notification ->
            batch.delete(collection.document(notification.id))
        }

        if (_notifications.value.isNotEmpty()) {
            batch.commit().await()
        }
    }

    fun clearError() {
        _error.value = null
    }
}
