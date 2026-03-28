package com.hoabui.virtualbody3d.data.mapper

import com.hoabui.virtualbody3d.data.model.WorkoutFeedItemDto
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutFeedItem
import java.time.LocalDate
import javax.inject.Inject

class WorkoutFeedMapper @Inject constructor(
    private val exerciseMapper: ExerciseMapper
) {
    fun toDomain(dto: WorkoutFeedItemDto): WorkoutFeedItem = WorkoutFeedItem(
        label = dto.label,
        date = LocalDate.parse(dto.dateString),
        workoutName = dto.workoutName,
        exercises = dto.exercises.map(exerciseMapper::toFeedExercise),
        durationMinutes = dto.durationMinutes,
        estimatedCalories = dto.estimatedCalories,
        muscleGroups = dto.muscleGroups
    )
}
