package com.autodock.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.autodock.app.automation.AutomationEngine

class SystemEventReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d("AutoDock", "Received system event: $action")

        when (action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                Log.d("AutoDock", "Boot completed! Initializing dock if permissions exist.")
                // Start DockService
            }
            "android.bluetooth.device.action.ACL_CONNECTED" -> {
                Log.d("AutoDock", "Bluetooth connected!")
                AutomationEngine.evaluateEvent("BLUETOOTH_CONNECTED")
            }
        }
    }
}
