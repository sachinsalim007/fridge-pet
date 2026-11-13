# Design System: Fridge Pet ("Fresh Sprout")
**Project ID:** `8343919699699103156`  
**Platform:** Android (Jetpack Compose + Material 3, Mobile-first 360–430dp)

---

## 1. Visual Theme & Atmosphere
- **Aesthetic**: Lively Naturalism meets friendly Apple/Duolingo utility.
- **Mood**: Crisp, refrigerated freshness, sustainable abundance, and gamified companion warmth. Caring for your pantry is treated as caring for a living ecosystem rather than a tedious chore.
- **Depth & Elevation**: Soft ambient forest-tinted dispersion (`rgba(31, 107, 79, 0.05)`) with delicate hairline strokes (`1px solid rgba(31, 107, 79, 0.08)`) instead of heavy muddy drop shadows.

---

## 2. Color Palette & Roles

| Token | Hex | Role & Usage |
|---|---|---|
| **Base Canvas** | `#F4F8F5` | Frost-white canvas with jade undertone, evokes cold interior lighting. |
| **Surface Card** | `#FFFFFF` | Alpine white container for cards, sheets, and active dialogue bubbles. |
| **Primary (Evergreen)** | `#1F6B4F` | Main brand color, primary action buttons, active tabs, and stable health states. |
| **Primary Container** | `#D8F3E5` | Gentle mint tint for selected chips, celebratory highlights, and badges. |
| **Warning / Attention (Amber)** | `#F2A93B` | Items expiring within 2–3 days, nutrition tips, anxious mood accents. |
| **Warning Container** | `#FEF3C7` | Soft apricot backdrop behind soon-expiring chips and notifications. |
| **Danger / Urgent (Coral Red)** | `#E5604D` | Spoiled/expired food alerts, sick pet state, destructive action confirmations. |
| **Danger Container** | `#FFE4E6` | Pale berry surface for overdue expiry warnings and waste logs. |
| **Deep Ink** | `#14201B` | High-contrast body text and headers, avoids pure `#000000` harshness. |
| **Muted Ink** | `#6B7C74` | Secondary metadata, dates, storage advice, and unselected icons. |

---

## 3. Typography Rules
- **Font Family**: Inter (system default on Android sans-serif fallback).
- **Scale**:
  - `Display / Headline Large` (`28–32sp`, Bold/ExtraBold, `-0.02em` tracking): Pet status announcements, big AED savings metrics, weekly summaries.
  - `Title Medium / Large` (`16–18sp`, SemiBold): Food item titles, section headings ("Needs Attention", "Pantry & Fridge").
  - `Body Medium / Large` (`13–15sp`, Normal, `1.4` line-height): Storage tips, recipe cooking steps, pet dialogue speech bubbles.
  - `Label Medium` (`11–12sp`, SemiBold, `+0.02em` tracking): Expiry chips, unit tags ("1 kg", "500 g"), shelf pills.

---

## 4. Component Stylings
- **Buttons**:
  - *Primary*: 48–54dp height, pill / 24dp rounded corners, `#1F6B4F` background with `#FFFFFF` text.
  - *Secondary / Outlined*: White surface with `1px` subtle outline (`#BFC9C1`) and `#1F6B4F` label.
  - *Freezer Action*: Soft ice-tinted button with snowflake icon (`#E9F7EE` / `#1F6B4F`).
- **Cards & Shelves**:
  - 20–24dp corner radius, white surface, 12–16dp internal padding.
  - Expiry chips on top-right: pill-shaped (`rounded-full`) with background and text color matching urgency.
- **Inputs & Review Rows**:
  - 16dp rounded outline text fields with clear labels and immediate quantity/price editing.
- **Pet Companion Feature Box**:
  - Gradient feature container (`#FFFFFF` to `#EBF4EE`), centered creature avatar, and speech bubble with direct micro-dialogue.
- **Food Safety Banner**:
  - Constant reminder card across Review, Inventory, and Recipe screens: *"AI expiry dates are estimates — always smell, inspect, and check before eating."*

---

## 5. Screen Layout & Navigation (7 MVP Screens)
1. **Onboarding**: Meet & name your pet, optional demo UAE grocery seeding.
2. **Home**: Large pet creature widget, mood badge, speech bubble, quick actions ("Scan Receipt", "Rescue Cook"), and "Needs Attention" shelf (items expiring <= 2 days).
3. **Scan**: CameraX viewfinder with receipt brackets (Arabic + English receipt guide vs. open fridge photo toggle), gallery picker, and one-tap demo scan.
4. **Review**: Editable list of extracted items (name cleanup, shelf selection, expiry days slider, AED price, storage tip).
5. **Inventory**: Shelf tabs (All, Fridge, Freezer, Pantry) with expiry chips, "Used", "Freeze (+30d)", and "Tossed" actions.
6. **Rescue Recipe**: 2–3 chef-crafted recipes prioritizing urgent ingredients, minimal staples, cooking steps, and pet cheer line.
7. **Weekly Report**: Total AED saved, kg of food diverted from waste, zero-waste streak progress, and evolution stage unlocks.
