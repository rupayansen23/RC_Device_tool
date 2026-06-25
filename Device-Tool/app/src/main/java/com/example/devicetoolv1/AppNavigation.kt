package com.example.devicetoolv1

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    // Refresh device list every 300 ms.
    LaunchedEffect(Unit) {
        while (true) {
            HardwareControllerState.refreshAndroidInputDevices()
            delay(300)
        }
    }

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home")               { HomeScreen(navController) }
        composable("channel_monitoring") { ChannelMonitoringScreen(navController) }
        composable("video_viewing")      { VideoViewingScreen(navController) }
        composable("advanced_options")   { AdvancedOptionsScreen(navController) }
        composable("frequency_matching") { FrequencyMatchingScreen(navController) }
    }
}