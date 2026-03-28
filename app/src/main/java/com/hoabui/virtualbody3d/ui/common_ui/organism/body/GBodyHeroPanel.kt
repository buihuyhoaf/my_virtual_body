package com.hoabui.virtualbody3d.ui.common_ui.organism.body

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Fill
import com.adamglin.phosphoricons.fill.Barbell
import com.adamglin.phosphoricons.fill.Circle
import com.adamglin.phosphoricons.fill.Drop
import com.adamglin.phosphoricons.fill.Scales
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.common_ui.molecule.section.GSectionHeader
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveAlphaTokens

data class GHeroMetricChipUiModel(
    val id: String,
    val icon: ImageVector?,
    val value: String?,
)

data class GBodyHeroPanelUiModel(
    val title: String,
    val actionText: String?,
    val bmiStatus: String,
    val bmiIndicatorColor: Color,
    val metrics: List<GHeroMetricChipUiModel>,
)

@Composable
fun GBodyHeroPanel(
    uiModel: GBodyHeroPanelUiModel,
    modifier: Modifier = Modifier,
    onActionClick: (() -> Unit)? = null,
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

            GBodyStatusChip(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(
                        top = bodyToken.scoreChipTopPadding,
                        start = bodyToken.metricChipSidePadding,
                    ),
                status = uiModel.bmiStatus,
                indicatorColor = uiModel.bmiIndicatorColor,
            )

            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(
                        top = bodyToken.scoreChipTopPadding,
                        end = bodyToken.metricChipSidePadding,
                    ),
                verticalArrangement = Arrangement.spacedBy(bodyToken.heroSlimChipIconTextGap),
                horizontalAlignment = Alignment.End,
            ) {
                uiModel.metrics.forEach { chip ->
                    GFloatingMetricChip(
                        icon = chip.icon,
                        value = chip.value,
                    )
                }
            }
        }
    }
}

@Composable
private fun GHeroBaseChip(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    val chipFill = token.colors.surfaceOverlay.copy(
        alpha = PrimitiveAlphaTokens.HERO_CHIP_GLASS_FILL,
    )
    Surface(
        modifier = modifier
            .height(bodyToken.heroSlimChipHeight)
            .wrapContentWidth(),
        shape = CircleShape,
        color = chipFill,
        border = BorderStroke(token.borderWidth.hairline, token.colors.borderSubtle),
        shadowElevation = token.card.elevation,
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = bodyToken.heroSlimChipPaddingHorizontal),
            horizontalArrangement = Arrangement.spacedBy(bodyToken.heroSlimChipIconTextGap),
            verticalAlignment = Alignment.CenterVertically,
            content = content,
        )
    }
}

@Composable
private fun GFloatingMetricChip(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    value: String? = null,
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    val chipTextStyle = token.typography.labelSmall.copy(
        letterSpacing = bodyToken.heroSlimChipLabelLetterSpacing,
        fontWeight = FontWeight.Bold,
    )

    GHeroBaseChip(modifier = modifier) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = token.colors.primary,
                modifier = Modifier.size(bodyToken.heroSlimChipIconSize),
            )
        }
        if (!value.isNullOrEmpty()) {
            GText(
                text = value,
                style = chipTextStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun GBodyStatusChip(
    modifier: Modifier = Modifier,
    status: String,
    indicatorColor: Color,
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    val dotColor = if (indicatorColor == Color.Unspecified) {
        token.colors.primary
    } else {
        indicatorColor
    }
    val chipTextStyle = token.typography.labelSmall.copy(
        letterSpacing = bodyToken.heroSlimChipLabelLetterSpacing,
        fontWeight = FontWeight.Bold,
    )

    GHeroBaseChip(modifier = modifier) {
        Icon(
            imageVector = PhosphorIcons.Fill.Circle,
            contentDescription = null,
            tint = dotColor,
            modifier = Modifier.size(bodyToken.heroSlimChipIconSize),
        )
        GText(
            text = status,
            style = chipTextStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
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
                bmiStatus = "Normal",
                bmiIndicatorColor = token.colors.success,
                metrics = listOf(
                    GHeroMetricChipUiModel("weight", PhosphorIcons.Fill.Scales, "72.4 kg"),
                    GHeroMetricChipUiModel("bodyFat", PhosphorIcons.Fill.Drop, "18%"),
                    GHeroMetricChipUiModel("muscleMass", PhosphorIcons.Fill.Barbell, "42%"),
                ),
            ),
            onActionClick = {},
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
                bmiStatus = "Overweight",
                bmiIndicatorColor = token.colors.warning,
                metrics = listOf(
                    GHeroMetricChipUiModel("weight", PhosphorIcons.Fill.Scales, "80.1 kg"),
                    GHeroMetricChipUiModel("bodyFat", PhosphorIcons.Fill.Drop, "24%"),
                    GHeroMetricChipUiModel("muscleMass", PhosphorIcons.Fill.Barbell, "38%"),
                ),
            ),
            onActionClick = {},
            modelContent = { mod ->
                Box(
                    modifier = mod.background(token.colors.surface),
                )
            },
            modifier = Modifier.padding(token.spacing.md),
        )
    }
}
