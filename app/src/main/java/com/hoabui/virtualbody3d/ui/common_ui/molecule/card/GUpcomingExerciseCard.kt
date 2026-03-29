package com.hoabui.virtualbody3d.ui.common_ui.molecule.card

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import com.hoabui.virtualbody3d.ui.common_ui.atom.surface.GSurface
import com.hoabui.virtualbody3d.ui.theme.tokens.component.GSurfaceTreatment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.GymTheme

/**
 * Horizontal slim chip for the home upcoming-exercises row: bordered surface, thumbnail + texts.
 */
@Composable
fun GUpcomingExerciseCard(
    model: Any?,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    val chipWidth = bodyToken.upcomingExerciseChipWidth
    val chipHeight = bodyToken.upcomingExerciseChipHeight
    val imageSize = bodyToken.upcomingExerciseChipImageSize
    val imageCorner = RoundedCornerShape(bodyToken.upcomingExerciseChipImageCornerRadius)
    val contentPaddingH = bodyToken.upcomingExerciseChipContentHorizontalPadding
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        label = "upcoming_exercise_card_scale",
    )
    val surfaceShape = RoundedCornerShape(token.radius.md)
    val chipBorder = BorderStroke(
        width = token.borderWidth.hairline,
        color = token.colors.borderSubtle,
    )

    GSurface(
        modifier = modifier
            .width(chipWidth)
            .height(chipHeight)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        shape = surfaceShape,
        color = token.colors.surface,
        shadowElevation = token.elevation.level0,
        border = chipBorder,
        treatment = GSurfaceTreatment.Flat,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = contentPaddingH),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            AsyncImage(
                model = model,
                contentDescription = title,
                modifier = Modifier
                    .size(imageSize)
                    .clip(imageCorner),
                contentScale = ContentScale.Crop,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = token.spacing.sm),
            ) {
                GText(
                    text = title,
                    style = token.typography.labelSmall,
                    color = token.colors.textPrimary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                GText(
                    text = subtitle,
                    style = token.typography.labelSmall,
                    color = token.colors.textSecondary.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "GUpcomingExerciseCard — Light")
@Composable
private fun PreviewGUpcomingExerciseCardLight() {
    GymTheme {
        val token = GymTheme.token
        GUpcomingExerciseCard(
            modifier = Modifier.padding(token.spacing.md),
            model = R.drawable.body_unsplash,
            title = "Push-ups",
            subtitle = "12 reps · 3 sets",
            onClick = {},
        )
    }
}

@Preview(
    showBackground = true,
    name = "GUpcomingExerciseCard — Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewGUpcomingExerciseCardDark() {
    GymTheme(darkTheme = true) {
        val token = GymTheme.token
        GUpcomingExerciseCard(
            modifier = Modifier.padding(token.spacing.md),
            model = R.drawable.body_unsplash,
            title = "Bench press",
            subtitle = "8 reps · 4 sets",
            onClick = {},
        )
    }
}
