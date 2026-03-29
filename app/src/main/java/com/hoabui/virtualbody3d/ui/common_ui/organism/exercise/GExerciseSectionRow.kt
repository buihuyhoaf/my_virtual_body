package com.hoabui.virtualbody3d.ui.common_ui.organism.exercise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.ui.common_ui.image.LocalResourceProvider
import com.hoabui.virtualbody3d.ui.common_ui.molecule.card.CardSize
import com.hoabui.virtualbody3d.ui.common_ui.molecule.card.GImageCard
import com.hoabui.virtualbody3d.ui.common_ui.molecule.card.GImageCardBadgeChrome
import com.hoabui.virtualbody3d.ui.common_ui.molecule.card.GImageCardHolisticCapsuleLabel
import com.hoabui.virtualbody3d.ui.common_ui.molecule.section.GSectionHeader
import com.hoabui.virtualbody3d.ui.exerciselibrary.model.ExerciseLibraryCardImage
import com.hoabui.virtualbody3d.ui.exerciselibrary.model.toCoilModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Immutable
data class GExerciseCardUiModel(
    val id: String,
    val image: ExerciseLibraryCardImage,
    val title: String,
    val subtitle: String,
    val badgeText: String? = null,
    /** Strong highlight: active line in the cart. */
    val isSelected: Boolean = false,
    /** Softer highlight: in cart but not the active line. */
    val isInCartInactive: Boolean = false,
)

data class GExerciseSectionUiModel(
    val id: String,
    val title: String,
    val items: List<GExerciseCardUiModel>,
)

/**
 * Horizontal row of exercise cards only (no section title). Use with a sticky or inline header when embedding in a parent list.
 */
@Composable
fun GExerciseSectionCardRow(
    section: GExerciseSectionUiModel,
    modifier: Modifier = Modifier,
    onItemClick: (exerciseId: String) -> Unit = {},
    onQuickAdd: ((exerciseId: String) -> Unit)? = null,
    quickAddContentDescription: String = "",
    badgeContent: (@Composable (GExerciseCardUiModel) -> Unit)? = null,
) {
    val token = GymTheme.token
    val resourceProvider = LocalResourceProvider.current
    val hapticFeedback = LocalHapticFeedback.current
    val latestOnItemClick = rememberUpdatedState(onItemClick)
    val latestOnQuickAdd = rememberUpdatedState(onQuickAdd)
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(token.spacing.sm),
        contentPadding = PaddingValues(horizontal = token.spacing.xxs, vertical = token.spacing.xxs),
    ) {
        items(section.items, key = { it.id }, contentType = { _ -> "exercise_card" }) { item ->
            val coilModel = remember(item.image) { item.image.toCoilModel(resourceProvider) }
            val chrome = if (badgeContent != null) {
                GImageCardBadgeChrome.None
            } else {
                GImageCardBadgeChrome.Holistic
            }
            val badgeSlot: (@Composable BoxScope.() -> Unit)? = if (badgeContent != null) {
                { badgeContent.invoke(item) }
            } else if (item.badgeText != null) {
                val badgeText = item.badgeText
                { GImageCardHolisticCapsuleLabel(badgeText!!) }
            } else {
                null
            }
            val onCardClick = remember(item.id) {
                { latestOnItemClick.value(item.id) }
            }
            val onToggleSelection = remember(item.id) {
                {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                    latestOnQuickAdd.value?.invoke(item.id)
                    Unit
                }
            }
            val onOpenDetail = remember(item.id) {
                {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    latestOnItemClick.value(item.id)
                }
            }
            val shortClick = if (onQuickAdd != null) onToggleSelection else onCardClick
            val longClick = if (onQuickAdd != null) onOpenDetail else null
            GImageCard(
                model = coilModel,
                contentDescription = item.title,
                firstLineText = item.title,
                secondLineText = item.subtitle,
                cardSize = CardSize.ExerciseLibraryTile,
                badge = badgeSlot,
                badgeChrome = chrome,
                selectionHighlight = item.isSelected,
                weakSelectionHighlight = item.isInCartInactive,
                onClick = shortClick,
                onLongClick = longClick,
            )
        }
    }
}

@Composable
fun GExerciseSectionRow(
    section: GExerciseSectionUiModel,
    modifier: Modifier = Modifier,
    onItemClick: (exerciseId: String) -> Unit = {},
    onQuickAdd: ((exerciseId: String) -> Unit)? = null,
    quickAddContentDescription: String = "",
    badgeContent: (@Composable (GExerciseCardUiModel) -> Unit)? = null,
) {
    val token = GymTheme.token
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(token.spacing.xxs),
    ) {
        GSectionHeader(title = section.title)
        GExerciseSectionCardRow(
            section = section,
            onItemClick = onItemClick,
            onQuickAdd = onQuickAdd,
            quickAddContentDescription = quickAddContentDescription,
            badgeContent = badgeContent,
        )
    }
}
