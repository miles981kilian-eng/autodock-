# AutoDock Proguard Rules

# Keep Compose Components
-keep class androidx.compose.** { *; }

# Keep Room Database Classes
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep class com.autodock.app.ai.db.** { *; }

# Keep Service Signatures intact for the OS
-keep class com.autodock.app.service.** { *; }

# Prevent Warning Spam during Build
-dontwarn androidx.**
-dontwarn kotlinx.coroutines.**
