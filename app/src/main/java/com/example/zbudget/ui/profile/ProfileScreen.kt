
package com.example.zbudget.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.zbudget.ui.theme.ZbudgetCream
import com.example.zbudget.ui.theme.ZbudgetGreen
import com.example.zbudget.ui.theme.ZbudgetGreenLight
import com.example.zbudget.ui.theme.ZbudgetRed
import com.example.zbudget.ui.theme.ZbudgetText

@Composable
fun ProfileScreen(
    onLogOut: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profile by viewModel.uiState.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember {
        mutableStateOf(false)
    }
    var nameInput by remember { mutableStateOf("") }

    ProfileScreenContent(
        displayName = profile.displayName,
        email = profile.email,
        isLoading = profile.isLoading,
        error = profile.error,
        successMessage = profile.successMessage,
        onLogOut = {
            showLogoutDialog = true
        },
        onEditName = {
            nameInput = profile.displayName
            viewModel.clearMessage()
            showEditDialog = true
        },
        onChangePassword = {
            viewModel.clearMessage()
            showPasswordDialog = true
        }
    )

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!profile.isLoading) {
                    showEditDialog = false
                    viewModel.clearMessage()
                }
            },
            title = {
                Text(
                    text = "Chỉnh sửa tên",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Tên hiển thị") },
                    singleLine = true,
                    enabled = !profile.isLoading
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateDisplayName(nameInput)
                        showEditDialog = false
                    },
                    enabled = !profile.isLoading
                ) {
                    Text("Lưu")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showEditDialog = false
                        viewModel.clearMessage()
                    },
                    enabled = !profile.isLoading
                ) {
                    Text("Hủy")
                }
            }
        )
    }

    if (showPasswordDialog) {
        ChangePasswordDialog(
            isLoading = profile.isChangingPassword,
            onDismiss = {
                if (!profile.isChangingPassword) {
                    showPasswordDialog = false
                    viewModel.clearMessage()
                }
            },
            onSubmit = { current, new, confirm ->
                viewModel.changePassword(current, new, confirm)
            },
            error = profile.error,
            successMessage = profile.successMessage,
            onSuccessDismiss = {
                showPasswordDialog = false
                viewModel.clearMessage()
            }
        )
    }
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = {
                showLogoutDialog = false
            },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = ZbudgetRed
                )
            },
            title = {
                Text(
                    text = "Xác nhận đăng xuất",
                    fontWeight = FontWeight.Bold,
                    color = ZbudgetText
                )
            },
            text = {
                Text(
                    text = "Bạn có chắc chắn muốn đăng xuất khỏi tài khoản này không?",
                    color = ZbudgetText
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogOut()
                    }
                ) {
                    Text(
                        text = "Đăng xuất",
                        color = ZbudgetRed,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                    }
                ) {
                    Text(
                        text = "Hủy",
                        color = ZbudgetGreen
                    )
                }
            },
            containerColor = ZbudgetCream
        )
    }
}

@Composable
private fun ChangePasswordDialog(
    isLoading: Boolean,
    error: String?,
    successMessage: String?,
    onDismiss: () -> Unit,
    onSubmit: (String, String, String) -> Unit,
    onSuccessDismiss: () -> Unit
) {
    var currentPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var showCurrent by rememberSaveable { mutableStateOf(false) }
    var showNew by rememberSaveable { mutableStateOf(false) }
    var showConfirm by rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Đổi mật khẩu",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PasswordField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = "Mật khẩu hiện tại",
                    visible = showCurrent,
                    onToggleVisibility = { showCurrent = !showCurrent },
                    enabled = !isLoading
                )

                PasswordField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = "Mật khẩu mới",
                    visible = showNew,
                    onToggleVisibility = { showNew = !showNew },
                    enabled = !isLoading
                )

                PasswordField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = "Xác nhận mật khẩu mới",
                    visible = showConfirm,
                    onToggleVisibility = { showConfirm = !showConfirm },
                    enabled = !isLoading
                )

                Text(
                    text = "Mật khẩu mới cần có ít nhất 6 ký tự.",
                    style = MaterialTheme.typography.bodySmall,
                    color = ZbudgetText.copy(alpha = 0.6f)
                )

                error?.let {
                    Text(
                        text = it,
                        color = ZbudgetRed,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                successMessage?.let {
                    Text(
                        text = it,
                        color = ZbudgetGreen,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            if (successMessage != null) {
                TextButton(onClick = onSuccessDismiss) {
                    Text("Đóng")
                }
            } else {
                TextButton(
                    onClick = {
                        onSubmit(
                            currentPassword,
                            newPassword,
                            confirmPassword
                        )
                    },
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Xác nhận")
                    }
                }
            }
        },
        dismissButton = {
            if (successMessage == null) {
                TextButton(
                    onClick = onDismiss,
                    enabled = !isLoading
                ) {
                    Text("Hủy")
                }
            }
        }
    )
}

@Composable
private fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    visible: Boolean,
    onToggleVisibility: () -> Unit,
    enabled: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = true,
        enabled = enabled,
        visualTransformation = if (visible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    imageVector = if (visible) {
                        Icons.Default.VisibilityOff
                    } else {
                        Icons.Default.Visibility
                    },
                    contentDescription = if (visible) {
                        "Ẩn mật khẩu"
                    } else {
                        "Hiện mật khẩu"
                    }
                )
            }
        }
    )
}

@Composable
private fun ProfileScreenContent(
    displayName: String,
    email: String,
    isLoading: Boolean,
    error: String?,
    successMessage: String?,
    onLogOut: () -> Unit,
    onEditName: () -> Unit,
    onChangePassword: () -> Unit
) {
    val avatarLetter = displayName.firstOrNull()?.uppercase() ?: "U"

    Scaffold(
        containerColor = ZbudgetCream,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Tài khoản",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZbudgetText
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(ZbudgetGreenLight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = avatarLetter,
                    color = ZbudgetGreen,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = displayName.ifBlank { "Người dùng" },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = ZbudgetText
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium,
                color = ZbudgetText.copy(alpha = 0.65f)
            )

            if (isLoading) {
                Spacer(modifier = Modifier.height(12.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = ZbudgetGreen
                )
            }

            error?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = it,
                    color = ZbudgetRed,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            successMessage?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = it,
                    color = ZbudgetGreen,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            ProfileOption(
                icon = Icons.Default.Edit,
                title = "Chỉnh sửa tên",
                subtitle = "Thay đổi tên hiển thị của bạn",
                onClick = onEditName
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileOption(
                icon = Icons.Default.Lock,
                title = "Đổi mật khẩu",
                subtitle = "Cập nhật mật khẩu đăng nhập",
                onClick = onChangePassword
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onLogOut,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ZbudgetRed
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = "Đăng xuất",
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ProfileOption(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ZbudgetGreenLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ZbudgetGreen
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = ZbudgetText,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = subtitle,
                    color = ZbudgetText.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = ZbudgetText.copy(alpha = 0.5f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    MaterialTheme {
        ProfileScreenContent(
            displayName = "Nguyen Van A",
            email = "nguyenvana@gmail.com",
            isLoading = false,
            error = null,
            successMessage = null,
            onLogOut = {},
            onEditName = {},
            onChangePassword = {}
        )
    }
}
