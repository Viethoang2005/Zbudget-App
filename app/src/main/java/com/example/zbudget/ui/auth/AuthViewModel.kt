
package com.example.zbudget.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zbudget.data.repository.AuthRepository
import com.example.zbudget.utils.AuthValidate
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val userEmail: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AuthUiState(
            isLoggedIn = authRepository.currentUser != null,
            userEmail = authRepository.currentUser?.email
        )
    )

    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun register(email: String, password: String) {
        val trimmedEmail = email.trim()

        val validationError = AuthValidate.validateEmail(trimmedEmail)
            ?: AuthValidate.validateRegisterPassword(password)

        if (validationError != null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = validationError
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val user = authRepository.register(trimmedEmail, password)

                _uiState.value = AuthUiState(
                    isLoggedIn = true,
                    userEmail = user.email
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = getRegisterErrorMessage(e)
                )
            } finally {
                _uiState.value = _uiState.value.copy(
                    isLoading = false
                )
            }
        }
    }

    fun login(email: String, password: String) {
        val trimmedEmail = email.trim()

        val validationError = AuthValidate.validateEmail(trimmedEmail)
            ?: AuthValidate.validateLoginPassword(password)

        if (validationError != null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = validationError
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val user = authRepository.login(trimmedEmail, password)

                _uiState.value = AuthUiState(
                    isLoggedIn = true,
                    userEmail = user.email
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = getLoginErrorMessage(e)
                )
            } finally {
                _uiState.value = _uiState.value.copy(
                    isLoading = false
                )
            }
        }
    }

    fun loginWithGoogle(idToken: String) {
        if (idToken.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Không lấy được thông tin tài khoản Google. Vui lòng thử lại."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val user = authRepository.loginWithGoogle(idToken)

                _uiState.value = AuthUiState(
                    isLoggedIn = true,
                    userEmail = user.email
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = getGoogleLoginErrorMessage(e)
                )
            } finally {
                _uiState.value = _uiState.value.copy(
                    isLoading = false
                )
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _uiState.value = AuthUiState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }

    private fun getLoginErrorMessage(e: Exception): String {
        if (e is FirebaseNetworkException) {
            return "Không có kết nối mạng. Vui lòng kiểm tra Internet và thử lại."
        }

        if (e is FirebaseAuthException) {
            return when (e.errorCode) {
                "ERROR_INVALID_EMAIL" ->
                    "Địa chỉ email không hợp lệ."

                "ERROR_USER_NOT_FOUND" ->
                    "Không tìm thấy tài khoản với email này."

                "ERROR_WRONG_PASSWORD",
                "ERROR_INVALID_CREDENTIAL" ->
                    "Email hoặc mật khẩu không chính xác."

                "ERROR_USER_DISABLED" ->
                    "Tài khoản này đã bị vô hiệu hóa. Vui lòng liên hệ hỗ trợ."

                "ERROR_TOO_MANY_REQUESTS" ->
                    "Bạn đã thử đăng nhập quá nhiều lần. Vui lòng thử lại sau."

                else ->
                    "Đăng nhập không thành công. Vui lòng kiểm tra thông tin và thử lại."
            }
        }

        return "Đã xảy ra lỗi khi đăng nhập. Vui lòng thử lại."
    }

    private fun getRegisterErrorMessage(e: Exception): String {
        if (e is FirebaseNetworkException) {
            return "Không có kết nối mạng. Vui lòng kiểm tra Internet và thử lại."
        }

        if (e is FirebaseAuthException) {
            return when (e.errorCode) {
                "ERROR_INVALID_EMAIL" ->
                    "Địa chỉ email không hợp lệ."

                "ERROR_EMAIL_ALREADY_IN_USE" ->
                    "Email này đã được đăng ký. Vui lòng sử dụng email khác hoặc đăng nhập."

                "ERROR_WEAK_PASSWORD" ->
                    "Mật khẩu chưa đủ mạnh. Vui lòng sử dụng mật khẩu có ít nhất 6 ký tự."

                "ERROR_TOO_MANY_REQUESTS" ->
                    "Bạn đã thực hiện quá nhiều yêu cầu. Vui lòng thử lại sau."

                else ->
                    "Đăng ký không thành công. Vui lòng kiểm tra thông tin và thử lại."
            }
        }

        return "Đã xảy ra lỗi khi đăng ký. Vui lòng thử lại."
    }

    private fun getGoogleLoginErrorMessage(e: Exception): String {
        if (e is FirebaseNetworkException) {
            return "Không có kết nối mạng. Vui lòng kiểm tra Internet và thử lại."
        }

        if (e is FirebaseAuthException) {
            return when (e.errorCode) {
                "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" ->
                    "Email này đã được liên kết với một phương thức đăng nhập khác."

                "ERROR_TOO_MANY_REQUESTS" ->
                    "Bạn đã thử quá nhiều lần. Vui lòng thử lại sau."

                else ->
                    "Đăng nhập bằng Google không thành công. Vui lòng thử lại."
            }
        }

        return "Đã xảy ra lỗi khi đăng nhập bằng Google. Vui lòng thử lại."
    }
}
