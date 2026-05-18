package com.autodock.app.utils

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs

object HardwareMonitor {

    fun getBatteryStats(context: Context): Pair<Int, Boolean> {
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, intentFilter)
        
        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        
        val batteryPct = if (level != -1 && scale != -1) (level * 100 / scale) else 0
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
        
        return Pair(batteryPct, isCharging)
    }

    fun getMemoryStats(context: Context): Pair<Float, Float> {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        
        val totalMemGB = memoryInfo.totalMem / (1024f * 1024f * 1024f)
        val availMemGB = memoryInfo.availMem / (1024f * 1024f * 1024f)
        val usedMemGB = totalMemGB - availMemGB
        
        return Pair(usedMemGB, totalMemGB)
    }

    fun getStorageStats(): Pair<Int, Boolean> {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        val availableBlocks = stat.availableBlocksLong
        
        val totalStorage = totalBlocks * blockSize
        val availableStorage = availableBlocks * blockSize
        val usedStorage = totalStorage - availableStorage
        
        val usedPercentage = if (totalStorage > 0) ((usedStorage.toDouble() / totalStorage) * 100).toInt() else 0
        
        return Pair(usedPercentage, usedPercentage > 90) // return true if almost full
    }
}
