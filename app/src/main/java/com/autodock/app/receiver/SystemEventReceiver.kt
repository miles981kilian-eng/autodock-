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
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                Log.d("AutoDock", "System event: $action. Starting AutomationService...")
                
                val serviceIntent = Intent(context, com.autodock.app.service.AutomationService::class.java)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }

                // Schedule watchdog
                val workRequest = androidx.work.PeriodicWorkRequestBuilder<com.autodock.app.automation.AutomationWorker>(
                    15, java.util.concurrent.TimeUnit.MINUTES
                ).build()
                
                androidx.work.WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    "AutomationWatchdog",
                    androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                    workRequest
                )
            }
        }
    }
}
