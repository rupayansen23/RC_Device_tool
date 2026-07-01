package com.example.devicetoolv1.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.devicetoolv1.ui.theme.*
import com.example.devicetoolv1.ui.components.*

@Composable
fun AdvancedOptionsScreen(navController: NavHostController) {

    AppScreen(verticalArrangement = Arrangement.spacedBy(20.dp)) {

        AppHeader(
            eyebrow = "CONFIGURATION",
            title = "Advanced Options",
            subtitle = "Device level settings will appear here as they are enabled."
        )

        EmptyStatePanel(
            title = "Ready for setup tools",
            message = "This area is styled for future receiver, failsafe, calibration, and diagnostic controls.",
            accent = AppYellow,
            modifier = Modifier.padding(top = 8.dp)
        )

        Column(modifier = Modifier.weight(1f)) {}
        AppBackButton(navController)
    }
}
