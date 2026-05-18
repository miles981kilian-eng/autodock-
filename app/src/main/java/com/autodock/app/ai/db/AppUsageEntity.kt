package com.autodock.app.ai.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_usage")
data class AppUsageEntity(
    @PrimaryKey val packageName: String,
    val launchCount: Int,
    val lastLaunchedTimestamp: Long,
    val associatedBluetoothDevice: String? = null // For predicting Maps when "Car" connects
)
