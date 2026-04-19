package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.repository.ExercisesRepository
import com.hoabui.virtualbody3d.domain.repository.WorkoutScheduleRepository
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Exercises for the nearest day that has a scheduled workout.
 * If there are no upcoming days, falls back to the most recent scheduled past day.
 */
class GetDashboardUpcomingExercisesUseCase @Inject constructor(
    private val exercisesRepository: ExercisesRepository,
    private val workoutScheduleRepository: WorkoutScheduleRepository,
) {
    operator fun invoke(): Flow<List<Exercise>> =
        combine(
            workoutScheduleRepository.observeWorkoutSchedules(),
            exercisesRepository.getAllExercises(),
        ) { schedules, exercises ->
            if (schedules.isEmpty()) return@combine emptyList()

            val todayEpochDay = LocalDate.now().toEpochDay()
            val schedulesByDay = schedules.groupBy { it.scheduledAt.toLocalDate().toEpochDay() }
            val nearestEpochDay = schedulesByDay.keys
                .filter { it >= todayEpochDay }
                .minOrNull()
                // If no upcoming day exists, fall back to the most recent past scheduled day.
                ?: schedulesByDay.keys.maxOrNull()
                ?: return@combine emptyList()

            val exerciseById = exercises.associateBy { it.id }
            schedulesByDay[nearestEpochDay].orEmpty()
                .sortedBy { it.scheduledAt }
                .mapNotNull { schedule -> exerciseById[schedule.exerciseId] }
                .take(PREVIEW_COUNT)
        }

    companion object {
        private const val PREVIEW_COUNT = 3
    }
}
