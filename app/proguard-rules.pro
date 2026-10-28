# ProGuard & R8 rules for MAXO Dashboard

# Keep data models intact from obfuscation/reflection issues
-keep class com.example.data.model.** { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Coroutines
-keepclassmembers class kotlinx.coroutines.** { *; }

# Moshi / JSON
-keepclassmembers class * {
    @com.squareup.moshi.* <fields>;
    @com.squareup.moshi.* <methods>;
}
