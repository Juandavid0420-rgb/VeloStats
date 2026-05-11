package com.example.proyecto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.ui.Modifier
import com.example.proyecto.ui.ViewModels.SettingsViewModel
import com.example.proyecto.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel by viewModels<SettingsViewModel>()

            AppTheme(
                viewModel = settingsViewModel,
                dynamicColor = true,
                content = {
                    NavigationStack(
                        modifier = Modifier,
                        viewModel = settingsViewModel
                    )
                }

            )
        }
    }
}



