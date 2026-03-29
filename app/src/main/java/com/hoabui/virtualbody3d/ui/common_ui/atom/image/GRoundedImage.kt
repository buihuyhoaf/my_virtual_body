package com.hoabui.virtualbody3d.ui.common_ui.atom.image

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import com.hoabui.virtualbody3d.ui.common_ui.atom.surface.GSurface
import com.hoabui.virtualbody3d.ui.theme.tokens.component.GSurfaceTreatment
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.GymTheme

/**
 * Single source of truth for rounded media.
 * Uses Coil [AsyncImage] with a single [model] entry point (resource id / URL / file / URI / drawable / painter).
 */
@Composable
fun GRoundedImage(
    modifier: Modifier = Modifier,
    model: Any?,
    contentDescription: String?,
    cornerRadius: Dp? = null,
    borderWidth: Dp = 0.dp,
    borderColor: Color? = null,
    contentScale: ContentScale = ContentScale.Crop,
    alignment: Alignment = Alignment.Center,
) {
    val token = GymTheme.token
    val resolvedCornerRadius = cornerRadius ?: token.radius.md
    val resolvedBorderColor = borderColor ?: token.colors.borderSubtle
    AsyncImage(
        model = model,
        contentDescription = contentDescription,
        modifier = modifier.gRoundedDecoration(
            cornerRadius = resolvedCornerRadius,
            borderWidth = borderWidth,
            borderColor = resolvedBorderColor,
        ),
        contentScale = contentScale,
        alignment = alignment,
    )
}

/**
 * Generic vertical media-text container.
 * Handles only spacing and positioning; content styling is provided by slots.
 */
@Composable
fun GVerticalMediaCard(
    modifier: Modifier = Modifier,
    media: @Composable () -> Unit,
    title: @Composable () -> Unit,
    description: (@Composable () -> Unit)? = null,
    spacing: Dp? = null,
) {
    val token = GymTheme.token
    val resolvedSpacing = spacing ?: token.spacing.xs
    Column(
        modifier = modifier.semantics(mergeDescendants = true) {},
        verticalArrangement = Arrangement.spacedBy(resolvedSpacing),
    ) {
        media()
        title()
        description?.invoke()
    }
}

private fun Modifier.gRoundedDecoration(
    cornerRadius: Dp,
    borderWidth: Dp,
    borderColor: Color,
): Modifier {
    val shape = RoundedCornerShape(cornerRadius)
    val withBorder = if (borderWidth > 0.dp) {
        this.border(width = borderWidth, color = borderColor, shape = shape)
    } else {
        this
    }
    return withBorder.clip(shape)
}

@Preview(showBackground = true, name = "GVerticalMediaCard - flexible")
@Composable
private fun PreviewGVerticalMediaCard() {
    GymTheme {
        val token = GymTheme.token
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            // Example 1: simple rounded image + plain text
            GVerticalMediaCard(
                media = {
                    GRoundedImage(
                        modifier = Modifier.size(96.dp),
                        model = R.drawable.body_unsplash,
                        contentDescription = "Body image",
                        cornerRadius = 12.dp,
                        borderWidth = 1.dp,
                    )
                },
                title = {
                    GText(text = "Simple media title")
                },
                description = {
                    GText(
                        text = "Description from GText atomic component.",
                        style = token.typography.labelSmall,
                        color = token.colors.textSecondary,
                    )
                },
            )

            // Example 2: custom media slot (image + badge)
            GVerticalMediaCard(
                media = {
                    Box(modifier = Modifier.size(120.dp)) {
                        GRoundedImage(
                            modifier = Modifier.fillMaxSize(),
                            model = R.drawable.body_unsplash,
                            contentDescription = "Body image with badge",
                            cornerRadius = 16.dp,
                            borderWidth = 1.dp,
                        )
                        GSurface(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(token.spacing.xs),
                            shape = RoundedCornerShape(token.radius.sm),
                            color = token.colors.primary,
                            treatment = GSurfaceTreatment.Flat,
                        ) {
                            GText(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .background(Color.Transparent)
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                text = "NEW",
                                style = token.typography.labelSmall,
                                color = token.colors.onPrimary,
                            )
                        }
                    }
                },
                title = {
                    GText(
                        text = "Slot-based media block",
                        style = token.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    )
                },
                description = {
                    GText(
                        text = "Any composable can be passed into the media slot.",
                        style = token.typography.labelSmall,
                        color = token.colors.textSecondary,
                    )
                },
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "GVerticalMediaCard - dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewGVerticalMediaCardDark() {
    GymTheme(darkTheme = true) {
        val token = GymTheme.token
        GVerticalMediaCard(
            modifier = Modifier.padding(16.dp),
            media = {
                GRoundedImage(
                    modifier = Modifier.size(96.dp),
                    model = R.drawable.body_unsplash,
                    contentDescription = "Dark mode media",
                    cornerRadius = token.radius.md,
                )
            },
            title = {
                GText(text = "Dark mode title")
            },
            description = {
                GText(
                    text = "Supports theme tokens by default.",
                    style = token.typography.labelSmall,
                    color = token.colors.textSecondary,
                )
            },
        )
    }
}
