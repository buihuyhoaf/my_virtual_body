package com.hoabui.virtualbody3d.ui.exerciselibrary.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.icon.GIcon
import com.hoabui.virtualbody3d.ui.common_ui.atom.field.GTextField
import com.hoabui.virtualbody3d.ui.common_ui.atom.surface.GSurface
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.wiring.ExerciseCatalogActions
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.icons.ExerciseLibraryPhosphorIcons
import com.hoabui.virtualbody3d.ui.theme.tokens.component.GSurfaceTreatment

@Composable
fun ExerciseSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    GSurface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(token.radius.pill),
        color = token.colors.surface,
        shadowElevation = token.elevation.level1,
        treatment = GSurfaceTreatment.Flat,
        border = null,
    ) {
        GTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = token.spacing.lg + token.spacing.md),
            placeholder = stringResource(R.string.exercise_library_search_hint),
            textStyle = token.typography.bodyMedium,
            placeholderStyle = token.typography.bodyMedium,
            shape = RoundedCornerShape(token.radius.pill),
            leadingIcon = {
                GIcon(
                    imageVector = ExerciseLibraryPhosphorIcons.search,
                    contentDescription = null,
                    modifier = Modifier.size(token.spacing.md),
                    tint = token.colors.textSecondary,
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        )
    }
}

/**
 * Search field in the exercise library header.
 */
@Composable
fun ExerciseLibrarySearchLayer(
    state: ExerciseLibraryUiState,
    actions: ExerciseCatalogActions,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        ExerciseSearchBar(
            query = state.filters.searchQuery,
            onQueryChange = actions.onQueryChange,
        )
    }
}
