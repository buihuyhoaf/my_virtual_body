package com.hoabui.virtualbody3d.ui.theme.tokens

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import com.hoabui.virtualbody3d.ui.theme.tokens.component.BodyAnalysisTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.BodyDetailTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.ButtonTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.CalendarTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.CameraTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.CardTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.ChatTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.CreateBaselineTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.ControlPanelTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.LoginTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.MealTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.OnboardingTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.SliderTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.StatusPopupTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.SurfaceTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.ThinkingCardTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.gymBodyDetailTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.gymCalendarTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.gymCameraTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.gymChatTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.gymCreateBaselineTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.gymLoginTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.gymMealTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.gymOnboardingTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.gymBodyAnalysisTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.gymButtonTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.gymCardTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.gymControlPanelTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.gymSliderTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.gymStatusPopupTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.gymSurfaceTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.gymThinkingCardTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveAlphaTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveBorderTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveColorTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveRadiusTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveSpacingTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.semantic.SemanticColorTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.semantic.darkSemanticColors
import com.hoabui.virtualbody3d.ui.theme.tokens.semantic.gymTypographyTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.semantic.lightSemanticColors

/**
 * Aggregated token contract for warm terracotta body-inspired theme.
 */
@Immutable
data class GymToken(
    val colors: SemanticColorTokens,
    val spacing: SpacingTokens,
    val radius: RadiusTokens,
    val elevation: ElevationTokens,
    val motion: MotionTokens,
    val borderWidth: BorderWidthTokens,
    val typography: Typography,
    val surface: SurfaceTokens,
    val button: ButtonTokens,
    val card: CardTokens,
    val chat: ChatTokens,
    val slider: SliderTokens,
    val controlPanel: ControlPanelTokens,
    val bodyAnalysis: BodyAnalysisTokens,
    val calendar: CalendarTokens,
    val onboarding: OnboardingTokens,
    val login: LoginTokens,
    val createBaseline: CreateBaselineTokens,
    val camera: CameraTokens,
    val statusPopup: StatusPopupTokens,
    val thinkingCard: ThinkingCardTokens,
    val meal: MealTokens,
    val bodyDetail: BodyDetailTokens
)

@Immutable
data class SpacingTokens(
    val xxxs: Dp,
    val xxs: Dp,
    val xs: Dp,
    val sm: Dp,
    val md: Dp,
    val lg: Dp,
    val xl: Dp,
    val xxl: Dp,
    val xxxl: Dp,
    /** Leading icons in list rows (from primitive icon scale). */
    val iconMedium: Dp,
    val dividerThickness: Dp
)

@Immutable
data class RadiusTokens(
    val sm: Dp,
    val md: Dp,
    val lg: Dp,
    val xl: Dp,
    val pill: Dp
)

fun darkGymToken(
    primitiveColors: PrimitiveColorTokens = PrimitiveColorTokens.default(),
    primitiveSpacing: PrimitiveSpacingTokens = PrimitiveSpacingTokens.default(),
    primitiveRadius: PrimitiveRadiusTokens = PrimitiveRadiusTokens.default(),
    primitiveBorder: PrimitiveBorderTokens = PrimitiveBorderTokens.default(),
    primitiveAlpha: PrimitiveAlphaTokens = PrimitiveAlphaTokens,
    elevation: ElevationTokens = ElevationTokens.default(),
    motion: MotionTokens = MotionTokens.default(),
    borderWidth: BorderWidthTokens = BorderWidthTokens.default(),
    typography: Typography = gymTypographyTokens().material
): GymToken {
    val colors = darkSemanticColors(primitiveColors)
    return GymToken(
    colors = colors,
    spacing = SpacingTokens(
        xxxs = primitiveSpacing.xxxs,
        xxs = primitiveSpacing.xxs,
        xs = primitiveSpacing.xs,
        sm = primitiveSpacing.sm,
        md = primitiveSpacing.md,
        lg = primitiveSpacing.lg,
        xl = primitiveSpacing.xl,
        xxl = primitiveSpacing.xxl,
        xxxl = primitiveSpacing.xxxl,
        iconMedium = primitiveSpacing.iconMedium,
        dividerThickness = primitiveSpacing.dividerThickness
    ),
    radius = RadiusTokens(
        sm = primitiveRadius.sm,
        md = primitiveRadius.md,
        lg = primitiveRadius.lg,
        xl = primitiveRadius.xl,
        pill = primitiveRadius.pill
    ),
    elevation = elevation,
    motion = motion,
    borderWidth = borderWidth,
    typography = typography,
    surface = gymSurfaceTokens(elevation),
    button = gymButtonTokens(primitiveSpacing, primitiveRadius, primitiveBorder, primitiveAlpha),
    card = gymCardTokens(primitiveSpacing, primitiveRadius, elevation),
    chat = gymChatTokens(primitiveSpacing),
    slider = gymSliderTokens(primitiveSpacing),
    controlPanel = gymControlPanelTokens(primitiveSpacing),
    bodyAnalysis = gymBodyAnalysisTokens(primitiveSpacing, primitiveBorder, primitiveAlpha, elevation),
    calendar = gymCalendarTokens(colors, primitiveSpacing, primitiveBorder),
    onboarding = gymOnboardingTokens(primitiveSpacing, primitiveBorder),
    login = gymLoginTokens(primitiveSpacing, primitiveRadius),
    createBaseline = gymCreateBaselineTokens(primitiveSpacing, primitiveColors, primitiveBorder, primitiveAlpha),
    camera = gymCameraTokens(primitiveSpacing),
    statusPopup = gymStatusPopupTokens(primitiveSpacing, primitiveRadius, primitiveBorder),
    thinkingCard = gymThinkingCardTokens(primitiveSpacing, primitiveRadius, elevation, primitiveAlpha),
    meal = gymMealTokens(primitiveSpacing),
    bodyDetail = gymBodyDetailTokens()
    )
}

fun lightGymToken(
    primitiveColors: PrimitiveColorTokens = PrimitiveColorTokens.default(),
    primitiveSpacing: PrimitiveSpacingTokens = PrimitiveSpacingTokens.default(),
    primitiveRadius: PrimitiveRadiusTokens = PrimitiveRadiusTokens.default(),
    primitiveBorder: PrimitiveBorderTokens = PrimitiveBorderTokens.default(),
    primitiveAlpha: PrimitiveAlphaTokens = PrimitiveAlphaTokens,
    elevation: ElevationTokens = ElevationTokens.default(),
    motion: MotionTokens = MotionTokens.default(),
    borderWidth: BorderWidthTokens = BorderWidthTokens.default(),
    typography: Typography = gymTypographyTokens().material
): GymToken {
    val colors = lightSemanticColors(primitiveColors)
    return GymToken(
    colors = colors,
    spacing = SpacingTokens(
        xxxs = primitiveSpacing.xxxs,
        xxs = primitiveSpacing.xxs,
        xs = primitiveSpacing.xs,
        sm = primitiveSpacing.sm,
        md = primitiveSpacing.md,
        lg = primitiveSpacing.lg,
        xl = primitiveSpacing.xl,
        xxl = primitiveSpacing.xxl,
        xxxl = primitiveSpacing.xxxl,
        iconMedium = primitiveSpacing.iconMedium,
        dividerThickness = primitiveSpacing.dividerThickness
    ),
    radius = RadiusTokens(
        sm = primitiveRadius.sm,
        md = primitiveRadius.md,
        lg = primitiveRadius.lg,
        xl = primitiveRadius.xl,
        pill = primitiveRadius.pill
    ),
    elevation = elevation,
    motion = motion,
    borderWidth = borderWidth,
    typography = typography,
    surface = gymSurfaceTokens(elevation),
    button = gymButtonTokens(primitiveSpacing, primitiveRadius, primitiveBorder, primitiveAlpha),
    card = gymCardTokens(primitiveSpacing, primitiveRadius, elevation),
    chat = gymChatTokens(primitiveSpacing),
    slider = gymSliderTokens(primitiveSpacing),
    controlPanel = gymControlPanelTokens(primitiveSpacing),
    bodyAnalysis = gymBodyAnalysisTokens(primitiveSpacing, primitiveBorder, primitiveAlpha, elevation),
    calendar = gymCalendarTokens(colors, primitiveSpacing, primitiveBorder),
    onboarding = gymOnboardingTokens(primitiveSpacing, primitiveBorder),
    login = gymLoginTokens(primitiveSpacing, primitiveRadius),
    createBaseline = gymCreateBaselineTokens(primitiveSpacing, primitiveColors, primitiveBorder, primitiveAlpha),
    camera = gymCameraTokens(primitiveSpacing),
    statusPopup = gymStatusPopupTokens(primitiveSpacing, primitiveRadius, primitiveBorder),
    thinkingCard = gymThinkingCardTokens(primitiveSpacing, primitiveRadius, elevation, primitiveAlpha),
    meal = gymMealTokens(primitiveSpacing),
    bodyDetail = gymBodyDetailTokens()
    )
}
