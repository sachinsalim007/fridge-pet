---
name: android-release-deployment
description: >-
  Guides configuring Gradle signing, ProGuard/R8 obfuscation, app icons, splash screens,
  and building release Android App Bundles (AAB) or signed APKs for distribution. Use during Phase 9.
---

# Android Release & Deployment Guide

## Release Build Checklist
1. **App Icon & Splash Screen**:
   - Adaptive Icon (`mipmap-anydpi-v26`) with 108dp foreground and background layer.
   - Core splash screen API (`androidx.core.splashscreen.SplashScreen`).
2. **ProGuard / R8 Optimization**:
   - Ensure `isMinifyEnabled = true` and `isShrinkResources = true` in `build.gradle.kts` release block.
   - Verify `proguard-rules.pro` retains kotlinx.serialization, Room, and OkHttp serializers.
3. **Keystore Security**:
   - Generate release keystore using Java `keytool`:
     `keytool -genkey -v -keystore release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias fridgepet`
   - **Never commit `*.jks` or `*.keystore` into Git.** Store passwords in `local.properties` or environment variables.
4. **Build Release AAB / APK**:
   - Generate bundle: `./gradlew bundleRelease`
   - Generate standalone APK: `./gradlew assembleRelease`
