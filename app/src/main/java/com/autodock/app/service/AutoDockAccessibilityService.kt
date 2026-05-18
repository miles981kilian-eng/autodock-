package com.autodock.app.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityEvent
import android.util.Log

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AutoDockAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onServiceConnected() {
        super.onServiceConnected()
        val info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        info.flags = AccessibilityServiceInfo.DEFAULT
        this.serviceInfo = info
        Log.d("AutoDockAccessibility", "Accessibility Service Connected!")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Run completely off the main UI thread to prevent stutter
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val packageName = event.packageName?.toString()
            if (packageName != null && packageName != "com.autodock.app") {
                serviceScope.launch {
                    Log.d("AutoDockAccessibility", "User opened: $packageName")
                    // Pipe this into PredictionEngine.recordAppLaunch(packageName)
                }
            }
        }
    }

    override fun onInterrupt() {
        Log.d("AutoDockAccessibility", "Service Interrupted")
    }
    
    // Example global action trigger for custom gestures
    fun triggerRecentApps() {
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS)
    }
}
