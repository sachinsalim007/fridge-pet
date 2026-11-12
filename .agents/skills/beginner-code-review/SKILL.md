---
name: beginner-code-review
description: >-
  Performs a beginner-friendly code review after completing a feature or phase.
  Checks diff cleanliness, error handling, tests, and explains any issues simply.
---

# Beginner-Friendly Code Review

When reviewing changes in Fridge Pet:

1. **Scope Check**: Confirm changes match the single requested feature and follow `GEMINI.md`.
2. **State & Edge Cases**:
   - What happens if the user has 0 groceries?
   - What happens if Gemini fails, returns 0 items, or the phone is offline?
   - What happens on screen rotation (state survives in `ViewModel` / `rememberSaveable`)?
3. **Readability**: Flag overly long composables (>120 lines) or duplicated logic that should be a small helper.
4. **Tests & Verification**:
   - Run unit tests (`./gradlew testDebugUnitTest` when Gradle is configured, or `npm run build` in `firebase/functions`).
   - Report findings in plain language grouped into **Must Fix**, **Nice to Improve**, and **What Looks Good**.
