package com.example.zbudget.ui.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.zbudget.data.model.Notification
import com.example.zbudget.ui.theme.ZbudgetCream
import com.example.zbudget.ui.theme.ZbudgetGreen
import com.example.zbudget.ui.theme.ZbudgetGreenLight
import com.example.zbudget.ui.theme.ZbudgetRed
import com.example.zbudget.ui.theme.ZbudgetText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBack: () -> Unit,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val notifications by viewModel.notifications.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()
    val actionError by viewModel.actionError.collectAsState()

    var selectedNotificationId by remember { mutableStateOf<String?>(null) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = ZbudgetCream,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Thông báo",
                            fontWeight = FontWeight.Bold,
                            color = ZbudgetText
                        )
                        Text(
                            text = "$unreadCount thông báo chưa đọc",
                            style = MaterialTheme.typography.bodySmall,
                            color = ZbudgetText.copy(alpha = 0.6f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = ZbudgetText
                        )
                    }
                },
                actions = {
                    if (notifications.isNotEmpty()) {
                        IconButton(
                            onClick = { showDeleteAllDialog = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Xóa tất cả",
                                tint = ZbudgetRed
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ZbudgetCream
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            if (notifications.isNotEmpty() && unreadCount > 0) {
                TextButton(
                    onClick = viewModel::markAllAsRead,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = "Đánh dấu tất cả đã đọc",
                        color = ZbudgetGreen,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            when {
                isLoading && notifications.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = ZbudgetGreen)
                    }
                }

                notifications.isEmpty() -> {
                    EmptyNotifications()
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = notifications,
                            key = { it.id }
                        ) { notification ->
                            NotificationItem(
                                notification = notification,
                                onClick = {
                                    if (!notification.isRead) {
                                        viewModel.markAsRead(notification.id)
                                    }
                                },
                                onDelete = {
                                    selectedNotificationId = notification.id
                                }
                            )
                        }
                    }
                }
            }

            actionError?.let { error ->
                Text(
                    text = error,
                    color = ZbudgetRed,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }

    selectedNotificationId?.let { notificationId ->
        AlertDialog(
            onDismissRequest = {
                selectedNotificationId = null
            },
            title = {
                Text("Xóa thông báo?")
            },
            text = {
                Text("Bạn có chắc chắn muốn xóa thông báo này không?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteNotification(notificationId)
                        selectedNotificationId = null
                    }
                ) {
                    Text("Xóa", color = ZbudgetRed)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        selectedNotificationId = null
                    }
                ) {
                    Text("Hủy", color = ZbudgetGreen)
                }
            },
            containerColor = ZbudgetCream
        )
    }

    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteAllDialog = false
            },
            title = {
                Text("Xóa tất cả thông báo?")
            },
            text = {
                Text("Toàn bộ thông báo sẽ bị xóa và không thể khôi phục.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAllNotifications()
                        showDeleteAllDialog = false
                    }
                ) {
                    Text("Xóa tất cả", color = ZbudgetRed)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteAllDialog = false
                    }
                ) {
                    Text("Hủy", color = ZbudgetGreen)
                }
            },
            containerColor = ZbudgetCream
        )
    }
}

@Composable
private fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val icon: ImageVector
    val iconColor: Color

    when (notification.type) {
        "BUDGET_WARNING" -> {
            icon = Icons.Default.Warning
            iconColor = ZbudgetRed
        }

        "REMINDER" -> {
            icon = Icons.Default.Notifications
            iconColor = Color(0xFFD69A32)
        }

        else -> {
            icon = Icons.AutoMirrored.Filled.ReceiptLong
            iconColor = ZbudgetGreen
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (notification.isRead) Color.White
                else ZbudgetGreenLight.copy(alpha = 0.7f)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = notification.title,
                    fontWeight = FontWeight.Bold,
                    color = ZbudgetText,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )

                if (!notification.isRead) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(ZbudgetGreen)
                    )
                }
            }

            Text(
                text = notification.message,
                color = ZbudgetText.copy(alpha = 0.75f),
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = formatNotificationDate(notification.createdAt),
                color = ZbudgetText.copy(alpha = 0.5f),
                style = MaterialTheme.typography.labelSmall
            )
        }

        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Xóa thông báo",
                tint = ZbudgetRed.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun EmptyNotifications() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                tint = ZbudgetGreen,
                modifier = Modifier.size(64.dp)
            )

            Text(
                text = "Chưa có thông báo",
                color = ZbudgetText,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Các thông báo của bạn sẽ xuất hiện ở đây",
                color = ZbudgetText.copy(alpha = 0.6f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun formatNotificationDate(timestamp: Long): String {
    return SimpleDateFormat(
        "HH:mm - dd/MM/yyyy",
        Locale("vi", "VN")
    ).format(Date(timestamp))
}

@Preview(showBackground = true)
@Composable
private fun NotificationsScreenPreview() {
    MaterialTheme {
        EmptyNotifications()
    }
}
