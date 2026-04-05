package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarExerciseLine
import com.hoabui.virtualbody3d.domain.repository.ExercisesRepository
import com.hoabui.virtualbody3d.domain.repository.WorkoutScheduleRepository
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveWorkoutCalendarDayDetailUseCase @Inject constructor(
    private val workoutScheduleRepository: WorkoutScheduleRepository,
    private val exercisesRepository: ExercisesRepository,
) {
    operator fun invoke(day: LocalDate, zoneId: ZoneId): Flow<List<WorkoutCalendarExerciseLine>> {
        val dayKey = day.toEpochDay()
        return combine(
            workoutScheduleRepository.observeSchedulesInDayRange(dayKey, dayKey),
            exercisesRepository.getAllExercises(),
        ) { schedules, exercises ->
            val nameById = exercises.associate { it.id to it.name }
            schedules.mapNotNull { sch ->
                val rowId = sch.rowId ?: return@mapNotNull null
                WorkoutCalendarExerciseLine(
                    rowId = rowId,
                    exerciseId = sch.exerciseId,
                    exerciseDisplayName = nameById[sch.exerciseId] ?: sch.exerciseId,
                    sets = sch.sets,
                    reps = sch.reps,
                    durationSeconds = sch.durationSeconds,
                    measurementMode = sch.measurementMode,
                    executionStatus = sch.executionStatus,
                    sessionId = sch.sessionId,
                )
            }
        }
    }
}
