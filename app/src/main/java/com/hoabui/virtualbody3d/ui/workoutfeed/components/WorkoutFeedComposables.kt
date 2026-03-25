package com.hoabui.virtualbody3d.ui.workoutfeed.components

import androidx.compose.runtime.Composable
import com.hoabui.virtualbody3d.core.extensions.toVietnameseTopBarDate
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutFeedItem
import com.hoabui.virtualbody3d.ui.common_ui.organism.workout.GWorkoutDayCard
import com.hoabui.virtualbody3d.ui.common_ui.organism.workout.GWorkoutDayUiModel
import com.hoabui.virtualbody3d.ui.common_ui.organism.workout.GWorkoutExerciseUiModel

@Composable
fun TodayWorkoutCard(day: WorkoutFeedItem) {
    WorkoutDayCard(day = day, isToday = true)
}

@Composable
fun WorkoutDayCard(
    day: WorkoutFeedItem,
    isToday: Boolean = false,
) {
    GWorkoutDayCard(day = day.toOrganismUiModel(isToday = isToday))
}

private fun WorkoutFeedItem.toOrganismUiModel(isToday: Boolean): GWorkoutDayUiModel {
    val dateLabel = if (label.equals("Today", ignoreCase = true)) {
        "Hôm nay"
    } else {
        date.toVietnameseTopBarDate()
    }
    return GWorkoutDayUiModel(
        id = "${date}-${workoutName}",
        dateLabel = dateLabel,
        isToday = isToday,
        exercises = exercises.map {
            GWorkoutExerciseUiModel(
                id = it.id,
                imageModel = it.imageResId,
                title = it.name,
                subtitle = "${it.sets}x${it.reps}",
            )
        },
        durationMinutes = durationMinutes,
        estimatedCalories = estimatedCalories,
        muscleGroups = muscleGroups,
    )
}
