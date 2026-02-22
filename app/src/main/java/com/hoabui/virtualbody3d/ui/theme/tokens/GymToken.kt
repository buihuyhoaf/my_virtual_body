package com.hoabui.virtualbody3d.ui.theme.tokens

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import com.hoabui.virtualbody3d.ui.theme.tokens.component.BodyAnalysisTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.ButtonTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.CalendarTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.CardTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.ControlPanelTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.SliderTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.gymCalendarTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.gymBodyAnalysisTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.gymButtonTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.gymCardTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.gymControlPanelTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.gymSliderTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveColorTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveRadiusTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveSpacingTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.semantic.SemanticColorTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.semantic.darkSemanticColors
import com.hoabui.virtualbody3d.ui.theme.tokens.semantic.gymTypographyTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.semantic.lightSemanticColors

/**
 * Aggregated token contract for Rose Social Calm theme.
 */
@Immutable
data class GymToken(
    val colors: SemanticColorTokens,
    val spacing: SpacingTokens,
    val radius: RadiusTokens,
    val elevation: ElevationTokens,
    val typography: Typography,
    val button: ButtonTokens,
    val card: CardTokens,
    val slider: SliderTokens,
    val controlPanel: ControlPanelTokens,
    val bodyAnalysis: BodyAnalysisTokens,
    val calendar: CalendarTokens
)

@Immutable
data class SpacingTokens(
    val xxs: Dp,
    val xs: Dp,
    val md: Dp,
    val lg: Dp,
    val xl: Dp
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
    elevation: ElevationTokens = ElevationTokens.default()
): GymToken {
    val colors = darkSemanticColors(primitiveColors)
    return GymToken(
    colors = colors,
    spacing = SpacingTokens(
        xxs = primitiveSpacing.xxs,
        xs = primitiveSpacing.xs,
        md = primitiveSpacing.md,
        lg = primitiveSpacing.lg,
        xl = primitiveSpacing.xl
    ),
    radius = RadiusTokens(
        sm = primitiveRadius.sm,
        md = primitiveRadius.md,
        lg = primitiveRadius.lg,
        xl = primitiveRadius.xl,
        pill = primitiveRadius.pill
    ),
    elevation = elevation,
    typography = gymTypographyTokens().material,
    button = gymButtonTokens(primitiveSpacing, primitiveRadius),
    card = gymCardTokens(primitiveSpacing, primitiveRadius),
    slider = gymSliderTokens(primitiveSpacing),
    controlPanel = gymControlPanelTokens(primitiveSpacing),
    bodyAnalysis = gymBodyAnalysisTokens(primitiveSpacing),
    calendar = gymCalendarTokens(colors)
    )
}

fun lightGymToken(
    primitiveColors: PrimitiveColorTokens = PrimitiveColorTokens.default(),
    primitiveSpacing: PrimitiveSpacingTokens = PrimitiveSpacingTokens.default(),
    primitiveRadius: PrimitiveRadiusTokens = PrimitiveRadiusTokens.default(),
    elevation: ElevationTokens = ElevationTokens.default()
): GymToken {
    val colors = lightSemanticColors(primitiveColors)
    return GymToken(
    colors = colors,
    spacing = SpacingTokens(
        xxs = primitiveSpacing.xxs,
        xs = primitiveSpacing.xs,
        md = primitiveSpacing.md,
        lg = primitiveSpacing.lg,
        xl = primitiveSpacing.xl
    ),
    radius = RadiusTokens(
        sm = primitiveRadius.sm,
        md = primitiveRadius.md,
        lg = primitiveRadius.lg,
        xl = primitiveRadius.xl,
        pill = primitiveRadius.pill
    ),
    elevation = elevation,
    typography = gymTypographyTokens().material,
    button = gymButtonTokens(primitiveSpacing, primitiveRadius),
    card = gymCardTokens(primitiveSpacing, primitiveRadius),
    slider = gymSliderTokens(primitiveSpacing),
    controlPanel = gymControlPanelTokens(primitiveSpacing),
    bodyAnalysis = gymBodyAnalysisTokens(primitiveSpacing),
    calendar = gymCalendarTokens(colors)
    )
}
