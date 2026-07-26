# Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Room / SQLite (not used, but safe)
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.**

# Keep WiFi model
-keep class io.github.evenlove77.wifilens.data.model.** { *; }

# General
-keepattributes Signature
-keepattributes *Annotation*
