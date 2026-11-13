---
name: pet-3d-character-artist
description: >-
  Guides designing, modeling, and animating 3D and 2.5D interactive companion characters for Fridge Pet.
  Use when creating Spline scenes, Rive state machines, Blender clay assets, or Compose Canvas creatures.
---

# 3D Pet Character Artist Guide (Fridge Pet)

## Visual Signature
- **Style**: Soft clay / matte silicone jelly aesthetic with subtle studio rim lighting and soft ambient occlusion shadows.
- **Form Factor**: Round, squishy, friendly silhouettes inspired by food ingredients (mochi, sesame puff, chilli pepper, ice cube, laban droplet).

## Touch & Pampering Interactions
1. **Idle State**: Gentle rhythmic breathing (2s period), random eye blinks every 3–4 seconds.
2. **Tap / Poke**: 18% elastic squash on Y-axis with quick rebound and happy chirp.
3. **Continuous Petting (Rubbing)**:
   - Eyes crescent-closed in bliss.
   - Blush cheeks brighten to pink.
   - Floating heart particles drift upward with fading opacity.
4. **Mood Visual Indicators**:
   - `Content`: Relaxed smile, upright posture.
   - `Anxious`: Subtle cyan shivering, sweat drop, nervous wavy mouth.
   - `Sick`: Olive-green body tint, spiral/X eyes, dizzy bubbles.
   - `Celebrating`: Confetti particles, waving arms, wide open smile.
   - `Sleepy`: Gentle floating Zzz, dimmed lighting.

## Implementation Formats
- **Prototype**: Custom Jetpack Compose Canvas (built-in, 0 MB, instant load).
- **Interactive 2.5D**: Rive (`.riv`) State Machine with `isPetted`, `poke`, and `mood` inputs.
- **Real 3D**: Spline 3D (`.splinecode`) or Blender glTF/GLB with bone rigs.
