package com.hoabui.virtualbody3d.ui.exerciselibrary.components.internal

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.core.extensions.onHeightMeasured
import com.hoabui.virtualbody3d.core.extensions.verticalDraggable
import com.hoabui.virtualbody3d.ui.common_ui.atom.surface.GSurface
import com.hoabui.virtualbody3d.ui.common_ui.organism.exercise.GExerciseCardUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.ActiveExerciseInfo
import com.hoabui.virtualbody3d.ui.exerciselibrary.wiring.WorkoutBuilderActions
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
    isSelectionBarEditMode: Boolean,
    isSelectionBarConfirmEnabled: Boolean,
    actions: WorkoutBuilderActions,
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
                    .onHeightMeasured { heightPx ->
                        if (heightPx > collapsedHeightPx) {
                            fullContentHeightPx = heightPx
                        }
                    },
            ) {
                SelectionBarHeader(
                    cartItems = cartItems,
                    activeExerciseInfo = activeExerciseInfo,
                    actions = actions,
                    dragModifier = dragModifier,
                    isSelectionBarEditMode = isSelectionBarEditMode,
                )
                SelectionBarBody(
                    activeExerciseInfo = activeExerciseInfo,
                    actions = actions,
                )
                SelectionBarFooter(
                    isSelectionBarEditMode = isSelectionBarEditMode,
                    bookingEnabled = bookingEnabled,
                    isConfirmEnabled = isSelectionBarConfirmEnabled,
                    onAddToSession = actions.onAddToSession,
                    onConfirmSelectionBarEdit = actions.onConfirmSelectionBarEdit,
                    onCancelSelectionBarEdit = actions.onCancelSelectionBarEdit,
                )
            }
        }
    }
}
