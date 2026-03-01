package com.hoabui.virtualbody3d.ui.createbaseline.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.Typography
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.theme.tokens.ElevationTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.RadiusTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.SpacingTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.CreateBaselineTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.component.OnboardingTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.semantic.SemanticColorTokens

@Composable
fun CreateBaselineActionsSection(
    colors: SemanticColorTokens,
    spacing: SpacingTokens,
    radius: RadiusTokens,
    createBaselineTokens: CreateBaselineTokens,
    onboardingTokens: OnboardingTokens,
    elevation: ElevationTokens,
    typography: Typography,
    buttonsEnabled: Boolean = true,
    onCapture: () -> Unit,
    onUpload: () -> Unit
) {

    Button(
        onClick = onCapture,
        modifier = Modifier
            .fillMaxWidth()
            .height(onboardingTokens.primaryButtonHeight),
        enabled = buttonsEnabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.primary,
            disabledContainerColor = colors.surfaceSubtle,
            disabledContentColor = colors.textMuted
        ),
        shape = RoundedCornerShape(radius.lg),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = elevation.level0)
    ) {
        Icon(
            imageVector = Icons.Outlined.CameraAlt,
            contentDescription = null,
            modifier = Modifier.size(createBaselineTokens.buttonIconSize)
        )
        Spacer(modifier = Modifier.size(createBaselineTokens.buttonIconTextGap))
        Text(
            text = stringResource(R.string.create_baseline_capture),
            style = typography.titleMedium
        )
    }
    Spacer(modifier = Modifier.height(spacing.md))

    OutlinedButton(
        onClick = onUpload,
        modifier = Modifier
            .fillMaxWidth()
            .height(onboardingTokens.primaryButtonHeight),
        enabled = buttonsEnabled,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = colors.primary,
            disabledContentColor = colors.textMuted
        ),
        border = BorderStroke(createBaselineTokens.borderWidth, colors.primary),
        shape = RoundedCornerShape(radius.lg)
    ) {
        Icon(
            imageVector = Icons.Outlined.FileUpload,
            contentDescription = null,
            modifier = Modifier.size(createBaselineTokens.buttonIconSize)
        )
        Spacer(modifier = Modifier.size(createBaselineTokens.buttonIconTextGap))
        Text(
            text = stringResource(R.string.create_baseline_upload),
            style = typography.titleMedium
        )
    }
}
