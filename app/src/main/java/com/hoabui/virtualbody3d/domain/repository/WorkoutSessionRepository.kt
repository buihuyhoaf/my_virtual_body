package com.hoabui.virtualbody3d.domain.repository

import com.hoabui.virtualbody3d.domain.model.exercise.GymLocation
import com.hoabui.virtualbody3d.domain.model.exercise.InstantInterval
import com.hoabui.virtualbody3d.domain.model.exercise.SessionExerciseLine
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSession
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId

sealed class BookWorkoutSessionResult {
    data class Success(val scheduledCount: Int) : BookWorkoutSessionResult()
    data object Conflict : BookWorkoutSessionResult()
    data object InvalidDraft : BookWorkoutSessionResult()
}

interface WorkoutSessionRepository {

    suspend fun bookSession(
        session: WorkoutSession,
        lines: List<SessionExerciseLine>,
        zoneId: ZoneId,
    ): BookWorkoutSessionResult

    fun observeBusyIntervals(
        date: LocalDate,
        zoneId: ZoneId,
        locationId: String,
    ): Flow<List<InstantInterval>>

    fun observeGymLocations(): Flow<List<GymLocation>>

    /** Idempotent: merges legacy schedules without [sessionId] into sessions once. */
    suspend fun migrateLegacySchedulesIfNeeded(zoneId: ZoneId)
}
