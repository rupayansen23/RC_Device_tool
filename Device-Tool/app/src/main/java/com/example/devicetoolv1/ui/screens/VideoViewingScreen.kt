package com.example.devicetoolv1.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.example.devicetoolv1.ui.theme.*

@Composable
fun VideoViewingScreen(navController: NavHostController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Compact Header Row to maximize video space
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "LIVE",
                    color = AppCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppDisplayFont
                )
                Text(
                    text = "FPV STREAM",
                    color = AppTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = AppDisplayFont
                )
            }

            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = AppSurfaceAlt),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = "BACK",
                    color = AppTextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = AppDisplayFont
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(AppSurface)
        ) {
            // WebView renders the MJPEG stream exactly like a browser does
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        settings.builtInZoomControls = false

                        // REPLACE WITH YOUR LAPTOP IP
                        loadUrl("http://192.168.0.102:8080/video")
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
