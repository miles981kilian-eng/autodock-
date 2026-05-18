package com.autodock.app.automation

import android.content.Context
import android.os.BatteryManager
import android.provider.Settings
import android.util.Log

object OptimizationEngine {

    fun checkDeviceHealth(context: Context) {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        
        Log.d("OptimizationEngine", "Current Battery Level: $batteryLevel%")
        
        if (batteryLevel <= 20) {
            enableBatteryEcoMode(context)
        }
    }

    private fun enableBatteryEcoMode(context: Context) {
        Log.d("OptimizationEngine", "Battery critically low. Activating Eco Mode...")
        // In a real app, we would write to Settings.System to lower brightness here
        // Requires WRITE_SETTINGS permission which we requested in manifest!
        try {
            if (Settings.System.canWrite(context)) {
                Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, 50) // Very low brightness
                Log.d("OptimizationEngine", "Brightness reduced successfully.")
            }
        } catch (e: Exception) {
            Log.e("OptimizationEngine", "Eco mode failed: Missing WRITE_SETTINGS permission")
        }
    }
}
