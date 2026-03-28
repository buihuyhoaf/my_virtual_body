package com.hoabui.virtualbody3d.ui.workoutfeed.components

import androidx.compose.runtime.Composable
import com.hoabui.virtualbody3d.core.extensions.toVietnameseTopBarDate
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutFeedItem
import com.hoabui.virtualbody3d.ui.common_ui.image.LocalResourceProvider
import com.hoabui.virtualbody3d.ui.common_ui.image.toImageModel
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

@Composable
private fun WorkoutFeedItem.toOrganismUiModel(isToday: Boolean): GWorkoutDayUiModel {
    val resourceProvider = LocalResourceProvider.current
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
                imageModel = it.image.toImageModel(resourceProvider),
                title = it.name,
                subtitle = "${it.sets}x${it.reps}",
            )
        },
        durationMinutes = durationMinutes,
        estimatedCalories = estimatedCalories,
        muscleGroups = muscleGroups,
    )
}
