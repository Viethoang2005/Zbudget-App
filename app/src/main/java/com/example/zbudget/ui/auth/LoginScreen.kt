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
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.zbudget.R
import com.example.zbudget.ui.components.ZbudgetLoading
import com.example.zbudget.ui.theme.ZbudgetGreen
import com.example.zbudget.ui.theme.ZbudgetGreenDark
import com.example.zbudget.ui.theme.ZbudgetGreenLight
import com.example.zbudget.ui.theme.ZbudgetTheme
import com.example.zbudget.utils.AuthValidate
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch
import androidx.compose.ui.tooling.preview.Preview as ComposePreview

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    var googleErrorMessage by remember { mutableStateOf<String?>(null) }
    var isGoogleLoading by remember { mutableStateOf(false) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val webClientId = stringResource(R.string.default_web_client_id)

    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember(context) {
        CredentialManager.create(context)
    }

    LaunchedEffect(uiState.isLoggedIn) {
        if (uiState.isLoggedIn) {
            onLoginSuccess()
        }
    }

    LoginScreenContent(
        email = email,
        onEmailChange = {
            email = it
            emailError = null
            googleErrorMessage = null
            viewModel.clearError()
        },
        password = password,
        onPasswordChange = {
            password = it
            passwordError = null
            googleErrorMessage = null
            viewModel.clearError()
        },
        passwordVisible = passwordVisible,
        onPasswordVisibilityChange = {
            passwordVisible = it
        },
        isLoading = uiState.isLoading,
        isGoogleLoading = isGoogleLoading,
        errorMessage = googleErrorMessage ?: uiState.errorMessage,
        emailError = emailError,
        passwordError = passwordError,
        onLoginClick = {
            googleErrorMessage = null
            viewModel.clearError()

            emailError = AuthValidate.validateEmail(email.trim())
            passwordError = AuthValidate.validateLoginPassword(password)

            if (emailError == null && passwordError == null) {
                viewModel.login(
                    email = email.trim(),
                    password = password
                )
            }
        },
        onGoogleLoginClick = {
            coroutineScope.launch {
                googleErrorMessage = null
                isGoogleLoading = true

                try {
                    val googleIdOption = GetGoogleIdOption.Builder()
                        .setFilterByAuthorizedAccounts(false)
                        .setServerClientId(webClientId)
                        .setAutoSelectEnabled(false)
                        .build()

                    val request = GetCredentialRequest.Builder()
                        .addCredentialOption(googleIdOption)
                        .build()

                    val result = credentialManager.getCredential(
                        context = context,
                        request = request
                    )

                    val credential = result.credential

                    if (
                        credential is CustomCredential &&
                        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                    ) {
                        val googleCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)

                        viewModel.loginWithGoogle(googleCredential.idToken)
                    } else {
                        googleErrorMessage = "Không thể lấy thông tin tài khoản Google"
                    }
                } catch (e: GetCredentialCancellationException) {
                    googleErrorMessage = null
                } catch (e: GetCredentialException) {
                    googleErrorMessage = e.localizedMessage
                        ?: "Không thể chọn tài khoản Google"
                } catch (e: Exception) {
                    googleErrorMessage = e.localizedMessage
                        ?: "Đăng nhập Google thất bại"
                } finally {
                    isGoogleLoading = false
                }
            }
        },
        onRegisterClick = onNavigateToRegister
    )
}

@Composable
private fun LoginScreenContent(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    isLoading: Boolean,
    isGoogleLoading: Boolean,
    errorMessage: String?,
    emailError: String?,
    passwordError: String?,
    onLoginClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
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
                .padding(horizontal = 24.dp),
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

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Chào mừng trở lại!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Đăng nhập để tiếp tục sử dụng Zbudget",
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
                enabled = !isLoading && !isGoogleLoading,
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
                enabled = !isLoading && !isGoogleLoading,
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
                }
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
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isLoading && !isGoogleLoading,
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
                        text = "Đăng nhập",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Hoặc tiếp tục với",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onGoogleLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isLoading && !isGoogleLoading,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isGoogleLoading) {
                    ZbudgetLoading(
                        size = 24.dp,
                        strokeWidth = 3.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "G",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = ZbudgetGreen
                        )

                        Spacer(modifier = Modifier.padding(horizontal = 6.dp))

                        Text(
                            text = "Đăng nhập bằng Google",
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Chưa có tài khoản?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                TextButton(
                    onClick = onRegisterClick,
                    enabled = !isLoading && !isGoogleLoading
                ) {
                    Text(
                        text = "Đăng ký",
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
private fun LoginScreenPreview() {
    ZbudgetTheme {
        LoginScreenContent(
            email = "example@gmail.com",
            password = "123456",
            passwordVisible = false,
            isLoading = false,
            isGoogleLoading = false,
            errorMessage = null,
            emailError = null,
            passwordError = null,
            onEmailChange = {},
            onPasswordChange = {},
            onPasswordVisibilityChange = {},
            onLoginClick = {},
            onGoogleLoginClick = {},
            onRegisterClick = {}
        )
    }
}
