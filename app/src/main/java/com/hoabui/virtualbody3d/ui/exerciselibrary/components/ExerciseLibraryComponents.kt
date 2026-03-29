package com.hoabui.virtualbody3d.ui.exerciselibrary.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDatePickerState
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButton
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButtonVariant
import com.hoabui.virtualbody3d.ui.common_ui.atom.field.GTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.chip.GFilterChip
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.common_ui.organism.exercise.GExerciseSectionCardRow
import com.hoabui.virtualbody3d.ui.common_ui.organism.exercise.GExerciseSectionUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.ExerciseLibraryQuickChip
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.ExerciseDisplayResources
import com.hoabui.virtualbody3d.ui.exerciselibrary.selectedQuickChip
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseSectionUiItem
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun ExerciseSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchFocusChange: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(token.radius.pill),
        color = token.colors.surface,
        shadowElevation = token.elevation.level1,
    ) {
        GTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = token.spacing.lg + token.spacing.md)
                .onFocusChanged { onSearchFocusChange(it.isFocused) },
            placeholder = stringResource(R.string.exercise_library_search_hint),
            textStyle = token.typography.bodyMedium,
            placeholderStyle = token.typography.bodyMedium,
            shape = RoundedCornerShape(token.radius.pill),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(token.spacing.md),
                    tint = token.colors.textSecondary,
                )
            },
            singleLine = true,
        )
    }
}

@Composable
fun ExerciseSearchSuggestionChips(
    libraryState: ExerciseLibraryUiState,
    onQuickChipSelect: (ExerciseLibraryQuickChip?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val selected = libraryState.selectedQuickChip()
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
        contentPadding = PaddingValues(
            top = token.spacing.xs,
            bottom = token.spacing.xxs,
        ),
    ) {
        items(ExerciseLibraryQuickChip.entries.toList(), key = { it.name }) { chip ->
            GFilterChip(
                label = stringResource(chip.labelRes),
                selected = chip == selected,
                onSelectedChange = { nowSelected ->
                    if (nowSelected) onQuickChipSelect(chip) else onQuickChipSelect(null)
                },
                labelStyle = token.typography.labelSmall,
            )
        }
    }
}

/**
 * Anchored bottom slab: header row (name + date), action row (sets/reps + add).
 */
@Composable
fun ExerciseLibrarySelectionBar(
    libraryState: ExerciseLibraryUiState,
    onDraftChange: (reps: Int, sets: Int, dateMillis: Long) -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val zone = ZoneId.systemDefault()
    val barMin = token.bodyAnalysis.exerciseLibrarySelectionBarMinHeight
    val fieldWidth = token.bodyAnalysis.exerciseLibraryCartNumericFieldWidth
    val addButtonMaxWidth = token.bodyAnalysis.exerciseLibrarySelectionBarAddButtonMaxWidth
    val slabShape = remember(token.radius.md, token.borderWidth.none) {
        RoundedCornerShape(
            topStart = token.radius.md,
            topEnd = token.radius.md,
            bottomStart = token.borderWidth.none,
            bottomEnd = token.borderWidth.none,
        )
    }
    val fieldShape = remember(token.radius.md) {
        RoundedCornerShape(token.radius.md)
    }
    var showDatePicker by remember { mutableStateOf(false) }
    val dateLabel = remember(libraryState.selectedDate) {
        val d = Instant.ofEpochMilli(libraryState.selectedDate).atZone(zone).toLocalDate()
        DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(d)
    }
    val selectedExerciseName = remember(libraryState.sections, libraryState.selectedExerciseId) {
        val selectedId = libraryState.selectedExerciseId ?: return@remember ""
        libraryState.sections
            .asSequence()
            .flatMap { it.items.asSequence() }
            .firstOrNull { item -> item.id == selectedId }
            ?.title
            .orEmpty()
    }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = barMin),
        shape = slabShape,
        color = token.colors.surface,
        shadowElevation = token.elevation.level2,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = barMin)
                .padding(
                    horizontal = token.spacing.md,
                    vertical = token.spacing.sm,
                ),
            verticalArrangement = Arrangement.spacedBy(token.spacing.sm),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(token.spacing.sm),
            ) {
                GText(
                    text = selectedExerciseName,
                    style = token.typography.labelLarge,
                    color = token.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                GButton(
                    text = dateLabel,
                    onClick = { showDatePicker = true },
                    variant = GButtonVariant.Ghost,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
            ) {
                Box(modifier = Modifier.width(fieldWidth)) {
                    GTextField(
                        value = libraryState.globalSets.toString(),
                        onValueChange = { raw ->
                            val digits = raw.filter { it.isDigit() }.take(3)
                            digits.toIntOrNull()?.let { s ->
                                onDraftChange(
                                    libraryState.globalReps,
                                    s.coerceAtLeast(1),
                                    libraryState.selectedDate,
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = null,
                        placeholder = null,
                        singleLine = true,
                        textStyle = token.typography.labelLarge,
                        placeholderStyle = token.typography.labelLarge,
                        shape = fieldShape,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = {
                            GText(
                                text = stringResource(R.string.exercise_library_cart_placeholder_sets),
                                style = token.typography.labelSmall,
                                color = token.colors.textMuted,
                            )
                        },
                    )
                }
                Box(modifier = Modifier.width(fieldWidth)) {
                    GTextField(
                        value = libraryState.globalReps.toString(),
                        onValueChange = { raw ->
                            val digits = raw.filter { it.isDigit() }.take(3)
                            digits.toIntOrNull()?.let { r ->
                                onDraftChange(
                                    r.coerceAtLeast(1),
                                    libraryState.globalSets,
                                    libraryState.selectedDate,
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = null,
                        placeholder = null,
                        singleLine = true,
                        textStyle = token.typography.labelLarge,
                        placeholderStyle = token.typography.labelLarge,
                        shape = fieldShape,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = {
                            GText(
                                text = stringResource(R.string.exercise_library_cart_placeholder_reps),
                                style = token.typography.labelSmall,
                                color = token.colors.textMuted,
                            )
                        },
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                GButton(
                    text = stringResource(R.string.exercise_library_cart_add),
                    onClick = onConfirm,
                    variant = GButtonVariant.Primary,
                    modifier = Modifier.widthIn(max = addButtonMaxWidth),
                )
            }
        }
    }
    if (showDatePicker) {
        ExerciseLibraryCartDatePickerDialog(
            initialSelectedDateMillis = libraryState.selectedDate,
            onConfirm = { millis ->
                onDraftChange(libraryState.globalReps, libraryState.globalSets, millis)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExerciseLibraryCartDatePickerDialog(
    initialSelectedDateMillis: Long,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    val state = rememberDatePickerState(initialSelectedDateMillis = initialSelectedDateMillis)
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            GButton(
                text = stringResource(android.R.string.ok),
                onClick = {
                    state.selectedDateMillis?.let { millis ->
                        val zone = ZoneId.systemDefault()
                        val localDate =
                            Instant.ofEpochMilli(millis).atZone(zone).toLocalDate()
                        onConfirm(localDate.atStartOfDay(zone).toInstant().toEpochMilli())
                    } ?: onDismiss()
                },
            )
        },
        dismissButton = {
            GButton(
                text = stringResource(android.R.string.cancel),
                onClick = onDismiss,
                variant = GButtonVariant.Outlined,
            )
        },
    ) {
        DatePicker(state = state)
    }
}

@Composable
fun ExerciseLibraryEmptyState(modifier: Modifier = Modifier) {
    val token = GymTheme.token
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = token.spacing.lg, vertical = token.spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        GText(
            text = stringResource(R.string.exercise_library_empty_title),
            style = token.typography.titleMedium,
            color = token.colors.textPrimary,
        )
        GText(
            text = stringResource(R.string.exercise_library_empty_message),
            style = token.typography.bodyMedium,
            color = token.colors.textSecondary,
            modifier = Modifier.padding(top = token.spacing.sm),
        )
    }
}

@Composable
fun ExerciseSection(
    modifier: Modifier = Modifier,
    section: ExerciseSectionUiItem,
    onExerciseClick: (String) -> Unit = {},
    onQuickAdd: ((String) -> Unit)? = null,
    quickAddContentDescription: String = "",
) {
    val regionLabel = stringResource(ExerciseDisplayResources.bodyRegionResId(section.bodyRegion))
    val uiSection = remember(section.bodyRegion, section.items, regionLabel) {
        GExerciseSectionUiModel(
            id = section.bodyRegion.name,
            title = regionLabel,
            items = section.items,
        )
    }
    GExerciseSectionCardRow(
        section = uiSection,
        modifier = modifier,
        onItemClick = onExerciseClick,
        onQuickAdd = onQuickAdd,
        quickAddContentDescription = quickAddContentDescription,
    )
}
