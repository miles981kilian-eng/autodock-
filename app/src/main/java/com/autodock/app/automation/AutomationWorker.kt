package com.autodock.app.automation

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.autodock.app.service.AutomationService

class AutomationWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("AutomationWorker", "Watchdog checking if AutomationService is alive...")
        
        try {
            val serviceIntent = Intent(appContext, AutomationService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                appContext.startForegroundService(serviceIntent)
            } else {
                appContext.startService(serviceIntent)
            }
            Log.d("AutomationWorker", "Watchdog revived AutomationService.")
        } catch (e: Exception) {
            Log.e("AutomationWorker", "Watchdog failed to revive service: ${e.message}")
        }
        
        return Result.success()
    }
}
