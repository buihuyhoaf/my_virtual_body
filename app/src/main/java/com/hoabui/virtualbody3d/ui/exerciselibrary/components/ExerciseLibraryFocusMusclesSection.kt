package com.hoabui.virtualbody3d.ui.exerciselibrary.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.emptyFocusMusclesStripImageNames
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.rememberFocusMusclesStripDrawableResIds
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

@Composable
fun ExerciseLibraryFocusMusclesSection(
    imageNames: ImmutableList<String>,
    onQuadrantClick: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val body = token.bodyAnalysis
    val imageHeight = body.exerciseLibraryFocusMuscleStripImageHeight
    val itemSpacing = body.exerciseLibraryFocusMuscleStripItemSpacing
    val imageCorner = token.spacing.xs
    val drawableResIds = rememberFocusMusclesStripDrawableResIds(imageNames)
    val shape = RoundedCornerShape(imageCorner)
    val borderStroke = BorderStroke(
        width = token.borderWidth.hairline,
        color = token.colors.exerciseLibraryHeatmapCardBorder,
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(itemSpacing),
    ) {
        drawableResIds.forEachIndexed { index, resId ->
            Box(Modifier.weight(1f)) {
                FocusMuscleQuadrantCell(
                    resId = resId,
                    imageHeight = imageHeight,
                    borderStroke = borderStroke,
                    shape = shape,
                    onClick = { onQuadrantClick(index) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun FocusMuscleQuadrantCell(
    resId: Int,
    imageHeight: Dp,
    borderStroke: BorderStroke,
    shape: RoundedCornerShape,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(resId),
        contentDescription = null,
        modifier = modifier
            .height(imageHeight)
            .fillMaxWidth()
            .border(borderStroke, shape)
            .clip(shape)
            .clickable(onClick = onClick),
        contentScale = ContentScale.Crop,
    )
}

@Preview(name = "Focus muscles — Light", showBackground = true)
@Composable
private fun ExerciseLibraryFocusMusclesSectionPreviewLight() {
    GymTheme(darkTheme = false) {
        ExerciseLibraryFocusMusclesSection(
            imageNames = persistentListOf(
                "chest_upper_pectoralis",
                "back_latissimus_dorsi",
                "back_normal",
                "back_normal",
            ),
            modifier = Modifier.padding(GymTheme.token.spacing.md),
        )
    }
}

@Preview(name = "Focus muscles — Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ExerciseLibraryFocusMusclesSectionPreviewDark() {
    GymTheme(darkTheme = true) {
        ExerciseLibraryFocusMusclesSection(
            imageNames = emptyFocusMusclesStripImageNames(),
            modifier = Modifier.padding(GymTheme.token.spacing.md),
        )
    }
}
