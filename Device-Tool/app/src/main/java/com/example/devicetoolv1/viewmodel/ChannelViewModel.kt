package com.example.devicetoolv1.viewmodel

import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.util.Locale
import kotlin.math.abs
import com.example.devicetoolv1.model.*

private data class StickAxisMapping(
    val leftX: Int,
    val leftY: Int,
    val rightX: Int,
    val rightY: Int
)

fun channelValueFromMovement(movement: Float): Int =
    (1500 + movement.coerceIn(-1f, 1f) * 500).toInt().coerceIn(1000, 2000)

// All candidate axis mappings tried in order — the one with the most movement wins.
private val candidateMappings = listOf(
    StickAxisMapping(MotionEvent.AXIS_X,     MotionEvent.AXIS_Y,   MotionEvent.AXIS_Z,        MotionEvent.AXIS_RZ),
    StickAxisMapping(MotionEvent.AXIS_X,     MotionEvent.AXIS_Y,   MotionEvent.AXIS_RX,       MotionEvent.AXIS_RY),
    StickAxisMapping(MotionEvent.AXIS_X,     MotionEvent.AXIS_Y,   MotionEvent.AXIS_HAT_X,    MotionEvent.AXIS_HAT_Y),
    StickAxisMapping(MotionEvent.AXIS_X,     MotionEvent.AXIS_Y,   MotionEvent.AXIS_THROTTLE, MotionEvent.AXIS_RUDDER),
    StickAxisMapping(MotionEvent.AXIS_X,     MotionEvent.AXIS_Y,   MotionEvent.AXIS_RUDDER,   MotionEvent.AXIS_THROTTLE),
    StickAxisMapping(MotionEvent.AXIS_X,     MotionEvent.AXIS_Y,   MotionEvent.AXIS_GAS,      MotionEvent.AXIS_BRAKE),
    StickAxisMapping(MotionEvent.AXIS_HAT_X, MotionEvent.AXIS_HAT_Y, MotionEvent.AXIS_Z,     MotionEvent.AXIS_RZ),
    StickAxisMapping(MotionEvent.AXIS_RX,    MotionEvent.AXIS_RY,  MotionEvent.AXIS_Z,        MotionEvent.AXIS_RZ)
)

private val usefulAxes = setOf(
    MotionEvent.AXIS_X,
    MotionEvent.AXIS_Y,
    MotionEvent.AXIS_Z,
    MotionEvent.AXIS_RZ,
    MotionEvent.AXIS_RX,
    MotionEvent.AXIS_RY,
    MotionEvent.AXIS_HAT_X,
    MotionEvent.AXIS_HAT_Y,
    MotionEvent.AXIS_THROTTLE,
    MotionEvent.AXIS_RUDDER,
    MotionEvent.AXIS_GAS,
    MotionEvent.AXIS_BRAKE
)

object ChannelViewModel {

    var selectedModeIndex by mutableIntStateOf(0)
        private set

    var leftX  by mutableFloatStateOf(0f); private set
    var leftY  by mutableFloatStateOf(0f); private set
    var rightX by mutableFloatStateOf(0f); private set
    var rightY by mutableFloatStateOf(0f); private set

    var ch1 by mutableIntStateOf(1500); private set
    var ch2 by mutableIntStateOf(1500); private set
    var ch3 by mutableIntStateOf(1000); private set
    var ch4 by mutableIntStateOf(1500); private set
    var ch5 by mutableIntStateOf(1500); private set
    var ch6 by mutableIntStateOf(1500); private set
    var ch7 by mutableIntStateOf(1500); private set
    var ch8 by mutableIntStateOf(1500); private set
    var ch9 by mutableIntStateOf(1500); private set
    var ch10 by mutableIntStateOf(1500); private set
    var ch11 by mutableIntStateOf(1500); private set
    var ch12 by mutableIntStateOf(1500); private set

    var inputEventCount  by mutableIntStateOf(0);                                    private set
    var lastInputDevice  by mutableStateOf("No Android joystick event yet");         private set
    var lastInputSource  by mutableStateOf("none");                                  private set
    var lastAxisSummary  by mutableStateOf("Move the H12 sticks to detect axes.");   private set
    var androidInputDevices by mutableStateOf("Scanning Android input devices..."); private set

    val selectedMode: StickModePreset
        get() = StickModePresets[selectedModeIndex]

    fun selectMode(index: Int) {
        selectedModeIndex = index.coerceIn(0, StickModePresets.lastIndex)
        updateChannels()
    }

    fun updateFromMotionEvent(event: MotionEvent): Boolean {
        // Only process move-type actions
        if (event.action != MotionEvent.ACTION_MOVE &&
            event.action != MotionEvent.ACTION_HOVER_MOVE
        ) return false

        val hasAnyAxisMovement = usefulAxes.any { axis ->
            abs(event.getAxisValue(axis)) > 0.005f
        }
        if (!hasAnyAxisMovement) return false

        val mapping = bestMappingFor(event)

        leftX  = event.centeredAxis(mapping.leftX)
        leftY  = event.centeredAxis(mapping.leftY)
        rightX = event.centeredAxis(mapping.rightX)
        rightY = event.centeredAxis(mapping.rightY)

        inputEventCount += 1
        lastInputDevice = event.device?.name ?: "Unknown device"
        lastInputSource = "0x${event.source.toString(16)}"
        lastAxisSummary = event.axisSummary()

        updateChannels()
        return true
    }

    fun updateFromKeyEvent(event: KeyEvent): Boolean {
        val device = event.device ?: return false
        if (!event.isControllerLike()) return false

        inputEventCount += 1
        lastInputDevice = device.name ?: "Unknown device"
        lastInputSource = "0x${event.source.toString(16)}"
        lastAxisSummary = "Controller key ${KeyEvent.keyCodeToString(event.keyCode)} ${event.actionName()}"

        return false // don't consume — let system handle volume keys etc.
    }

    fun refreshAndroidInputDevices() {
        val devices = InputDevice.getDeviceIds()
            .toList()
            .mapNotNull { id -> InputDevice.getDevice(id) }

        androidInputDevices = if (devices.isEmpty()) {
            "No Android input devices found."
        } else {
            devices.joinToString("\n") { device ->
                val axes = device.motionRanges
                    .joinToString(", ") { range -> MotionEvent.axisToString(range.axis) }
                    .ifBlank { "no axes" }
                val src = device.sources.toSourceSummary()
                "${device.name}  src=$src  axes=$axes"
            }
        }
    }

    fun currentChannels(): List<Int> = listOf(ch1, ch2, ch3, ch4, ch5, ch6, ch7, ch8, ch9, ch10, ch11, ch12)

    private fun updateChannels() {
        ch1 = 1500; ch2 = 1500; ch3 = 1000; ch4 = 1500
        ch5 = 1500; ch6 = 1500; ch7 = 1500; ch8 = 1500
        ch9 = 1500; ch10 = 1500; ch11 = 1500; ch12 = 1500

        assignAxis(selectedMode.leftHorizontal,  leftX)
        assignAxis(selectedMode.leftVertical,    -leftY)
        assignAxis(selectedMode.rightHorizontal, rightX)
        assignAxis(selectedMode.rightVertical,   -rightY)
    }

    private fun assignAxis(axis: ControlAxis, value: Float) {
        val v = channelValueFromMovement(value)
        when (axis) {
            ControlAxis.Roll     -> ch1 = v
            ControlAxis.Pitch    -> ch2 = v
            ControlAxis.Throttle -> ch3 = v
            ControlAxis.Yaw      -> ch4 = v
        }
    }

    private fun bestMappingFor(event: MotionEvent): StickAxisMapping =
        candidateMappings.maxBy { m ->
            abs(event.centeredAxis(m.leftX))  +
                    abs(event.centeredAxis(m.leftY))  +
                    abs(event.centeredAxis(m.rightX)) +
                    abs(event.centeredAxis(m.rightY))
        }
}

private fun MotionEvent.centeredAxis(axis: Int): Float {
    val range = device?.getMotionRange(axis, source)
    val value = getAxisValue(axis)
    val flat  = range?.flat ?: 0.04f
    val centered = if (range != null && range.min >= 0f && range.max > range.min) {
        val center = (range.min + range.max) / 2f
        val radius = (range.max - range.min) / 2f
        if (radius == 0f) 0f else (value - center) / radius
    } else {
        value
    }
    return if (abs(centered) <= flat) 0f else centered.coerceIn(-1f, 1f)
}

private fun MotionEvent.axisSummary(): String {
    val axes = listOf(
        "X"     to MotionEvent.AXIS_X,
        "Y"     to MotionEvent.AXIS_Y,
        "Z"     to MotionEvent.AXIS_Z,
        "RZ"    to MotionEvent.AXIS_RZ,
        "RX"    to MotionEvent.AXIS_RX,
        "RY"    to MotionEvent.AXIS_RY,
        "HAT_X" to MotionEvent.AXIS_HAT_X,
        "HAT_Y" to MotionEvent.AXIS_HAT_Y,
        "THR"   to MotionEvent.AXIS_THROTTLE,
        "RUD"   to MotionEvent.AXIS_RUDDER,
        "GAS"   to MotionEvent.AXIS_GAS,
        "BRK"   to MotionEvent.AXIS_BRAKE
    )
    return axes.joinToString("  ") { (name, axis) ->
        "$name:${String.format(Locale.US, "%.2f", getAxisValue(axis))}"
    }
}

private fun InputDevice.supportsSource(source: Int) = sources and source == source

private fun MotionEvent.isControllerLike() =
    isFromSource(InputDevice.SOURCE_JOYSTICK) ||
            isFromSource(InputDevice.SOURCE_GAMEPAD)  ||
            isFromSource(InputDevice.SOURCE_DPAD)

private fun KeyEvent.isControllerLike() =
    isFromSource(InputDevice.SOURCE_JOYSTICK) ||
            isFromSource(InputDevice.SOURCE_GAMEPAD)  ||
            isFromSource(InputDevice.SOURCE_DPAD)

private fun KeyEvent.actionName() = when (action) {
    KeyEvent.ACTION_DOWN     -> "down"
    KeyEvent.ACTION_UP       -> "up"
    KeyEvent.ACTION_MULTIPLE -> "multiple"
    else                     -> action.toString()
}

private fun Int.toSourceSummary(): String {
    val names = buildList {
        if (this@toSourceSummary and InputDevice.SOURCE_JOYSTICK  == InputDevice.SOURCE_JOYSTICK)  add("JOYSTICK")
        if (this@toSourceSummary and InputDevice.SOURCE_GAMEPAD   == InputDevice.SOURCE_GAMEPAD)   add("GAMEPAD")
        if (this@toSourceSummary and InputDevice.SOURCE_DPAD      == InputDevice.SOURCE_DPAD)      add("DPAD")
        if (this@toSourceSummary and InputDevice.SOURCE_TOUCHSCREEN == InputDevice.SOURCE_TOUCHSCREEN) add("TOUCH")
        if (this@toSourceSummary and InputDevice.SOURCE_MOUSE     == InputDevice.SOURCE_MOUSE)     add("MOUSE")
        if (this@toSourceSummary and InputDevice.SOURCE_KEYBOARD  == InputDevice.SOURCE_KEYBOARD)  add("KEYBOARD")
    }
    return if (names.isEmpty()) "0x${toString(16)}"
    else "${names.joinToString("+")} (0x${toString(16)})"
}
