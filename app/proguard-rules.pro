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
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile
-keep class android.content.Intent { *; }
-keep class android.net.Uri { *; }
-keep class com.huytran.goodlife.pages.home.HomeActivity { *; }
# Giữ lại Activity Dietary
-keep class com.huytran.goodlife.pages.dietary.DietaryActivity { *; }

# Giữ lại ViewPager Adapter
-keep class com.huytran.goodlife.adapter.VPAdapter { *; }

# Giữ lại các Fragment được sử dụng trong Dietary
-keep class com.huytran.goodlife.fragment.FoodFragment { *; }
-keep class com.huytran.goodlife.fragment.GroceriesFragment { *; }
-keep class com.huytran.goodlife.fragment.DrinksFragment { *; }
-keep class com.huytran.goodlife.fragment.DiaryFragment { *; }

# Nếu bạn dùng ViewBinding hoặc DataBinding
-keep class **.databinding.* { *; }
-keep class **.viewbinding.* { *; }

# Optional: giữ toàn bộ gói nếu vẫn lỗi
-keep class com.huytran.goodlife.** { *; }

-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
}

-keep public class * extends androidx.fragment.app.Fragment
-keepclassmembers class * extends androidx.fragment.app.Fragment { public <init>(); }


-dontwarn android.os.ServiceManager*
-dontwarn com.bun.miitmdid.core.MdidSdkHelper*
-dontwarn com.bun.miitmdid.interfaces.IIdentifierListener*
-dontwarn com.bun.miitmdid.interfaces.IdSupplier*
-dontwarn com.google.firebase.iid.FirebaseInstanceId*
-dontwarn com.google.firebase.iid.InstanceIdResult*
-dontwarn com.huawei.hms.ads.identifier.AdvertisingIdClient$Info*
-dontwarn com.huawei.hms.ads.identifier.AdvertisingIdClient*
-dontwarn com.tencent.android.tpush.otherpush.OtherPushClient*