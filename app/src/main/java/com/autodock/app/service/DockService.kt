package com.autodock.app.service

import android.animation.ValueAnimator
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.drawable.GradientDrawable
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView

class DockService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: FrameLayout
    private lateinit var params: WindowManager.LayoutParams
    private lateinit var primaryIcon: TextView
    private var isExpanded = false

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        // Glassmorphism + Nothing OS Aesthetic
        floatingView = FrameLayout(this).apply {
            val shape = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 60f
                setColor(android.graphics.Color.parseColor("#E60A0A0A")) // 90% opaque DeepGraphite
                setStroke(4, android.graphics.Color.parseColor("#4400F0FF")) // Subtle neon blue border glow
            }
            background = shape
        }

        // FLAG_HARDWARE_ACCELERATED is enabled by default for application overlay windows,
        // but FLAG_SPLIT_TOUCH ensures multi-touch doesn't stutter on low-end devices.
        params = WindowManager.LayoutParams(
            200, // Compact Width
            200, // Compact Height
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or WindowManager.LayoutParams.FLAG_SPLIT_TOUCH,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.CENTER_VERTICAL or Gravity.END
        params.x = 20
        params.y = 0

        // Internal Layout
        val internalLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }

        primaryIcon = TextView(this).apply {
            text = "[]" // Monospace icon placeholder
            setTextColor(android.graphics.Color.parseColor("#F0F0F0")) // TextPrimary
            textSize = 24f
            typeface = android.graphics.Typeface.MONOSPACE
            gravity = Gravity.CENTER
        }
        
        internalLayout.addView(primaryIcon)
        
        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        floatingView.addView(internalLayout, layoutParams)

        setupTouchListener()

        windowManager.addView(floatingView, params)
    }

    private fun setupTouchListener() {
        // Dragging & Expansion Logic
        floatingView.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f
            private var isDragging = false

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event == null) return false
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        isDragging = false
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        // Pre-calculate to avoid object allocation during fast touch events
                        val dx = event.rawX - initialTouchX
                        val dy = event.rawY - initialTouchY
                        if (dx > 10 || dx < -10 || dy > 10 || dy < -10) {
                            isDragging = true
                            params.x = initialX - dx.toInt() // End gravity inverted
                            params.y = initialY + dy.toInt()
                            windowManager.updateViewLayout(floatingView, params)
                        }
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        if (!isDragging) {
                            toggleExpansion()
                        }
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun toggleExpansion() {
        isExpanded = !isExpanded
        val targetHeight = if (isExpanded) 600 else 200

        // Futuristic smooth UI spring animation
        val animator = ValueAnimator.ofInt(params.height, targetHeight)
        animator.duration = 300
        animator.interpolator = OvershootInterpolator(1.2f)
        animator.addUpdateListener { animation ->
            params.height = animation.animatedValue as Int
            windowManager.updateViewLayout(floatingView, params)
        }
        animator.start()

        if (isExpanded) {
            primaryIcon.text = "[PREDICTIONS]"
            primaryIcon.textSize = 12f
            primaryIcon.setTextColor(android.graphics.Color.parseColor("#00F0FF"))
        } else {
            primaryIcon.text = "[]"
            primaryIcon.textSize = 24f
            primaryIcon.setTextColor(android.graphics.Color.parseColor("#F0F0F0"))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::floatingView.isInitialized) {
            windowManager.removeView(floatingView)
        }
    }
}
