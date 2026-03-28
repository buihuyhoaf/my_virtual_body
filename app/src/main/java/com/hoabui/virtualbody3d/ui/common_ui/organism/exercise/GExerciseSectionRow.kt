package com.hoabui.virtualbody3d.ui.common_ui.organism.exercise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.hoabui.virtualbody3d.ui.common_ui.molecule.card.CardSize
import com.hoabui.virtualbody3d.ui.common_ui.molecule.card.GImageCard
import com.hoabui.virtualbody3d.ui.common_ui.molecule.card.GImageCardBadgeChrome
import com.hoabui.virtualbody3d.ui.common_ui.molecule.card.GImageCardHolisticCapsuleLabel
import com.hoabui.virtualbody3d.ui.common_ui.molecule.section.GSectionHeader
import com.hoabui.virtualbody3d.ui.theme.GymTheme

data class GExerciseCardUiModel(
    val id: String,
    val imageModel: Any?,
    val title: String,
    val subtitle: String,
    val badgeText: String? = null,
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
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(token.spacing.md),
        contentPadding = PaddingValues(horizontal = token.spacing.xxs, vertical = token.spacing.xs),
    ) {
        items(section.items, key = { it.id }) { item ->
            val chrome = if (badgeContent != null) {
                GImageCardBadgeChrome.None
            } else {
                GImageCardBadgeChrome.Holistic
            }
            val badgeSlot: (@Composable BoxScope.() -> Unit)? = if (badgeContent != null) {
                { badgeContent.invoke(item) }
            } else if (item.badgeText != null) {
                { GImageCardHolisticCapsuleLabel(item.badgeText!!) }
            } else {
                null
            }
            val quickAddOverlay: (@Composable BoxScope.() -> Unit)? =
                if (onQuickAdd != null) {
                    {
                        IconButton(
                            onClick = {
                                onQuickAdd.invoke(item.id)
                                // Haptics: when enabling feedback, use
                                // val haptic = LocalHapticFeedback.current
                                // haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
                                // (or Confirm) — pick type per UX; requires
                                // androidx.compose.ui:ui + HapticFeedbackType import.
                            },
                            modifier = Modifier.clip(RoundedCornerShape(token.radius.sm)),
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = token.colors.surface.copy(alpha = 0.92f),
                                contentColor = token.colors.primary,
                            ),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = quickAddContentDescription,
                            )
                        }
                    }
                } else {
                    null
                }
            GImageCard(
                model = item.imageModel,
                contentDescription = item.title,
                firstLineText = item.title,
                secondLineText = item.subtitle,
                cardSize = CardSize.Large,
                badge = badgeSlot,
                badgeChrome = chrome,
                imageOverlayEnd = quickAddOverlay,
                onClick = { onItemClick(item.id) },
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
        verticalArrangement = Arrangement.spacedBy(token.spacing.xs),
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
