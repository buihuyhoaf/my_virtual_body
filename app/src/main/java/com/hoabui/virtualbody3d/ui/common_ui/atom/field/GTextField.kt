package com.hoabui.virtualbody3d.ui.common_ui.atom.field

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.outlinedTextFieldColors

// ─────────────────────────────────────────────────────────────────────────────
// GTextField
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Tokenized outlined text field atom for the Gym design system.
 *
 * Wraps M3's [OutlinedTextField] with:
 * - Shape from `GymTheme.token.radius.md`
 * - All colours from `outlinedTextFieldColors()` (which reads `GymTheme.token.colors`)
 * - Error state: border, cursor, and supporting text switch to `token.colors.error`
 *   automatically when [isError] = `true`
 * - Accessibility: `semantics { error(supportingText) }` is applied when [isError] = `true`
 *   so TalkBack reads the error message alongside the field
 *
 * ### Icon slots
 * [leadingIcon] and [trailingIcon] receive [androidx.compose.material3.LocalContentColor]
 * equal to the field's current state colour (focused primary, error red, etc.) via
 * `outlinedTextFieldColors`, so bare `Icon(...)` calls inside the slots auto-tint correctly.
 *
 * @param value Current field value.
 * @param onValueChange Called on every keystroke.
 * @param label Floating label text. Rendered by M3's `OutlinedTextField` label mechanism.
 * @param placeholder Hint text shown when [value] is empty and the field is unfocused.
 * @param supportingText Helper / error text shown below the field.
 *   Rendered in `token.colors.error` when [isError] = `true`, else `token.colors.textMuted`.
 * @param isError When `true`, field border, cursor, and [supportingText] use error colours.
 * @param leadingIcon Optional composable rendered at the start of the field.
 * @param trailingIcon Optional composable rendered at the end of the field.
 * @param textStyle Optional typography for input text; defaults to [LocalTextStyle].
 * @param placeholderStyle Optional typography for [placeholder]; defaults to [GymTheme.token.typography.bodyLarge].
 * @param shape Optional outline shape; defaults to `token.radius.md`.
 * @param visualTransformation Use [PasswordVisualTransformation] for password fields.
 * @param singleLine When `true` (default), field stays on one line and sends IME action.
 * @param maxLines Maximum lines when [singleLine] = `false`.
 */
@Composable
fun GTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    supportingText: String? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    textStyle: TextStyle? = null,
    placeholderStyle: TextStyle? = null,
    shape: Shape? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
) {
    val token = GymTheme.token
    val resolvedTextStyle = textStyle ?: LocalTextStyle.current
    val resolvedPlaceholderStyle = placeholderStyle ?: token.typography.bodyLarge

    // Announce error message to accessibility services (TalkBack)
    val semanticsModifier = if (isError && !supportingText.isNullOrBlank()) {
        modifier.semantics { error(supportingText) }
    } else {
        modifier
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = semanticsModifier.fillMaxWidth(),
        enabled = enabled,
        readOnly = readOnly,
        textStyle = resolvedTextStyle,
        label = if (label != null) {
            { GText(text = label, style = token.typography.bodyMedium) }
        } else null,
        placeholder = if (placeholder != null) {
            {
                GText(
                    text = placeholder,
                    style = resolvedPlaceholderStyle,
                    color = token.colors.textPlaceholder,
                )
            }
        } else null,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        supportingText = if (supportingText != null) {
            {
                GText(
                    text = supportingText,
                    style = token.typography.labelSmall,
                    color = if (isError) token.colors.error else token.colors.textMuted,
                )
            }
        } else null,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        shape = shape ?: RoundedCornerShape(token.radius.md),
        colors = outlinedTextFieldColors(
            colors = token.colors,
            isError = isError,
        ),
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "GTextField — States")
@Composable
private fun PreviewGTextFieldStates() {
    GymTheme {
        val token = GymTheme.token
        Column(
            modifier = Modifier.padding(token.spacing.md),
            verticalArrangement = Arrangement.spacedBy(token.spacing.md),
        ) {
            // Empty / default
            GTextField(
                value = "",
                onValueChange = {},
                placeholder = "Enter your email",
                label = "Email",
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = null)
                },
            )

            // Filled
            GTextField(
                value = "john.doe@example.com",
                onValueChange = {},
                label = "Email",
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = null)
                },
            )

            // With supporting text
            GTextField(
                value = "john.doe",
                onValueChange = {},
                label = "Email",
                supportingText = "Enter a full email address including @",
            )

            // Error state
            GTextField(
                value = "not-an-email",
                onValueChange = {},
                label = "Email",
                supportingText = "Invalid email address",
                isError = true,
            )

            // Password with toggle (demonstrates trailingIcon slot)
            var passwordVisible by remember { mutableStateOf(false) }
            GTextField(
                value = "secret123",
                onValueChange = {},
                label = "Password",
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) {
                                Icons.Default.VisibilityOff
                            } else {
                                Icons.Default.Visibility
                            },
                            contentDescription = if (passwordVisible) {
                                "Hide password"
                            } else {
                                "Show password"
                            },
                        )
                    }
                },
            )

            // Disabled
            GTextField(
                value = "readonly@example.com",
                onValueChange = {},
                label = "Email",
                enabled = false,
            )

            // Multi-line
            GTextField(
                value = "This is a longer\nnote that spans\nmultiple lines.",
                onValueChange = {},
                label = "Notes",
                singleLine = false,
                maxLines = 4,
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "GTextField — Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewGTextFieldDark() {
    GymTheme(darkTheme = true) {
        val token = GymTheme.token
        Column(
            modifier = Modifier.padding(token.spacing.md),
            verticalArrangement = Arrangement.spacedBy(token.spacing.md),
        ) {
            GTextField(
                value = "",
                onValueChange = {},
                label = "Email",
                placeholder = "Enter your email",
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = null)
                },
            )
            GTextField(
                value = "bad-input",
                onValueChange = {},
                label = "Email",
                supportingText = "Invalid email address",
                isError = true,
            )
        }
    }
}
