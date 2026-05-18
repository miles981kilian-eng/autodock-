package com.autodock.app.automation

import android.content.Context
import android.content.Intent
import android.util.Log

object AutomationEngine {

    fun evaluateEvent(context: Context, event: String) {
        Log.d("AutomationEngine", "Evaluating event: $event")
        val prefs = context.getSharedPreferences("AutoDockPrefs", Context.MODE_PRIVATE)
        
        when (event) {
            "BLUETOOTH_CONNECTED" -> {
                val isSpotifyEnabled = prefs.getBoolean("spotify_auto", true)
                if (isSpotifyEnabled) {
                    executeAction(context, "OPEN_SPOTIFY")
                } else {
                    logFailed(context, "Spotify automation disabled by user.")
                }
            }
            "BATTERY_LOW" -> {
                executeAction(context, "TURN_ON_BATTERY_SAVER")
            }
        }
    }

    private fun executeAction(context: Context, action: String) {
        Log.d("AutomationEngine", "Executing Action: $action")
        when (action) {
            "OPEN_SPOTIFY" -> {
                try {
                    val intent = context.packageManager.getLaunchIntentForPackage("com.spotify.music")
                    if (intent != null) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(intent)
                        logSuccess(context, "Launched Spotify")
                    } else {
                        logFailed(context, "Spotify not installed")
                    }
                } catch (e: Exception) {
                    logFailed(context, "Error: ${e.message}")
                }
            }
            "TURN_ON_BATTERY_SAVER" -> {
                logSuccess(context, "Battery saver triggered")
            }
        }
    }

    private fun logSuccess(context: Context, msg: String) {
        val prefs = context.getSharedPreferences("AutoDockPrefs", Context.MODE_PRIVATE)
        prefs.edit().putString("last_successful_automation", msg).apply()
        Log.d("AutomationEngine", "SUCCESS: $msg")
    }

    private fun logFailed(context: Context, msg: String) {
        val prefs = context.getSharedPreferences("AutoDockPrefs", Context.MODE_PRIVATE)
        prefs.edit().putString("last_failed_automation", msg).apply()
        Log.e("AutomationEngine", "FAILED: $msg")
    }
}
