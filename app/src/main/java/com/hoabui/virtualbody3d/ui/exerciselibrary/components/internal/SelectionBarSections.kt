package com.hoabui.virtualbody3d.ui.exerciselibrary.components.internal

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.core.extensions.verticalDraggable
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButton
import com.hoabui.virtualbody3d.ui.common_ui.atom.surface.GSurface
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.common_ui.organism.exercise.GExerciseCardUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.CartSetStepperSection
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.CartThumbnailRow
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.CartDragHandle
import com.hoabui.virtualbody3d.ui.exerciselibrary.model.ActiveExerciseInfo
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryActions
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.tokens.component.GSurfaceTreatment
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@Composable
internal fun SelectionBarSections(
    cartItems: List<GExerciseCardUiModel>,
    activeExerciseInfo: ActiveExerciseInfo?,
    isCartExpanded: Boolean,
    bookingEnabled: Boolean,
    actions: ExerciseLibraryActions,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val collapsedHeightPx = with(density) {
        token.bodyAnalysis.exerciseLibrarySelectionBarCollapsedListBottomInset.toPx()
    }
    var fullContentHeightPx by remember { mutableFloatStateOf(with(density) { 400.dp.toPx() }) }
    var expansionProgress by remember {
        mutableFloatStateOf(if (isCartExpanded) 1f else 0f)
    }
    var isDragging by remember { mutableStateOf(false) }
    val rangePx = (fullContentHeightPx - collapsedHeightPx).coerceAtLeast(1f)

    LaunchedEffect(isCartExpanded) {
        if (!isDragging) {
            animate(
                initialValue = expansionProgress,
                targetValue = if (isCartExpanded) 1f else 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessLow,
                ),
            ) { value, _ -> expansionProgress = value }
        }
    }

    val currentHeight = with(density) {
        (collapsedHeightPx + (rangePx * expansionProgress)).toDp()
    }
    val velocityThreshold = with(density) { 500.dp.toPx() }
    val dragModifier = Modifier.verticalDraggable(
        onDragStart = { isDragging = true },
        onDrag = { dragAmount ->
            expansionProgress = (expansionProgress - dragAmount / rangePx).coerceIn(0f, 1f)
        },
        onDragEnd = { result ->
            isDragging = false
            if (result.totalDragY.absoluteValue < 10f) {
                actions.onToggleCartExpanded()
            } else {
                val shouldExpand = when {
                    result.velocityY < -velocityThreshold -> true
                    result.velocityY > velocityThreshold -> false
                    expansionProgress > 0.5f -> true
                    else -> false
                }
                if (shouldExpand != isCartExpanded) {
                    actions.onToggleCartExpanded()
                } else {
                    scope.launch {
                        animate(
                            initialValue = expansionProgress,
                            targetValue = if (isCartExpanded) 1f else 0f,
                            animationSpec = spring(Spring.DampingRatioNoBouncy),
                        ) { value, _ -> expansionProgress = value }
                    }
                }
            }
        },
    )

    GSurface(
        modifier = modifier
            .fillMaxWidth()
            .height(currentHeight)
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
        color = token.colors.surface,
        shadowElevation = token.elevation.level3,
        treatment = GSurfaceTreatment.Flat,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(align = Alignment.Top, unbounded = true),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .onSizeChanged { size ->
                        if (size.height > collapsedHeightPx) {
                            fullContentHeightPx = size.height.toFloat()
                        }
                    },
            ) {
                SelectionBarHeader(
                    cartItems = cartItems,
                    activeExerciseInfo = activeExerciseInfo,
                    actions = actions,
                    dragModifier = dragModifier,
                )
                SelectionBarBody(
                    activeExerciseInfo = activeExerciseInfo,
                    actions = actions,
                )
                SelectionBarFooter(
                    enabled = bookingEnabled,
                    onClick = actions.onAddToSession,
                )
            }
        }
    }
}

@Composable
private fun SelectionBarHeader(
    cartItems: List<GExerciseCardUiModel>,
    activeExerciseInfo: ActiveExerciseInfo?,
    actions: ExerciseLibraryActions,
    dragModifier: Modifier,
) {
    val token = GymTheme.token
    CartDragHandle(modifier = dragModifier)
    CartThumbnailRow(
        cartItems = cartItems,
        activeExerciseId = activeExerciseInfo?.id,
        onSelectCartItem = actions.onSelectCartItem,
        onRemoveCartItem = actions.onRemoveCartItem,
        onClearAll = actions.onClearCart,
        modifier = Modifier.padding(horizontal = token.spacing.sm),
    )
}

@Composable
private fun ColumnScope.SelectionBarBody(
    activeExerciseInfo: ActiveExerciseInfo?,
    actions: ExerciseLibraryActions,
) {
    val token = GymTheme.token
    activeExerciseInfo?.title?.let { title ->
        GText(
            text = title,
            style = token.typography.titleMedium,
            color = token.colors.textPrimary,
            modifier = Modifier.padding(
                start = token.spacing.sm,
                end = token.spacing.sm,
                top = token.spacing.sm,
                bottom = token.spacing.xs,
            ),
        )
    }
    val activeId = activeExerciseInfo?.id
    val activeDraft = activeExerciseInfo?.draft
    if (activeId != null && activeDraft != null) {
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .fillMaxWidth()
                .padding(horizontal = token.spacing.sm)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(token.spacing.xs),
        ) {
            CartSetStepperSection(
                exerciseId = activeId,
                setRows = activeDraft.setRows,
                measurementMode = activeExerciseInfo.measurementMode,
                onStepField = actions.onStepCartField,
                onSetFieldManual = actions.onSetCartFieldManual,
            )
        }
    }
}

@Composable
private fun SelectionBarFooter(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val token = GymTheme.token
    GButton(
        text = stringResource(R.string.exercise_library_add_to_session),
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(token.spacing.sm),
        enabled = enabled,
    )
}
