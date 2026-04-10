package com.hoabui.virtualbody3d.ui.exerciselibrary.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButton
import com.hoabui.virtualbody3d.ui.common_ui.atom.card.GCard
import com.hoabui.virtualbody3d.ui.common_ui.atom.surface.GSurface
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.common_ui.image.LocalResourceProvider
import com.hoabui.virtualbody3d.ui.exerciselibrary.model.ExerciseLibraryCardImage
import com.hoabui.virtualbody3d.ui.exerciselibrary.model.toCoilModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseDetailSheetUiModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme

private const val HeroAspectRatio = 4f / 3f

private fun exerciseDetailTextStyle(base: TextStyle): TextStyle =
    base.merge(TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)))

// ----- Small reusable composables -----

@Composable
fun ExerciseBulletList(
    items: List<String>,
    modifier: Modifier = Modifier,
    bulletColor: Color? = null,
    textColor: Color? = null,
) {
    val token = GymTheme.token
    val resolvedBullet = bulletColor ?: token.colors.textPrimary
    val resolvedText = textColor ?: token.colors.textPrimary
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(token.spacing.sm),
    ) {
        items.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
                verticalAlignment = Alignment.Top,
            ) {
                GText(
                    text = "•",
                    style = exerciseDetailTextStyle(token.typography.bodyMedium),
                    color = resolvedBullet,
                )
                GText(
                    text = item,
                    style = exerciseDetailTextStyle(token.typography.bodyMedium),
                    color = resolvedText,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
fun ExerciseNumberedStepsList(
    items: List<String>,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(token.spacing.sm),
    ) {
        items.forEachIndexed { index, step ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
                verticalAlignment = Alignment.Top,
            ) {
                GText(
                    text = "${index + 1}.",
                    style = exerciseDetailTextStyle(token.typography.labelSmall),
                    color = token.colors.textSecondary,
                )
                GText(
                    text = step,
                    style = exerciseDetailTextStyle(token.typography.bodyMedium),
                    color = token.colors.textPrimary,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
fun SafetyNoteCard(
    notes: String,
    modifier: Modifier = Modifier,
    useWarningAppearance: Boolean = false,
) {
    val token = GymTheme.token
    val items = notes.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
    val backgroundColor =
        if (useWarningAppearance) token.colors.warningContainer else token.colors.surfaceSubtle
    val contentColor =
        if (useWarningAppearance) token.colors.onWarningContainer else token.colors.textPrimary
    val padding: Dp = token.spacing.sm

    GSurface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(token.radius.sm),
        color = backgroundColor,
    ) {
        Column(
            modifier = Modifier.padding(padding),
            verticalArrangement = Arrangement.spacedBy(token.spacing.sm),
        ) {
            if (items.isEmpty()) {
                GText(
                    text = notes,
                    style = exerciseDetailTextStyle(token.typography.bodyMedium),
                    color = contentColor,
                )
            } else {
                ExerciseBulletList(
                    items = items,
                    bulletColor = contentColor,
                    textColor = contentColor,
                )
            }
        }
    }
}

private fun parseDescriptionSteps(description: String): List<String> {
    return description
        .split("\n")
        .map { line ->
            line.replace(Regex("^\\s*\\d+[.)]\\s*"), "").trim()
        }
        .filter { it.isNotEmpty() }
}

@Composable
private fun DialogSectionLabel(
    text: String,
    modifier: Modifier = Modifier,
    includeTopSpacing: Boolean = true,
) {
    val token = GymTheme.token
    val top = if (includeTopSpacing) token.spacing.sm else token.spacing.none
    GText(
        text = text,
        style = exerciseDetailTextStyle(token.typography.labelMedium),
        color = token.colors.textSecondary,
        modifier = modifier.padding(
            top = top,
            bottom = token.spacing.xxs,
        ),
    )
}

/**
 * Symmetric 50/50 zones: body region (“where it hits”) vs equipment (“what to use”).
 */
@Composable
private fun ExerciseDetailIconicRow(
    targetRegionLabel: String,
    equipmentLabel: String,
) {
    val token = GymTheme.token
    val tile = token.bodyDetail.exerciseDetailIconicTileSize
    val shapeMd = RoundedCornerShape(token.radius.md)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(token.spacing.xs),
        ) {
            Image(
                painter = painterResource(R.drawable.body_unsplash),
                contentDescription = null,
                modifier = Modifier
                    .size(tile)
                    .clip(shapeMd)
                    .background(token.colors.surfaceSubtle),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
            )
            GText(
                text = targetRegionLabel,
                style = exerciseDetailTextStyle(token.typography.labelMedium),
                color = token.colors.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(token.spacing.xs),
        ) {
            Image(
                painter = painterResource(R.drawable.body_unsplash),
                contentDescription = null,
                modifier = Modifier
                    .size(tile)
                    .clip(shapeMd)
                    .background(token.colors.surfaceSubtle),
                contentScale = ContentScale.Crop,
                alignment = Alignment.Center,
            )
            GText(
                text = equipmentLabel,
                style = exerciseDetailTextStyle(token.typography.labelMedium),
                color = token.colors.textPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
fun ExerciseDetailDialog(
    detail: ExerciseDetailSheetUiModel,
    onDismiss: () -> Unit,
) {
    val token = GymTheme.token
    val scrollState = rememberScrollState()
    val configuration = LocalConfiguration.current
    val maxDialogHeight =
        (configuration.screenHeightDp.toFloat() * token.bodyDetail.exerciseDetailDialogMaxHeightFraction).dp
    val cardWidthFraction = token.bodyDetail.exerciseDetailCardWidthFraction
    val resourceProvider = LocalResourceProvider.current
    val heroCoilModel = remember(detail.heroImage) { detail.heroImage.toCoilModel(resourceProvider) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = token.spacing.md),
            contentAlignment = Alignment.Center,
        ) {
            GCard(
                modifier = Modifier
                    .fillMaxWidth(cardWidthFraction)
                    .heightIn(max = maxDialogHeight),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = maxDialogHeight),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                    ) {
                        // Static header: 4:3 media + title (does not scroll)
                        Column(modifier = Modifier.fillMaxWidth()) {
                            AsyncImage(
                                model = heroCoilModel,
                                contentDescription = detail.heroContentDescription,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(HeroAspectRatio)
                                    .clip(RoundedCornerShape(token.card.cornerRadius))
                                    .background(token.colors.surfaceSubtle),
                                contentScale = ContentScale.Crop,
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = token.spacing.md,
                                        top = token.spacing.md,
                                        end = token.spacing.md,
                                        bottom = token.spacing.sm,
                                    ),
                            ) {
                                GText(
                                    text = detail.name,
                                    style = exerciseDetailTextStyle(token.typography.titleLarge),
                                    color = token.colors.textPrimary,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                        // Scrollable body: iconic row, instructions, safety (clipped between anchors)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(scrollState)
                                    .padding(horizontal = token.spacing.md)
                                    .padding(bottom = token.spacing.md),
                                verticalArrangement = Arrangement.spacedBy(token.spacing.lg),
                            ) {
                                ExerciseDetailIconicRow(
                                    targetRegionLabel = detail.targetRegionLabel,
                                    equipmentLabel = detail.equipmentLabel,
                                )
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(token.spacing.sm),
                                ) {
                                    DialogSectionLabel(
                                        text = stringResource(R.string.exercise_detail_how_to_perform),
                                        includeTopSpacing = false,
                                    )
                                    val steps = parseDescriptionSteps(detail.description)
                                    if (steps.isNotEmpty()) {
                                        ExerciseNumberedStepsList(items = steps)
                                    } else {
                                        GText(
                                            text = detail.description,
                                            style = exerciseDetailTextStyle(token.typography.bodyMedium),
                                            color = token.colors.textPrimary,
                                        )
                                    }
                                }
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(token.spacing.sm),
                                ) {
                                    DialogSectionLabel(
                                        text = stringResource(R.string.exercise_detail_safety_section),
                                        includeTopSpacing = false,
                                    )
                                    SafetyNoteCard(
                                        notes = detail.safetyNotes,
                                        useWarningAppearance = true,
                                    )
                                }
                            }
                        }
                        // Sticky footer: last weight + OK
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(token.spacing.md),
                            verticalArrangement = Arrangement.spacedBy(token.spacing.sm),
                        ) {
                            detail.lastWeightKg?.let { kg ->
                                GText(
                                    text = stringResource(R.string.exercise_detail_last_weight, kg),
                                    style = exerciseDetailTextStyle(token.typography.bodySmall),
                                    color = token.colors.textSecondary,
                                )
                            }
                            GButton(
                                text = stringResource(R.string.exercise_detail_ok),
                                onClick = onDismiss,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "ExerciseDetailDialog — Light")
@Composable
private fun PreviewExerciseDetailDialogLight() {
    val preview = ExerciseDetailSheetUiModel(
        id = "preview",
        name = "Very long exercise name for preview ellipsis behavior in the knowledge card",
        description = "1. Step one with enough text to wrap\n2. Step two\n3. Step three",
        safetyNotes = "Keep core tight.\nStop if sharp pain.",
        lastWeightKg = 60.0,
        targetRegionLabel = "Chest",
        equipmentLabel = "Barbell",
        heroImage = ExerciseLibraryCardImage.LocalDrawableName("ic_logo_whitecat"),
        heroContentDescription = "Preview",
    )
    GymTheme {
        ExerciseDetailDialog(detail = preview, onDismiss = {})
    }
}

@Preview(showBackground = true, name = "ExerciseDetailDialog — Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewExerciseDetailDialogDark() {
    val preview = ExerciseDetailSheetUiModel(
        id = "preview",
        name = "Barbell bench press",
        description = "Lower with control.\nPress up evenly.",
        safetyNotes = "Use a spotter for heavy sets.",
        lastWeightKg = null,
        targetRegionLabel = "Chest",
        equipmentLabel = "Dumbbell",
        heroImage = ExerciseLibraryCardImage.LocalDrawableName("ic_logo_whitecat"),
        heroContentDescription = "Preview",
    )
    GymTheme(darkTheme = true) {
        ExerciseDetailDialog(detail = preview, onDismiss = {})
    }
}
