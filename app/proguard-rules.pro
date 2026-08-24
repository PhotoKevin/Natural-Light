# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep the entire zstd-jni package structure intact
-keep class com.github.luben.zstd.** {
    *;
}
# Keep the class that loads the resource file from being modified
-keep class com.esri.core.**  { *; }

# Keep all native method implementations and their fields
-keepclasseswithmembernames class com.github.luben.zstd.** {
    native <methods>;
}

# Prevent stripping of fields that are accessed purely via JNI (native code)
-keepclassmembers class com.github.luben.zstd.** {
    *** *;
}


# Prevent optimization from breaking internal reflection or resources
-keepattributes InnerClasses,EnclosingMethod,Signature

# If you use a newer version of the SDK (ArcGIS Maps SDK for Kotlin/Java)
-keep class com.arcgismaps.** { *; }

-dontwarn androidx.concurrent.futures.SuspendToFutureAdapter
