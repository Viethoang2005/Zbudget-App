
package com.example.zbudget.ui.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zbudget.data.model.Notification
import com.example.zbudget.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: NotificationRepository
) : ViewModel() {

    val notifications = repository.notifications

    val isLoading = repository.isLoading

    val unreadCount: StateFlow<Int> = repository.notifications
        .map { notifications ->
            notifications.count { notification -> !notification.isRead }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0
        )

    private val _actionError = MutableStateFlow<String?>(null)
    val actionError: StateFlow<String?> = _actionError

    fun addNotification(notification: Notification) {
        viewModelScope.launch {
            try {
                _actionError.value = null
                repository.addNotification(notification)
            } catch (e: Exception) {
                _actionError.value = e.message ?: "Không thể thêm thông báo"
            }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            try {
                _actionError.value = null
                repository.markAsRead(notificationId)
            } catch (e: Exception) {
                _actionError.value = e.message ?: "Không thể cập nhật thông báo"
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            try {
                _actionError.value = null
                repository.markAllAsRead()
            } catch (e: Exception) {
                _actionError.value = e.message ?: "Không thể cập nhật thông báo"
            }
        }
    }

    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            try {
                _actionError.value = null
                repository.deleteNotification(notificationId)
            } catch (e: Exception) {
                _actionError.value = e.message ?: "Không thể xóa thông báo"
            }
        }
    }

    fun deleteAllNotifications() {
        viewModelScope.launch {
            try {
                _actionError.value = null
                repository.deleteAllNotifications()
            } catch (e: Exception) {
                _actionError.value = e.message ?: "Không thể xóa thông báo"
            }
        }
    }

    fun clearError() {
        _actionError.value = null
        repository.clearError()
    }
}
