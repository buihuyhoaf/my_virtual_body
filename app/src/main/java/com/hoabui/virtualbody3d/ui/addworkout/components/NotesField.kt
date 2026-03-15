package com.hoabui.virtualbody3d.ui.addworkout.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.theme.GymTheme

/**
 * Optional multi-line text input for workout notes.
 * Uses [GymTheme.token] for typography and styling.
 */
@Composable
fun NotesField(
    notes: String,
    onNotesChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.add_workout_notes),
            style = token.typography.titleSmall,
            color = token.colors.textPrimary,
            modifier = Modifier.padding(bottom = token.spacing.xxs)
        )
        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = stringResource(R.string.add_workout_notes_hint),
                    style = token.typography.bodyMedium,
                    color = token.colors.textPlaceholder
                )
            },
            minLines = 2,
            maxLines = 4,
            shape = RoundedCornerShape(token.radius.sm),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = token.colors.primary,
                unfocusedBorderColor = token.colors.borderStrong,
                focusedTextColor = token.colors.textPrimary,
                unfocusedTextColor = token.colors.textPrimary,
                cursorColor = token.colors.primary,
                focusedContainerColor = token.colors.surface,
                unfocusedContainerColor = token.colors.surface
            )
        )
    }
}
