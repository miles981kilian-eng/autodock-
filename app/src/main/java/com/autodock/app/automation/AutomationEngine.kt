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
                    Log.d("AutomationEngine", "Spotify automation is disabled by user.")
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
                        Log.d("AutomationEngine", "Successfully launched Spotify.")
                    } else {
                        Log.e("AutomationEngine", "Spotify is not installed.")
                    }
                } catch (e: Exception) {
                    Log.e("AutomationEngine", "Failed to launch Spotify: ${e.message}")
                }
            }
            "TURN_ON_BATTERY_SAVER" -> {
                Log.d("AutomationEngine", "Battery saver action triggered.")
            }
        }
    }
}
