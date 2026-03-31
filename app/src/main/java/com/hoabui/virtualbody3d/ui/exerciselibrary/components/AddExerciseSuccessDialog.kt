package com.hoabui.virtualbody3d.ui.exerciselibrary.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButton
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButtonVariant
import com.hoabui.virtualbody3d.ui.common_ui.atom.dialog.GDialog
import com.hoabui.virtualbody3d.ui.common_ui.atom.icon.GIcon
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.AddExerciseSuccessSummary
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.defaultExerciseLibraryCartDateMillis
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.defaultExerciseLibraryCartTime
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.icons.ExerciseLibraryPhosphorIcons
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@Composable
fun AddExerciseSuccessDialog(
    summary: AddExerciseSuccessSummary,
    onDismiss: () -> Unit,
    onViewWorkoutPlan: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val zone = ZoneId.systemDefault()
    val title = stringResource(R.string.exercise_library_add_success_title)
    val dateStr = remember(summary.scheduledDateMillis, zone) {
        val d = Instant.ofEpochMilli(summary.scheduledDateMillis).atZone(zone).toLocalDate()
        DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).format(d)
    }
    val timeStr = remember(summary.scheduledTime) {
        DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault()).format(summary.scheduledTime)
    }
    val description = stringResource(
        R.string.exercise_library_add_success_description,
        summary.exerciseCount,
        dateStr,
        timeStr,
    )
    GDialog(
        onDismissRequest = onDismiss,
        title = title,
        modifier = modifier,
        description = description,
        useEntranceAnimation = true,
        icon = {
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

@Preview(showBackground = true, name = "AddExerciseSuccessDialog — Light")
@Composable
private fun PreviewAddExerciseSuccessLight() {
    GymTheme {
        AddExerciseSuccessDialog(
            summary = AddExerciseSuccessSummary(
                exerciseCount = 3,
                scheduledDateMillis = defaultExerciseLibraryCartDateMillis(),
                scheduledTime = defaultExerciseLibraryCartTime(),
            ),
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
            summary = AddExerciseSuccessSummary(
                exerciseCount = 1,
                scheduledDateMillis = defaultExerciseLibraryCartDateMillis(),
                scheduledTime = defaultExerciseLibraryCartTime(),
            ),
            onDismiss = {},
        )
    }
}
