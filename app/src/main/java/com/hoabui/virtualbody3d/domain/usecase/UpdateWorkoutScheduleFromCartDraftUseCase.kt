package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.repository.WorkoutScheduleRepository
import javax.inject.Inject

class UpdateWorkoutScheduleFromCartDraftUseCase @Inject constructor(
    private val workoutScheduleRepository: WorkoutScheduleRepository,
) {
    suspend operator fun invoke(
        rowId: Long,
        exerciseId: String,
        measurementMode: ExerciseMeasurementMode,
        sets: Int,
        reps: Int,
        weightKg: Double,
        durationSeconds: Int?,
    ): Boolean = workoutScheduleRepository.updateWorkoutScheduleRow(
        rowId = rowId,
        exerciseId = exerciseId,
        measurementMode = measurementMode,
        sets = sets,
        reps = reps,
        weightKg = weightKg,
        durationSeconds = durationSeconds,
    )
}
