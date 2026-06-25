package com.example.devicetoolv1

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.roundToInt

private data class FrequencyBand(
    val name: String,
    val label: String,
    val startMhz: Int,
    val stepMhz: Int,
    val channels: Int,
    val defaultChannel: Int
)

private val FrequencyBands = listOf(
    FrequencyBand("RC 2.4G", "2.4 GHz Control", 2405, 5, 16, 8),
    FrequencyBand("FPV 5.8G", "5.8 GHz Video", 5658, 20, 32, 12),
    FrequencyBand("915M", "915 MHz Telemetry", 902, 2, 27, 10),
    FrequencyBand("868M", "868 MHz Telemetry", 863, 1, 10, 5)
)

private enum class MatchStatus {
    Idle,
    Scanning,
    Ready,
    Sending,
    Matched,
    Failed
}

private fun FrequencyBand.frequencyFor(channel: Int, fineTuneKhz: Int): Float {
    return startMhz + (channel - 1) * stepMhz + fineTuneKhz / 1000f
}

private fun formatFrequencyMhz(frequencyMhz: Float): String {
    return String.format(Locale.US, "%.3f", frequencyMhz)
}

@Composable
fun FrequencyMatchingScreen(navController: NavHostController) {

    var selectedBandIndex by remember { mutableIntStateOf(0) }
    var channel by remember { mutableIntStateOf(FrequencyBands.first().defaultChannel) }
    var fineTuneKhz by remember { mutableIntStateOf(0) }
    var powerMw by remember { mutableIntStateOf(25) }
    var receiverId by remember { mutableStateOf("RX-01") }
    var status by remember { mutableStateOf(MatchStatus.Idle) }
    var linkQuality by remember { mutableFloatStateOf(0f) }
    var lastMessage by remember { mutableStateOf("Waiting for frequency command.") }
    var detectedChannel by remember { mutableIntStateOf(channel) }

    val scope = rememberCoroutineScope()
    val band = FrequencyBands[selectedBandIndex]
    val frequencyMhz = band.frequencyFor(channel, fineTuneKhz)

    AppScreen {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppHeader(
                eyebrow = "RADIO",
                title = "Frequency Matching",
                subtitle = "Scan, select, and bind the controller radio frequency."
            )

            FrequencyStatusPanel(
                status = status,
                frequencyMhz = frequencyMhz,
                linkQuality = linkQuality,
                message = lastMessage
            )

            BandSelector(
                selectedIndex = selectedBandIndex,
                onSelect = { index ->
                    selectedBandIndex = index
                    channel = FrequencyBands[index].defaultChannel
                    fineTuneKhz = 0
                    status = MatchStatus.Idle
                    linkQuality = 0f
                    detectedChannel = FrequencyBands[index].defaultChannel
                    lastMessage = "Band changed to ${FrequencyBands[index].label}."
                }
            )

            FrequencyControls(
                band = band,
                channel = channel,
                fineTuneKhz = fineTuneKhz,
                powerMw = powerMw,
                receiverId = receiverId,
                onChannelChange = {
                    channel = it
                    status = MatchStatus.Idle
                },
                onFineTuneChange = {
                    fineTuneKhz = it
                    status = MatchStatus.Idle
                },
                onPowerChange = {
                    powerMw = it
                    status = MatchStatus.Idle
                },
                onReceiverChange = {
                    receiverId = it
                    status = MatchStatus.Idle
                }
            )

            ScanResultPanel(
                detectedChannel = detectedChannel,
                detectedFrequencyMhz = band.frequencyFor(detectedChannel, 0),
                currentChannel = channel,
                onUseDetected = {
                    channel = detectedChannel
                    fineTuneKhz = 0
                    status = MatchStatus.Ready
                    lastMessage = "Detected channel copied into active selection."
                }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FrequencyActionButton(
                    label = "SCAN",
                    color = AppCyan,
                    modifier = Modifier.weight(1f)
                ) {
                    scope.launch {
                        status = MatchStatus.Scanning
                        linkQuality = 0.24f
                        lastMessage = "Scanning ${band.label}..."
                        delay(900)
                        detectedChannel = ((channel + 1).coerceAtMost(band.channels))
                        linkQuality = 0.68f
                        status = MatchStatus.Ready
                        lastMessage = "Scan complete. Best channel: CH$detectedChannel."
                    }
                }

                FrequencyActionButton(
                    label = "APPLY",
                    color = AppYellow,
                    modifier = Modifier.weight(1f)
                ) {
                    scope.launch {
                        status = MatchStatus.Sending
                        lastMessage = "Applying ${formatFrequencyMhz(frequencyMhz)} MHz..."
                        delay(500)
                        status = MatchStatus.Ready
                        linkQuality = linkQuality.coerceAtLeast(0.78f)
                        lastMessage = "Frequency applied."
                    }
                }

                FrequencyActionButton(
                    label = "BIND",
                    color = AppGreen,
                    modifier = Modifier.weight(1f)
                ) {
                    scope.launch {
                        status = MatchStatus.Sending
                        lastMessage = "Binding $receiverId..."
                        delay(700)
                        status = MatchStatus.Matched
                        linkQuality = 1f
                        lastMessage = "Matched $receiverId on CH$channel."
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }

        AppBackButton(navController)
    }
}

@Composable
private fun FrequencyStatusPanel(
    status: MatchStatus,
    frequencyMhz: Float,
    linkQuality: Float,
    message: String
) {
    val statusColor = when (status) {
        MatchStatus.Idle -> AppTextMuted
        MatchStatus.Scanning -> AppCyan
        MatchStatus.Ready -> AppYellow
        MatchStatus.Sending -> AppCyan
        MatchStatus.Matched -> AppGreen
        MatchStatus.Failed -> AppRed
    }
    val statusText = when (status) {
        MatchStatus.Idle -> "IDLE"
        MatchStatus.Scanning -> "SCANNING"
        MatchStatus.Ready -> "READY"
        MatchStatus.Sending -> "SENDING"
        MatchStatus.Matched -> "MATCHED"
        MatchStatus.Failed -> "FAILED"
    }

    AppPanel {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ACTIVE FREQUENCY",
                    color = AppTextMuted,
                    fontSize = 10.sp,
                    fontFamily = AppDisplayFont,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${formatFrequencyMhz(frequencyMhz)} MHz",
                    color = AppTextPrimary,
                    fontSize = 28.sp,
                    fontFamily = AppValueFont,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(statusColor.copy(alpha = 0.14f))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = statusText,
                    color = statusColor,
                    fontSize = 11.sp,
                    fontFamily = AppDisplayFont,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        LinkQualityMeter(
            progress = linkQuality,
            color = statusColor,
            modifier = Modifier.padding(top = 16.dp)
        )

        Text(
            text = message,
            color = AppTextMuted,
            fontSize = 12.sp,
            fontFamily = AppDisplayFont,
            lineHeight = 17.sp,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

@Composable
private fun BandSelector(
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    AppPanel {
        Text(
            text = "BAND",
            color = AppTextMuted,
            fontSize = 10.sp,
            fontFamily = AppDisplayFont,
            fontWeight = FontWeight.Bold
        )
        Column(
            modifier = Modifier.padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FrequencyBands.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowItems.forEach { band ->
                        val index = FrequencyBands.indexOf(band)
                        val selected = index == selectedIndex
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(58.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (selected) AppCyan else AppSurfaceAlt)
                                .clickable { onSelect(index) }
                                .padding(10.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Column {
                                Text(
                                    text = band.name,
                                    color = if (selected) AppBackground else AppTextPrimary,
                                    fontSize = 14.sp,
                                    fontFamily = AppDisplayFont,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Text(
                                    text = band.label,
                                    color = if (selected) AppBackground else AppTextMuted,
                                    fontSize = 10.sp,
                                    fontFamily = AppDisplayFont,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun FrequencyControls(
    band: FrequencyBand,
    channel: Int,
    fineTuneKhz: Int,
    powerMw: Int,
    receiverId: String,
    onChannelChange: (Int) -> Unit,
    onFineTuneChange: (Int) -> Unit,
    onPowerChange: (Int) -> Unit,
    onReceiverChange: (String) -> Unit
) {
    AppPanel {
        FrequencySliderRow(
            label = "CHANNEL",
            value = "CH$channel / ${band.channels}",
            sliderValue = channel.toFloat(),
            range = 1f..band.channels.toFloat(),
            onChange = { onChannelChange(it.roundToInt().coerceIn(1, band.channels)) }
        )
        FrequencySliderRow(
            label = "FINE TUNE",
            value = "${fineTuneKhz} kHz",
            sliderValue = fineTuneKhz.toFloat(),
            range = -500f..500f,
            onChange = { onFineTuneChange(it.roundToInt()) },
            modifier = Modifier.padding(top = 14.dp)
        )
        FrequencySliderRow(
            label = "TX POWER",
            value = "${powerMw} mW",
            sliderValue = powerMw.toFloat(),
            range = 25f..1000f,
            onChange = { onPowerChange(it.roundToInt()) },
            modifier = Modifier.padding(top = 14.dp)
        )

        Text(
            text = "RECEIVER ID",
            color = AppTextMuted,
            fontSize = 10.sp,
            fontFamily = AppDisplayFont,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 18.dp)
        )
        Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("RX-01", "RX-02", "RX-03").forEach { id ->
                val selected = id == receiverId
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selected) AppGreen else AppSurfaceAlt)
                        .clickable { onReceiverChange(id) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = id,
                        color = if (selected) AppBackground else AppTextPrimary,
                        fontSize = 12.sp,
                        fontFamily = AppValueFont,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun FrequencySliderRow(
    label: String,
    value: String,
    sliderValue: Float,
    range: ClosedFloatingPointRange<Float>,
    onChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                color = AppTextMuted,
                fontSize = 10.sp,
                fontFamily = AppDisplayFont,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = value,
                color = AppTextPrimary,
                fontSize = 13.sp,
                fontFamily = AppValueFont,
                fontWeight = FontWeight.Bold
            )
        }
        Slider(
            value = sliderValue,
            onValueChange = onChange,
            valueRange = range,
            modifier = Modifier.padding(top = 2.dp),
            colors = SliderDefaults.colors(
                thumbColor = AppCyan,
                activeTrackColor = AppCyan,
                inactiveTrackColor = AppBorder
            )
        )
    }
}

@Composable
private fun ScanResultPanel(
    detectedChannel: Int,
    detectedFrequencyMhz: Float,
    currentChannel: Int,
    onUseDetected: () -> Unit
) {
    AppPanel {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SCAN RESULT",
                    color = AppTextMuted,
                    fontSize = 10.sp,
                    fontFamily = AppDisplayFont,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "CH$detectedChannel  ${formatFrequencyMhz(detectedFrequencyMhz)} MHz",
                    color = AppTextPrimary,
                    fontSize = 16.sp,
                    fontFamily = AppValueFont,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
            Button(
                onClick = onUseDetected,
                enabled = detectedChannel != currentChannel,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppSurfaceAlt,
                    disabledContainerColor = AppSurfaceAlt.copy(alpha = 0.45f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "USE",
                    color = if (detectedChannel != currentChannel) AppCyan else AppTextMuted,
                    fontSize = 12.sp,
                    fontFamily = AppDisplayFont,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FrequencyActionButton(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier.height(52.dp)
    ) {
        Text(
            text = label,
            color = AppBackground,
            fontSize = 13.sp,
            fontFamily = AppDisplayFont,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun LinkQualityMeter(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(450),
        label = "frequency_link_quality"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(10.dp)
    ) {
        drawRoundRect(
            color = AppBorder,
            size = Size(size.width, 4.dp.toPx()),
            topLeft = Offset(0f, 3.dp.toPx()),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
        )
        drawRoundRect(
            brush = Brush.horizontalGradient(listOf(AppCyan, color)),
            size = Size(size.width * animatedProgress, 4.dp.toPx()),
            topLeft = Offset(0f, 3.dp.toPx()),
            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
        )
        drawCircle(
            color = color,
            radius = 4.dp.toPx(),
            center = Offset(size.width * animatedProgress, size.height / 2f)
        )
    }
}
