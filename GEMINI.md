# Fridge Pet — Project Rules (`GEMINI.md`)

You are pair-programming with a first-time app developer building **Fridge Pet**, an Android food-waste companion app. Follow these rules on every task in this repository.

## 1. Workflow & Beginner-Safe Habits
- **Plan before coding**: For any new feature, screen, or refactor, briefly explain the plan and list the files you will touch before editing. Wait for confirmation on multi-file changes when scope is uncertain.
- **One feature per task**: Keep edits focused on a single screen, data model, or bug. Avoid sweeping refactors across unrelated files.
- **Explain the "why"**: Briefly explain Android/Kotlin concepts (e.g., `StateFlow`, Room migrations, `WorkManager`) in plain language when introducing them.
- **Verify after changes**: Run the relevant build or tests after edits and report only observed results.

## 2. Tech Stack & Architecture
- **Language & UI**: Kotlin + Jetpack Compose + Material 3 (`android/`).
- **Architecture**: Clean MVVM with unidirectional data flow (`UI Screen` -> `FridgeViewModel` `StateFlow` -> `FridgeRepository` -> `Room DAO` / `FirebaseAiClient`).
- **Local Storage**: Room (`food_items`, `pet_profile`, `waste_events`) as the single source of truth.
- **Background Work**: `WorkManager` (`ExpiryCheckWorker`) for periodic expiry notifications.
- **Backend AI Layer**: Call Gemini exclusively through the backend (`firebase/functions/` using `@google/genai` with `gemini-3.8-flash` / `gemini-3.5-flash-lite`) or Firebase AI Logic with App Check.

## 3. Security & Privacy (Non-Negotiable)
- **Never put API keys, secrets, or service-account JSONs inside `android/`** (not in Kotlin files, `strings.xml`, `gradle.properties`, or `BuildConfig`).
- **Never commit** `.env`, `local.properties`, `*.jks` / `*.keystore`, or `google-services.json` if it contains restricted credentials unless intentionally managed.
- **Minimal permissions**: Only request `CAMERA` when scanning and `POST_NOTIFICATIONS` after onboarding.

## 4. Product & UX Guardrails (v1 Scope)
- **Leave out of v1**: user accounts/login, barcode scanning, social feeds, and household sharing.
- **UAE Bilingual Receipts**: Expect mixed Arabic + English receipt text, abbreviations (e.g., `CHKN BRST 1KG`), and **AED** currency. Always let users edit extracted items on the Review screen before saving.
- **Food Safety**: AI expiry dates are estimates. Always keep the `"check before eating"` safety note visible on Review, Inventory, and Rescue Recipe screens.
- **Empty, Loading, and Error States**: Every screen must handle empty data, network/AI loading, and offline fallback gracefully.
