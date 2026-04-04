package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.InstantInterval
import com.hoabui.virtualbody3d.domain.repository.WorkoutSessionRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

class ObserveBusyIntervalsUseCase @Inject constructor(
    private val workoutSessionRepository: WorkoutSessionRepository,
) {
    operator fun invoke(
        date: LocalDate,
        zoneId: ZoneId,
        locationId: String,
    ): Flow<List<InstantInterval>> =
        workoutSessionRepository.observeBusyIntervals(date, zoneId, locationId)
}
