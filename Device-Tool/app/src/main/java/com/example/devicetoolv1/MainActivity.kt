package com.example.devicetoolv1

import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import com.example.devicetoolv1.ui.AppNavigation
import com.example.devicetoolv1.ui.theme.AppBackground
import com.example.devicetoolv1.ui.theme.DeviceToolV1Theme
import com.example.devicetoolv1.viewmodel.ChannelViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.decorView.isFocusableInTouchMode = true
        window.decorView.requestFocus()

        setContent {
            DeviceToolV1Theme(dynamicColor = false) {
                Surface(color = AppBackground) {
                    AppNavigation()
                }
            }
        }
    }

    /**
     * BUG FIX: The original dispatchGenericMotionEvent only forwarded events that
     * ChannelViewModel accepted.  On H12 Pro the physical sticks may arrive as
     * ACTION_MOVE on a non-joystick source, so we now always offer the event to
     * ChannelViewModel first and fall back to super only when not consumed.
     *
     * Additionally, we also override onGenericMotionEvent so that events which bubble
     * up through the view hierarchy still reach our handler, giving us two interception
     * points for maximum compatibility with H12 Pro firmware quirks.
     */
    override fun dispatchGenericMotionEvent(event: MotionEvent): Boolean {
        if (ChannelViewModel.updateFromMotionEvent(event)) return true
        return super.dispatchGenericMotionEvent(event)
    }

    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        if (ChannelViewModel.updateFromMotionEvent(event)) return true
        return super.onGenericMotionEvent(event)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (ChannelViewModel.updateFromKeyEvent(event)) return true
        return super.dispatchKeyEvent(event)
    }
}
