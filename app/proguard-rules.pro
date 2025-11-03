# Add project specific ProGuard rules here.
# By default, the flags in the accompanying file are applied.
# See http://developer.android.com/tools/proguard/tips.html for more details.

-dontwarn com.google.android.gms.**
-keep class com.google.android.gms.location.** { *; }
