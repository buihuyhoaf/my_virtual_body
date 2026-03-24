package com.hoabui.virtualbody3d.ui.initialsetup.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.theme.tokens.SpacingTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.OnboardingTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.semantic.SemanticColorTokens

@Composable
fun InitialSetupHeader(
    currentStep: Int,
    totalSteps: Int,
    colors: SemanticColorTokens,
    spacing: SpacingTokens,
    typography: Typography,
    onboardingTokens: OnboardingTokens,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GText(
            text = stringResource(R.string.initial_setup_step, currentStep + 1),
            style = typography.labelMedium,
            color = colors.textSecondary
        )
        Spacer(modifier = Modifier.height(spacing.xs))
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(totalSteps) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = spacing.xxs)
                        .size(onboardingTokens.dotSize)
                        .clip(CircleShape)
                        .background(
                            if (index <= currentStep) colors.primary
                            else colors.initialSetupProgressDotUnselected
                        )
                )
            }
        }
    }
}
