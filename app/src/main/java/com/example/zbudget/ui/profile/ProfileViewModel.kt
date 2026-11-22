
package com.example.zbudget.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zbudget.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ProfileUiState(
    val displayName: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null
            )

            try {
                val user = auth.currentUser
                    ?: throw Exception("Không tìm thấy tài khoản")

                authRepository.saveUserProfile(user)

                val snapshot = firestore
                    .collection("users")
                    .document(user.uid)
                    .get()
                    .await()

                _uiState.value = ProfileUiState(
                    displayName = snapshot.getString("displayName").orEmpty(),
                    email = user.email.orEmpty()
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Không thể tải hồ sơ"
                )
            }
        }
    }

    fun updateDisplayName(newName: String) {
        val name = newName.trim()

        if (name.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Tên không được để trống",
                successMessage = null
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                successMessage = null
            )

            try {
                val user = auth.currentUser
                    ?: throw Exception("Không tìm thấy tài khoản")

                firestore
                    .collection("users")
                    .document(user.uid)
                    .set(
                        mapOf("displayName" to name),
                        SetOptions.merge()
                    )
                    .await()

                val request = userProfileChangeRequest {
                    displayName = name
                }

                user.updateProfile(request).await()

                _uiState.value = _uiState.value.copy(
                    displayName = name,
                    email = user.email.orEmpty(),
                    isLoading = false,
                    successMessage = "Cập nhật tên thành công"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Không thể cập nhật tên"
                )
            }
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(
            error = null,
            successMessage = null
        )
    }
}
