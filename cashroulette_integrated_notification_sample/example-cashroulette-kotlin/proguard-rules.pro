# AVATYE SDK & SUPPORT
-keep class com.avatye.cashroulette.** { *; }
-keep class com.avatye.library.support.** { *; }
-keep interface com.avatye.cashroulette.** { *; }
-keep interface com.avatye.library.support.** { *; }
## IGAWORKS
-keep class com.igaworks.** { *; }
-dontwarn com.igaworks.**
-keep class com.igaworks.gson.stream.** { *; }
-keep class com.igaworks.adbrix.model.** { *; }
## UnityAds
-keep class com.unity3d.ads.** { *; }
-keep class com.unity3d.services.** { *; }
-dontwarn com.google.ar.core.**
# Vungle
-dontwarn com.vungle.warren.downloader.DownloadRequestMediator$Status
-dontwarn com.vungle.warren.error.VungleError$ErrorCode
-dontwarn com.google.android.gms.common.GoogleApiAvailabilityLight
-dontwarn com.google.android.gms.ads.identifier.AdvertisingIdClient
-dontwarn com.google.android.gms.ads.identifier.AdvertisingIdClient$Info
-keep class com.moat.** { *; }
-dontwarn com.moat.**
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn okhttp3.internal.platform.ConscryptPlatform
# AdColony - Mediation
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-dontwarn android.app.Activity
-dontwarn com.httpmodule.**
-dontwarn com.imgmodule.**
-keep class com.httpmodule.** { *; }
-keep class com.imgmodule.** { *; }
-keep public class com.mobon.** { public *; }
## FAN
-keep class com.facebook.ads.** { *; }
## Channel Talk
-dontwarn com.zoyi.**
-keep class com.google.zxing.** { *; }
-keep class com.zoyi.** { *; }
## JODA-DATETIME
-dontwarn org.joda.convert.FromString
-dontwarn org.joda.convert.ToString
-keepnames class org.joda.** implements java.io.Serializable
-keepclassmembers class org.joda.** implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}