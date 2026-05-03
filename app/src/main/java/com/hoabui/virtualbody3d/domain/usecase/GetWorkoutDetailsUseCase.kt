package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.calendar.buildWorkoutCalendarExerciseLines
import com.hoabui.virtualbody3d.domain.calendar.groupLinesIntoWorkoutSessionBlocks
import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarSessionBlock
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSession
import com.hoabui.virtualbody3d.domain.repository.ExercisesRepository
import com.hoabui.virtualbody3d.domain.repository.WorkoutScheduleRepository
import com.hoabui.virtualbody3d.domain.repository.WorkoutSessionRepository
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetWorkoutDetailsUseCase @Inject constructor(
    private val workoutScheduleRepository: WorkoutScheduleRepository,
    private val workoutSessionRepository: WorkoutSessionRepository,
    private val exercisesRepository: ExercisesRepository,
) {
    /**
     * Returns workout session blocks grouped by sessionId for the given day.
     * Each block contains the session time range and associated exercises.
     */
    operator fun invoke(day: LocalDate): Flow<List<WorkoutCalendarSessionBlock>> {
        val dayKey = day.toEpochDay()
        return combine(
            workoutScheduleRepository.observeSchedulesInDayRange(dayKey, dayKey),
            workoutSessionRepository.observeWorkoutSessionsInDayRange(dayKey, dayKey),
            exercisesRepository.getAllExercises(),
        ) { schedules, workoutSessions, exercises ->
            val exerciseById = exercises.associateBy { it.id }
            val workoutSessionsById = workoutSessions.associateBy { it.id }
            val lines = buildWorkoutCalendarExerciseLines(schedules, exerciseById)
            groupLinesIntoWorkoutSessionBlocks(lines, workoutSessionsById)
        }
    }
}
