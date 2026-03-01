# Design Token Audit — VirtualBody (Jetpack Compose)

Senior Android UI Architect evaluation of the current Design Token implementation for scalability and best practices.

---

## === DESIGN TOKEN STRUCTURE ===

**Centralized design system package:** Yes, but under feature package.

- **Location:** `com.hoabui.virtualbody3d.ui.theme` (and `ui.theme.tokens.*`). There is no separate `core/designsystem` or shared module; the design system lives inside the app’s `ui/theme` tree.
- **Single entry point:** `GymTheme` composable + `GymTheme.token` (backed by `LocalGymToken`). All tokens are consumed via `GymTheme.token` or `LocalGymToken.current`.

**Token separation:**

| Category | Present | Location | Notes |
|----------|---------|----------|--------|
| **Color** | Yes | `tokens/primitive/PrimitiveColorTokens.kt`, `tokens/semantic/SemanticColorTokens.kt` | Primitive → semantic mapping; legacy aliases (e.g. `dashboard*`, `surfaceBorder`) kept for compatibility. |
| **Typography** | Yes | `tokens/semantic/SemanticTypographyTokens.kt`, `font/InterFontFamily.kt` | Full Material3 `Typography` built from tokens; Inter variable font. |
| **Spacing** | Yes | `tokens/primitive/PrimitiveSpacingTokens.kt`, `GymToken.spacing` (`SpacingTokens`) | Scale: xxs, xs, md, lg, xl, xxl, xxxl (no `sm`). |
| **Shape** | Partial | `GymToken.radius` (`RadiusTokens`: sm, md, lg, xl, pill), `GymTheme` builds `MaterialTheme.shapes` from `token.radius` | Shapes are radius-based; no separate “shape tokens” object. |
| **Elevation** | Yes | `tokens/ElevationTokens.kt` (level0, level1, level2), exposed via `GymToken.elevation` | Used in some components; not yet used everywhere (e.g. cards). |

**Component tokens:** Yes. Dedicated token objects per feature/component: `ButtonTokens`, `CardTokens`, `SliderTokens`, `ControlPanelTokens`, `BodyAnalysisTokens`, `CalendarTokens`, `OnboardingTokens`, `LoginTokens`. They are composed from primitives (spacing, radius) or semantic colors.

**Immutability:** Yes. Token data classes are `@Immutable` and use `data class` with `val` (e.g. `GymToken`, `SemanticColorTokens`, `SpacingTokens`, `RadiusTokens`, `ElevationTokens`, component tokens).

**Gaps:**

- No standalone “shape tokens” type (only radius; corners are built as `RoundedCornerShape(token.radius.xx)` in theme/screens).
- `Color.kt` in `ui/theme` duplicates brand/semantic colors and defines `FitnessLightColorScheme` / `FitnessDarkColorScheme` that are **not** used by the app (app uses `GymTheme` → `token.colors.toMaterialColorScheme()`). This is dead code and a second source of truth.
- Primitive spacing has no `sm` (jump from `xs` to `md`); acceptable but can force hardcoded `4.dp`/`8.dp` where a token would be clearer.

---

## === HARDCODED VALUES FOUND ===

**Colors (raw hex in UI or token layer):**

| File | Line(s) | Value / usage |
|------|--------|----------------|
| `ui/theme/Color.kt` | 9–31, 70–94 | Full file: private/val `Color(0xFF...)` (Brand*, Light*, Dark*, Body*, GlassChip*, etc.). Not used by GymTheme; legacy. |
| `ui/theme/tokens/semantic/SemanticColorTokens.kt` | 67–68, 110–111 | `textPlaceholder = Color(0xFF757575)`, `textBlack = Color(0xFF000000)` (light); dark: `0xFFB0B0B0`, `0xFFFFFFFF`. Should be primitive-backed. |
| `ui/theme/tokens/primitive/PrimitiveColorTokens.kt` | 37–59 | Defines hex in `default()`; acceptable as single source for primitives. |

**Hardcoded dp (in UI, not in token definitions):**

| File | Line(s) | Value | Suggested |
|------|--------|--------|-----------|
| `ui/login/component/LoginForm.kt` | 120 | `PaddingValues(0.dp)` | Token e.g. `spacing.xxs` or explicit `0.dp` token if needed. |
| `ui/login/component/SocialLoginButton.kt` | 45, 74 | `BorderStroke(1.dp, ...)`, `size(20.dp)` | Token: e.g. `token.bodyAnalysis.topBarBorderWidth` or login token; icon size from token. |
| `ui/body/screen/BodyDashboardCommon.kt` | 38, 95 | `widthIn(min = 92.dp)` | BodyAnalysis token (e.g. chip min width). |
| `ui/body/screen/BodyDashboardCommon.kt` | 61, 72, 119, 127 | `size(22.dp)`, `size(13.dp)`, `size(22.dp)`, `strokeWidth = 4.dp`, `size(12.dp)` | Prefer `token.bodyAnalysis.*` (icon sizes, stroke). |
| `ui/calendar/screen/CalendarScreen.kt` | 131 | `padding(bottom = 14.dp)` | `token.spacing` (e.g. xs+xxs or new token). |
| `ui/calendar/screen/CalendarScreen.kt` | 170, 174–175 | `offset(y = (-12).dp)`, `bottomStart = 0.dp`, `bottomEnd = 0.dp` | Consider calendar tokens for layout. |
| `ui/calendar/screen/CalendarScreen.kt` | 178, 189, 231 | `BorderStroke(1.dp, ...)`, `height(4.dp)`, `height(1.dp)` | Token border width / divider height. |
| `ui/calendar/screen/CalendarScreen.kt` | 239, 309, 325, 332, 406, 411 | `spacedBy(8.dp)`, `padding(4.dp)`, `padding(6.dp)`, `padding(horizontal = 6.dp, vertical = 2.dp)`, `size(34.dp)`, `spacedBy(2.dp)` | Replace with `token.spacing.*` (xs, xxs, etc.). |
| `ui/body/screen/AnalysisPanel.kt` | 158 | `BorderStroke(1.dp, ...)` | Token border width. |
| `ui/body/screen/AnalysisPanel.kt` | 171, 177, 184, 193, 209–210, 222, 238–261, 328, 341, 350, 402, 415–416, 423, 439, 447–448, 454, 470, 508, 518, 530–534 | Many `4.dp`, `8.dp`, `999.dp`, `80.dp`, `8.dp`, `12.dp`, `2.dp`, `56.dp`, `2.dp`, `40.dp`, `20.dp`, `48.dp`, `4.dp`, `320.dp`, `8.dp`, `8.dp` | Use `token.spacing.*`, `token.radius.pill`, `token.bodyAnalysis.*` (e.g. ring size, icon sizes), and shared border/divider tokens. |
| `ui/onboarding/OnboardingScreen.kt` | 161–182, 241–249, 295–310 | Dozens of `X.dp.toPx()` in drawScope (illustration coords) | Optional: illustration-specific tokens if reused; else document as intentional. |

**Hardcoded shape (RoundedCornerShape with literal):**

| File | Line(s) | Value | Suggested |
|------|--------|--------|-----------|
| `ui/body/screen/AnalysisPanel.kt` | 184, 239, 416, 448, 454, 518, 534 | `RoundedCornerShape(999.dp)` | `token.radius.pill`. |

**Hardcoded fontSize:** None found in UI. All text styles use `MaterialTheme.typography` or token typography; sizes live in `SemanticTypographyTokens.kt` (acceptable).

---

## === MATERIAL3 STATUS ===

**MaterialTheme configuration:** Yes.

- `GymTheme` wraps `MaterialTheme` and supplies:
  - `colorScheme = token.colors.toMaterialColorScheme(darkTheme)`
  - `typography = token.typography`
  - `shapes = Shapes(small/medium/large)` from `token.radius.sm/md/lg`
- Entry point: `MainActivity.setContent { GymTheme { ... } }`.

**Light/dark:** Supported. `darkTheme` defaults to `isSystemInDarkTheme()`; `lightGymToken()` and `darkGymToken()` provide separate semantic color mappings; `toMaterialColorScheme(isDark)` returns `lightColorScheme` or `darkColorScheme`.

**Semantic vs raw colors:**

- **Semantic:** Primary, Surface, Background, Error, OnPrimary, OnError, etc. are used via `token.colors.*` and mapped into Material `ColorScheme`. Many screens use `token.colors` (e.g. primary, surfaceBorder, primarySoft, dashboardRingTrack).
- **Raw in token layer:** Only in `PrimitiveColorTokens.default()` and the four semantic overrides in `SemanticColorTokens` (textPlaceholder, textBlack for light/dark). Rest is semantic.
- **Legacy:** `Color.kt` exposes raw/feature names (BodyPrimary, GlassChipBorder, etc.). These are **not** referenced by the current app UI (grep shows no imports of these in screens). Only `GymTheme` + token drive the app. So Material3 is effectively driven by semantic tokens; the legacy file is dead for runtime theming.

**Remaining MaterialTheme usage in UI:** Three places still use MaterialTheme directly instead of token:

- `OnboardingScreen.kt:68` — `MaterialTheme.colorScheme.surface` → use `token.colors.surface`.
- `OnboardingScreen.kt:128` — `MaterialTheme.shapes.large` → use `RoundedCornerShape(token.radius.lg)` (or a wrapper).
- `BodyDashboardCommon.kt:77, 133` — `MaterialTheme.typography.labelLarge` → use `token.typography.labelLarge`.
- `BodyAnalysisScreen.kt:413` — `MaterialTheme.colorScheme.surface` in gradient → use `token.colors.surface`.

---

## === NAMING CONVENTION ===

**Semantic naming (good):** Primary, OnPrimary, Surface, Background, SurfaceElevated, SurfaceSubtle, BorderSubtle, BorderStrong, TextPrimary, TextSecondary, TextMuted, Error, OnError. These follow Material/semantic meaning.

**Legacy / feature-specific names (kept for compatibility):** SurfaceBorder, OutlineSoft, DashboardPanelBackground, DashboardRingTrack, DashboardMealCardBackground, CalendarYearBackground, CalendarSelectedBorder, SplashBackground, SplashCardBackground, etc. They are semantic in intent (e.g. “panel background”) but tied to feature names. Documented as compatibility layer; new code should prefer generic semantics (e.g. surface, surfaceSubtle).

**Primitive naming:** Primitive colors are named by role (primary, surfaceLight, outlineLight, textPrimaryLight) not by hue (e.g. Blue500). Good for re-skin.

**Raw color names in Color.kt:** BodyPrimary, GlassChipBorder, etc. — visual/feature names; file is legacy and unused by GymTheme.

---

## === SCALABILITY CHECK ===

**Re-skin in one place:** Largely yes.

- **Colors:** Change `PrimitiveColorTokens.default()` and/or semantic mapping in `lightSemanticColors` / `darkSemanticColors`; Material and token-driven UI follow.
- **Typography:** Change `SemanticTypographyTokens` / Inter font; `GymTheme` and `token.typography` propagate.
- **Spacing / radius / elevation:** Change primitives and component token factories; no need to touch screens that already use `GymTheme.token`.

**Coupling issues:**

- **Feature tokens:** BodyAnalysis, Calendar, Onboarding, Login tokens are feature-specific. Re-skin stays centralized, but adding a new feature requires new component tokens and wiring in `GymToken` / factory. Acceptable for a single app.
- **Legacy Color.kt:** Duplicates theme and would need to be updated or removed for a second brand; currently unused.
- **Hardcoded dp in screens:** CalendarScreen, AnalysisPanel, BodyDashboardCommon, Login components still have many literal dp values. Re-skin of “spacing feel” or component sizes would require editing those files.
- **Token arithmetic:** `ButtonTokens`: `height = spacing.lg + spacing.xxs`; `SliderTokens`: `trackThickness = spacing.xs / 2`, `thumbSize = spacing.md + spacing.xs`. Composed values are fine if documented; for strict one-place re-skin, consider explicit token properties (e.g. `buttonHeight`, `sliderThumbSize`) so scale changes don’t rely on arithmetic.

**Verdict:** Re-skin of colors/typography/radius/elevation and any token-backed spacing is centralized. Scalability is reduced by (1) remaining hardcoded dp/shape in UI and (2) mixed use of MaterialTheme vs token in a few composables.

---

## === ARCHITECTURE SCORE (1–10) ===

**Score: 7/10**

**Reasoning:**

- **Strengths:** Central token bundle (`GymToken`), CompositionLocal injection, immutable tokens, clear primitive → semantic → Material flow, light/dark support, component tokens per feature, typography and radius/elevation tokens, and most UI using `GymTheme.token`.
- **Deductions:**
  - **−0.5** Legacy `Color.kt` and unused Material schemes (dead code, two sources of truth).
  - **−0.5** Hardcoded dp and `999.dp` (pill) in Calendar, AnalysisPanel, BodyDashboardCommon, Login (should use spacing/radius/component tokens).
  - **−0.5** A few composables still use `MaterialTheme.colorScheme` / `MaterialTheme.typography` / `MaterialTheme.shapes` instead of token.
  - **−0.5** Semantic layer still has four raw hex overrides (textPlaceholder, textBlack) and many legacy feature-named semantics; plus token arithmetic in Button/Slider.

---

## === REFACTOR PLAN ===

1. **Remove or migrate legacy Color.kt**
   - Either delete `Color.kt` (and any stray references) or replace its usages with `GymTheme.token.colors.*` and then delete. Ensures a single source of truth for theme.

2. **Replace remaining MaterialTheme usage with token**
   - `OnboardingScreen`: use `token.colors.surface` and `RoundedCornerShape(token.radius.lg)` (or token-based shape).
   - `BodyDashboardCommon`: use `token.typography.labelLarge`.
   - `BodyAnalysisScreen`: use `token.colors.surface` in gradient list. After this, no UI should depend on `MaterialTheme` for colors/typography/shapes; only token (and thus MaterialTheme is fully driven by token).

3. **Semantic color overrides**
   - Move `textPlaceholder` and `textBlack` (light/dark) to `PrimitiveColorTokens` (e.g. `placeholderLight/Dark`, `onSurfaceLight/Dark`) and reference them in `SemanticColorTokens`. Removes raw hex from semantic layer.

4. **Introduce shared border/divider tokens**
   - Add e.g. `borderWidthDefault = 1.dp` and `dividerHeight = 1.dp` (and 4.dp if needed) in primitive or component tokens. Use in CalendarScreen, AnalysisPanel, LoginForm, SocialLoginButton to replace `BorderStroke(1.dp, ...)` and `height(1.dp)` / `height(4.dp)`.

5. **Replace hardcoded spacing in UI**
   - CalendarScreen: replace `14.dp`, `8.dp`, `4.dp`, `6.dp`, `2.dp`, `34.dp` with `token.spacing.*` (and calendar tokens if added).
   - AnalysisPanel: replace all literal spacing (4, 8, 2, etc.) and sizes (80, 56, 40, 20, 48, 12, 8) with `token.spacing.*`, `token.radius.pill`, and `token.bodyAnalysis.*` (add fields if needed, e.g. nutrition ring size, legend dot size).
   - BodyDashboardCommon: replace 92.dp, 22.dp, 13.dp, 4.dp, 12.dp with BodyAnalysis (or shared) tokens.
   - Login: replace `0.dp`, `1.dp`, `20.dp` with login/spacing tokens.

6. **Use radius.pill everywhere**
   - Replace every `RoundedCornerShape(999.dp)` with `RoundedCornerShape(token.radius.pill)` in AnalysisPanel and elsewhere.

7. **Optional: reduce token arithmetic**
   - Add explicit `buttonHeight`, `sliderThumbSize`, `sliderTrackThickness` (or similar) to ButtonTokens/SliderTokens and set them in factory from primitives. Keeps re-skin in one place without relying on `spacing.lg + spacing.xxs` in multiple places.

8. **Optional: illustration tokens**
   - If onboarding illustrations are ever themed or reused, extract key dimensions (e.g. stroke width, radii) into `OnboardingTokens` and use in drawScope; otherwise leave as-is and document.

9. **Documentation**
   - Add a short README or doc in `ui/theme` describing: token hierarchy (primitive → semantic → component), that `GymTheme.token` is the single API, and that new UI should use tokens only (no MaterialTheme direct, no hardcoded dp/color/shape). Optionally list which component tokens exist and when to add new ones.

After these steps, the design token implementation would align with best practices for a scalable Jetpack Compose app and support a single-place brand re-skin with minimal coupling to feature screens.
