package com.hoabui.virtualbody3d.ui.common_ui.molecule.card

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.hoabui.virtualbody3d.ui.common_ui.atom.card.GCard
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.GymTheme

/**
 * Horizontal media card: image thumbnail on the left + title/subtitle/badge column on the right.
 *
 * Wraps [GCard] so the optional [onClick] ripple applies to the whole card.
 *
 * @param imageModel Coil-compatible image source — URL [String], [android.net.Uri],
 *   `@DrawableRes Int`, [java.io.File], etc.
 * @param title Primary text shown below the image area.
 * @param subtitle Optional secondary line.
 * @param badge Optional composable slot (e.g. difficulty badge) rendered below [subtitle].
 * @param imageWidthFraction Fraction of the card width used by the image. Default `0.25f`.
 * @param contentDescription Accessibility description for the image.
 * @param onClick If non-null, the card is clickable with M3 ripple.
 */
@Composable
fun GMediaInfoCard(
    imageModel: Any?,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    badge: (@Composable () -> Unit)? = null,
    imageWidthFraction: Float = 0.25f,
    contentDescription: String? = null,
    elevation: Dp = GymTheme.token.card.elevation,
    onClick: (() -> Unit)? = null,
) {
    val token = GymTheme.token
    val imageHeight = token.spacing.xxl + token.spacing.lg

    GCard(
        modifier = modifier,
        onClick = onClick,
        elevation = elevation,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(token.colors.surface)
                .padding(token.spacing.md),
            horizontalArrangement = Arrangement.spacedBy(token.spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .height(imageHeight)
                    .fillMaxWidth(imageWidthFraction)
                    .clip(RoundedCornerShape(token.radius.sm))
                    .background(token.colors.surfaceSubtle),
            ) {
                AsyncImage(
                    model = imageModel,
                    contentDescription = contentDescription,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(),
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(token.spacing.xxs),
            ) {
                GText(
                    text = title,
                    style = token.typography.titleMedium,
                    color = token.colors.textPrimary,
                )
                if (subtitle != null) {
                    GText(
                        text = subtitle,
                        style = token.typography.bodySmall,
                        color = token.colors.textSecondary,
                    )
                }
                if (badge != null) {
                    badge()
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "GMediaInfoCard — light")
@Composable
private fun PreviewGMediaInfoCardLight() {
    GymTheme {
        GMediaInfoCard(
            imageModel = null,
            title = "Bench Press",
            subtitle = "Chest",
            badge = {
                val token = GymTheme.token
                GText(
                    text = "Beginner",
                    style = token.typography.labelSmall,
                    color = token.colors.primary,
                )
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview(
    showBackground = true,
    name = "GMediaInfoCard — dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewGMediaInfoCardDark() {
    GymTheme(darkTheme = true) {
        GMediaInfoCard(
            imageModel = null,
            title = "Chicken Salad",
            subtitle = "Lunch idea",
            elevation = 0.dp,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
