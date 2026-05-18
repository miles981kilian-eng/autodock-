package com.autodock.app.ai

import android.content.Context
import com.autodock.app.ai.db.AppUsageEntity
import com.autodock.app.ai.db.AutoDockDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PredictionEngine(context: Context) {
    private val dao = AutoDockDatabase.getDatabase(context).appUsageDao()

    suspend fun recordAppLaunch(packageName: String, bluetoothContext: String? = null) {
        withContext(Dispatchers.IO) {
            val existing = dao.getAppUsage(packageName)
            val count = (existing?.launchCount ?: 0) + 1
            
            dao.recordAppLaunch(
                AppUsageEntity(
                    packageName = packageName,
                    launchCount = count,
                    lastLaunchedTimestamp = System.currentTimeMillis(),
                    associatedBluetoothDevice = bluetoothContext ?: existing?.associatedBluetoothDevice
                )
            )
        }
    }

    suspend fun getPredictedApps(): List<String> {
        return withContext(Dispatchers.IO) {
            // Very simple prediction: most used apps
            dao.getTopPredictedApps().map { it.packageName }
        }
    }
}
