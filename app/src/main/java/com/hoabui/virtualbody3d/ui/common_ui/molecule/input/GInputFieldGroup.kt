package com.hoabui.virtualbody3d.ui.common_ui.molecule.input

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hoabui.virtualbody3d.ui.common_ui.atom.field.GTextField
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.GymTheme

/**
 * Input field molecule: external label above the field + [GTextField] below.
 *
 * The **external label** (rendered via [GText] above the field) is distinct from the
 * floating `label` parameter inside [GTextField] (which floats inside the M3 outline).
 * Use this molecule when the design calls for a static label sitting above the field.
 *
 * @param label Static label text displayed above the field.
 * @param value Current field value.
 * @param onValueChange Called on every keystroke.
 * @param placeholder Hint text shown when [value] is empty.
 * @param helperText Supporting / error text shown below the field.
 * @param isError When `true`, field border and [helperText] use error colours.
 * @param leadingIcon Optional composable at the start of the field.
 * @param trailingIcon Optional composable at the end of the field.
 * @param keyboardOptions IME options for the field.
 * @param keyboardActions IME action callbacks.
 * @param singleLine When `true` (default), field stays on one line.
 * @param maxLines Maximum lines when [singleLine] = `false`.
 */
@Composable
fun GInputFieldGroup(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    helperText: String? = null,
    isError: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
) {
    val token = GymTheme.token
    Column(modifier = modifier) {
        GText(
            text = label,
            style = token.typography.titleSmall,
            color = token.colors.textPrimary,
            modifier = Modifier.padding(bottom = token.spacing.xxs),
        )
        GTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            supportingText = helperText,
            isError = isError,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            maxLines = maxLines,
        )
    }
}
