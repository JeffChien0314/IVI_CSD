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

-dontwarn lombok.**
-dontwarn org.joda.**
-dontwarn kotlinx.coroutines.flow.**
-dontwarn javax.lang.**
-dontwarn javax.mail.**
-dontwarn java.lang.ClassValue
-dontwarn com.google.errorprone.**
-dontwarn com.google.common.util.concurrent.**
-dontwarn com.google.j2objc.**

-repackageclasses com.tomtom.online.sdk.common.internal
-keep public class com.tomtom.online.sdk.common.** {
    public protected *;
}