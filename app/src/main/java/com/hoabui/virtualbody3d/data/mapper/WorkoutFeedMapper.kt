package com.hoabui.virtualbody3d.data.mapper

import com.hoabui.virtualbody3d.data.model.WorkoutFeedItemDto
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutFeedItem
import java.time.LocalDate

fun WorkoutFeedItemDto.toDomain(): WorkoutFeedItem = WorkoutFeedItem(
    label = label,
    date = LocalDate.parse(dateString),
    workoutName = workoutName,
    exercises = exercises.map { it.toFeedExercise() },
    durationMinutes = durationMinutes,
    estimatedCalories = estimatedCalories,
    muscleGroups = muscleGroups
)
