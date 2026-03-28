package com.hoabui.virtualbody3d.ui.common_ui.organism.workout

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButton
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButtonVariant
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GIconButton
import com.hoabui.virtualbody3d.ui.common_ui.atom.card.GCard
import com.hoabui.virtualbody3d.ui.common_ui.atom.surface.GSurface
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.common_ui.molecule.card.GMediaInfoCard
import com.hoabui.virtualbody3d.ui.common_ui.molecule.input.GInputFieldGroup
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon

data class GAddWorkoutFormUiModel(
    val exerciseImageModel: Any?,
    val exerciseTitle: String,
    val exerciseSubtitle: String?,
    val exerciseBadgeText: String?,

    val scheduleTitle: String,
    val dateLabel: String,
    val timeLabel: String,
    val selectedDate: LocalDate,
    val selectedTime: LocalTime,
    val dateValueText: String,
    val timeValueText: String,

    val workoutSetupTitle: String,
    val setsLabel: String,
    val repsLabel: String,
    val weightKgLabel: String,
    val restSecondsLabel: String,

    val notesLabel: String,
    val notesPlaceholder: String?,
    val notesValue: String,

    val sets: Int,
    val reps: Int,
    val weightKg: Double,
    val restSeconds: Int,
)

@Composable
fun GAddWorkoutForm(
    uiModel: GAddWorkoutFormUiModel,
    modifier: Modifier = Modifier,
    onDateChange: (LocalDate) -> Unit,
    onTimeChange: (LocalTime) -> Unit,
    onSetsChange: (Int) -> Unit,
    onRepsChange: (Int) -> Unit,
    onWeightChange: (Double) -> Unit,
    onRestChange: (Int) -> Unit,
    onNotesChange: (String) -> Unit,
) {
    val token = GymTheme.token
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(token.spacing.lg),
    ) {
        GMediaInfoCard(
            imageModel = uiModel.exerciseImageModel,
            title = uiModel.exerciseTitle,
            subtitle = uiModel.exerciseSubtitle,
            badge = uiModel.exerciseBadgeText?.let { badgeText ->
                {
                    GSurface(shape = RoundedCornerShape(token.radius.sm)) {
                        GText(
                            text = badgeText,
                            style = token.typography.labelSmall,
                            color = token.colors.textPrimary,
                            modifier = Modifier.padding(
                                horizontal = token.spacing.xs,
                                vertical = token.spacing.xxs,
                            ),
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )

        ScheduleSectionOrganism(
            title = uiModel.scheduleTitle,
            dateLabel = uiModel.dateLabel,
            timeLabel = uiModel.timeLabel,
            selectedDate = uiModel.selectedDate,
            selectedTime = uiModel.selectedTime,
            dateValueText = uiModel.dateValueText,
            timeValueText = uiModel.timeValueText,
            onDateChange = onDateChange,
            onTimeChange = onTimeChange,
        )

        WorkoutSetupSectionOrganism(
            title = uiModel.workoutSetupTitle,
            setsLabel = uiModel.setsLabel,
            repsLabel = uiModel.repsLabel,
            weightKgLabel = uiModel.weightKgLabel,
            restSecondsLabel = uiModel.restSecondsLabel,
            sets = uiModel.sets,
            reps = uiModel.reps,
            weightKg = uiModel.weightKg,
            restSeconds = uiModel.restSeconds,
            onSetsChange = onSetsChange,
            onRepsChange = onRepsChange,
            onWeightChange = onWeightChange,
            onRestChange = onRestChange,
        )

        GInputFieldGroup(
            label = uiModel.notesLabel,
            value = uiModel.notesValue,
            onValueChange = onNotesChange,
            placeholder = uiModel.notesPlaceholder,
            singleLine = false,
            maxLines = 4,
        )
    }
}

@Composable
private fun ScheduleSectionOrganism(
    title: String,
    dateLabel: String,
    timeLabel: String,
    selectedDate: LocalDate,
    selectedTime: LocalTime,
    dateValueText: String,
    timeValueText: String,
    onDateChange: (LocalDate) -> Unit,
    onTimeChange: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        GText(
            text = title,
            style = token.typography.titleSmall,
            color = token.colors.textPrimary,
            modifier = Modifier.padding(bottom = token.spacing.xs),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(token.spacing.md),
        ) {
            ScheduleRowChip(
                modifier = Modifier.weight(1f),
                label = dateLabel,
                value = dateValueText,
                onClick = { showDatePicker = true },
            )
            ScheduleRowChip(
                modifier = Modifier.weight(1f),
                label = timeLabel,
                value = timeValueText,
                onClick = { showTimePicker = true },
            )
        }

        if (showDatePicker) {
            ScheduleDatePickerDialog(
                initialDate = selectedDate,
                onConfirm = {
                    onDateChange(it)
                    showDatePicker = false
                },
                onDismiss = { showDatePicker = false },
            )
        }
        if (showTimePicker) {
            ScheduleTimePickerDialog(
                title = timeLabel,
                initialTime = selectedTime,
                onConfirm = {
                    onTimeChange(it)
                    showTimePicker = false
                },
                onDismiss = { showTimePicker = false },
            )
        }
    }
}

@Composable
private fun ScheduleRowChip(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    GSurface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(token.radius.sm),
    ) {
        Column(
            modifier = Modifier.padding(token.spacing.md),
            verticalArrangement = Arrangement.spacedBy(token.spacing.xxs),
        ) {
            GText(text = label, style = token.typography.labelSmall, color = token.colors.textSecondary)
            GText(text = value, style = token.typography.bodyMedium, color = token.colors.textPrimary)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleDatePickerDialog(
    initialDate: LocalDate,
    onConfirm: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
) {
    val initialMillis = initialDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val state = androidx.compose.material3.rememberDatePickerState(
        initialSelectedDateMillis = initialMillis,
    )
    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            GButton(
                text = stringResource(android.R.string.ok),
                onClick = {
                    state.selectedDateMillis?.let { millis ->
                        onConfirm(
                            Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate(),
                        )
                    }
                },
            )
        },
        dismissButton = {
            GButton(
                text = stringResource(android.R.string.cancel),
                onClick = onDismiss,
                variant = GButtonVariant.Outlined,
            )
        },
    ) {
        androidx.compose.material3.DatePicker(state = state)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleTimePickerDialog(
    title: String,
    initialTime: LocalTime,
    onConfirm: (LocalTime) -> Unit,
    onDismiss: () -> Unit,
) {
    val state = androidx.compose.material3.rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = true,
    )
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        val token = GymTheme.token
        GCard(containerColor = token.colors.surface) {
            Column(
                modifier = Modifier.padding(token.spacing.md),
                verticalArrangement = Arrangement.spacedBy(token.spacing.md),
            ) {
                GText(
                    text = title,
                    style = token.typography.titleMedium,
                    color = token.colors.textPrimary,
                )
                androidx.compose.material3.TimePicker(state = state)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(token.spacing.xs, Alignment.End),
                ) {
                    GButton(
                        text = stringResource(android.R.string.cancel),
                        onClick = onDismiss,
                        variant = GButtonVariant.Ghost,
                    )
                    GButton(
                        text = stringResource(android.R.string.ok),
                        onClick = { onConfirm(LocalTime.of(state.hour, state.minute)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkoutSetupSectionOrganism(
    title: String,
    setsLabel: String,
    repsLabel: String,
    weightKgLabel: String,
    restSecondsLabel: String,
    sets: Int,
    reps: Int,
    weightKg: Double,
    restSeconds: Int,
    onSetsChange: (Int) -> Unit,
    onRepsChange: (Int) -> Unit,
    onWeightChange: (Double) -> Unit,
    onRestChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    Column(modifier = modifier.fillMaxWidth()) {
        GText(
            text = title,
            style = token.typography.titleSmall,
            color = token.colors.textPrimary,
            modifier = Modifier.padding(bottom = token.spacing.xs),
        )
        Column(verticalArrangement = Arrangement.spacedBy(token.spacing.md)) {
            WorkoutNumberStepperOrganism(
                modifier = Modifier.fillMaxWidth(),
                label = setsLabel,
                value = sets,
                onValueChange = onSetsChange,
                minValue = 1,
                maxValue = 20,
            )
            WorkoutNumberStepperOrganism(
                modifier = Modifier.fillMaxWidth(),
                label = repsLabel,
                value = reps,
                onValueChange = onRepsChange,
                minValue = 1,
                maxValue = 100,
            )
            WorkoutWeightStepperOrganism(
                modifier = Modifier.fillMaxWidth(),
                label = weightKgLabel,
                valueKg = weightKg,
                onValueChange = onWeightChange,
            )
            WorkoutNumberStepperOrganism(
                modifier = Modifier.fillMaxWidth(),
                label = restSecondsLabel,
                value = restSeconds,
                onValueChange = onRestChange,
                minValue = 0,
                maxValue = 600,
                valueSuffix = " s",
            )
        }
    }
}

@Composable
private fun WorkoutNumberStepperOrganism(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minValue: Int = 0,
    maxValue: Int? = null,
    valueSuffix: String = "",
) {
    val token = GymTheme.token
    val effectiveMax = maxValue ?: Int.MAX_VALUE

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        GText(text = label, style = token.typography.bodyMedium, color = token.colors.textPrimary)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(token.spacing.xxs),
        ) {
            GIconButton(
                onClick = { if (value > minValue) onValueChange(value - 1) },
                enabled = value > minValue,
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = stringResource(R.string.add_workout_decrease),
                )
            }
            GText(
                text = "$value$valueSuffix",
                style = token.typography.titleMedium,
                color = token.colors.textPrimary,
                modifier = Modifier.padding(horizontal = token.spacing.xxs),
            )
            GIconButton(
                onClick = { if (value < effectiveMax) onValueChange(value + 1) },
                enabled = value < effectiveMax,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_workout_increase),
                )
            }
        }
    }
}

@Composable
private fun WorkoutWeightStepperOrganism(
    label: String,
    valueKg: Double,
    onValueChange: (Double) -> Unit,
    modifier: Modifier = Modifier,
    minValue: Double = 0.0,
    maxValue: Double = 500.0,
    step: Double = 2.5,
) {
    val token = GymTheme.token
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        GText(text = label, style = token.typography.bodyMedium, color = token.colors.textPrimary)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(token.spacing.xxs),
        ) {
            GIconButton(
                onClick = {
                    val next = (valueKg - step).coerceIn(minValue, maxValue)
                    if (next != valueKg) onValueChange(next)
                },
                enabled = valueKg > minValue,
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = stringResource(R.string.add_workout_decrease),
                )
            }
            GText(
                text = "%.1f kg".format(valueKg),
                style = token.typography.titleMedium,
                color = token.colors.textPrimary,
                modifier = Modifier.padding(horizontal = token.spacing.xxs),
            )
            GIconButton(
                onClick = {
                    val next = (valueKg + step).coerceIn(minValue, maxValue)
                    if (next != valueKg) onValueChange(next)
                },
                enabled = valueKg < maxValue,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_workout_increase),
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "GAddWorkoutForm — light")
@Composable
private fun PreviewGAddWorkoutFormLight() {
    GymTheme {
        val date = LocalDate.of(2026, 3, 23)
        val time = LocalTime.of(9, 30)
        GAddWorkoutForm(
            uiModel = GAddWorkoutFormUiModel(
                exerciseImageModel = null,
                exerciseTitle = "Bench Press",
                exerciseSubtitle = "Chest",
                exerciseBadgeText = "Beginner",
                scheduleTitle = "Schedule",
                dateLabel = "Date",
                timeLabel = "Time",
                selectedDate = date,
                selectedTime = time,
                dateValueText = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                timeValueText = time.format(DateTimeFormatter.ofPattern("HH:mm")),
                workoutSetupTitle = "Workout setup",
                setsLabel = "Sets",
                repsLabel = "Reps",
                weightKgLabel = "Weight (kg)",
                restSecondsLabel = "Rest (s)",
                notesLabel = "Notes",
                notesPlaceholder = "Optional notes…",
                notesValue = "",
                sets = 4,
                reps = 10,
                weightKg = 60.0,
                restSeconds = 90,
            ),
            onDateChange = {},
            onTimeChange = {},
            onSetsChange = {},
            onRepsChange = {},
            onWeightChange = {},
            onRestChange = {},
            onNotesChange = {},
            modifier = Modifier.padding(GymTheme.token.spacing.md),
        )
    }
}

@Preview(
    showBackground = true,
    name = "GAddWorkoutForm — dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewGAddWorkoutFormDark() {
    GymTheme(darkTheme = true) {
        val date = LocalDate.of(2026, 3, 23)
        val time = LocalTime.of(18, 15)
        GAddWorkoutForm(
            uiModel = GAddWorkoutFormUiModel(
                exerciseImageModel = null,
                exerciseTitle = "Squat",
                exerciseSubtitle = "Legs",
                exerciseBadgeText = "Advanced",
                scheduleTitle = "Schedule",
                dateLabel = "Date",
                timeLabel = "Time",
                selectedDate = date,
                selectedTime = time,
                dateValueText = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                timeValueText = time.format(DateTimeFormatter.ofPattern("HH:mm")),
                workoutSetupTitle = "Workout setup",
                setsLabel = "Sets",
                repsLabel = "Reps",
                weightKgLabel = "Weight (kg)",
                restSecondsLabel = "Rest (s)",
                notesLabel = "Notes",
                notesPlaceholder = "Optional notes…",
                notesValue = "Tempo focus",
                sets = 5,
                reps = 5,
                weightKg = 120.0,
                restSeconds = 120,
            ),
            onDateChange = {},
            onTimeChange = {},
            onSetsChange = {},
            onRepsChange = {},
            onWeightChange = {},
            onRestChange = {},
            onNotesChange = {},
            modifier = Modifier.padding(GymTheme.token.spacing.md),
        )
    }
}

