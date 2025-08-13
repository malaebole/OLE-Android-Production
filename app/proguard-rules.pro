# ===== Realm =====
-keep class io.realm.** { *; }
-keep class io.realm.internal.** { *; }
-keepattributes *Annotation*
-keep class ** extends io.realm.RealmObject { *; }
-keepclassmembers class * extends io.realm.RealmObject {
    <fields>;
}
-keepclassmembers class * {
    @io.realm.annotations.RealmModule <fields>;
}

# ===== Glide =====
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** { *; }
-keep class com.bumptech.glide.** { *; }
-keep class * extends com.bumptech.glide.** { *; }
-keepattributes Signature
-keepattributes *Annotation*

# ===== AndroidPdfViewer =====
-keep class com.github.barteksc.pdfviewer.** { *; }
-keep class com.shockwave.pdfium.** { *; }
-keep class com.github.barteksc.pdfviewer.util.** { *; }

# ===== Your project models =====
-keepclassmembers class ae.oleapp.models.** { *; }

# ===== DragListView =====
-keep class com.woxthebox.draglistview.** { *; }

# ===== Firebase (BoM-managed) =====
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }

# ===== Optional: Java annotations =====
-keepattributes *Annotation*

# ===== Optional: keep line numbers for crashlogs =====
-keepattributes SourceFile,LineNumberTable



## Add project specific ProGuard rules here.
## You can control the set of applied configuration files using the
## proguardFiles setting in build.gradle.
##
## For more details, see
##   http://developer.android.com/guide/developing/tools/proguard.html
#
## If your project uses WebView with JS, uncomment the following
## and specify the fully qualified class name to the JavaScript interface
## class:
##-keepclassmembers class fqcn.of.javascript.interface.for.webview {
##   public *;
##}
#
## Uncomment this to preserve the line number information for
## debugging stack traces.
##-keepattributes SourceFile,LineNumberTable
#
## If you keep the line number information, uncomment this to
## hide the original source file name.
##-renamesourcefileattribute SourceFile
#-keep class com.woxthebox.draglistview.** { *; }
#-keepclassmembers class ae.oleapp.models.** { *; }