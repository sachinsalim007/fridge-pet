---
name: android-a11y-audit
description: >-
  Audits Jetpack Compose Android screens for TalkBack accessibility, touch target sizes,
  color contrast, semantic headings, and font scaling. Use during Phase 8 or whenever reviewing UI screens.
---

# Android Compose Accessibility Audit

Review modified Compose screens against these checks:

1. **TalkBack Labels (`contentDescription`)**:
   - Every standalone `IconButton`, `Icon` conveying meaning, and custom `Canvas` (like `FridgePetAvatar`) must have a clear `contentDescription` (or `Modifier.semantics { contentDescription = ... }`).
   - Decorative icons should pass `contentDescription = null`.
2. **Touch Targets (48dp minimum)**:
   - Interactive controls (`IconButton`, `FilterChip`, custom clickable rows) must be at least `48.dp x 48.dp` (Compose Material 3 components handle this by default; avoid overriding with smaller fixed `.size(...)` on clickable containers).
3. **Color Contrast & Color Independence**:
   - Never communicate expiry urgency by color alone — always pair colors with text labels (`"Expired 1d ago"`, `"Due today"`, `"2d left"`).
   - Ensure text over tinted containers (`MintContainer`, `ApricotContainer`, `BerryContainer`) has high contrast.
4. **Font Scaling & Layout**:
   - Avoid fixed heights (`height(48.dp)`) on cards or text containers that hold multi-line text; use `heightIn(min = ...)` or `wrapContentHeight()` so large accessibility font sizes do not clip text.
5. **Semantics**:
   - Mark section titles (`"Needs Attention"`, shelf headers) with `Modifier.semantics { heading() }` where helpful for TalkBack navigation.
