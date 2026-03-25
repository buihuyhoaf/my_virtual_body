package com.hoabui.virtualbody3d.ui.addworkout.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.material3.ExperimentalMaterial3Api
import com.hoabui.virtualbody3d.ui.common_ui.atom.surface.GSurface
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButton
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButtonVariant
import com.hoabui.virtualbody3d.ui.common_ui.atom.card.GCard
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
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
        GText(
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
    GSurface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(token.radius.sm),
    ) {
        Column(
            modifier = Modifier.padding(token.spacing.md),
            verticalArrangement = Arrangement.spacedBy(token.spacing.xxs)
        ) {
            GText(
                text = label,
                style = token.typography.labelSmall,
                color = token.colors.textSecondary
            )
            GText(
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
            GButton(
                text = stringResource(android.R.string.ok),
                onClick = {
                    state.selectedDateMillis?.let { millis ->
                        onConfirm(
                            Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
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
        GCard(containerColor = token.colors.surface) {
            Column(
                modifier = Modifier.padding(token.spacing.md),
                verticalArrangement = Arrangement.spacedBy(token.spacing.md)
            ) {
                GText(
                    text = stringResource(R.string.add_workout_time),
                    style = token.typography.titleMedium,
                    color = token.colors.textPrimary
                )
                androidx.compose.material3.TimePicker(state = state)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(token.spacing.xs, Alignment.End)
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
