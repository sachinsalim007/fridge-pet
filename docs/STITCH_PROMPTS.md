# Fridge Pet: Complete Stitch Design Prompts

**Project ID:** `8343919699699103156`  
**Design System:** `Fresh Sprout` (`assets/9f39bedde1284fb1a32482b8a74f6c66`)  
**Target Platform:** Mobile (iOS / Android 390x844dp viewport, single-thumb ergonomics)

> **Important Stitch Rule:** The `Fresh Sprout` design system is already attached to Project `8343919699699103156`. Therefore, **do not mention hex colors, fonts, or color palettes in the generation prompts**. Stitch applies them automatically. Focus exclusively on layout, hierarchy, components, and content!

---

## 🎨 Generated Screen Registry (Project `8343919699699103156`)

All 7 core MVP screens have been generated in Google Stitch under the `Fresh Sprout` design system:

| # | Screen Name | Stitch Screen Resource / ID | Status | Key Features |
|---|---|---|---|---|
| **1** | **Onboarding & Hatching** | `screens/15e989cb6b2744dfb86f3371e697423c` | ✅ Generated | 10-pet carousel, custom naming, wobbling egg hero, notification chips |
| **2** | **Home & Pamper Stage** | `screens/f9e71c2172504396a3cf5559e3a705ae` | ✅ Generated | 3D Mochi companion, tummy rub pampering, urgent shelf (chicken, spinach) |
| **3** | **Scan Groceries Viewfinder** | `screens/eeeb82e3739a49979e0b1ad5fc753f9b` | ✅ Generated | Camera HUD, UAE receipt framing guides, torch/gallery buttons |
| **4** | **Review Groceries** | `screens/c9885bc90ba74e1c95f8b69ba4d18e3c` | ✅ Generated | Arabic/English item edit, AED prices, storage toggle (Fridge/Freezer +90d) |
| **5** | **My Fridge & Pantry** | `screens/6f4d7dd13ba84905805a07a3564c1dee` | ✅ Generated | Crisper, Freezer, Dry Pantry categories, days left countdown pills |
| **6** | **Rescue Kitchen** | `screens/024d44ba5ddf42d698af849d516ab8f7` | ✅ Generated | Chef Mochi toque banner, 15-min skillet recipe, "Cooked This!" CTA |
| **7** | **Weekly Impact Report** | `screens/d10e5a44b9c24ab98fe887cd30b0fa9d` | ✅ Generated | AED 59 saved, 83% zero-waste radial ring, Cozy Coral Scarf equipped |

---

## Screen 1: Onboarding & 10-Pet Selection Carousel
**Screen Title:** `Onboarding - Meet & Hatch Your Pet`  
**Device Type:** `MOBILE`

```markdown
A playful, welcoming mobile onboarding screen for a food-waste companion app where users choose and hatch their virtual fridge creature.

**PLATFORM:** Mobile-first, single-column vertical flow.

**PAGE STRUCTURE:**
1. **Header Section:**
   - Brand logo and tiny playful leaf icon.
   - Stepper indicator showing Step 1 of 2.
   - Centered large bold headline: "Meet Your Fridge Pet".
   - Subtitle: "A tiny companion who lives in your fridge and helps you save food and dirhams."

2. **Hero Feature Stage:**
   - Large elevated stage card with subtle rounded container (24px radius).
   - Centered 3D clay egg gently wobbling on a clean refrigerator glass shelf with glowing ambient particles.
   - Speech bubble above the egg: "Tap to hear who's inside!".
   - Pill badge below showing: "Frosty Sprout Stage".

3. **10-Pet Horizontal Carousel:**
   - Section title: "Choose Your Pantry Buddy".
   - Horizontally scrolling carousel of 10 creature cards (each card is pill/rounded-xl with an avatar preview, pet name, and food affinity tag):
     - Mochi (Fresh Greens & Dairy) [Selected state with active ring]
     - Zaatar (Bakery & Breads)
     - Chilli (Spices & Leftovers)
     - Frosty (Freezer Goods)
     - Laban (Yogurts & Cheeses)
     - Berry (Fresh Fruits)
     - Olive (Pantry Jars & Oils)
     - Ginger (Roots & Herbs)
     - Spud (Potatoes & Staples)
     - Citrus (Citrus & Juices)

4. **Interactive Customization Section:**
   - Text input card for "Name Your Pet" with active placeholder "Mochi".
   - Quick-select chip row of preset suggestions: "Mochi", "Habibi", "Zuzu", "Pickles".
   - Preferred daily notification card with clock icon: "Daily Alert Time" showing selectable pill chips for "8:00 AM", "9:00 AM" (Selected), "10:00 AM", and "6:00 PM".
   - Checkbox card: "Start with sample UAE groceries (Chicken, Spinach, Milk, Flatbread)".

5. **Fixed Bottom Action Dock:**
   - Large full-width pill button: "Hatch Mochi" with a sparkle icon.
   - Micro-copy below: "No account required · Works 100% offline".
```

---

## Screen 2: Home Screen ("Needs Attention" Shelf & Pamper Stage)
*(Note: You already have this partially generated in Stitch as `Fridge Pet Home`, but here is the enhanced prompt if generating a clean variant).*  
**Screen Title:** `Home - Fridge Pet Companion`  
**Device Type:** `MOBILE`

```markdown
A cozy, daily companion home dashboard for a smart fridge food-saving mobile app.

**PLATFORM:** Mobile-first, vertical scroll.

**PAGE STRUCTURE:**
1. **Top Navigation Bar:**
   - Left: User's custom pet name "Mochi" in bold headline typography with small "Level 1 Sprout" subtitle.
   - Right: Mood pill chip displaying "Content" with a glowing green dot, and a streak badge showing "5-day streak".

2. **Hero Pet Stage Card:**
   - Elevated large feature card with 28px rounded corners.
   - Centered 3D clay creature mascot sitting comfortably on a glass refrigerator shelf.
   - Pamper micro-interaction hint: "Rub Mochi's tummy to cheer up".
   - Speech bubble: "Crisp, cool, and cozy! Everything on the shelves is looking fresh today."
   - Quick two-button action row:
     - Primary button with camera icon: "Scan Receipt".
     - Tonal button with chef hat icon: "Rescue Cook".

3. **"Needs Attention" Shelf Section:**
   - Section header: "Needs Attention" with a pill counter: "2 items urgent".
   - Card 1 (Urgent item):
     - Left: Fresh chicken breast illustration/icon.
     - Title: "Chicken Breast, 1 kg", subtitle: "Fridge Shelf · AED 26.50".
     - Right: Coral pill chip: "1 day left (est.)".
     - Action buttons: "Used It" and "Freeze (+90d)".
   - Card 2 (Approaching expiry):
     - Left: Baby spinach bag icon.
     - Title: "Baby Spinach, 200 g", subtitle: "Crisper Drawer · AED 12.00".
     - Right: Amber pill chip: "2 days left (est.)".
     - Action buttons: "Used It" and "Freeze (+90d)".

4. **Food Safety Disclaimer Card:**
   - Shield icon with small reassurance: "AI expiry dates are estimates (est.) — always smell, inspect, and check before eating."

5. **Bottom Navigation Bar:**
   - 4 floating dock tabs: "Home" (Active), "Scan", "Inventory", "Weekly Report".
```

---

## Screen 3: Scan Screen (Camera Viewfinder with Guide Brackets)
**Screen Title:** `Scan - Receipt & Shelf Camera Viewfinder`  
**Device Type:** `MOBILE`

```markdown
A high-tech yet approachable mobile camera scanning interface designed to capture grocery receipts or open refrigerator shelves.

**PLATFORM:** Fullscreen dark mobile camera viewfinder.

**PAGE STRUCTURE:**
1. **Top Viewfinder Header:**
   - Semi-transparent glass bar with close button (X), flash toggle, and title "Scan Groceries".
   - Segmented pill toggle: "Receipt (AR / EN)" [Active] and "Fridge Shelf Photo".

2. **Center Camera Viewfinder Area:**
   - Dark camera preview viewport with high-contrast glowing neon green corner guide brackets.
   - Aspect ratio framing optimized for long grocery store receipts (Carrefour, Lulu, Spinneys).
   - Animated soft horizontal laser scanline sweeping slowly down the frame.
   - Micro-guidance pill centered at bottom of viewport: "Align Arabic or English receipt inside brackets".
   - Mini corner indicator showing pet's wide excited eyes peeking into the frame.

3. **Bottom Controls Deck:**
   - Large circular primary shutter button with glowing double outer ring.
   - Left secondary button: "Gallery" with photo library icon.
   - Right secondary button: "Manual Add" with plus icon.

4. **One-Tap Demo Shortcut:**
   - Floating outlined pill button at the very bottom: "Try Sample UAE Bilingual Receipt" with sparkle icon.
```

---

## Screen 4: Review Screen (Editable Extracted Items List)
**Screen Title:** `Review - Extracted Grocery Items`  
**Device Type:** `MOBILE`

```markdown
An editable review and confirmation screen where users inspect items parsed by Gemini AI from a grocery receipt before saving to their inventory.

**PLATFORM:** Mobile-first, scrollable form with sticky bottom bar.

**PAGE STRUCTURE:**
1. **Top Header & Context:**
   - Back button with screen title: "Review Groceries".
   - Subtitle card: "Carrefour Mall of the Emirates · 7 items detected".
   - Mascot message bubble: "Mochi says: 'Found 7 items! Anything look off?'".
   - Safety banner: "Check before eating · Estimates can be adjusted below".

2. **Editable Item Cards List:**
   - **Card 1 (Food Item - Short Shelf):**
     - Checkbox: Checked.
     - Raw receipt snippet text: "CHKN BRST 1KG / صدر دجاج".
     - Editable text field: "Chicken breast".
     - Row with Quantity: "1 kg" and Price: "AED 26.50".
     - Storage segment selector: [Fridge (Active)] [Freezer] [Pantry].
     - Shelf-life slider / stepper: "2 days remaining (est.)" with amber "Check" badge.
     - Storage tip: "Keep on coldest bottom shelf or freeze portions today."
   - **Card 2 (Food Item - Changed to Freezer):**
     - Checkbox: Checked.
     - Raw receipt snippet: "ALMARAI FF 1L".
     - Name: "Milk, 1 L", Price: "AED 7.25".
     - Storage: [Fridge] [Freezer (Active)] [Pantry].
     - Shelf life jumped to: "90 days (Frozen in time!)".
   - **Card 3 (Non-Food Detected - Auto-unselected):**
     - Checkbox: Unchecked (dimmed opacity).
     - Raw receipt snippet: "FAIRY LEMON 750ML".
     - Name: "Dish soap", Price: "AED 14.00".
     - Grey pill tag: "Non-food (Skipped)".
     - Trash icon to delete.

3. **Add Missing Item Button:**
   - Outlined dashed card with plus icon: "+ Add Missing Grocery Item".

4. **Sticky Bottom Action Dock:**
   - Large full-width pill button: "Add 6 Items to Fridge".
   - Secondary button: "Retake Photo".
```

---

## Screen 5: Inventory Screen (Grouped Fridge Shelves)
**Screen Title:** `Inventory - Shelves & Expiry Management`  
**Device Type:** `MOBILE`

```markdown
A clean, organized pantry and refrigerator inventory screen with categorized shelves and smart expiry tags.

**PLATFORM:** Mobile-first, scrollable vertical shelf layout.

**PAGE STRUCTURE:**
1. **Header & Search:**
   - Title: "My Fridge & Pantry".
   - Search bar with magnifying glass: "Search ingredients, milk, chicken...".
   - Horizontal filter chips: "All (8)", "Fridge (4)", "Freezer (2)", "Pantry (2)".

2. **Categorized Shelf Sections:**
   - **Section 1: "Crisper & Fridge" (4 items):**
     - Item row: "Baby Spinach (200 g)" · Coral pill: "Tomorrow (est.)" · Buttons: "Used" | "Freeze" | "Toss".
     - Item row: "Fresh Milk (1 L)" · Amber pill: "3 days left" · Buttons: "Used" | "Freeze" | "Toss".
     - Item row: "Greek Yogurt (400 g)" · Green pill: "6 days left" · Buttons: "Used" | "Freeze" | "Toss".
     - Item row: "Cheddar Cheese (200 g)" · Green pill: "14 days left" · Buttons: "Used" | "Freeze" | "Toss".
   - **Section 2: "Freezer Shelf" (2 items):**
     - Item row: "Chicken Breast (1 kg)" · Snowflake icon · Ice-blue pill: "88 days left" · Button: "Thaw/Use".
     - Item row: "Arabic Flatbread (6 pcs)" · Snowflake icon · Ice-blue pill: "60 days left" · Button: "Thaw/Use".
   - **Section 3: "Dry Pantry" (2 items):**
     - Item row: "Tomatoes (500 g)" · Amber pill: "3 days left" · Button: "Used".
     - Item row: "Basmati Rice (2 kg)" · Green pill: "180 days left" · Button: "Used".

3. **Floating Action Button (FAB):**
   - Floating pill button in bottom right: "+ Quick Add".

4. **Bottom Navigation Bar:**
   - 4 dock tabs: "Home", "Scan", "Inventory" (Active), "Weekly Report".
```

---

## Screen 6: Rescue Recipe Screen (Zero-Waste Cooking Assistant)
**Screen Title:** `Rescue Recipe - Chef Guardian Kitchen`  
**Device Type:** `MOBILE`

```markdown
A mouth-watering, beginner-friendly recipe suggestions screen designed to rescue expiring fridge groceries in under 20 minutes.

**PLATFORM:** Mobile-first, recipe card feed.

**PAGE STRUCTURE:**
1. **Header Section:**
   - Back button with title: "Rescue Kitchen".
   - Refresh button with sparkle: "New Ideas".
   - Mascot banner: 3D pet wearing a tiny golden chef toque hat saying: "Let's turn your urgent spinach and tomatoes into dinner!".

2. **Urgent Ingredients Pill Row:**
   - Horizontal chip row of highlighted ingredients being rescued:
     - Pill: "Spinach (Expires tomorrow)"
     - Pill: "Tomatoes (3 days left)"
     - Pill: "Eggs (In pantry)"

3. **Featured Rescue Recipe Cards (2 to 3 cards):**
   - **Recipe Card 1 (Hero Recipe):**
     - Big appetizing food photography/illustration of "15-Min Creamy Spinach & Tomato Eggs".
     - Metadata chips: Prep time "15 mins" · Difficulty "Beginner" · Servings "2".
     - Ingredients tag cloud: "Uses: Baby Spinach, Tomatoes, Eggs" (all highlighted in green).
     - Extra pantry staples required: "Olive oil, salt, black pepper, garlic".
     - Step-by-step accordion preview:
       1. Heat 1 tbsp olive oil with garlic in a pan.
       2. Toss in tomatoes and spinach until tender (4 mins).
       3. Crack eggs on top, cover, and simmer for 5 minutes.
     - Pet cheer quote: "Mochi's tip: Toast leftover flatbread to dip in the eggs!".
     - Primary action button: "Cooked This! Mark Ingredients Used" with checkmark icon.

   - **Recipe Card 2 (Quick Alternate):**
     - Title: "Zero-Waste Mediterranean Breakfast Skillet".
     - Prep time "10 mins" · Beginner.
     - Action button: "Cook This Instead".

4. **Bottom Food Safety Note:**
   - "Always check ingredients before cooking · AI recipes assume basic kitchen staples".
```

---

## Screen 7: Weekly Impact Report (AED Saved & Evolution Streak)
**Screen Title:** `Weekly Report - Food & Dirhams Saved`  
**Device Type:** `MOBILE`

```markdown
A gamified, rewarding weekly celebration screen celebrating food waste reduction, financial savings in AED, and pet evolution unlocks.

**PLATFORM:** Mobile-first, visual celebration summary.

**PAGE STRUCTURE:**
1. **Header Section:**
   - Title: "Weekly Impact Report".
   - Date range pill: "Nov 9 – Nov 15, 2026".
   - Confetti particle background effect.

2. **Hero Financial & Food Metric Cards:**
   - Two large side-by-side metric cards (24px rounded corners):
     - Card 1: "Money Saved" · Huge bold green number: "AED 59" · Subtitle: "5 items rescued".
     - Card 2: "Food Diverted" · Huge bold number: "1.75 kg" · Subtitle: "Equal to 4 family meals".

3. **Zero-Waste Score Radial Ring:**
   - Clean circular progress indicator showing "83% Zero-Waste Score".
   - Subtext: "5 items rescued · 1 forgotten (Milk)".
   - Gentle, forgiving pet cheer bubble: "Mochi says: 'You saved AED 59 this week! Don't worry about the milk—next shop is a fresh start!'".

4. **Pet Evolution & Accessory Showcase:**
   - Elevated feature card showing 3D Mochi wearing its newly unlocked "Cozy Knitted Coral Scarf".
   - Badge: "1-Week Low Waste Streak Achieved!".
   - Progress bar to Next Unlock: "2 more good weeks to unlock Leafy Sprout Antenna (Pantry Puffle)".

5. **Weekly Activity Log List:**
   - "This Week's Rescues & Losses":
     - Row: "Chicken breast" · Green tag "+AED 26.50" (Rescued).
     - Row: "Baby spinach" · Green tag "+AED 12.00" (Rescued).
     - Row: "Tomatoes" · Green tag "+AED 5.50" (Rescued).
     - Row: "Arabic flatbread" · Green tag "+AED 3.00" (Frozen).
     - Row: "Fresh milk" · Red tag "-AED 7.25" (Expired Thursday).

6. **Bottom Celebration Action:**
   - Large full-width pill button: "Celebrate & Start New Week" with emoji trophy.
```
