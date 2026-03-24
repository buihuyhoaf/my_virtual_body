package com.hoabui.virtualbody3d.ui.addworkout.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.molecule.input.GInputFieldGroup

/**
 * Optional multi-line text input for workout notes.
 */
@Composable
fun NotesField(
    notes: String,
    onNotesChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    GInputFieldGroup(
        label = stringResource(R.string.add_workout_notes),
        value = notes,
        onValueChange = onNotesChange,
        placeholder = stringResource(R.string.add_workout_notes_hint),
        singleLine = false,
        maxLines = 4,
        modifier = modifier,
    )
}
