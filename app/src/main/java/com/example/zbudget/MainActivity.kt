
package com.example.zbudget

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.example.zbudget.navigation.AppNavBar
import com.example.zbudget.ui.theme.ZbudgetTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.core.content.edit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val preferences = getSharedPreferences(
            "zbudget_preferences",
            MODE_PRIVATE
        )


        setContent {
            val systemDarkTheme = isSystemInDarkTheme()

            var isDarkTheme by remember {
                mutableStateOf(
                    preferences.getBoolean(
                        "dark_theme",
                        systemDarkTheme
                    )
                )
            }

            ZbudgetTheme(darkTheme = isDarkTheme) {
                AppNavBar(
                    isDarkTheme = isDarkTheme,
                    onDarkThemeChange = { enabled ->
                        isDarkTheme = enabled
                        preferences.edit {
                            putBoolean("dark_theme", enabled)
                        }
                    }
                )
            }
        }


        requestNotificationPermission()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS

            if (
                ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(permission)
            }
        }
    }
}
