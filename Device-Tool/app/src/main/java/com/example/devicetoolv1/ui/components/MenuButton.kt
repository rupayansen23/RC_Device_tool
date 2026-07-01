package com.example.devicetoolv1.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.devicetoolv1.ui.theme.*

@Composable
fun MenuButton(
    title: String,
    subtitle: String = "",
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Box(
        modifier = modifier
            .height(106.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(AppSurfaceAlt, AppSurface)
                )
            )
            .clickable {

                onClick()
            }
            .padding(14.dp),

        contentAlignment = Alignment.BottomStart
    ) {

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = title,
                color = AppTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                fontFamily = AppDisplayFont,
                lineHeight = 18.sp
            )
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    color = AppTextMuted,
                    fontSize = 11.sp,
                    fontFamily = AppDisplayFont,
                    lineHeight = 15.sp,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}
