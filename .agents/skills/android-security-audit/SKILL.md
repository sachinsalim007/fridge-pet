---
name: android-security-audit
description: >-
  Audits the Android app and Firebase backend for leaked API keys, insecure network/storage usage,
  excessive permissions, and unsafe input handling. Use before committing phases or shipping an APK.
---

# Fridge Pet Security Audit

Run these checks before completing a milestone or building a release APK:

1. **Secret & Key Leak Scan**:
   - Search `android/` for patterns like `AIza`, `GEMINI_API_KEY`, `sk-`, `Bearer `, or hardcoded tokens.
   - Verify `GEMINI_API_KEY` is read only inside `firebase/functions/src/index.ts` via `defineSecret("GEMINI_API_KEY")`.
2. **Network & Transport Security**:
   - Ensure `FRIDGE_PET_API_BASE_URL` uses `https://` in production and `usesCleartextTraffic` is not enabled in `AndroidManifest.xml`.
   - Ensure `proguard-rules.pro` and `isMinifyEnabled` are reviewed before release builds.
3. **Permissions & Privacy**:
   - Confirm `AndroidManifest.xml` requests only `INTERNET`, `CAMERA`, and `POST_NOTIFICATIONS`.
   - Verify receipt/fridge photos are compressed in memory or deleted immediately from `cacheDir` after upload (never left accumulating on disk), and Gemini `store: false` is set on receipt extraction calls.
4. **Backend Validation**:
   - Confirm every Cloud Function validates request bodies with `zod` (size limits, MIME type allowlist, bounded array lengths) and returns generic error messages without stack traces.
