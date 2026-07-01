package com.example.devicetoolv1.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.devicetoolv1.ui.theme.*

@Composable
fun StatusCard(
    title: String,
    value: String,
    progress: Float,
    color: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                color = AppSurface,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = title,
            color = AppTextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = AppDisplayFont
        )
        Text(
            text = value,
            color = color,
            fontSize = 22.sp,
            fontFamily = AppValueFont,
            modifier = Modifier.padding(top = 8.dp)
        )
        val stableProgress = progress
        LinearProgressIndicator(
            progress = { stableProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .padding(top = 12.dp),
            color = color,
            trackColor = AppBorder
        )
    }
}
