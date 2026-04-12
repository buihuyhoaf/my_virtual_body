package com.hoabui.virtualbody3d.ui.exerciselibrary.components

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.verticalDrag
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import kotlin.math.absoluteValue
import kotlin.math.abs
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
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.CartSetField
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryActions
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.SetRowDraft
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.icons.ExerciseLibraryPhosphorIcons
import com.hoabui.virtualbody3d.ui.theme.tokens.component.GSurfaceTreatment
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveAlphaTokens
import kotlinx.collections.immutable.ImmutableList

// ─────────────────────────────────────────────────────────
// Thumbnail row (unchanged)
// ─────────────────────────────────────────────────────────

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

// ─────────────────────────────────────────────────────────
// Stepper control: [-] value [+]
// ─────────────────────────────────────────────────────────

/**
 * Reusable [-] value [+] stepper.
 * Tapping the value label opens a number-pad dialog for manual entry.
 */
@Composable
private fun StepperControl(
    label: String,
    displayValue: String,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    onManualInput: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val stepperButtonSize = token.bodyAnalysis.exerciseLibraryCartStepperButtonSize
    val stepperValueMinWidth = token.bodyAnalysis.exerciseLibraryCartStepperValueMinWidth
    var showDialog by remember { mutableStateOf(false) }
    var dialogInput by remember(displayValue) { mutableStateOf(displayValue) }

    val decreaseCd = stringResource(R.string.exercise_library_stepper_decrease_cd, label)
    val increaseCd = stringResource(R.string.exercise_library_stepper_increase_cd, label)
    val valueCd = stringResource(R.string.exercise_library_stepper_value_cd, label)
    val stepperShape = remember(token.radius.pill) { RoundedCornerShape(token.radius.pill) }
    val buttonShape = remember { CircleShape }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(token.spacing.spacingStep1),
    ) {
        GText(
            text = label,
            style = token.typography.labelSmall,
            color = token.colors.textSecondary,
        )
        Row(
            modifier = Modifier
                .background(
                    color = token.colors.surface,
                    shape = stepperShape,
                )
                .border(
                    BorderStroke(token.borderWidth.hairline, token.colors.borderSubtle),
                    stepperShape,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // [-] button
            Box(
                modifier = Modifier
                    .size(stepperButtonSize)
                    .clip(buttonShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true),
                        role = Role.Button,
                        onClickLabel = decreaseCd,
                        onClick = onDecrease,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                GText(
                    text = "−",
                    style = token.typography.titleMedium,
                    color = token.colors.primary,
                )
            }
            // Value (tap for manual input)
            Box(
                modifier = Modifier
                    .widthIn(min = stepperValueMinWidth)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true),
                        role = Role.Button,
                        onClickLabel = valueCd,
                    ) {
                        dialogInput = displayValue
                        showDialog = true
                    },
                contentAlignment = Alignment.Center,
            ) {
                GText(
                    text = displayValue,
                    style = token.typography.titleSmall,
                    color = token.colors.textPrimary,
                    modifier = Modifier.padding(horizontal = token.spacing.xxs),
                )
            }
            // [+] button
            Box(
                modifier = Modifier
                    .size(stepperButtonSize)
                    .clip(buttonShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true),
                        role = Role.Button,
                        onClickLabel = increaseCd,
                        onClick = onIncrease,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                GText(
                    text = "+",
                    style = token.typography.titleMedium,
                    color = token.colors.primary,
                )
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { GText(text = label, style = GymTheme.token.typography.titleSmall) },
            text = {
                GTextField(
                    value = dialogInput,
                    onValueChange = { dialogInput = it },
                    label = null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onManualInput(dialogInput)
                    showDialog = false
                }) {
                    GText(
                        text = stringResource(R.string.exercise_detail_ok),
                        style = GymTheme.token.typography.labelMedium,
                        color = GymTheme.token.colors.primary,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    GText(text = stringResource(android.R.string.cancel), style = GymTheme.token.typography.labelMedium, color = GymTheme.token.colors.textSecondary)
                }
            },
        )
    }
}

// ─────────────────────────────────────────────────────────
// One set row: Strength → Reps + Weight; Cardio → Minutes + Seconds
// ─────────────────────────────────────────────────────────

@Composable
private fun CartSetRowItem(
    setIndex: Int,
    row: SetRowDraft,
    measurementMode: ExerciseMeasurementMode,
    exerciseId: String,
    onStep: (field: CartSetField, delta: Int) -> Unit,
    onManual: (field: CartSetField, value: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val repsLabel = stringResource(R.string.exercise_library_stepper_reps_label)
    val weightLabel = stringResource(R.string.exercise_library_stepper_weight_label)
    val minutesLabel = stringResource(R.string.exercise_library_stepper_minutes_label)
    val secondsLabel = stringResource(R.string.exercise_library_stepper_seconds_label)
    val rowLabel = stringResource(R.string.exercise_library_set_row_label, setIndex + 1)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
    ) {
        GText(
            text = rowLabel,
            style = token.typography.labelSmall,
            color = token.colors.textSecondary,
            modifier = Modifier.width(token.spacing.xl),
        )
        when (measurementMode) {
            ExerciseMeasurementMode.Strength -> {
                StepperControl(
                    label = repsLabel,
                    displayValue = row.reps.toString(),
                    onDecrease = { onStep(CartSetField.REPS, -1) },
                    onIncrease = { onStep(CartSetField.REPS, +1) },
                    onManualInput = { onManual(CartSetField.REPS, it) },
                    modifier = Modifier.weight(1f),
                )
                StepperControl(
                    label = weightLabel,
                    displayValue = WEIGHT_FORMAT.format(row.weightKg),
                    onDecrease = { onStep(CartSetField.WEIGHT, -1) },
                    onIncrease = { onStep(CartSetField.WEIGHT, +1) },
                    onManualInput = { onManual(CartSetField.WEIGHT, it) },
                    modifier = Modifier.weight(1f),
                )
            }
            ExerciseMeasurementMode.Duration -> {
                StepperControl(
                    label = minutesLabel,
                    displayValue = row.minutes.toString(),
                    onDecrease = { onStep(CartSetField.MINUTES, -1) },
                    onIncrease = { onStep(CartSetField.MINUTES, +1) },
                    onManualInput = { onManual(CartSetField.MINUTES, it) },
                    modifier = Modifier.weight(1f),
                )
                StepperControl(
                    label = secondsLabel,
                    displayValue = row.seconds.toString(),
                    onDecrease = { onStep(CartSetField.SECONDS, -1) },
                    onIncrease = { onStep(CartSetField.SECONDS, +1) },
                    onManualInput = { onManual(CartSetField.SECONDS, it) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────
// Full stepper section: Sets header + per-row inputs
// ─────────────────────────────────────────────────────────

@Composable
fun CartSetStepperSection(
    exerciseId: String,
    setRows: ImmutableList<SetRowDraft>,
    measurementMode: ExerciseMeasurementMode,
    onStepField: (exerciseId: String, setIndex: Int, field: CartSetField, delta: Int) -> Unit,
    onSetFieldManual: (exerciseId: String, setIndex: Int, field: CartSetField, value: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val setListMax = token.bodyAnalysis.exerciseLibraryCartSetRowsListMaxHeight
    val setsLabel = stringResource(R.string.exercise_library_stepper_sets_label)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(token.spacing.xs),
    ) {
        // Sets stepper (controls row count)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            StepperControl(
                label = setsLabel,
                displayValue = setRows.size.toString(),
                onDecrease = { onStepField(exerciseId, 0, CartSetField.SETS, -1) },
                onIncrease = { onStepField(exerciseId, 0, CartSetField.SETS, +1) },
                onManualInput = { onSetFieldManual(exerciseId, 0, CartSetField.SETS, it) },
            )
        }

        GDivider(modifier = Modifier.fillMaxWidth(), color = token.colors.borderSubtle)

        // One row per set — LazyColumn with stable keys for performance
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = setListMax),
            verticalArrangement = Arrangement.spacedBy(token.spacing.xs),
        ) {
            itemsIndexed(
                items = setRows,
                key = { index, _ -> "${exerciseId}_set_$index" },
            ) { index, row ->
                CartSetRowItem(
                    setIndex = index,
                    row = row,
                    measurementMode = measurementMode,
                    exerciseId = exerciseId,
                    onStep = { field, delta -> onStepField(exerciseId, index, field, delta) },
                    onManual = { field, value -> onSetFieldManual(exerciseId, index, field, value) },
                    modifier = Modifier.animateItem(),
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────
// Drag Handle
// ─────────────────────────────────────────────────────────

@Composable
private fun CartDragHandle(
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val handleW = token.bodyAnalysis.exerciseLibraryCartDragHandleWidth
    val handleH = token.bodyAnalysis.exerciseLibraryCartDragHandleHeight
    val handleColor = token.colors.borderStrong.copy(alpha = PrimitiveAlphaTokens.MEDIUM)
    val pillShape = remember(token.radius.pill) { RoundedCornerShape(token.radius.pill) }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = token.spacing.xs),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .width(handleW)
                .height(handleH)
                .background(
                    color = handleColor,
                    shape = pillShape,
                ),
        )
    }
}

// ─────────────────────────────────────────────────────────
// Selection bar (dual-state: collapsed / expanded)
// ─────────────────────────────────────────────────────────

private const val EXPANSION_REVEAL_EPSILON = 0.02f

@Composable
fun ExerciseLibrarySelectionBar(
    libraryState: ExerciseLibraryUiState,
    actions: ExerciseLibraryActions,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val density = LocalDensity.current
    val topCorner = token.bodyAnalysis.exerciseLibrarySelectionBarTopCornerRadius
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
    val activeId = libraryState.cart.activeExerciseId
    val activeDraft = activeId?.let { libraryState.cart.itemDrafts[it] }
    val activeMeasurementMode = activeId?.let { id ->
        libraryState.libraryList.exerciseMeasurementById[id]
    } ?: ExerciseMeasurementMode.Strength
    val bookingEnabled = libraryState.libraryList.isAddToSessionEnabled
    val isCartExpanded = libraryState.cart.isCartExpanded
    val activeExerciseTitle = remember(activeId, cartItems) {
        activeId?.let { id -> cartItems.firstOrNull { it.id == id }?.title }
    }

    val collapsedInsetDp = token.bodyAnalysis.exerciseLibrarySelectionBarCollapsedListBottomInset
    val collapsedHeightPx = remember(collapsedInsetDp, density) {
        with(density) { collapsedInsetDp.toPx() }
    }
    val fallbackExtraPx = remember(token.bodyAnalysis.exerciseLibraryCartExpandedContentFallbackExtra, density) {
        with(density) { token.bodyAnalysis.exerciseLibraryCartExpandedContentFallbackExtra.toPx() }
    }

    var expandedMeasuredPx by remember { mutableFloatStateOf(0f) }
    val expandedHeightPx = remember(expandedMeasuredPx, collapsedHeightPx, fallbackExtraPx) {
        if (expandedMeasuredPx > 0f) expandedMeasuredPx
        else collapsedHeightPx + fallbackExtraPx
    }

    var expansionProgress by remember {
        mutableFloatStateOf(if (libraryState.cart.isCartExpanded) 1f else 0f)
    }
    var isDragging by remember { mutableStateOf(false) }

    val onToggleUpdated = rememberUpdatedState(actions.onToggleCartExpanded)
    val isCartExpandedUpdated = rememberUpdatedState(isCartExpanded)
    val velThreshold = token.bodyAnalysis.exerciseLibraryCartSnapVelocityThresholdPxPerSec
    val tapSlopPx = remember(token.spacing.xs, density) {
        with(density) { token.spacing.xs.toPx() }
    }

    LaunchedEffect(isCartExpanded, isDragging) {
        if (!isDragging) {
            val target = if (isCartExpanded) 1f else 0f
            if ((expansionProgress - target).absoluteValue > 0.001f) {
                animate(
                    initialValue = expansionProgress,
                    targetValue = target,
                    animationSpec = tween(
                        durationMillis = token.motion.duration.standard,
                        easing = token.motion.easing.standard,
                    ),
                ) { v, _ ->
                    expansionProgress = v
                }
            }
        }
    }

    val barHeightPx = remember(collapsedHeightPx, expandedHeightPx, expansionProgress) {
        collapsedHeightPx + (expandedHeightPx - collapsedHeightPx) * expansionProgress
    }
    val barHeightDp = with(density) { barHeightPx.toDp() }

    val revealDetail =
        isCartExpanded || isDragging || expansionProgress > EXPANSION_REVEAL_EPSILON

    val handleDragModifier = Modifier.pointerInput(
        collapsedHeightPx,
        expandedHeightPx,
        velThreshold,
        tapSlopPx,
    ) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            val tracker = VelocityTracker()
            tracker.addPosition(down.uptimeMillis, down.position)
            isDragging = true
            var totalAbs = 0f
            verticalDrag(down.id) { change ->
                tracker.addPosition(change.uptimeMillis, change.position)
                val dy = change.positionChange().y
                totalAbs += abs(dy)
                val range = (expandedHeightPx - collapsedHeightPx).coerceAtLeast(1f)
                expansionProgress = (expansionProgress - dy / range).coerceIn(0f, 1f)
                change.consume()
            }
            val vy = tracker.calculateVelocity().y
            val targetExpanded = when {
                vy < -velThreshold -> true
                vy > velThreshold -> false
                expansionProgress > 0.5f -> true
                else -> false
            }
            isDragging = false
            if (totalAbs < tapSlopPx) {
                onToggleUpdated.value()
            } else if (targetExpanded != isCartExpandedUpdated.value) {
                onToggleUpdated.value()
            }
        }
    }

    GSurface(
        modifier = modifier
            .fillMaxWidth()
            .height(barHeightDp)
            .clip(slabShape),
        shape = slabShape,
        color = token.colors.surface,
        shadowElevation = token.elevation.level3,
        treatment = GSurfaceTreatment.Flat,
        border = null,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged {
                    if (revealDetail) {
                        expandedMeasuredPx = it.height.toFloat()
                    }
                },
        ) {
            CartDragHandle(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(handleDragModifier),
            )
            if (!revealDetail) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = actions.onToggleCartExpanded,
                        )
                        .padding(
                            start = token.spacing.sm,
                            end = token.spacing.sm,
                            bottom = token.spacing.sm,
                        ),
                ) {
                    CartThumbnailRow(
                        cartItems = cartItems,
                        activeExerciseId = activeId,
                        onSelectCartItem = actions.onSelectCartItem,
                        onRemoveCartItem = actions.onRemoveCartItem,
                        onClearAll = actions.onClearCart,
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = token.spacing.sm,
                            end = token.spacing.sm,
                            bottom = token.spacing.sm,
                        ),
                    verticalArrangement = Arrangement.spacedBy(token.spacing.sm),
                ) {
                    CartThumbnailRow(
                        cartItems = cartItems,
                        activeExerciseId = activeId,
                        onSelectCartItem = actions.onSelectCartItem,
                        onRemoveCartItem = actions.onRemoveCartItem,
                        onClearAll = actions.onClearCart,
                    )
                    activeExerciseTitle?.let { title ->
                        GText(
                            text = title,
                            style = token.typography.titleMedium,
                            color = token.colors.textPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    if (activeDraft != null) {
                        CartSetStepperSection(
                            exerciseId = checkNotNull(activeId),
                            setRows = activeDraft.setRows,
                            measurementMode = activeMeasurementMode,
                            onStepField = actions.onStepCartField,
                            onSetFieldManual = actions.onSetCartFieldManual,
                        )
                    }
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
}

// ─────────────────────────────────────────────────────────
// Cart thumbnail composables (unchanged)
// ─────────────────────────────────────────────────────────

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

// ─────────────────────────────────────────────────────────
// File-level constants
// ─────────────────────────────────────────────────────────

/** Kotlin format pattern for displaying weight values (e.g. "20.0"). */
private const val WEIGHT_FORMAT = "%.1f"
