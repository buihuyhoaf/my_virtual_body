package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.calendar.groupSchedulesToDaySummaries
import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarVolumeLevel
import com.hoabui.virtualbody3d.domain.model.exercise.dashboard.ExerciseLibraryWeekStripDay
import com.hoabui.virtualbody3d.domain.repository.WorkoutScheduleRepository
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveExerciseLibraryWeekStripUseCase @Inject constructor(
    private val workoutScheduleRepository: WorkoutScheduleRepository,
) {
    operator fun invoke(): Flow<List<ExerciseLibraryWeekStripDay>> {
        val clock = Clock.systemDefaultZone()
        return workoutScheduleRepository.observeWorkoutSchedules().map { schedules ->
            val summaries = groupSchedulesToDaySummaries(schedules)
            val today = LocalDate.now(clock)
            (6 downTo 0).map { offset ->
                val date = today.minusDays(offset.toLong())
                val epoch = date.toEpochDay()
                val summary = summaries[epoch]
                val level = summary?.volumeLevel?.toDensity() ?: 0
                ExerciseLibraryWeekStripDay(epochDay = epoch, densityLevel = level)
            }
        }
    }
}

private fun WorkoutCalendarVolumeLevel.toDensity(): Int = when (this) {
    WorkoutCalendarVolumeLevel.None -> 0
    WorkoutCalendarVolumeLevel.ActiveRecovery -> 1
    WorkoutCalendarVolumeLevel.SolidWorkout -> 2
    WorkoutCalendarVolumeLevel.HighVolume -> 3
}
