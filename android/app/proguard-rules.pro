# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep Firestore-mapped model classes (no-arg constructor + reflection-based field access)
-keepclassmembers class com.cynorra.lexio.model.** {
    public <init>();
    public *;
}

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
