# Fridge Pet

A food-waste companion app for Android where an expressive creature lives inside your fridge, reacts to upcoming expiry dates, parses bilingual (Arabic + English) UAE grocery receipts with Gemini, and suggests rescue recipes to save food and AED.

## Project Structure

- `android/` — Kotlin + Jetpack Compose Android app
  - **UI**: 7 MVP screens (`Onboarding`, `Home`, `Scan`, `Review`, `Inventory`, `RescueRecipe`, `WeeklyReport`) + custom animated Compose Canvas `FridgePetAvatar` with 6 mood states (`Content`, `Hungry`, `Anxious`, `Feeling Sick`, `Celebrating`, `Sleepy`) and 3 evolution stages (`Frosty Sprout`, `Pantry Puffle`, `Chef Guardian`).
  - **Storage**: Room database (`food_items`, `pet_profile`, `waste_events`) for offline-first inventory and weekly AED savings.
  - **Notifications**: WorkManager periodic `ExpiryCheckWorker` alerting when items are expiring within 2 days or expired.
  - **Camera**: CameraX receipt/fridge preview with guide frame, gallery picker, and one-tap UAE sample receipt demo mode.
- `firebase/` — Firebase Cloud Functions (Node 20 + TypeScript) calling Gemini (`gemini-3.8-flash` & `gemini-3.5-flash-lite`) via `@google/genai` Interactions API so the API key never ships in the APK.
  - `parseReceipt` — Multimodal vision -> structured JSON (`cleanName`, `rawLabel`, `quantityLabel`, `shelf`, `shelfLifeDays`, `priceAed`, `storageTip`, `safetyNote`)
  - `suggestRescueRecipes` — Generates 2–3 fast rescue recipes prioritizing soonest-expiring items
  - `getPetDialogue` — Generates short in-character pet micro-dialogue

## Backend Setup (Firebase Cloud Functions)

1. Install dependencies and build:
   ```bash
   cd firebase/functions
   npm install
   npm run build
   ```
2. Configure your Firebase project (`cp firebase/.firebaserc.example firebase/.firebaserc` and set your project ID).
3. Store your Gemini API key securely in Firebase Secret Manager:
   ```bash
   firebase functions:secrets:set GEMINI_API_KEY
   ```
4. Deploy functions (or run locally with `npm run serve`):
   ```bash
   cd firebase
   firebase deploy --only functions
   ```

## Android Setup

1. Open `android/` in Android Studio (or build via Gradle with Android SDK 35 & JDK 17).
2. Optional — point the app to your deployed Cloud Functions base URL by adding to `android/gradle.properties` (or passing `-PFRIDGE_PET_API_BASE_URL=...`):
   ```properties
   FRIDGE_PET_API_BASE_URL=https://me-central1-<your-project>.cloudfunctions.net
   ```
   When `FRIDGE_PET_API_BASE_URL` is left blank, the app automatically uses `DemoFallbackAiService` so you can test the entire UAE receipt -> Review -> Inventory -> Rescue Recipe -> Weekly AED Report loop immediately offline.
