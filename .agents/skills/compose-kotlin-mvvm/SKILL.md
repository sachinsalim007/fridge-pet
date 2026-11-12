---
name: compose-kotlin-mvvm
description: >-
  Guides Jetpack Compose, Kotlin, Material 3, Room, and MVVM development for Fridge Pet.
  Use when creating or editing Android screens, ViewModels, Room entities/DAOs, or Compose UI components.
---

# Compose, Kotlin & MVVM Guide (Fridge Pet)

## Architecture Rules
1. Keep UI screens stateless where possible: pass `FridgeUiState` down and event lambdas (`onClick`, `onValueChange`) up.
2. Put business logic (mood calculation, expiry day math, AED totals) in pure Kotlin helpers (`domain/`) or `FridgeRepository`, not inside `@Composable` functions.
3. Collect flows lifecycle-safely in Compose using `collectAsStateWithLifecycle()`.
4. Keep heavy work (bitmap decoding/compression, file I/O, HTTP calls) on `Dispatchers.IO`.

## Compose & Material 3 Checklist
- Use `MaterialTheme.colorScheme` and `MaterialTheme.typography` from `ui/theme/Theme.kt` rather than hardcoded colors or font sizes.
- Provide `key = { ... }` in `LazyColumn` / `LazyRow` `items(...)` calls so list animations and scroll performance stay smooth.
- Always design all 4 UI states: **Loading**, **Empty**, **Error** (with retry or offline fallback), and **Content**.
- Add a `@Preview` or verify layout visually using `compose-preview` / emulator when UI changes.
