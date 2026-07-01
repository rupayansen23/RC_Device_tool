package com.example.devicetoolv1.model

enum class ControlAxis {
    Roll,
    Pitch,
    Throttle,
    Yaw
}

data class StickModePreset(
    val country: String,
    val mode: String,
    val leftHorizontal: ControlAxis,
    val leftVertical: ControlAxis,
    val rightHorizontal: ControlAxis,
    val rightVertical: ControlAxis
)

val StickModePresets = listOf(
    StickModePreset("USA",    "MODE 2", ControlAxis.Yaw,  ControlAxis.Throttle, ControlAxis.Roll, ControlAxis.Pitch),
    StickModePreset("JAPAN",  "MODE 1", ControlAxis.Yaw,  ControlAxis.Pitch,    ControlAxis.Roll, ControlAxis.Throttle),
    StickModePreset("EUROPE", "MODE 3", ControlAxis.Roll, ControlAxis.Throttle, ControlAxis.Yaw,  ControlAxis.Pitch),
    StickModePreset("CHINA",  "MODE 4", ControlAxis.Roll, ControlAxis.Pitch,    ControlAxis.Yaw,  ControlAxis.Throttle)
)

fun ControlAxis.shortName(): String = when (this) {
    ControlAxis.Roll     -> "R"
    ControlAxis.Pitch    -> "P"
    ControlAxis.Throttle -> "T"
    ControlAxis.Yaw      -> "Y"
}

fun ControlAxis.displayName(): String = when (this) {
    ControlAxis.Roll     -> "ROLL"
    ControlAxis.Pitch    -> "PITCH"
    ControlAxis.Throttle -> "THROTTLE"
    ControlAxis.Yaw      -> "YAW"
}
