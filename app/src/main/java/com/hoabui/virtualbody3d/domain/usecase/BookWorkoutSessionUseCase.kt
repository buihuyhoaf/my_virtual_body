package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.SessionExerciseLine
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSession
import com.hoabui.virtualbody3d.domain.repository.BookWorkoutSessionResult
import com.hoabui.virtualbody3d.domain.repository.WorkoutSessionRepository
import javax.inject.Inject

class BookWorkoutSessionUseCase @Inject constructor(
    private val workoutSessionRepository: WorkoutSessionRepository,
) {
    suspend operator fun invoke(
        session: WorkoutSession,
        lines: List<SessionExerciseLine>,
    ): BookWorkoutSessionResult = workoutSessionRepository.bookSession(session, lines)
}
