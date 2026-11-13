# Product Requirements Document (PRD): Fridge Pet (v1)

**Document Version:** 1.0.0  
**Target Platform:** Android (Jetpack Compose, Material 3, Room, WorkManager) + Firebase Cloud Functions (Gemini 3.8 Flash / 3.5 Flash-Lite)  
**Primary Currency:** United Arab Emirates Dirham (AED)  
**Design Reference:** `docs/DESIGN.md` (Stitch Design System "Fresh Sprout", Project ID: `8343919699699103156`)

---

## 1. Executive Summary & Core Value Proposition

Fridge Pet is a playful, beginner-friendly food waste companion app for Android. A virtual creature lives inside the user's pantry/fridge, reacting in real time to food freshness. Users scan grocery receipts (including UAE bilingual Arabic/English receipts), Gemini extracts items with estimated shelf-lives, and the pet nudges users with rescue recipes before food spoils. 

### Emotional & Behavioral Core
- **No Guilt, No Shame**: Progress focuses on **AED saved** and **food rescued**. Expired items trigger gentle sympathy rather than harsh score punishment.
- **Micro-Investment**: The entire shopping loop takes under 2 minutes per grocery run.
- **Zero-Friction Onboarding**: No account creation, login screens, or cloud sync walls in v1.

---

## 2. Personas & Core User Journey ("One Week with Sara")

### Persona: Sara
- 32-year-old busy professional living in Dubai.
- Shops once a week at Carrefour / Lulu / Spinneys.
- Throws away fresh herbs, milk, or chicken because she forgets they are in the crisper drawer.

### 8-Step Weekly Lifecycle
```
[1. Meet & Name Pet] ➔ [2. Assisted Snap Receipt] ➔ [3. Gemini Vision Extraction (5s)]
         │
         ▼
[4. Review & Confirm Data (20s)] ➔ [5. Pet Reflects Fridge State]
         │
         ▼
[6. Personalized Pet Alert] ➔ [7. 15-Min Rescue Recipe] ➔ [8. Weekly AED Impact Report]
```

1. **Meet & Name Pet**: Hatches from an egg, named by the user (e.g., "Mochi"), requests notification permission and preferred notification hour.
2. **Scan Receipt**: Assisted manual snap with clear guide brackets and visual shutter feedback.
3. **Gemini Extraction**: Receipt image sent to Firebase backend; Gemini cleans Arabic/English shorthand, flags non-food items (`FAIRY 750ML`), estimates shelf life, and provides storage tips. Image deleted immediately.
4. **Review & Confirm**: User confirms items. Tapping "Freezer" extends shelf life to 3 months (+90 days). Non-food unselected by default.
5. **Home & Mood**: Pet displays real-time mood based on inventory (Content, Hungry, Anxious, Sick, Celebrating, Sleepy).
6. **Smart Notification**: Pet speaks directly at the user's chosen reminder time (short-shelf items alert the morning before; medium-shelf alert 2 days before).
7. **Rescue Cook**: Generates 2–3 fast recipes from urgent items. Marking "Cooked" marks ingredients used; leftovers can be frozen in one tap.
8. **Weekly Report**: Tracks money saved (AED), food weight diverted, low-waste streak, and cosmetic accessory unlocks (Scarf, Antenna, Chef Hat).

---

## 3. Detailed Feature Specifications

### 3.1. Onboarding, Pet Selection & Custom Naming
- **10-Pet Selection Carousel**:
  - Users swipe horizontally through 10 interactive creature eggs / preview avatars before hatching:
    1. **Mochi**: Soft mint jelly blob (Fresh greens & dairy)
    2. **Zaatar**: Fluffy golden-toasted sesame puff (Bakery & grains)
    3. **Chilli**: Teardrop crimson pepper with tiny stem (Spices & sauces)
    4. **Frosty**: Translucent ice cube sprite with blue glow (Freezer goods)
    5. **Laban**: Creamy porcelain droplet with a soft swirl (Milk & yogurts)
    6. **Berry**: Round lavender-magenta berry puff (Fruits & berries)
    7. **Olive**: Sleek emerald oval with glossy highlight (Oils & Mediterranean)
    8. **Ginger**: Knobby playful root creature with sprout (Root vegetables)
    9. **Spud**: Cozy rounded potato with cute freckles (Dry pantry staples)
    10. **Citrus**: Sun-gold wedge buddy with mint leaf tail (Citrus & herbs)
- **Custom Name & Rename**:
  - Free text input (1 to 24 characters) allowing any custom name (e.g., "Mochi", "Habibi", "Mr. Pickles", "Chef Zuzu").
  - Quick-preset chips based on chosen species for one-tap naming.
  - Can be renamed at any time from the Pet Profile / Home screen.
- **Egg Hatching Sequence**:
  - Tapping the selected egg triggers wobbling, haptic feedback, shell crack particles, and emergence of the chosen pet.
- **Notification Preferences**: Custom reminder time picker (default 09:00 AM local time).
- **Demo Mode Option**: Toggle to pre-populate 4 sample UAE items for immediate testing.

### 3.1.1. Interactive Pamper & Touch Engine
- **Touch / Tap**: Pet bounces, squishes elastically, and triggers a happy chirp and blink.
- **Continuous Petting / Rubbing (Pamper Gesture)**: Rubbing across the creature's tummy activates the **Purr / Pamper Loop** (floating heart particles, closed contented eyes, and a temporary +30 second mood cheer).
- **Drag & Pull**: Stretches with spring physics and snaps back smoothly.

### 3.2. Receipt & Fridge Scanner
- **Camera Viewfinder**: CameraX with aspect-ratio guide brackets.
- **Capture Modes**:
  - `Receipt (Arabic / English)`: Optimized for receipt slips, store headers, and line items.
  - `Fridge Photo`: Identifies open shelf items when receipts are lost or discarded.
- **Assisted Shutter**: Prominent shutter button with corner lock indicators, plus photo gallery import fallback.
- **Privacy & Security**: Photos compressed in-memory to max 1280px JPEG. Deleted from disk immediately after transmission. No API keys inside Android.

### 3.3. AI Extraction & Review Screen
- **Extraction Schema**:
  - `cleanName`: Lowercase plain English (e.g., "chicken breast").
  - `rawLabel`: Original receipt text snippet (e.g., "CHKN BRST 1KG / صدر دجاج").
  - `quantityLabel`: Standardized quantity (e.g., "1 kg", "200 g", "1 L").
  - `shelf`: `FRIDGE`, `FREEZER`, or `PANTRY`.
  - `shelfLifeDays`: Realistic home storage estimate in days.
  - `priceAed`: Price extracted or estimated in AED.
  - `storageTip`: Concise 1-sentence tip.
  - `isFood`: Boolean (`false` for detergent, tissues, bags).
- **Review Controls**:
  - Non-food items unchecked by default.
  - Ambiguous / short-life items tagged with amber "Check" badge.
  - One-tap shelf selector (`Fridge` ➔ `Freezer` automatically sets shelf-life to 90 days).
  - Add missing item button.
  - Permanent safety banner: *"AI expiry dates are estimates (est.) — always smell, inspect, and check before eating."*

### 3.4. Pet Brain & Mood State Machine
The pet's state is computed deterministically from Room database records:

| Priority | State | Condition | Visual Indicator | Pet Tone / Behavior |
|---|---|---|---|---|
| **1** | `CELEBRATING` | Within 45 seconds of a rescue or zero-waste streak | Confetti particles, waving arms, glowing cheek blush | *"Woohoo! We rescued that in time!"* |
| **2** | `SICK` | >= 1 active item past expiry date (`expiryEpochDay < today`) | Olive-green body tint, spiral/X eyes, dizzy bubbles | *"Ugh, my tummy hurts... something expired in here!"* |
| **3** | `SLEEPY` | Local time between 22:00 and 05:59 (when not sick) | Soft blue tint, closed crescent eyes, floating Zzz | *"Zzz... keeping the crisper cool tonight."* |
| **4** | `ANXIOUS` | >= 1 item expiring within 2 days (`0 <= delta <= 2`) | Cyan shiver animation, sweat drop, nervous mouth | *"Psst! The spinach only has a day left — rescue cook?"* |
| **5** | `HUNGRY` | Active inventory <= 2 items | Pale belly, wide hopeful round eyes | *"Echo... echo... it's roomy in here! Grocery run soon?"* |
| **6** | `CONTENT` | Normal inventory with comfortable shelf-life | Gentle breathing bob, warm smile, pink cheeks | *"Crisp, cool, and cozy! Everything looks fresh."* |

### 3.5. Evolution & Accessory Progression
Progress is driven strictly by **consecutive low-waste weeks** (weeks where 0 items expired):
- **Stage 1: Frosty Sprout** (0 weeks): Base creature with stubby arms.
- **Stage 1 + Accessory: Cozy Scarf** (1 week streak): Earns a tiny knitted coral-colored scarf.
- **Stage 2: Pantry Puffle** (2 week streak): Evolves a perky green leafy sprout antenna.
- **Stage 3: Chef Guardian** (4 week streak): Wears a golden-banded white chef toque hat.

*Forgiveness Rule*: If an item is wasted, the streak pauses for that week rather than resetting all unlocked cosmetics.

### 3.6. Inventory Management
- **Shelf Tabs**: Filter by `All`, `Fridge`, `Freezer`, `Pantry`.
- **Item Cards**:
  - Title, quantity, price in AED, days remaining chip.
  - Urgency chips: Green (`>2d left`), Amber (`<=2d left`), Coral (`Due today` / `Expired`).
  - Action buttons:
    - **Used**: Logs as rescued food event (+AED saved), triggers brief celebration.
    - **Freeze**: Moves item to `FREEZER` and extends expiry by +90 days.
    - **Toss**: Logs as waste event without shame, updates pet dialogue.

### 3.7. Rescue Chef
- Triggers on-demand from Home or Inventory.
- Backend prompt receives only urgent items (`delta <= 2 days`).
- Generates 2–3 beginner recipes under 20 minutes requiring only basic kitchen staples (olive oil, salt, garlic, eggs, bread).
- Tapping **"Cooked This!"** marks recipe ingredients as consumed in Room and logs cumulative AED saved.

### 3.8. Notifications (WorkManager)
- Runs daily background check via `WorkManager` at the user's preferred hour (default 09:00 AM).
- **Notification Logic**:
  - Short-shelf items (`shelfLifeDays <= 2`): Triggers **the morning before**.
  - Medium/long-shelf items (`shelfLifeDays > 2`): Triggers **2 days before**.
- **Tone**: Always voiced from the pet (e.g., `"{PetName} is nervous about the spinach."`).
- Tapping notification deep-links directly to Home with the urgent item highlighted.

### 3.9. Weekly Impact Report
- **Savings Cards**:
  - `Money Saved`: Sum of AED for all rescued items this week.
  - `Food Diverted`: Estimated kilograms rescued (standardized 350g/unit average).
- **Streak Tracker**: Current consecutive low-waste week streak and next unlock preview.
- **Activity Log**: Chronological list of items used (+AED) or tossed.
- **Zero-Waste Celebration**: Manual completion trigger at end-of-week rewarding pet streak.

---

## 4. Technical Architecture & Data Schemas

### 4.1. Room Database Tables (`android/`)
1. **`food_items`**:
   - `id`: Long (PK, auto-increment)
   - `name`: String (cleaned name)
   - `rawLabel`: String
   - `quantityLabel`: String
   - `shelf`: String (`FRIDGE`, `FREEZER`, `PANTRY`)
   - `expiryEpochDay`: Long
   - `priceAed`: Double
   - `storageTip`: String
   - `status`: String (`ACTIVE`, `USED`, `WASTED`)
   - `addedAtEpochMs`: Long
   - `resolvedAtEpochMs`: Long?
2. **`pet_profile`**:
   - `id`: Int (PK = 1)
   - `speciesId`: String (`MOCHI`, `ZAATAR`, `CHILLI`, `FROSTY`, `LABAN`, `BERRY`, `OLIVE`, `GINGER`, `SPUD`, `CITRUS`)
   - `name`: String (custom user-chosen name, editable at any time)
   - `onboarded`: Boolean
   - `notificationHour`: Int (0–23, default 9)
   - `consecutiveGoodWeeks`: Int
   - `celebratingUntilEpochMs`: Long
   - `lastDialogue`: String
   - `lastDialogueUpdatedMs`: Long
3. **`waste_events`**:
   - `id`: Long (PK, auto-increment)
   - `itemName`: String
   - `priceAed`: Double
   - `estimatedGrams`: Int
   - `rescued`: Boolean
   - `weekStartEpochDay`: Long
   - `timestampEpochMs`: Long

### 4.2. Backend API Interface (`firebase/functions/`)
- **`POST /parseReceipt`**:
  - Body: `{ imageBase64, mimeType, scanMode }`
  - Response: `{ merchantName, currency, items: [...], safetyNote }`
- **`POST /suggestRescueRecipes`**:
  - Body: `{ petName, petState, expiringItems: [...] }`
  - Response: `{ recipes: [...] }`
- **`POST /getPetDialogue`**:
  - Body: `{ petName, petState, evolutionStage, expiringSoonNames, expiredCount, totalItems, savedAedThisWeek }`
  - Response: `{ line: string }`

---

## 5. Non-Functional Requirements & Security
- **No Leaked Keys**: Gemini API key resides solely in Firebase Secret Manager (`defineSecret("GEMINI_API_KEY")`).
- **Offline Resilience**: When `FRIDGE_PET_API_BASE_URL` is empty or device has no network, `DemoFallbackAiService` serves realistic UAE items and recipes seamlessly.
- **Permissions**: Only `CAMERA` (when scanning) and `POST_NOTIFICATIONS` (after onboarding).
- **TalkBack & Accessibility**: 48dp touch targets, semantic headings, and color-independent expiry labels.

---

## 6. Numbered Milestone Task List

- [x] **Milestone 0: Tooling & Scaffold** (Git repo, Gradle Kotlin DSL, Room/WorkManager, `.agents/skills`, `GEMINI.md`)
- [x] **Milestone 1: Design System Synthesis** (`DESIGN.md` extracted from Stitch project `8343919699699103156`, Compose theme tokens aligned)
- [x] **Milestone 2: Product Specification** (PRD locked to "One Week with Sara", 4 design decisions approved)
- [ ] **Milestone 3: UI Enhancement & Polish** (Egg hatching onboarding flow, custom notification hour picker, accessory rendering: Scarf, Antenna, Chef Hat)
- [ ] **Milestone 4: Scanner Precision & Auto-Assistance** (Viewfinder brackets corner lock feedback, non-food auto-uncheck, instant Freezer 90-day expiry jump)
- [ ] **Milestone 5: Notification Precision** (WorkManager custom hour scheduling, short-shelf morning-before vs medium-shelf 2-day-before rules)
- [ ] **Milestone 6: Verification & Testing** (Unit tests for notification scheduling, Room migrations, and end-to-end Sara scenario verification)
