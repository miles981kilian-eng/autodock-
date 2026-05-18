package com.autodock.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.autodock.app.R
import com.autodock.app.automation.AutomationEngine

class AutomationService : Service() {

    private val CHANNEL_ID = "autodock_automation_channel"

    private val dynamicReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    Log.d("AutomationService", "Bluetooth Connected dynamically")
                    AutomationEngine.evaluateEvent(context, "BLUETOOTH_CONNECTED")
                }
                Intent.ACTION_BATTERY_LOW -> {
                    Log.d("AutomationService", "Battery Low dynamically")
                    AutomationEngine.evaluateEvent(context, "BATTERY_LOW")
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1001, buildNotification())
        
        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(Intent.ACTION_BATTERY_LOW)
        }
        registerReceiver(dynamicReceiver, filter)
        Log.d("AutomationService", "AutomationService Started and Receiver Registered")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // START_STICKY ensures the OS tries to recreate the service if it is killed.
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(dynamicReceiver)
        Log.d("AutomationService", "AutomationService Destroyed")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "AutoDock Automation Engine",
                NotificationManager.IMPORTANCE_MIN
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(serviceChannel)
        }
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("AutoDock Intelligence Engine")
            .setContentText("Listening for triggers in background...")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // Placeholder icon
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()
    }
}
