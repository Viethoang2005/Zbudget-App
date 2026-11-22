package com.example.zbudget.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview as ComposePreview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.zbudget.ui.components.ZbudgetLoading
import com.example.zbudget.ui.theme.ZbudgetGreen
import com.example.zbudget.ui.theme.ZbudgetGreenDark
import com.example.zbudget.ui.theme.ZbudgetGreenLight
import com.example.zbudget.ui.theme.ZbudgetTheme
import com.example.zbudget.utils.AuthValidate

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onRegisterSuccess()
        }
    }

    RegisterScreenContent(
        email = email,
        onEmailChange = {
            email = it
            emailError = null
            viewModel.clearError()
        },
        password = password,
        onPasswordChange = {
            password = it
            passwordError = null
            viewModel.clearError()
        },
        confirmPassword = confirmPassword,
        onConfirmPasswordChange = {
            confirmPassword = it
            confirmPasswordError = null
            viewModel.clearError()
        },
        passwordVisible = passwordVisible,
        onPasswordVisibilityChange = {
            passwordVisible = it
        },
        confirmPasswordVisible = confirmPasswordVisible,
        onConfirmPasswordVisibilityChange = {
            confirmPasswordVisible = it
        },
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        emailError = emailError,
        passwordError = passwordError,
        confirmPasswordError = confirmPasswordError,
        onRegisterClick = {
            viewModel.clearError()

            emailError = AuthValidate.validateEmail(email.trim())
            passwordError = AuthValidate.validateRegisterPassword(password)
            confirmPasswordError = AuthValidate.validateConfirmPassword(
                password = password,
                confirmPassword = confirmPassword
            )

            if (
                emailError == null &&
                passwordError == null &&
                confirmPasswordError == null
            ) {
                viewModel.register(
                    email = email.trim(),
                    password = password
                )
            }
        },
        onLoginClick = onNavigateToLogin
    )
}

@Composable
private fun RegisterScreenContent(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    confirmPasswordVisible: Boolean,
    onConfirmPasswordVisibilityChange: (Boolean) -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    emailError: String?,
    passwordError: String?,
    confirmPasswordError: String?,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Zbudget",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = ZbudgetGreen
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Quản lý tài chính cá nhân",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Tạo tài khoản",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Bắt đầu quản lý tài chính của bạn",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(28.dp))

            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email") },
                placeholder = { Text("Nhập email của bạn") },
                singleLine = true,
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ZbudgetGreen,
                    unfocusedBorderColor = ZbudgetGreenLight,
                    focusedLabelColor = ZbudgetGreen,
                    cursorColor = ZbudgetGreen,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                isError = emailError != null,
                supportingText = {
                    emailError?.let {
                        Text(text = it)
                    }
                },
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Mật khẩu") },
                placeholder = { Text("Nhập mật khẩu") },
                singleLine = true,
                enabled = !isLoading,
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            onPasswordVisibilityChange(!passwordVisible)
                        }
                    ) {
                        Icon(
                            imageVector = if (passwordVisible) {
                                Icons.Default.Visibility
                            } else {
                                Icons.Default.VisibilityOff
                            },
                            contentDescription = if (passwordVisible) "Ẩn" else "Hiện",
                            tint = ZbudgetGreen
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ZbudgetGreen,
                    unfocusedBorderColor = ZbudgetGreenLight,
                    focusedLabelColor = ZbudgetGreen,
                    cursorColor = ZbudgetGreen,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                isError = passwordError != null,
                supportingText = {
                    passwordError?.let {
                        Text(text = it)
                    }
                },
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Xác nhận mật khẩu") },
                placeholder = { Text("Nhập lại mật khẩu") },
                singleLine = true,
                enabled = !isLoading,
                visualTransformation = if (confirmPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            onConfirmPasswordVisibilityChange(
                                !confirmPasswordVisible
                            )
                        }
                    ) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) {
                                Icons.Default.Visibility
                            } else {
                                Icons.Default.VisibilityOff
                            },
                            contentDescription = if (confirmPasswordVisible) {
                                "Ẩn"
                            } else {
                                "Hiện"
                            },
                            tint = ZbudgetGreen
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ZbudgetGreen,
                    unfocusedBorderColor = ZbudgetGreenLight,
                    focusedLabelColor = ZbudgetGreen,
                    cursorColor = ZbudgetGreen,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                isError = confirmPasswordError != null,
                supportingText = {
                    confirmPasswordError?.let {
                        Text(text = it)
                    }
                },
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onRegisterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ZbudgetGreen,
                    contentColor = androidx.compose.ui.graphics.Color.White,
                    disabledContainerColor = ZbudgetGreenLight,
                    disabledContentColor = ZbudgetGreenDark
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    ZbudgetLoading(
                        size = 24.dp,
                        strokeWidth = 3.dp
                    )
                } else {
                    Text(
                        text = "Đăng ký",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Đã có tài khoản?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                TextButton(
                    onClick = onLoginClick,
                    enabled = !isLoading
                ) {
                    Text(
                        text = "Đăng Nhập",
                        color = ZbudgetGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@ComposePreview(showBackground = true)
@Composable
private fun RegisterScreenPreview() {
    ZbudgetTheme {
        RegisterScreenContent(
            email = "example@gmail.com",
            onEmailChange = {},
            password = "123456",
            onPasswordChange = {},
            confirmPassword = "123456",
            onConfirmPasswordChange = {},
            passwordVisible = false,
            onPasswordVisibilityChange = {},
            confirmPasswordVisible = false,
            onConfirmPasswordVisibilityChange = {},
            isLoading = false,
            errorMessage = null,
            emailError = null,
            passwordError = null,
            confirmPasswordError = null,
            onRegisterClick = {},
            onLoginClick = {}
        )
    }
}
