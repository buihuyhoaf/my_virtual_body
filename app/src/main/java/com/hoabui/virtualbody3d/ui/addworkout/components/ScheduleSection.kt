package com.hoabui.virtualbody3d.ui.addworkout.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Reusable section for choosing workout date and time.
 * Column with DateRow (opens DatePickerDialog) and TimeRow (opens TimePickerDialog).
 * Uses [GymTheme.token] for spacing and container style.
 */
@Composable
fun ScheduleSection(
    selectedDate: LocalDate,
    selectedTime: LocalTime,
    onDateChange: (LocalDate) -> Unit,
    onTimeChange: (LocalTime) -> Unit,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.add_workout_schedule),
            style = token.typography.titleSmall,
            color = token.colors.textPrimary,
            modifier = Modifier.padding(bottom = token.spacing.xs)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(token.spacing.md)
        ) {
            ScheduleRowChip(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.add_workout_date),
                value = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                onClick = { showDatePicker = true }
            )
            ScheduleRowChip(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.add_workout_time),
                value = selectedTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                onClick = { showTimePicker = true }
            )
        }
        if (showDatePicker) {
            ScheduleDatePickerDialog(
                initialDate = selectedDate,
                onConfirm = { onDateChange(it); showDatePicker = false },
                onDismiss = { showDatePicker = false }
            )
        }
        if (showTimePicker) {
            ScheduleTimePickerDialog(
                initialTime = selectedTime,
                onConfirm = { onTimeChange(it); showTimePicker = false },
                onDismiss = { showTimePicker = false }
            )
        }
    }
}

@Composable
private fun ScheduleRowChip(
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(token.radius.sm),
        color = token.colors.surfaceSubtle
    ) {
        Column(
            modifier = Modifier.padding(token.spacing.md),
            verticalArrangement = Arrangement.spacedBy(token.spacing.xxs)
        ) {
            Text(
                text = label,
                style = token.typography.labelSmall,
                color = token.colors.textSecondary
            )
            Text(
                text = value,
                style = token.typography.bodyMedium,
                color = token.colors.textPrimary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleDatePickerDialog(
    initialDate: LocalDate,
    onConfirm: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val initialMillis = initialDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val state = androidx.compose.material3.rememberDatePickerState(
        initialSelectedDateMillis = initialMillis
    )
    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    state.selectedDateMillis?.let { millis ->
                        onConfirm(
                            Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        )
                    }
                }
            ) {
                Text(stringResource(android.R.string.ok))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.cancel))
            }
        }
    ) {
        androidx.compose.material3.DatePicker(state = state)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleTimePickerDialog(
    initialTime: LocalTime,
    onConfirm: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    val state = androidx.compose.material3.rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = true
    )
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        val token = GymTheme.token
        Card(
            shape = RoundedCornerShape(token.radius.lg),
            colors = CardDefaults.cardColors(containerColor = token.colors.surface)
        ) {
            Column(
                modifier = Modifier.padding(token.spacing.md),
                verticalArrangement = Arrangement.spacedBy(token.spacing.md)
            ) {
                Text(
                    text = stringResource(R.string.add_workout_time),
                    style = token.typography.titleMedium,
                    color = token.colors.textPrimary
                )
                androidx.compose.material3.TimePicker(state = state)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text(stringResource(android.R.string.cancel))
                    }
                    Spacer(modifier = Modifier.width(token.spacing.xs))
                    Button(
                        onClick = { onConfirm(LocalTime.of(state.hour, state.minute)) }
                    ) {
                        Text(stringResource(android.R.string.ok))
                    }
                }
            }
        }
    }
}
