package com.hoabui.virtualbody3d.ui.initialsetup.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.theme.tokens.ElevationTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.RadiusTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.SpacingTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.OnboardingTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.semantic.SemanticColorTokens

@Composable
fun InitialSetupFooter(
    currentStep: Int,
    totalSteps: Int,
    colors: SemanticColorTokens,
    spacing: SpacingTokens,
    typography: Typography,
    radius: RadiusTokens,
    onboardingTokens: OnboardingTokens,
    elevation: ElevationTokens,
    isNextEnabled: Boolean = true,
    onSkip: () -> Unit,
    onPrimaryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.md)
    ) {
        if (currentStep < totalSteps - 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onSkip,
                    colors = ButtonDefaults.textButtonColors(contentColor = colors.primary),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text(
                        text = stringResource(R.string.initial_setup_skip),
                        style = typography.titleMedium
                    )
                }
            }
        }
        Button(
            onClick = onPrimaryClick,
            enabled = isNextEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(onboardingTokens.primaryButtonHeight),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.primary,
                disabledContainerColor = colors.surfaceSubtle,
                disabledContentColor = colors.textMuted
            ),
            shape = RoundedCornerShape(radius.lg),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = elevation.level0)
        ) {
            Text(
                text = if (currentStep == totalSteps - 1) {
                    stringResource(R.string.initial_setup_begin)
                } else {
                    stringResource(R.string.initial_setup_continue)
                },
                style = typography.titleMedium
            )
            Spacer(modifier = Modifier.size(spacing.xxs))
            if (currentStep < totalSteps - 1) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}