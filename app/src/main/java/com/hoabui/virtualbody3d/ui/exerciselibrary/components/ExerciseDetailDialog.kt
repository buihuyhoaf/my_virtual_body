package com.hoabui.virtualbody3d.ui.exerciselibrary.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import com.hoabui.virtualbody3d.ui.common_ui.atom.card.GCard
import com.hoabui.virtualbody3d.ui.common_ui.atom.surface.GSurface
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButton
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButtonVariant
import com.hoabui.virtualbody3d.ui.common_ui.image.LocalResourceProvider
import com.hoabui.virtualbody3d.ui.common_ui.image.toImageModel
import com.hoabui.virtualbody3d.ui.common_ui.molecule.info.GInfoRow
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.ExerciseDisplayResources
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import coil.compose.AsyncImage

// ----- Small reusable composables -----

@Composable
fun ExerciseBulletList(
    items: List<String>,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(token.spacing.xs)
    ) {
        items.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
                verticalAlignment = Alignment.Top
            ) {
                GText(
                    text = "•",
                    style = token.typography.bodyMedium,
                    color = token.colors.textPrimary
                )
                GText(
                    text = item,
                    style = token.typography.bodyMedium,
                    color = token.colors.textPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun SafetyNoteCard(
    notes: String,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    val items = notes.split("\n").map { it.trim() }.filter { it.isNotEmpty() }
    val backgroundColor = token.colors.surfaceSubtle
    val padding: Dp = token.spacing.xs

    GSurface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(token.radius.sm),
        color = backgroundColor,
    ) {
        Column(
            modifier = Modifier.padding(padding),
            verticalArrangement = Arrangement.spacedBy(token.spacing.xs)
        ) {
            if (items.isEmpty()) {
                GText(
                    text = notes,
                    style = token.typography.bodyMedium,
                    color = token.colors.textPrimary
                )
            } else {
                ExerciseBulletList(items = items)
            }
        }
    }
}

// ----- Description parsing: numbered steps -> bullet list -----

private fun parseDescriptionSteps(description: String): List<String> {
    return description
        .split("\n")
        .map { line ->
            line.replace(Regex("^\\s*\\d+[.)]\\s*"), "").trim()
        }
        .filter { it.isNotEmpty() }
}

// ----- Dialog sub-section label (labelMedium/textSecondary — intentionally different from GSectionHeader) -----

@Composable
private fun DialogSectionLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    GText(
        text = text,
        style = token.typography.labelMedium,
        color = token.colors.textSecondary,
        modifier = modifier.padding(bottom = token.spacing.xxs)
    )
}

// ----- Main dialog -----

@Composable
fun ExerciseDetailDialog(
    exercise: Exercise,
    onAddClick: (Exercise) -> Unit,
    onDismiss: () -> Unit
) {
    val token = GymTheme.token
    val scrollState = rememberScrollState()
    val resourceProvider = LocalResourceProvider.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        GCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(token.spacing.md),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                    // 1. Exercise hero: image (fixed height) + name below
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(token.spacing.xxl * 4)
                            .clip(
                                RoundedCornerShape(
                                    topStart = token.radius.lg,
                                    topEnd = token.radius.lg
                                )
                            )
                            .background(token.colors.surfaceSubtle)
                    ) {
                        AsyncImage(
                            model = exercise.image.toImageModel(resourceProvider),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(token.spacing.xxl * 4),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Column(
                        modifier = Modifier.padding(token.spacing.md),
                        verticalArrangement = Arrangement.spacedBy(token.spacing.md)
                    ) {
                    // Exercise name below image
                    GText(
                        text = exercise.name,
                        style = token.typography.titleLarge,
                        color = token.colors.textPrimary
                    )

                    // 2. Basic information section
                    Column(
                        verticalArrangement = Arrangement.spacedBy(token.spacing.xs)
                    ) {
                        GInfoRow(
                            label = stringResource(R.string.exercise_detail_body_region),
                            value = stringResource(ExerciseDisplayResources.bodyRegionResId(exercise.bodyRegion)),
                            leading = {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    modifier = Modifier.size(token.spacing.md),
                                    tint = token.colors.textSecondary,
                                )
                            },
                        )
                        GInfoRow(
                            label = stringResource(R.string.exercise_detail_equipment),
                            value = stringResource(ExerciseDisplayResources.equipmentResId(exercise.equipment)),
                            leading = {
                                Icon(
                                    imageVector = Icons.Default.Build,
                                    contentDescription = null,
                                    modifier = Modifier.size(token.spacing.md),
                                    tint = token.colors.textSecondary,
                                )
                            },
                        )
                        if (exercise.primaryMuscles.isNotEmpty()) {
                            val primaryLabels = exercise.primaryMuscles.map {
                                stringResource(ExerciseDisplayResources.muscleGroupResId(it))
                            }
                            GInfoRow(
                                label = stringResource(R.string.exercise_detail_primary_muscles),
                                value = primaryLabels.joinToString(),
                                leading = {
                                    Icon(
                                        imageVector = Icons.Default.FitnessCenter,
                                        contentDescription = null,
                                        modifier = Modifier.size(token.spacing.md),
                                        tint = token.colors.textSecondary,
                                    )
                                },
                            )
                        }
                        if (exercise.secondaryMuscles.isNotEmpty()) {
                            val secondaryLabels = exercise.secondaryMuscles.map {
                                stringResource(ExerciseDisplayResources.muscleGroupResId(it))
                            }
                            GInfoRow(
                                label = stringResource(R.string.exercise_detail_secondary_muscles),
                                value = secondaryLabels.joinToString(),
                                leading = {
                                    Icon(
                                        imageVector = Icons.Default.FitnessCenter,
                                        contentDescription = null,
                                        modifier = Modifier.size(token.spacing.md),
                                        tint = token.colors.textSecondary,
                                    )
                                },
                            )
                        }
                    }

                    // 3. Description section: "How to perform" as bullet list
                    DialogSectionLabel(text = stringResource(R.string.exercise_detail_how_to_perform))
                    val steps = parseDescriptionSteps(exercise.description)
                    if (steps.isNotEmpty()) {
                        ExerciseBulletList(items = steps)
                    } else {
                        GText(
                            text = exercise.description,
                            style = token.typography.bodyMedium,
                            color = token.colors.textPrimary
                        )
                    }

                    // 4. Safety section
                    DialogSectionLabel(text = stringResource(R.string.exercise_detail_safety_tips))
                    SafetyNoteCard(notes = exercise.safetyNotes)

                    // 5. Last weight (optional)
                    exercise.lastWeightKg?.let { kg ->
                        GText(
                            text = stringResource(R.string.exercise_detail_last_weight, kg),
                            style = token.typography.bodySmall,
                            color = token.colors.textSecondary
                        )
                    }

                    // 6. Action buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = token.spacing.xs),
                        horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GButton(
                            text = stringResource(R.string.confirm_image_cancel),
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            variant = GButtonVariant.Outlined,
                        )
                        GButton(
                            text = stringResource(R.string.exercise_detail_add_exercise),
                            onClick = { onAddClick(exercise) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}
