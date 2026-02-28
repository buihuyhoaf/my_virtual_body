package com.hoabui.virtualbody3d.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.graphics.Color
import com.hoabui.virtualbody3d.ui.theme.tokens.semantic.SemanticColorTokens

/**
 * OutlinedTextField colors from design tokens. Reusable across login, sign-up, and other screens.
 * Use [outlinedTextFieldColors] with optional icon color overrides.
 */
@Composable
fun outlinedTextFieldColors(
    colors: SemanticColorTokens,
    focusedLeadingIconColor: Color = colors.textSecondary,
    unfocusedLeadingIconColor: Color = colors.textSecondary,
    focusedTrailingIconColor: Color = colors.primary,
    unfocusedTrailingIconColor: Color = colors.textMuted
) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = colors.primary,
    unfocusedBorderColor = colors.borderStrong,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    cursorColor = colors.primary,
    focusedTextColor = colors.textBlack,
    unfocusedTextColor = colors.textBlack,
    focusedLeadingIconColor = focusedLeadingIconColor,
    unfocusedLeadingIconColor = unfocusedLeadingIconColor,
    focusedTrailingIconColor = focusedTrailingIconColor,
    unfocusedTrailingIconColor = unfocusedTrailingIconColor
)
