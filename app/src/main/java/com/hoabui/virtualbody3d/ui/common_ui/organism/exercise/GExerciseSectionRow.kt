package com.hoabui.virtualbody3d.ui.common_ui.organism.exercise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.common_ui.molecule.card.CardSize
import com.hoabui.virtualbody3d.ui.common_ui.molecule.card.GImageCard
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

@Composable
fun GExerciseSectionRow(
    section: GExerciseSectionUiModel,
    modifier: Modifier = Modifier,
    onItemClick: (exerciseId: String) -> Unit = {},
    badgeContent: (@Composable (GExerciseCardUiModel) -> Unit)? = null,
) {
    val token = GymTheme.token
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(token.spacing.xs),
    ) {
        GSectionHeader(title = section.title)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(token.spacing.md),
            contentPadding = PaddingValues(horizontal = token.spacing.xxs, vertical = token.spacing.xs),
        ) {
            items(section.items, key = { it.id }) { item ->
                GImageCard(
                    model = item.imageModel,
                    contentDescription = item.title,
                    firstLineText = item.title,
                    secondLineText = item.subtitle,
                    cardSize = CardSize.Large,
                    badge = {
                        when {
                            badgeContent != null -> badgeContent(item)
                            item.badgeText != null -> {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(token.radius.sm))
                                        .padding(horizontal = token.spacing.xs, vertical = token.spacing.xxs),
                                ) {
                                    GText(
                                        text = item.badgeText,
                                        style = token.typography.labelSmall,
                                        color = token.colors.textPrimary,
                                    )
                                }
                            }
                        }
                    },
                    onClick = { onItemClick(item.id) },
                )
            }
        }
    }
}
