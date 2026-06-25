package com.example.devicetoolv1

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun ChannelMonitoringScreen(navController: NavHostController) {
    val hardwareChannels = HardwareControllerState.currentChannels()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Header Row - Compact for landscape screens
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "DIAGNOSTICS",
                    color = AppCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppDisplayFont
                )
                Text(
                    text = "Channel Monitor",
                    color = AppTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = AppDisplayFont
                )
            }
            
            AppBackButton(navController, modifier = Modifier.width(80.dp).height(32.dp))
        }

        // Channels Container - Uses weight to take available space and allows scrolling if needed
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // Left Column: CH1 - CH6
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (i in 0 until 6) {
                        val chNum = i + 1
                        val valInt = if (i < hardwareChannels.size) hardwareChannels[i] else 1500
                        CompactChannelRow("CH$chNum", valInt)
                    }
                }

                // Right Column: CH7 - CH12
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    for (i in 6 until 12) {
                        val chNum = i + 1
                        val valInt = if (i < hardwareChannels.size) hardwareChannels[i] else 1500
                        CompactChannelRow("CH$chNum", valInt)
                    }
                }
            }
        }
        
        // Footer - Compact
        Text(
            text = "H12 Pro Controller Hardware Input",
            color = AppTextMuted,
            fontSize = 10.sp,
            fontFamily = AppDisplayFont,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 4.dp)
        )
    }
}

@Composable
fun CompactChannelRow(name: String, value: Int) {
    val accentColor = AppCyan

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp), // Reduced height slightly to fit more screens
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            color = AppTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(36.dp)
        )

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            val progress = ((value - 1000f) / 1000f).coerceIn(0f, 1f)

            // Horizontal Track
            Canvas(modifier = Modifier.fillMaxWidth().height(1.dp)) {
                drawLine(
                    color = AppBorder,
                    start = Offset(0f, size.height / 2),
                    end = Offset(size.width, size.height / 2),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Indicator (Tooltip + Dot)
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val xPos = maxWidth * progress
                
                Column(
                    modifier = Modifier.offset(x = xPos - 18.dp, y = (-1).dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Bubble
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(accentColor)
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = value.toString(),
                            color = AppBackground,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = AppValueFont
                        )
                    }
                    // Dot
                    Canvas(modifier = Modifier.size(6.dp)) {
                        drawCircle(color = accentColor, radius = 3.dp.toPx())
                    }
                }
            }
        }
    }
}
