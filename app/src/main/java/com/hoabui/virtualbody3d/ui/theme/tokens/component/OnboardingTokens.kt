package com.hoabui.virtualbody3d.ui.theme.tokens.component

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveBorderTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveSpacingTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.semantic.gymOnboardingLayoutSemantics

/**
 * Design tokens for onboarding screen: pager indicator dots and primary CTA button.
 * Sizes are derived from primitive spacing where possible.
 */
@Immutable
data class OnboardingTokens(
    val dotSize: Dp,
    val dotGap: Dp,
    val primaryButtonHeight: Dp,
    val illustrationBodyWidth: Dp,
    val illustrationBodyHeight: Dp,
    val illustrationScannerSize: Dp,
    val illustrationJournalWidth: Dp,
    val illustrationJournalHeight: Dp,
    val illustrationBaseUnit: Dp,
    val illustrationFineUnit: Dp,
    val illustrationStrokeThin: Dp,
    val illustrationStrokeStandard: Dp,
    val illustrationStrokeBold: Dp
)

fun gymOnboardingTokens(
    primitiveSpacing: PrimitiveSpacingTokens,
    border: PrimitiveBorderTokens,
): OnboardingTokens {
    val layout = gymOnboardingLayoutSemantics()
    return OnboardingTokens(
        dotSize = primitiveSpacing.xs,
        dotGap = primitiveSpacing.xxs,
        primaryButtonHeight = primitiveSpacing.xl + primitiveSpacing.lg,
        illustrationBodyWidth = layout.illustrationBodyWidth,
        illustrationBodyHeight = layout.illustrationBodyHeight,
        illustrationScannerSize = layout.illustrationScannerSize,
        illustrationJournalWidth = layout.illustrationJournalWidth,
        illustrationJournalHeight = layout.illustrationJournalHeight,
        illustrationBaseUnit = layout.illustrationBaseUnit,
        illustrationFineUnit = layout.illustrationFineUnit,
        illustrationStrokeThin = border.hairline,
        illustrationStrokeStandard = border.thin,
        illustrationStrokeBold = border.medium
    )
}
