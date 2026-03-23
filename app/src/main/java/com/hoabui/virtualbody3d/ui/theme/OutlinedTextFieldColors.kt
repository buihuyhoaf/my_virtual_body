package com.hoabui.virtualbody3d.ui.theme

import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.hoabui.virtualbody3d.ui.theme.tokens.semantic.SemanticColorTokens

/**
 * OutlinedTextField colors from design tokens. Reusable across login, sign-up, and other screens.
 *
 * When [isError] is `true`, border, cursor, and label colors switch to [SemanticColorTokens.error]
 * so that error states are visually distinct without any extra configuration at call sites.
 *
 * Use [outlinedTextFieldColors] with optional icon color overrides.
 */
@Composable
fun outlinedTextFieldColors(
    colors: SemanticColorTokens,
    isError: Boolean = false,
    focusedLeadingIconColor: Color = colors.textSecondary,
    unfocusedLeadingIconColor: Color = colors.textSecondary,
    focusedTrailingIconColor: Color = if (isError) colors.error else colors.primary,
    unfocusedTrailingIconColor: Color = if (isError) colors.error else colors.textMuted,
): TextFieldColors {
    val borderFocused = if (isError) colors.error else colors.primary
    val borderUnfocused = if (isError) colors.error else colors.borderStrong
    return OutlinedTextFieldDefaults.colors(
        focusedBorderColor = borderFocused,
        unfocusedBorderColor = borderUnfocused,
        errorBorderColor = colors.error,
        focusedContainerColor = colors.backgroundTransparent,
        unfocusedContainerColor = colors.backgroundTransparent,
        errorContainerColor = colors.backgroundTransparent,
        cursorColor = if (isError) colors.error else colors.primary,
        errorCursorColor = colors.error,
        focusedTextColor = colors.textBlack,
        unfocusedTextColor = colors.textBlack,
        errorTextColor = colors.textBlack,
        focusedLeadingIconColor = focusedLeadingIconColor,
        unfocusedLeadingIconColor = unfocusedLeadingIconColor,
        errorLeadingIconColor = focusedLeadingIconColor,
        focusedTrailingIconColor = focusedTrailingIconColor,
        unfocusedTrailingIconColor = unfocusedTrailingIconColor,
        errorTrailingIconColor = colors.error,
        focusedLabelColor = if (isError) colors.error else colors.primary,
        unfocusedLabelColor = colors.textSecondary,
        errorLabelColor = colors.error,
        focusedPlaceholderColor = colors.textPlaceholder,
        unfocusedPlaceholderColor = colors.textPlaceholder,
        errorPlaceholderColor = colors.textPlaceholder,
        focusedSupportingTextColor = colors.textMuted,
        unfocusedSupportingTextColor = colors.textMuted,
        errorSupportingTextColor = colors.error,
    )
}
