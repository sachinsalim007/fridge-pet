---
name: stitch-ui-ux-designer
description: >-
  Guides crafting high-conversion, playful, accessible mobile UI screens in Stitch for Fridge Pet.
  Use when writing Stitch generation prompts, defining screen layouts, hierarchies, and design systems.
---

# Stitch UI/UX Designer Guide (Fridge Pet)

## Core Design Philosophy: "Fresh Sprout"
- **Mood**: Playful companion gamification meets crisp, editorial Apple Health/Duolingo utility.
- **Color Roles**:
  - Frost Canvas (`#F4F8F5`): Chilled refrigerated backdrop.
  - Surface Card (`#FFFFFF`): Crisp white cards with 24px corner radii.
  - Evergreen Primary (`#1F6B4F`): Stability, fresh food, primary CTA buttons.
  - Warm Amber (`#F2A93B`): 2–3 days left warning chips.
  - Coral Urgent (`#E5604D`): Expired items, sick mood accents.
  - Deep Ink (`#14201B`): High-legibility text (avoids harsh `#000000`).

## Stitch Prompting Rules
1. **Never include hex codes or color names in new screen generation prompts** (Stitch applies these automatically from the project's design system).
2. Specify exact viewport: `DEVICE_TYPE: MOBILE`, vertical single-hand thumb navigation.
3. Structure prompts with clear sections:
   - Header & Status Bar
   - Hero / Creature Stage Area
   - Primary Interactive Cards
   - Bottom Action Bar / Navigation Dock
4. Emphasize tactile physical affordances: pill chips (`rounded-full`), soft floating depth, elevated cards.
