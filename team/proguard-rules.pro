# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keep class com.baidu.** {*;}
-keep class mapsdkvi.com.** {*;}
-dontwarn com.baidu.**

-keep class com.tencent.mm.opensdk.** {
*;
}
-keep class com.tencent.wxop.** {
*;
}
-keep class com.tencent.mm.** {
*;
}
#AndPermission
-dontwarn com.yanzhenjie.permission.**
-dontwarn com.squareup.picasso.**
-dontwarn org.codehaus.mojo.**

# glide 的混淆代码
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
# banner 的混淆代码
-keep class com.youth.banner.** {
    *;
 }

-keep class com.uuzuche.lib_zxing.**{
*;
}
-keep class cn.yipianfengye.android.**{
*;
}

-keep class com.google.code.gson.**{
*;
}

-keep class com.squareup.picasso.**{
*;
}
-keep class org.jetbrains.anko.**{
*;
}
-keep class org.jetbrains.kotlin.**{
*;
}
-keep class com.android.support.**{
*;
}

-keep class com.cyf.lezu.entity.**{
*;
}
-keep class com.cyf.team.entity.**{
*;
}


#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

#okio
-dontwarn okio.**
-keep class okio.**{*;}