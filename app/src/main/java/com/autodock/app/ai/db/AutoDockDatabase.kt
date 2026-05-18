package com.autodock.app.ai.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AppUsageEntity::class], version = 1, exportSchema = false)
abstract class AutoDockDatabase : RoomDatabase() {
    abstract fun appUsageDao(): AppUsageDao

    companion object {
        @Volatile
        private var INSTANCE: AutoDockDatabase? = null

        fun getDatabase(context: Context): AutoDockDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AutoDockDatabase::class.java,
                    "autodock_ai_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
