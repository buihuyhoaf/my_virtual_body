package com.hoabui.virtualbody3d.ui.common_ui.organism.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.ui.common_ui.image.LocalResourceProvider
import com.hoabui.virtualbody3d.ui.common_ui.molecule.card.CardSize
import com.hoabui.virtualbody3d.ui.common_ui.molecule.card.GImageCard
import com.hoabui.virtualbody3d.ui.common_ui.molecule.card.GImageCardBadgeChrome
import com.hoabui.virtualbody3d.ui.common_ui.molecule.card.GImageCardHolisticCapsuleLabel
import com.hoabui.virtualbody3d.ui.common_ui.molecule.section.GSectionHeader
import com.hoabui.virtualbody3d.ui.exerciselibrary.model.ExerciseLibraryCardImage
import com.hoabui.virtualbody3d.ui.exerciselibrary.model.toCoilModel
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.icon.GIcon
import com.hoabui.virtualbody3d.ui.theme.icons.ExerciseLibraryPhosphorIcons
import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCaloriesVisualLevel
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.toCaloriesVisualLevelColor
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveAlphaTokens

@Immutable
data class GExerciseCardUiModel(
    val id: String,
    val image: ExerciseLibraryCardImage,
    val title: String,
    val subtitle: String,
    /** When set with [subtitleCaloriesVisualLevel], the library row paints only this value with intensity color. */
    val libraryUptoKcal: Int? = null,
    /** When set, subtitle uses calendar-aligned calorie intensity colors on library cards. */
    val subtitleCaloriesVisualLevel: WorkoutCaloriesVisualLevel? = null,
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

@Composable
private fun ExerciseLibraryListCornerToggleSticker(
    inCart: Boolean,
    toggleAddContentDescription: String,
    toggleRemoveContentDescription: String,
    onClick: () -> Unit,
) {
    val token = GymTheme.token
    Box(
        modifier = Modifier
            .size(token.bodyAnalysis.exerciseLibraryCornerStickerTouchTargetSize)
            .clickable(
                role = Role.Button,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(token.bodyAnalysis.exerciseLibraryCornerStickerDiameter)
                .clip(CircleShape)
                .background(
                    token.colors.surfaceSubtle.copy(alpha = PrimitiveAlphaTokens.SUBTLE_LAYER),
                ),
            contentAlignment = Alignment.Center,
        ) {
            GIcon(
                imageVector = if (inCart) {
                    ExerciseLibraryPhosphorIcons.listToggleInCart
                } else {
                    ExerciseLibraryPhosphorIcons.listToggleNotInCart
                },
                contentDescription = if (inCart) {
                    toggleRemoveContentDescription
                } else {
                    toggleAddContentDescription
                },
                modifier = Modifier.size(token.bodyAnalysis.exerciseLibraryCornerActionGlyphSize),
                tint = token.colors.primary,
            )
        }
    }
}

/**
 * Horizontal row of exercise cards only (no section title). Use with a sticky or inline header when embedding in a parent list.
 *
 * @param onNavigateDetail Opens exercise detail / info (body tap on image and text only; no cart mutation).
 * @param onToggleSelection When non-null, shows the top-end add/check control; invokes symmetric cart toggle only.
 * @param toggleAddContentDescription Accessibility label when the exercise is not in the cart.
 * @param toggleRemoveContentDescription Accessibility label when the exercise is already in the cart.
 */
@Composable
fun GExerciseSectionCardRow(
    section: GExerciseSectionUiModel,
    modifier: Modifier = Modifier,
    onNavigateDetail: (exerciseId: String) -> Unit = {},
    onToggleSelection: ((exerciseId: String) -> Unit)? = null,
    toggleAddContentDescription: String = "",
    toggleRemoveContentDescription: String = "",
    badgeContent: (@Composable (GExerciseCardUiModel) -> Unit)? = null,
) {
    val token = GymTheme.token
    val resourceProvider = LocalResourceProvider.current
    val hapticFeedback = LocalHapticFeedback.current
    val latestOnNavigateDetail = rememberUpdatedState(onNavigateDetail)
    val latestOnToggleSelection = rememberUpdatedState(onToggleSelection)
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
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
                { GImageCardHolisticCapsuleLabel(badgeText) }
            } else {
                null
            }
            val onOpenDetail = remember(item.id) {
                { latestOnNavigateDetail.value(item.id) }
            }
            val inCart = item.isSelected || item.isInCartInactive
            val uptoPrefix = stringResource(R.string.exercise_library_card_upto_prefix)
            val uptoSuffix = stringResource(R.string.exercise_library_card_upto_suffix)
            val secondLineAnnotated = if (
                item.libraryUptoKcal != null && item.subtitleCaloriesVisualLevel != null
            ) {
                val kcal = item.libraryUptoKcal
                val level = item.subtitleCaloriesVisualLevel
                val muted =
                    token.colors.textSecondary.copy(alpha = PrimitiveAlphaTokens.IMAGE_CARD_OVERLAY_MEDIUM)
                val highlight = level.toCaloriesVisualLevelColor(token)
                buildAnnotatedString {
                    withStyle(SpanStyle(color = muted)) { append(uptoPrefix) }
                    withStyle(SpanStyle(color = highlight)) { append(kcal.toString()) }
                    withStyle(SpanStyle(color = muted)) { append(uptoSuffix) }
                }
            } else {
                null
            }
            val secondLineColor =
                if (secondLineAnnotated == null) {
                    item.subtitleCaloriesVisualLevel?.toCaloriesVisualLevelColor(token)
                } else {
                    null
                }
            val imageOverlayTop: (@Composable BoxScope.() -> Unit)? =
                if (onToggleSelection != null) {
                    {
                        ExerciseLibraryListCornerToggleSticker(
                            inCart = inCart,
                            toggleAddContentDescription = toggleAddContentDescription,
                            toggleRemoveContentDescription = toggleRemoveContentDescription,
                            onClick = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                                latestOnToggleSelection.value?.invoke(item.id)
                            },
                        )
                    }
                } else {
                    null
                }
            GImageCard(
                model = coilModel,
                contentDescription = item.title,
                firstLineText = item.title,
                secondLineText = item.subtitle,
                secondLineAnnotatedText = secondLineAnnotated,
                secondLineColor = secondLineColor,
                cardSize = CardSize.Small,
                badge = badgeSlot,
                badgeChrome = chrome,
                imageOverlayTopEnd = imageOverlayTop,
                imageOverlayTopEndEdgeInset = false,
                reserveExerciseLibraryTextEndInset = false,
                selectionHighlight = item.isSelected,
                weakSelectionHighlight = item.isInCartInactive,
                onClick = onOpenDetail,
            )
        }
    }
}

@Composable
fun GExerciseSectionRow(
    section: GExerciseSectionUiModel,
    modifier: Modifier = Modifier,
    onNavigateDetail: (exerciseId: String) -> Unit = {},
    onToggleSelection: ((exerciseId: String) -> Unit)? = null,
    toggleAddContentDescription: String = "",
    toggleRemoveContentDescription: String = "",
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
            onNavigateDetail = onNavigateDetail,
            onToggleSelection = onToggleSelection,
            toggleAddContentDescription = toggleAddContentDescription,
            toggleRemoveContentDescription = toggleRemoveContentDescription,
            badgeContent = badgeContent,
        )
    }
}
