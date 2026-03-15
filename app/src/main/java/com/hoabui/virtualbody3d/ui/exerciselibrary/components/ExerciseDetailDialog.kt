package com.hoabui.virtualbody3d.ui.exerciselibrary.components

import androidx.compose.foundation.Image
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.Difficulty
import com.hoabui.virtualbody3d.domain.model.Exercise
import com.hoabui.virtualbody3d.ui.exerciselibrary.data.ExerciseDisplayResources
import com.hoabui.virtualbody3d.ui.theme.GymTheme

// ----- Small reusable composables -----

@Composable
fun ExerciseInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(token.spacing.md),
            tint = token.colors.textSecondary
        )
        Text(
            text = label,
            style = token.typography.labelMedium,
            color = token.colors.textSecondary,
            modifier = Modifier.padding(end = token.spacing.xxs)
        )
        Text(
            text = value,
            style = token.typography.bodyMedium,
            color = token.colors.textPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
        )
    }
}

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
                Text(
                    text = "•",
                    style = token.typography.bodyMedium,
                    color = token.colors.textPrimary
                )
                Text(
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

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(token.radius.sm),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier.padding(padding),
            verticalArrangement = Arrangement.spacedBy(token.spacing.xs)
        ) {
            if (items.isEmpty()) {
                Text(
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

// ----- Section title -----

@Composable
private fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    Text(
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

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(token.spacing.md),
            shape = RoundedCornerShape(token.radius.lg),
            colors = CardDefaults.cardColors(containerColor = token.colors.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = token.elevation.level2)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                // 1. Exercise hero: image (fixed height) + name below + difficulty badge top-right
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
                        .background(token.colors.dashboardMealImageBackground)
                ) {
                    Image(
                        painter = painterResource(exercise.imageResId),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(token.spacing.xxl * 4),
                        contentScale = ContentScale.Crop
                    )
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(token.spacing.xs),
                        shape = RoundedCornerShape(token.radius.sm),
                        color = when (exercise.difficulty) {
                            Difficulty.Beginner -> token.colors.difficultyBeginnerBg
                            Difficulty.Intermediate -> token.colors.difficultyIntermediateBg
                            Difficulty.Advanced -> token.colors.difficultyAdvancedBg
                        }
                    ) {
                        Text(
                            text = stringResource(ExerciseDisplayResources.difficultyResId(exercise.difficulty)),
                            style = token.typography.labelSmall,
                            color = when (exercise.difficulty) {
                                Difficulty.Beginner -> token.colors.difficultyBeginnerText
                                Difficulty.Intermediate -> token.colors.difficultyIntermediateText
                                Difficulty.Advanced -> token.colors.difficultyAdvancedText
                            },
                            modifier = Modifier.padding(
                                horizontal = token.spacing.xs,
                                vertical = token.spacing.xxs
                            )
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(token.spacing.md),
                    verticalArrangement = Arrangement.spacedBy(token.spacing.md)
                ) {
                    // Exercise name below image
                    Text(
                        text = exercise.name,
                        style = token.typography.titleLarge,
                        color = token.colors.textPrimary
                    )

                    // 2. Basic information section
                    Column(
                        verticalArrangement = Arrangement.spacedBy(token.spacing.xs)
                    ) {
                        ExerciseInfoRow(
                            icon = Icons.Default.Place,
                            label = stringResource(R.string.exercise_detail_body_region),
                            value = stringResource(ExerciseDisplayResources.bodyRegionResId(exercise.bodyRegion))
                        )
                        ExerciseInfoRow(
                            icon = Icons.Default.Build,
                            label = stringResource(R.string.exercise_detail_equipment),
                            value = stringResource(ExerciseDisplayResources.equipmentResId(exercise.equipment))
                        )
                        if (exercise.primaryMuscles.isNotEmpty()) {
                            val primaryLabels = exercise.primaryMuscles.map {
                                stringResource(ExerciseDisplayResources.muscleGroupResId(it))
                            }
                            ExerciseInfoRow(
                                icon = Icons.Default.FitnessCenter,
                                label = stringResource(R.string.exercise_detail_primary_muscles),
                                value = primaryLabels.joinToString()
                            )
                        }
                        if (exercise.secondaryMuscles.isNotEmpty()) {
                            val secondaryLabels = exercise.secondaryMuscles.map {
                                stringResource(ExerciseDisplayResources.muscleGroupResId(it))
                            }
                            ExerciseInfoRow(
                                icon = Icons.Default.FitnessCenter,
                                label = stringResource(R.string.exercise_detail_secondary_muscles),
                                value = secondaryLabels.joinToString()
                            )
                        }
                    }

                    // 3. Description section: "How to perform" as bullet list
                    SectionTitle(text = stringResource(R.string.exercise_detail_how_to_perform))
                    val steps = parseDescriptionSteps(exercise.description)
                    if (steps.isNotEmpty()) {
                        ExerciseBulletList(items = steps)
                    } else {
                        Text(
                            text = exercise.description,
                            style = token.typography.bodyMedium,
                            color = token.colors.textPrimary
                        )
                    }

                    // 4. Safety section
                    SectionTitle(text = stringResource(R.string.exercise_detail_safety_tips))
                    SafetyNoteCard(notes = exercise.safetyNotes)

                    // 5. Last weight (optional)
                    exercise.lastWeightKg?.let { kg ->
                        Text(
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
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(token.button.cornerRadius),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = token.colors.textPrimary
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.confirm_image_cancel),
                                style = token.typography.labelLarge
                            )
                        }
                        Button(
                            onClick = { onAddClick(exercise) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(token.button.cornerRadius),
                            colors = ButtonDefaults.buttonColors(containerColor = token.colors.primary),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = token.elevation.level0)
                        ) {
                            Text(
                                text = stringResource(R.string.exercise_detail_add_exercise),
                                style = token.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}
