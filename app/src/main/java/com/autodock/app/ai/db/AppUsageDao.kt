package com.autodock.app.ai.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AppUsageDao {
    @Query("SELECT * FROM app_usage ORDER BY launchCount DESC LIMIT 5")
    suspend fun getTopPredictedApps(): List<AppUsageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordAppLaunch(usage: AppUsageEntity)

    @Query("SELECT * FROM app_usage WHERE packageName = :packageName")
    suspend fun getAppUsage(packageName: String): AppUsageEntity?
}
