package com.hoabui.virtualbody3d.ui.theme.tokens.component

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveSpacingTokens

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
    val illustrationJournalHeight: Dp
) {
    companion object {
        fun default(): OnboardingTokens = OnboardingTokens(
            dotSize = 8.dp,
            dotGap = 4.dp,
            primaryButtonHeight = 56.dp,
            illustrationBodyWidth = 240.dp,
            illustrationBodyHeight = 320.dp,
            illustrationScannerSize = 240.dp,
            illustrationJournalWidth = 240.dp,
            illustrationJournalHeight = 120.dp
        )
    }
}

fun gymOnboardingTokens(primitiveSpacing: PrimitiveSpacingTokens): OnboardingTokens =
    OnboardingTokens(
        dotSize = primitiveSpacing.xs,
        dotGap = primitiveSpacing.xxs,
        primaryButtonHeight = primitiveSpacing.xl + primitiveSpacing.lg,
        illustrationBodyWidth = 240.dp,
        illustrationBodyHeight = 320.dp,
        illustrationScannerSize = 240.dp,
        illustrationJournalWidth = 240.dp,
        illustrationJournalHeight = 120.dp
    )
