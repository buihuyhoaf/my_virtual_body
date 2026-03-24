package com.hoabui.virtualbody3d.ui.addworkout.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.field.GTextField
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.GymTheme

/**
 * Optional multi-line text input for workout notes.
 * Uses [GymTheme.token] for typography and styling via [GTextField].
 */
@Composable
fun NotesField(
    notes: String,
    onNotesChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    Column(modifier = modifier) {
        GText(
            text = stringResource(R.string.add_workout_notes),
            style = token.typography.titleSmall,
            color = token.colors.textPrimary,
            modifier = Modifier.padding(bottom = token.spacing.xxs)
        )
        GTextField(
            value = notes,
            onValueChange = onNotesChange,
            placeholder = stringResource(R.string.add_workout_notes_hint),
            singleLine = false,
            maxLines = 4,
        )
    }
}
