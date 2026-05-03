package com.hoabui.virtualbody3d.ui.common_ui.organism.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.BorderStroke
import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCaloriesVisualLevel
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.common_ui.image.LocalResourceProvider
import com.hoabui.virtualbody3d.ui.common_ui.molecule.card.CardSize
import com.hoabui.virtualbody3d.ui.common_ui.molecule.card.GImageCard
import com.hoabui.virtualbody3d.ui.common_ui.molecule.section.GSectionHeader
import com.hoabui.virtualbody3d.ui.exerciselibrary.catalog.ExerciseLibraryCardImage
import com.hoabui.virtualbody3d.ui.exerciselibrary.catalog.toCoilModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme
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

/**
 * Horizontal row of exercise cards only (no section title). Use with a sticky or inline header when embedding in a parent list.
 *
 * @param onCardTap Single tap toggles add/remove from cart.
 */
@Composable
fun GExerciseSectionCardRow(
    section: GExerciseSectionUiModel,
    modifier: Modifier = Modifier,
    onCardTap: (exerciseId: String) -> Unit = {},
) {
    val token = GymTheme.token
    val resourceProvider = LocalResourceProvider.current
    val hapticFeedback = LocalHapticFeedback.current
    val libraryTint = token.bodyAnalysis.exerciseLibraryCardSelectionOverlayTintAlpha
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
        contentPadding = PaddingValues(horizontal = token.spacing.xxs, vertical = token.spacing.xxs),
    ) {
        items(section.items, key = { it.id }, contentType = { _ -> "exercise_card" }) { item ->
            val coilModel = remember(item.image) { item.image.toCoilModel(resourceProvider) }
            val scrimBottomAlpha = token.bodyAnalysis.exerciseLibraryCardImageBottomScrimBottomAlpha
            val titleStyle = remember(token.typography.labelMedium) {
                token.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            }
            val selectionBorder = remember(
                token.colors.primary,
                token.bodyAnalysis.exerciseLibraryCardSelectedBorderWidth,
            ) {
                BorderStroke(
                    token.bodyAnalysis.exerciseLibraryCardSelectedBorderWidth,
                    token.colors.primary,
                )
            }
            val selectionScale = if (item.isSelected) {
                token.bodyAnalysis.exerciseLibraryCardSelectedGraphicsScale
            } else {
                PrimitiveAlphaTokens.GRAPHICS_SCALE_NEUTRAL
            }
            val showWeak = item.isInCartInactive && !item.isSelected
            GImageCard(
                model = coilModel,
                contentDescription = item.title,
                firstLineText = "",
                secondLineText = "",
                secondLineAnnotatedText = null,
                secondLineColor = null,
                cardSize = CardSize.Small,
                badge = null,
                imageOverlayBottomFullWidth = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colorStops = arrayOf(
                                        0f to Color.Black.copy(alpha = PrimitiveAlphaTokens.OVERLAY_ALPHA_TRANSPARENT),
                                        1f to Color.Black.copy(alpha = scrimBottomAlpha),
                                    ),
                                ),
                            )
                            .padding(
                                horizontal = token.spacing.xs,
                                vertical = token.spacing.xs,
                            ),
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(token.spacing.xxxs),
                        ) {
                            GText(
                                text = item.title,
                                style = titleStyle,
                                color = token.colors.surface,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                },
                imageOverlayTopEnd = null,
                reserveExerciseLibraryTextEndInset = false,
                showTextSection = false,
                selectionScale = selectionScale,
                selectionBorderStroke = if (item.isSelected) selectionBorder else null,
                weakSelectionBorderless = showWeak,
                selectionTintAlpha = if (item.isSelected) libraryTint else null,
                weakSelectionTintAlpha = if (showWeak) libraryTint else null,
                selectionHighlight = item.isSelected,
                weakSelectionHighlight = showWeak,
                onClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                    onCardTap(item.id)
                },
            )
        }
    }
}

@Composable
fun GExerciseSectionRow(
    section: GExerciseSectionUiModel,
    modifier: Modifier = Modifier,
    onCardTap: (exerciseId: String) -> Unit = {},
) {
    val token = GymTheme.token
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(token.spacing.xxs),
    ) {
        GSectionHeader(title = section.title)
        GExerciseSectionCardRow(
            section = section,
            onCardTap = onCardTap,
        )
    }
}
