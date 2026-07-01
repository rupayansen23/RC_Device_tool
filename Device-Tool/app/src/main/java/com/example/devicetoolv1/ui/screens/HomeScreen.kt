package com.example.devicetoolv1.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.devicetoolv1.ui.theme.AppBackground
import com.example.devicetoolv1.ui.components.AppHeader
import com.example.devicetoolv1.ui.components.MenuButton

@Composable
fun HomeScreen(navController: NavHostController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AppHeader(
            eyebrow = "GROUND STATION",
            title = "Device Tool",
            subtitle = "Controller, video, channel, and setup tools in one panel."
        )

        Box(modifier = Modifier.padding(top = 10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MenuButton(
                title = "CHANNEL MONITORING",
                subtitle = "16 channel output",
                modifier = Modifier.weight(1f)
            ) {

                navController.navigate("channel_monitoring")
            }

            MenuButton(
                title = "VIDEO VIEWING",
                subtitle = "Live FPV stream",
                modifier = Modifier.weight(1f)
            ) {

                navController.navigate("video_viewing")
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            MenuButton(
                title = "ADVANCED OPTIONS",
                subtitle = "Device setup",
                modifier = Modifier.weight(1f)
            ) {

                navController.navigate("advanced_options")
            }

            MenuButton(
                title = "FREQUENCY MATCHING",
                subtitle = "Radio pairing tools",
                modifier = Modifier.weight(1f)
            ) {

                navController.navigate("frequency_matching")
            }
        }
    }
}
