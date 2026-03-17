package com.hoabui.virtualbody3d.data.mapper

import com.hoabui.virtualbody3d.data.model.ExerciseDto
import com.hoabui.virtualbody3d.data.model.WorkoutFeedItemDto
import com.hoabui.virtualbody3d.domain.model.FeedExercise
import com.hoabui.virtualbody3d.domain.model.WorkoutFeedItem
import java.time.LocalDate

fun ExerciseDto.toFeedExercise(): FeedExercise = FeedExercise(
    id = id,
    name = name,
    imageResId = imageResId,
    imageResUrl = imageResUrl,
    sets = sets,
    reps = reps
)

fun WorkoutFeedItemDto.toDomain(): WorkoutFeedItem = WorkoutFeedItem(
    label = label,
    date = LocalDate.parse(dateString),
    workoutName = workoutName,
    exercises = exercises.map { it.toFeedExercise() },
    feeling = feeling
)
