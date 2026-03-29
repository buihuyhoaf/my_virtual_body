package com.hoabui.virtualbody3d.ui.exerciselibrary.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButton
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButtonVariant
import com.hoabui.virtualbody3d.ui.common_ui.atom.card.GCard
import com.hoabui.virtualbody3d.ui.common_ui.atom.chip.GFilterChip
import com.hoabui.virtualbody3d.ui.common_ui.atom.field.GTextField
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.common_ui.image.LocalResourceProvider
import com.hoabui.virtualbody3d.ui.common_ui.organism.exercise.GExerciseCardUiModel
import com.hoabui.virtualbody3d.ui.common_ui.organism.exercise.GExerciseSectionCardRow
import com.hoabui.virtualbody3d.ui.common_ui.organism.exercise.GExerciseSectionUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.ExerciseLibraryQuickChip
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.ExerciseDisplayResources
import com.hoabui.virtualbody3d.ui.exerciselibrary.model.toCoilModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.selectedQuickChip
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseSectionUiItem
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.defaultExerciseLibraryCartDateMillis
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.defaultExerciseLibraryCartTime
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.isAnchoredAddEnabled
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveAlphaTokens
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

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
 * High-density anchored console: docked slab, pill metadata chips, compact precision row (no auto-focus / prefill).
 */
@Composable
fun ExerciseLibrarySelectionBar(
    libraryState: ExerciseLibraryUiState,
    onSelectCartItem: (String) -> Unit,
    onClearAll: () -> Unit,
    onCartDateSelected: (Long) -> Unit,
    onCartTimeSelected: (LocalTime) -> Unit,
    onActiveDraftChange: (sets: String, reps: String) -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val zone = ZoneId.systemDefault()
    val barMin = token.bodyAnalysis.exerciseLibrarySelectionBarMinHeight
    val fieldWidth = token.bodyAnalysis.exerciseLibraryCartNumericFieldWidth
    val addButtonMaxWidth = token.bodyAnalysis.exerciseLibrarySelectionBarAddButtonMaxWidth
    val topCorner = token.bodyAnalysis.exerciseLibrarySelectionBarTopCornerRadius
    val dockTopThickness = token.bodyAnalysis.exerciseLibraryAnchoredConsoleTopBorderWidth
    val precisionH = token.bodyAnalysis.exerciseLibraryConsolePrecisionRowHeight
    val slabShape = remember(topCorner, token.borderWidth.none) {
        RoundedCornerShape(
            topStart = topCorner,
            topEnd = topCorner,
            bottomStart = token.borderWidth.none,
            bottomEnd = token.borderWidth.none,
        )
    }
    val fieldShape = remember(token.bodyAnalysis.upcomingExerciseChipImageCornerRadius) {
        RoundedCornerShape(token.bodyAnalysis.upcomingExerciseChipImageCornerRadius)
    }
    val compactInputStyle = token.typography.labelSmall
    val compactPlaceholderStyle =
        token.typography.labelSmall.copy(color = token.colors.borderStrong)
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val cartItems = remember(libraryState.sections, libraryState.itemDrafts) {
        val byId = libraryState.sections.asSequence()
            .flatMap { it.items.asSequence() }
            .associateBy { it.id }
        libraryState.itemDrafts.keys.mapNotNull { byId[it] }
    }
    val chooseDatePlaceholder = stringResource(R.string.exercise_library_cart_choose_date)
    val chooseTimePlaceholder = stringResource(R.string.exercise_library_cart_choose_time)
    val dateButtonLabelEx = libraryState.selectedDate?.let { millis ->
        val d = Instant.ofEpochMilli(millis).atZone(zone).toLocalDate()
        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).format(d)
    }
    val timeButtonLabelEx = libraryState.selectedTime?.let { t ->
        DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault()).format(t)
    }
    val activeDraft = libraryState.activeExerciseId?.let { libraryState.itemDrafts[it] }
    val repsCurrent = activeDraft?.reps ?: ""
    val setsCurrent = activeDraft?.sets ?: ""
    val consoleSnapshot = remember { mutableStateOf(libraryState) }
    consoleSnapshot.value = libraryState
    val addEnabled by remember {
        derivedStateOf { consoleSnapshot.value.isAnchoredAddEnabled() }
    }
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = barMin),
        shape = slabShape,
        color = token.colors.surface,
        shadowElevation = token.elevation.level3,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = dockTopThickness,
                color = token.colors.borderStrong.copy(alpha = PrimitiveAlphaTokens.MEDIUM),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = barMin)
                    .padding(
                        start = token.spacing.sm,
                        end = token.spacing.sm,
                        top = token.spacing.sm,
                        bottom = token.spacing.sm,
                    ),
                verticalArrangement = Arrangement.spacedBy(token.spacing.sm),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
                ) {
                    LazyRow(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        items(cartItems, key = { it.id }) { item ->
                            ExerciseLibraryCartThumbnail(
                                item = item,
                                isActive = item.id == libraryState.activeExerciseId,
                                onClick = { onSelectCartItem(item.id) },
                            )
                        }
                    }
                    GText(
                        text = stringResource(R.string.exercise_library_cart_clear_all),
                        style = token.typography.labelSmall,
                        color = token.colors.error,
                        modifier = Modifier
                            .clickable(onClick = onClearAll)
                            .padding(start = token.spacing.xs),
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(token.spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        onClick = { showDatePicker = true },
                        shape = RoundedCornerShape(token.radius.pill),
                        color = token.colors.surfaceSubtle,
                        modifier = Modifier.weight(1f),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = token.spacing.sm,
                                    vertical = token.spacing.xs,
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CalendarMonth,
                                contentDescription = null,
                                modifier = Modifier.size(token.bodyAnalysis.heroSlimChipIconSize),
                                tint = token.colors.primary,
                            )
                            GText(
                                text = dateButtonLabelEx ?: chooseDatePlaceholder,
                                style = token.typography.labelMedium,
                                color = token.colors.textPrimary,
                                maxLines = 1,
                            )
                        }
                    }
                    Surface(
                        onClick = { showTimePicker = true },
                        shape = RoundedCornerShape(token.radius.pill),
                        color = token.colors.surfaceSubtle,
                        modifier = Modifier.weight(1f),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = token.spacing.sm,
                                    vertical = token.spacing.xs,
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(token.bodyAnalysis.heroSlimChipIconSize),
                                tint = token.colors.primary,
                            )
                            GText(
                                text = timeButtonLabelEx ?: chooseTimePlaceholder,
                                style = token.typography.labelMedium,
                                color = token.colors.textPrimary,
                                maxLines = 1,
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(precisionH),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
                ) {
                    Box(
                        modifier = Modifier
                            .width(fieldWidth)
                            .height(precisionH),
                    ) {
                        GTextField(
                            value = setsCurrent,
                            onValueChange = { raw ->
                                val filtered = raw.filter { it.isDigit() }.take(3)
                                onActiveDraftChange(filtered, repsCurrent)
                            },
                            modifier = Modifier.fillMaxSize(),
                            label = null,
                            placeholder = stringResource(R.string.exercise_library_console_sets_placeholder),
                            singleLine = true,
                            textStyle = compactInputStyle,
                            placeholderStyle = compactPlaceholderStyle,
                            shape = fieldShape,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        )
                    }
                    Box(
                        modifier = Modifier.width(token.spacing.sm),
                        contentAlignment = Alignment.Center,
                    ) {
                        GText(
                            text = stringResource(R.string.exercise_library_console_times_operator),
                            style = token.typography.labelSmall,
                            color = token.colors.borderStrong,
                        )
                    }
                    Box(
                        modifier = Modifier
                            .width(fieldWidth)
                            .height(precisionH),
                    ) {
                        GTextField(
                            value = repsCurrent,
                            onValueChange = { raw ->
                                val filtered = raw.filter { it.isDigit() }.take(3)
                                onActiveDraftChange(setsCurrent, filtered)
                            },
                            modifier = Modifier.fillMaxSize(),
                            label = null,
                            placeholder = stringResource(R.string.exercise_library_console_reps_placeholder),
                            singleLine = true,
                            textStyle = compactInputStyle,
                            placeholderStyle = compactPlaceholderStyle,
                            shape = fieldShape,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Surface(
                        onClick = onConfirm,
                        modifier = Modifier
                            .height(precisionH)
                            .widthIn(max = addButtonMaxWidth),
                        enabled = addEnabled,
                        shape = RoundedCornerShape(token.button.cornerRadius),
                        color = token.colors.primary,
                        contentColor = token.colors.onPrimary,
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            GText(
                                text = stringResource(R.string.exercise_library_cart_add_short),
                                style = token.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = token.colors.onPrimary,
                            )
                        }
                    }
                }
            }
        }
    }
    if (showDatePicker) {
        ExerciseLibraryCartDatePickerDialog(
            initialSelectedDateMillis = libraryState.selectedDate
                ?: defaultExerciseLibraryCartDateMillis(),
            onConfirm = { millis ->
                onCartDateSelected(millis)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false },
        )
    }
    if (showTimePicker) {
        ExerciseLibraryCartTimePickerDialog(
            initialTime = libraryState.selectedTime ?: defaultExerciseLibraryCartTime(),
            onConfirm = { time ->
                onCartTimeSelected(time)
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false },
        )
    }
}

@Composable
private fun ExerciseLibraryCartThumbnail(
    item: GExerciseCardUiModel,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val resourceProvider = LocalResourceProvider.current
    val coilModel = remember(item.id, item.image) { item.image.toCoilModel(resourceProvider) }
    val size = token.bodyAnalysis.exerciseLibraryCartThumbnailSize
    val activeInset = token.bodyAnalysis.exerciseLibraryCartThumbnailActiveInset
    val borderWidth = if (isActive) token.borderWidth.medium else token.borderWidth.hairline
    val borderColor = if (isActive) token.colors.primary else token.colors.borderSubtle
    Surface(
        onClick = onClick,
        modifier = modifier.size(size),
        shape = CircleShape,
        color = token.colors.surface,
        border = BorderStroke(borderWidth, borderColor),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isActive) Modifier.padding(activeInset) else Modifier,
                ),
        ) {
            AsyncImage(
                model = coilModel,
                contentDescription = item.title,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExerciseLibraryCartDatePickerDialog(
    initialSelectedDateMillis: Long,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    val zone = ZoneId.systemDefault()
    val state = rememberDatePickerState(initialSelectedDateMillis = initialSelectedDateMillis)
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            GButton(
                text = stringResource(android.R.string.ok),
                onClick = {
                    state.selectedDateMillis?.let { millis ->
                        val localDate = Instant.ofEpochMilli(millis).atZone(zone).toLocalDate()
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExerciseLibraryCartTimePickerDialog(
    initialTime: LocalTime,
    onConfirm: (LocalTime) -> Unit,
    onDismiss: () -> Unit,
) {
    val state = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = true,
    )
    Dialog(onDismissRequest = onDismiss) {
        val token = GymTheme.token
        GCard(containerColor = token.colors.surface) {
            Column(
                modifier = Modifier.padding(token.spacing.md),
                verticalArrangement = Arrangement.spacedBy(token.spacing.md),
            ) {
                TimePicker(state = state)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(token.spacing.xs, Alignment.End),
                ) {
                    GButton(
                        text = stringResource(android.R.string.cancel),
                        onClick = onDismiss,
                        variant = GButtonVariant.Ghost,
                    )
                    GButton(
                        text = stringResource(android.R.string.ok),
                        onClick = {
                            onConfirm(LocalTime.of(state.hour, state.minute))
                        },
                    )
                }
            }
        }
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
