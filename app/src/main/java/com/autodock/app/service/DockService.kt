package com.autodock.app.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView

class DockService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: FrameLayout

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        // Setup a basic MVP Floating View (Cyberpunk Dot)
        // Note: For fully Jetpack Compose in Service, ViewTreeLifecycleOwner and ViewTreeSavedStateRegistryOwner must be configured.
        // For MVP, we provide a raw glowing blue dot.
        floatingView = FrameLayout(this).apply {
            setBackgroundColor(android.graphics.Color.parseColor("#00F0FF")) // CyberBlue
            alpha = 0.8f
        }

        val params = WindowManager.LayoutParams(
            150, // Width
            150, // Height
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.CENTER_VERTICAL or Gravity.END
        params.x = 20
        params.y = 0

        val textView = TextView(this).apply {
            text = "DOCK"
            setTextColor(android.graphics.Color.BLACK)
            textSize = 10f
            gravity = Gravity.CENTER
        }
        floatingView.addView(textView)

        windowManager.addView(floatingView, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::floatingView.isInitialized) {
            windowManager.removeView(floatingView)
        }
    }
}
