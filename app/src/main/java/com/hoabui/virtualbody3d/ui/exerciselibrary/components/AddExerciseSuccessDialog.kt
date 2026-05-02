package com.hoabui.virtualbody3d.ui.exerciselibrary.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.exercise.halfOpenInstantIntervalDurationMinutes
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButton
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButtonVariant
import com.hoabui.virtualbody3d.ui.common_ui.atom.dialog.GDialog
import com.hoabui.virtualbody3d.ui.common_ui.atom.icon.GIcon
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.AddExerciseSuccessSummary
import com.hoabui.virtualbody3d.ui.exerciselibrary.util.defaultExerciseLibraryCartDateMillis
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.icons.ExerciseLibraryPhosphorIcons
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlin.math.min

@Composable
fun AddExerciseSuccessDialog(
    modifier: Modifier = Modifier,
    summary: AddExerciseSuccessSummary,
    onDismiss: () -> Unit,
    onViewWorkoutPlan: () -> Unit = {},
) {
    val title = stringResource(R.string.exercise_library_add_success_title)
    GDialog(
        onDismissRequest = onDismiss,
        title = title,
        modifier = modifier,
        description = null,
        descriptionContent = { AddExerciseSuccessReceiptBody(summary) },
        useEntranceAnimation = true,
        icon = {
            val token = GymTheme.token
            GIcon(
                imageVector = ExerciseLibraryPhosphorIcons.addSuccess,
                contentDescription = stringResource(R.string.exercise_library_add_success_icon_cd),
                modifier = Modifier.size(token.spacing.iconMedium),
                tint = token.colors.success,
            )
        },
        buttons = {
            GButton(
                text = stringResource(R.string.exercise_library_add_success_view_plan),
                onClick = onViewWorkoutPlan,
                modifier = Modifier.fillMaxWidth(),
            )
            GButton(
                text = stringResource(R.string.exercise_library_add_success_close),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                variant = GButtonVariant.Outlined,
            )
        },
    )
}

@Composable
private fun AddExerciseSuccessReceiptBody(summary: AddExerciseSuccessSummary) {
    val token = GymTheme.token
    val systemZone = Clock.systemDefaultZone().zone
    val dateFormatter = remember {
        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault())
    }
    val sessionDate = remember(summary.sessionStartInstant, systemZone) {
        summary.sessionStartInstant.atZone(systemZone).toLocalDate()
    }
    val today = LocalDate.now()
    val dateText = when (sessionDate) {
        today -> stringResource(R.string.exercise_library_add_success_date_today)
        today.plusDays(1) -> stringResource(R.string.exercise_library_add_success_date_tomorrow)
        else -> dateFormatter.format(sessionDate)
    }
    val timeFormatter = remember {
        DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(Locale.getDefault())
    }
    val startLocalTime = remember(summary.sessionStartInstant, systemZone) {
        summary.sessionStartInstant.atZone(systemZone).toLocalTime()
    }
    val endLocalTime = remember(summary.sessionEndInstant, systemZone) {
        summary.sessionEndInstant.atZone(systemZone).toLocalTime()
    }
    val timeRangeText = stringResource(
        R.string.exercise_library_add_success_line_time_range,
        timeFormatter.format(startLocalTime),
        timeFormatter.format(endLocalTime),
    )
    val durationMinutes = remember(summary.sessionStartInstant, summary.sessionEndInstant) {
        halfOpenInstantIntervalDurationMinutes(summary.sessionStartInstant, summary.sessionEndInstant)
    }
    val durationQuantity = min(durationMinutes, Int.MAX_VALUE.toLong()).toInt()
    val durationText = pluralStringResource(
        R.plurals.exercise_library_add_success_duration_minutes,
        durationQuantity,
        durationQuantity,
    )
    val primary = summary.primaryExerciseTitle?.takeIf { it.isNotBlank() }
    val exerciseText = if (primary == null) {
        pluralStringResource(
            R.plurals.exercise_library_add_success_exercises_count_only,
            summary.exerciseCount,
            summary.exerciseCount,
        )
    } else when (summary.exerciseCount) {
        1 -> stringResource(R.string.exercise_library_add_success_line_exercises_one, primary)
        else -> stringResource(
            R.string.exercise_library_add_success_line_exercises_many,
            primary,
            summary.exerciseCount - 1,
        )
    }
    val locationText = stringResource(
        R.string.exercise_library_add_success_line_location,
        summary.locationDisplayName,
    )
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(token.spacing.xs),
    ) {
        AddExerciseSuccessReceiptRow(
            icon = ExerciseLibraryPhosphorIcons.bookingCalendar,
            text = dateText,
            contentDescription = stringResource(R.string.exercise_library_add_success_receipt_row_date_cd),
        )
        AddExerciseSuccessReceiptRow(
            icon = ExerciseLibraryPhosphorIcons.bookingClock,
            text = timeRangeText,
            contentDescription = stringResource(R.string.exercise_library_add_success_receipt_row_time_cd),
        )
        AddExerciseSuccessReceiptRow(
            icon = ExerciseLibraryPhosphorIcons.cartDurationTimer,
            text = durationText,
            contentDescription = stringResource(R.string.exercise_library_add_success_receipt_row_duration_cd),
        )
        AddExerciseSuccessReceiptRow(
            icon = ExerciseLibraryPhosphorIcons.bookingMapPin,
            text = locationText,
            contentDescription = stringResource(R.string.exercise_library_add_success_receipt_row_location_cd),
        )
        AddExerciseSuccessReceiptRow(
            icon = ExerciseLibraryPhosphorIcons.detailCategory,
            text = exerciseText,
            contentDescription = stringResource(R.string.exercise_library_add_success_receipt_row_exercises_cd),
        )
    }
}

@Composable
private fun AddExerciseSuccessReceiptRow(
    icon: ImageVector,
    text: String,
    contentDescription: String,
) {
    val token = GymTheme.token
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(token.spacing.sm),
    ) {
        GIcon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(token.spacing.iconMedium),
            tint = token.colors.textSecondary,
        )
        GText(
            text = text,
            style = token.typography.bodyMedium,
            color = token.colors.textSecondary,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f),
        )
    }
}

private fun previewSummary(exerciseCount: Int, primaryExerciseTitle: String?): AddExerciseSuccessSummary {
    val systemZone = Clock.systemDefaultZone().zone
    val start = Instant.ofEpochMilli(defaultExerciseLibraryCartDateMillis())
        .atZone(systemZone)
        .toLocalDate()
        .atTime(10, 0)
        .atZone(systemZone)
        .toInstant()
    val end = start.plusSeconds(30L * 60L * exerciseCount.coerceAtLeast(1))
    return AddExerciseSuccessSummary(
        sessionStartInstant = start,
        sessionEndInstant = end,
        scheduledDateMillis = defaultExerciseLibraryCartDateMillis(),
        exerciseCount = exerciseCount,
        primaryExerciseTitle = primaryExerciseTitle,
        locationDisplayName = "Main gym",
    )
}

@Preview(showBackground = true, name = "AddExerciseSuccessDialog — Light")
@Composable
private fun PreviewAddExerciseSuccessLight() {
    GymTheme {
        AddExerciseSuccessDialog(
            summary = previewSummary(exerciseCount = 3, primaryExerciseTitle = "Squat"),
            onDismiss = {},
        )
    }
}

@Preview(
    showBackground = true,
    name = "AddExerciseSuccessDialog — Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewAddExerciseSuccessDark() {
    GymTheme(darkTheme = true) {
        AddExerciseSuccessDialog(
            summary = previewSummary(exerciseCount = 1, primaryExerciseTitle = "Sample"),
            onDismiss = {},
        )
    }
}

@Preview(showBackground = true, name = "AddExerciseSuccessDialog — Count-only line")
@Composable
private fun PreviewAddExerciseSuccessCountOnlyFallback() {
    GymTheme {
        AddExerciseSuccessDialog(
            summary = previewSummary(exerciseCount = 2, primaryExerciseTitle = null),
            onDismiss = {},
        )
    }
}
