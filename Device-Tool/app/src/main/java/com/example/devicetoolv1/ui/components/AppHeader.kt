package com.example.devicetoolv1.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.devicetoolv1.ui.theme.*

@Composable
fun AppHeader(
    title: String,
    eyebrow: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = eyebrow,
            color = AppCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = AppDisplayFont,
            letterSpacing = 0.sp
        )
        Text(
            text = title,
            color = AppTextPrimary,
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            fontFamily = AppDisplayFont,
            letterSpacing = 0.sp
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                color = AppTextMuted,
                fontSize = 13.sp,
                fontFamily = AppDisplayFont,
                lineHeight = 18.sp,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
        HorizontalDivider(
            color = AppBorder,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
fun AppScreen(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        content = content
    )
}

@Composable
fun AppPanel(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(AppSurface)
            .padding(16.dp),
        content = content
    )
}

@Composable
fun EmptyStatePanel(
    title: String,
    message: String,
    accent: Color = AppCyan,
    modifier: Modifier = Modifier
) {
    AppPanel(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(Brush.horizontalGradient(listOf(accent, accent.copy(alpha = 0.15f))))
        )
        Text(
            text = title,
            color = AppTextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = AppDisplayFont,
            modifier = Modifier.padding(top = 18.dp)
        )
        Text(
            text = message,
            color = AppTextMuted,
            fontSize = 14.sp,
            fontFamily = AppDisplayFont,
            lineHeight = 20.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun AppBackButton(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { navController.popBackStack() },
        colors = ButtonDefaults.buttonColors(containerColor = AppSurfaceAlt),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Text(
            text = "BACK",
            color = AppTextMuted,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = AppDisplayFont,
            letterSpacing = 0.sp,
            textAlign = TextAlign.Center
        )
    }
}
