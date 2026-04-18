package com.hoabui.virtualbody3d.domain.repository

import com.hoabui.virtualbody3d.domain.model.exercise.GymLocation
import com.hoabui.virtualbody3d.domain.model.exercise.SessionExerciseLine
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSession
import kotlinx.coroutines.flow.Flow

sealed class BookWorkoutSessionResult {
    /**
     * @param resolvedSession Session row actually written or merged (id matches DB after find-or-create).
     */
    data class Success(
        val scheduledCount: Int,
        val resolvedSession: WorkoutSession,
    ) : BookWorkoutSessionResult()
    data object Conflict : BookWorkoutSessionResult()
    data object InvalidDraft : BookWorkoutSessionResult()
}

interface WorkoutSessionRepository {

    suspend fun bookSession(
        session: WorkoutSession,
        lines: List<SessionExerciseLine>,
    ): BookWorkoutSessionResult

    /** All stored workout sessions (timeline aggregate rows). */
    fun observeWorkoutSessions(): Flow<List<WorkoutSession>>

    fun observeGymLocations(): Flow<List<GymLocation>>

    /** Idempotent: merges legacy schedules without [sessionId] into sessions once. */
    suspend fun migrateLegacySchedulesIfNeeded()
}
