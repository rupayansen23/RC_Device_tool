package com.example.devicetoolv1.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.devicetoolv1.ui.components.AppScreen
import com.example.devicetoolv1.ui.components.AppHeader

@Composable
fun SettingsScreen(navController: NavHostController) {
    AppScreen {
        AppHeader(
            eyebrow = "SYSTEM",
            title = "Settings",
            subtitle = "Configure application and device preferences."
        )
    }
}
