# Keep kotlinx.serialization serializers
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.fridgepet.app.data.remote.**$$serializer { *; }
-keepclassmembers class com.fridgepet.app.data.remote.** {
    *** Companion;
}
-keepclasseswithmembers class com.fridgepet.app.data.remote.** {
    kotlinx.serialization.KSerializer serializer(...);
}
