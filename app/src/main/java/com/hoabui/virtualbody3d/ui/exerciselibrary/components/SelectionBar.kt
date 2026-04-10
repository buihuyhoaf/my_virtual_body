package com.hoabui.virtualbody3d.ui.exerciselibrary.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import coil.compose.AsyncImage
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButton
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButtonVariant
import com.hoabui.virtualbody3d.ui.common_ui.atom.divider.GDivider
import com.hoabui.virtualbody3d.ui.common_ui.atom.icon.GIcon
import com.hoabui.virtualbody3d.ui.common_ui.atom.field.GTextField
import com.hoabui.virtualbody3d.ui.common_ui.atom.surface.GSurface
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.common_ui.image.LocalResourceProvider
import com.hoabui.virtualbody3d.ui.common_ui.organism.exercise.GExerciseCardUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.model.toCoilModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryActions
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.icons.ExerciseLibraryPhosphorIcons
import com.hoabui.virtualbody3d.ui.theme.tokens.component.GSurfaceTreatment
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveAlphaTokens

@Composable
fun CartThumbnailRow(
    cartItems: List<GExerciseCardUiModel>,
    activeExerciseId: String?,
    onSelectCartItem: (String) -> Unit,
    onRemoveCartItem: (String) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
    ) {
        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items(cartItems, key = { it.id }) { item ->
                val selectHandler = remember(item.id, onSelectCartItem) {
                    { onSelectCartItem(item.id) }
                }
                val removeHandler = remember(item.id, onRemoveCartItem) {
                    { onRemoveCartItem(item.id) }
                }
                ExerciseLibraryCartThumbnail(
                    item = item,
                    isActive = item.id == activeExerciseId,
                    onSelectCartItem = selectHandler,
                    onRemoveCartItem = removeHandler,
                )
            }
        }
        GButton(
            text = stringResource(R.string.exercise_library_cart_clear_all),
            onClick = onClearAll,
            modifier = Modifier.padding(start = token.spacing.xs),
            variant = GButtonVariant.Ghost,
            contentColor = token.colors.error,
        )
    }
}

@Composable
fun CartInputRow(
    measurementMode: ExerciseMeasurementMode,
    sets: String,
    reps: String,
    onSetsChange: (String) -> Unit,
    onRepsChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val fieldWidth = token.bodyAnalysis.exerciseLibraryCartNumericFieldWidth
    val precisionH = token.bodyAnalysis.exerciseLibraryConsolePrecisionRowHeight
    val fieldShape = remember(token.bodyAnalysis.upcomingExerciseChipImageCornerRadius) {
        RoundedCornerShape(token.bodyAnalysis.upcomingExerciseChipImageCornerRadius)
    }
    val compactInputStyle = token.typography.labelSmall
    val compactPlaceholderStyle =
        token.typography.labelSmall.copy(color = token.colors.borderStrong)
    val firstPlaceholder = when (measurementMode) {
        ExerciseMeasurementMode.Strength -> stringResource(R.string.exercise_library_console_sets_placeholder)
        ExerciseMeasurementMode.Duration -> stringResource(R.string.exercise_library_console_minutes_placeholder)
    }
    val secondPlaceholder = when (measurementMode) {
        ExerciseMeasurementMode.Strength -> stringResource(R.string.exercise_library_console_reps_placeholder)
        ExerciseMeasurementMode.Duration -> stringResource(R.string.exercise_library_console_seconds_placeholder)
    }
    Row(
        modifier = modifier
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
                value = sets,
                onValueChange = onSetsChange,
                modifier = Modifier.fillMaxSize(),
                label = null,
                placeholder = firstPlaceholder,
                singleLine = true,
                textStyle = compactInputStyle,
                placeholderStyle = compactPlaceholderStyle,
                shape = fieldShape,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }
        Box(
            modifier = Modifier.width(token.bodyAnalysis.heroSlimChipIconSize),
            contentAlignment = Alignment.Center,
        ) {
            when (measurementMode) {
                ExerciseMeasurementMode.Strength -> {
                    GText(
                        text = stringResource(R.string.exercise_library_console_times_operator),
                        style = token.typography.labelSmall,
                        color = token.colors.borderStrong,
                    )
                }
                ExerciseMeasurementMode.Duration -> {
                    GIcon(
                        imageVector = ExerciseLibraryPhosphorIcons.cartDurationTimer,
                        contentDescription = stringResource(R.string.exercise_library_console_timer_cd),
                        modifier = Modifier.size(token.bodyAnalysis.heroSlimChipIconSize),
                        tint = token.colors.textSecondary,
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .width(fieldWidth)
                .height(precisionH),
        ) {
            GTextField(
                value = reps,
                onValueChange = onRepsChange,
                modifier = Modifier.fillMaxSize(),
                label = null,
                placeholder = secondPlaceholder,
                singleLine = true,
                textStyle = compactInputStyle,
                placeholderStyle = compactPlaceholderStyle,
                shape = fieldShape,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }
    }
}

@Composable
fun ExerciseLibrarySelectionBar(
    libraryState: ExerciseLibraryUiState,
    actions: ExerciseLibraryActions,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val barMin = token.bodyAnalysis.exerciseLibrarySelectionBarMinHeight
    val topCorner = token.bodyAnalysis.exerciseLibrarySelectionBarTopCornerRadius
    val dockTopThickness = token.bodyAnalysis.exerciseLibraryAnchoredConsoleTopBorderWidth
    val slabShape = remember(topCorner, token.borderWidth.none) {
        RoundedCornerShape(
            topStart = topCorner,
            topEnd = topCorner,
            bottomStart = token.borderWidth.none,
            bottomEnd = token.borderWidth.none,
        )
    }
    val cartItems = remember(libraryState.libraryList.sections, libraryState.cart.draftOrder) {
        val byId = libraryState.libraryList.sections.asSequence()
            .flatMap { it.items.asSequence() }
            .associateBy { it.id }
        libraryState.cart.draftOrder.mapNotNull { byId[it] }
    }
    val activeDraft = libraryState.cart.activeExerciseId?.let { libraryState.cart.itemDrafts[it] }
    val repsCurrent = activeDraft?.reps ?: ""
    val setsCurrent = activeDraft?.sets ?: ""
    val activeMeasurementMode = libraryState.cart.activeExerciseId?.let { id ->
        libraryState.libraryList.exerciseMeasurementById[id]
    } ?: ExerciseMeasurementMode.Strength
    val bookingEnabled = libraryState.libraryList.isAddToSessionEnabled
    GSurface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = barMin),
        shape = slabShape,
        color = token.colors.surface,
        shadowElevation = token.elevation.level3,
        treatment = GSurfaceTreatment.Flat,
        border = null,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            GDivider(
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
                CartThumbnailRow(
                    cartItems = cartItems,
                    activeExerciseId = libraryState.cart.activeExerciseId,
                    onSelectCartItem = actions.onSelectCartItem,
                    onRemoveCartItem = actions.onRemoveCartItem,
                    onClearAll = actions.onClearCart,
                )
                CartInputRow(
                    measurementMode = activeMeasurementMode,
                    sets = setsCurrent,
                    reps = repsCurrent,
                    onSetsChange = { raw ->
                        val filtered = raw.filter { it.isDigit() }.take(3)
                        actions.onActiveDraftChange(filtered, repsCurrent)
                    },
                    onRepsChange = { raw ->
                        val filtered = raw.filter { it.isDigit() }.take(3)
                        actions.onActiveDraftChange(setsCurrent, filtered)
                    },
                )
                GButton(
                    text = stringResource(R.string.exercise_library_add_to_session),
                    onClick = actions.onAddToSession,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = bookingEnabled,
                )
            }
        }
    }
}

@Composable
private fun ExerciseLibraryCartThumbnail(
    item: GExerciseCardUiModel,
    isActive: Boolean,
    onSelectCartItem: () -> Unit,
    onRemoveCartItem: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val resourceProvider = LocalResourceProvider.current
    val coilModel = remember(item.id, item.image) { item.image.toCoilModel(resourceProvider) }
    val size = token.bodyAnalysis.exerciseLibraryCartThumbnailSize
    val activeInset = token.bodyAnalysis.exerciseLibraryCartThumbnailActiveInset
    val borderWidth = if (isActive) token.borderWidth.medium else token.borderWidth.hairline
    val borderColor = if (isActive) token.colors.primary else token.colors.borderSubtle
    val circularShape = remember(token.radius.pill) { RoundedCornerShape(token.radius.pill) }
    val removeTouch = token.bodyAnalysis.exerciseLibraryCartRemoveTouchTargetSize
    Box(modifier = modifier.size(size)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(BorderStroke(borderWidth, borderColor), circularShape)
                .clip(circularShape)
                .background(token.colors.surface),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (isActive) Modifier.padding(activeInset) else Modifier,
                    )
                    .clip(circularShape),
            ) {
                AsyncImage(
                    model = coilModel,
                    contentDescription = item.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(circularShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(),
                            onClick = onSelectCartItem,
                        ),
                    contentScale = ContentScale.Crop,
                )
            }
        }
        ExerciseLibraryCartRemoveSticker(
            onRemove = onRemoveCartItem,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(removeTouch),
        )
    }
}

@Composable
private fun ExerciseLibraryCartRemoveSticker(
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val removeCd = stringResource(R.string.exercise_library_cart_remove_item_cd)
    val visualDiameter = token.bodyAnalysis.exerciseLibraryCartRemoveStickerVisualDiameter
    val glyphSize = token.bodyAnalysis.exerciseLibraryCartRemoveGlyphSize
    Box(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true),
                role = Role.Button,
                onClick = onRemove,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier.size(visualDiameter),
            shape = CircleShape,
            color = token.colors.error,
            border = BorderStroke(
                token.borderWidth.hairline,
                token.colors.onError.copy(alpha = PrimitiveAlphaTokens.LOW),
            ),
            shadowElevation = token.elevation.level1,
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                GIcon(
                    imageVector = ExerciseLibraryPhosphorIcons.cartRemove,
                    contentDescription = removeCd,
                    modifier = Modifier.size(glyphSize),
                    tint = token.colors.onError,
                )
            }
        }
    }
}
