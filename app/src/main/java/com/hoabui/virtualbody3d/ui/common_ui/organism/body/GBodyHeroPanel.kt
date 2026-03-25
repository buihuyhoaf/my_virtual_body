package com.hoabui.virtualbody3d.ui.common_ui.organism.body

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.progress.GCircularProgress
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.common_ui.molecule.section.GSectionHeader
import com.hoabui.virtualbody3d.ui.theme.GymTheme

data class GHeroMetricChipUiModel(
    val id: String,
    val iconResId: Int?,
    val value: String?,
)

data class GBodyHeroPanelUiModel(
    val title: String,
    val actionText: String?,
    val bodyScore: Int,
    val topEndChips: List<GHeroMetricChipUiModel>,
    val bottomStartChips: List<GHeroMetricChipUiModel>,
)

@Composable
fun GBodyHeroPanel(
    uiModel: GBodyHeroPanelUiModel,
    modifier: Modifier = Modifier,
    onActionClick: (() -> Unit)? = null,
    onModelInteractionChanged: (Boolean) -> Unit = {},
    modelContent: @Composable (Modifier) -> Unit,
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(token.spacing.xs),
    ) {
        GSectionHeader(
            title = uiModel.title,
            actionText = uiModel.actionText,
            onActionClick = onActionClick,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(token.radius.lg))
                .background(
                    brush = Brush.radialGradient(
                        center = androidx.compose.ui.geometry.Offset(0.5f, 0.5f),
                        radius = 1.2f,
                        colors = listOf(token.colors.primarySoft, token.colors.surface),
                    ),
                ),
        ) {
            modelContent(
                Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(token.radius.lg)),
            )

            GBodyScoreChip(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(
                        top = bodyToken.scoreChipTopPadding,
                        start = bodyToken.metricChipSidePadding,
                    ),
                score = uiModel.bodyScore,
                prominent = true,
            )

            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(
                        top = bodyToken.scoreChipTopPadding,
                        end = bodyToken.metricChipSidePadding,
                    ),
                verticalArrangement = Arrangement.spacedBy(token.spacing.xs),
            ) {
                uiModel.topEndChips.forEach { chip ->
                    GFloatingMetricChip(
                        iconResId = chip.iconResId,
                        value = chip.value,
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(
                        bottom = bodyToken.scoreChipTopPadding,
                        start = bodyToken.metricChipSidePadding,
                    ),
                verticalArrangement = Arrangement.spacedBy(token.spacing.xs),
            ) {
                uiModel.bottomStartChips.forEach { chip ->
                    GFloatingMetricChip(
                        iconResId = chip.iconResId,
                        value = chip.value,
                    )
                }
            }
        }
    }
}

@Composable
private fun GFloatingMetricChip(
    modifier: Modifier = Modifier,
    iconResId: Int? = null,
    value: String? = null,
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    val chipShape = RoundedCornerShape(token.radius.lg)

    Surface(
        modifier = modifier.widthIn(min = bodyToken.metricChipMinWidth),
        shape = chipShape,
        color = Color.Transparent,
        border = BorderStroke(bodyToken.topBarBorderWidth, token.colors.surfaceBorder),
        shadowElevation = token.card.elevation,
    ) {
        Row(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        listOf(token.colors.surfaceOverlay, token.colors.surfaceOverlay),
                    ),
                    shape = chipShape,
                )
                .padding(
                    horizontal = token.spacing.xs,
                    vertical = bodyToken.bottomBarSelectedVerticalPadding,
                ),
            horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (iconResId != null) {
                Box(
                    modifier = Modifier
                        .size(bodyToken.metricChipIconContainerSize)
                        .background(
                            color = token.colors.primarySoft,
                            shape = chipShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(id = iconResId),
                        contentDescription = null,
                        tint = token.colors.primary,
                        modifier = Modifier.size(bodyToken.metricChipIconSize),
                    )
                }
            }
            if (!value.isNullOrEmpty()) {
                GText(
                    text = value,
                    style = token.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun GBodyScoreChip(
    modifier: Modifier = Modifier,
    score: Int,
    prominent: Boolean = false,
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    val chipShape = RoundedCornerShape(token.radius.lg)
    val progressShape = RoundedCornerShape(token.radius.sm)
    val clamped = score.coerceIn(0, 100)
    val textStyle = if (prominent) token.typography.titleMedium else token.typography.labelLarge

    Surface(
        modifier = modifier.widthIn(
            min = if (prominent) bodyToken.scoreChipProminentMinWidth else bodyToken.scoreChipMinWidth,
        ),
        shape = chipShape,
        color = Color.Transparent,
        border = BorderStroke(bodyToken.topBarBorderWidth, token.colors.surfaceBorder),
        shadowElevation = token.card.elevation,
    ) {
        Row(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        listOf(token.colors.surfaceOverlay, token.colors.surfaceOverlay),
                    ),
                    shape = chipShape,
                )
                .padding(
                    horizontal = if (prominent) token.spacing.md else token.spacing.xs,
                    vertical = bodyToken.bottomBarSelectedVerticalPadding,
                ),
            horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(contentAlignment = Alignment.Center) {
                GCircularProgress(
                    progress = clamped / 100f,
                    modifier = Modifier.size(
                        if (prominent) bodyToken.scoreChipProminentProgressSize else bodyToken.scoreChipProgressSize,
                    ),
                    strokeWidth = if (prominent) bodyToken.scoreChipProminentStrokeWidth else bodyToken.scoreChipStrokeWidth,
                    trackColor = token.colors.outlineSoft,
                )
                Box(
                    modifier = Modifier
                        .size(
                            if (prominent) bodyToken.scoreChipProminentInnerSize else bodyToken.scoreChipInnerSize,
                        )
                        .background(token.colors.primarySoft, progressShape),
                )
            }
            GText(
                text = clamped.toString(),
                style = textStyle,
                fontWeight = FontWeight.Bold,
                color = token.colors.primary,
            )
        }
    }
}

@Preview(showBackground = true, name = "GBodyHeroPanel — light")
@Composable
private fun PreviewGBodyHeroPanelLight() {
    GymTheme {
        val token = GymTheme.token
        GBodyHeroPanel(
            uiModel = GBodyHeroPanelUiModel(
                title = "Body",
                actionText = "See more",
                bodyScore = 82,
                topEndChips = listOf(
                    GHeroMetricChipUiModel("weight", R.drawable.scale, "72.4 kg"),
                    GHeroMetricChipUiModel("height", R.drawable.ruler_vertical, "175 cm"),
                ),
                bottomStartChips = listOf(
                    GHeroMetricChipUiModel("bodyfat", R.drawable.scale, "18%"),
                    GHeroMetricChipUiModel("muscle", R.drawable.scale, "42%"),
                ),
            ),
            onActionClick = {},
            onModelInteractionChanged = {},
            modelContent = { mod ->
                Box(
                    modifier = mod.background(token.colors.surface),
                )
            },
            modifier = Modifier.padding(token.spacing.md),
        )
    }
}

@Preview(
    showBackground = true,
    name = "GBodyHeroPanel — dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewGBodyHeroPanelDark() {
    GymTheme(darkTheme = true) {
        val token = GymTheme.token
        GBodyHeroPanel(
            uiModel = GBodyHeroPanelUiModel(
                title = "Body",
                actionText = "See more",
                bodyScore = 64,
                topEndChips = listOf(
                    GHeroMetricChipUiModel("weight", R.drawable.scale, "80.1 kg"),
                    GHeroMetricChipUiModel("height", R.drawable.ruler_vertical, "175 cm"),
                ),
                bottomStartChips = listOf(
                    GHeroMetricChipUiModel("bodyfat", R.drawable.scale, "24%"),
                    GHeroMetricChipUiModel("muscle", R.drawable.scale, "38%"),
                ),
            ),
            onActionClick = {},
            onModelInteractionChanged = {},
            modelContent = { mod ->
                Box(
                    modifier = mod.background(token.colors.surface),
                )
            },
            modifier = Modifier.padding(token.spacing.md),
        )
    }
}

