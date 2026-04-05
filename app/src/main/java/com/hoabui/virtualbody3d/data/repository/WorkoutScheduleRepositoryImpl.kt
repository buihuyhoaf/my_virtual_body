package com.hoabui.virtualbody3d.data.repository

import android.util.Log
import com.hoabui.virtualbody3d.BuildConfig
import com.hoabui.virtualbody3d.data.local.WorkoutScheduleLocalDataSource
import com.hoabui.virtualbody3d.data.local.db.WORKOUT_DB_TRACE_LOG_TAG
import com.hoabui.virtualbody3d.data.mapper.toDomain
import com.hoabui.virtualbody3d.data.mapper.toEntity
import com.hoabui.virtualbody3d.data.mapper.toStorageString
import com.hoabui.virtualbody3d.data.local.db.formatEpochDayRangeForLog
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutExecutionStatus
import com.hoabui.virtualbody3d.domain.model.exercise.WorkoutSchedule
import com.hoabui.virtualbody3d.domain.repository.WorkoutScheduleRepository
import com.hoabui.virtualbody3d.di.IoDispatcher
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@Singleton
class WorkoutScheduleRepositoryImpl @Inject constructor(
    private val localDataSource: WorkoutScheduleLocalDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) : WorkoutScheduleRepository {

    /** Wall-clock zone used when mapping stored instants to [WorkoutSchedule.scheduledAt]. */
    private val mappingZoneId: ZoneId = ZoneId.systemDefault()

    override suspend fun saveWorkoutSchedule(schedule: WorkoutSchedule, planZoneId: ZoneId) {
        withContext(ioDispatcher) {
            try {
                if (BuildConfig.DEBUG) {
                    Log.d(
                        WORKOUT_DB_TRACE_LOG_TAG,
                        "upsert intent clientId=${schedule.id} rowId=${schedule.rowId}",
                    )
                }
                val now = System.currentTimeMillis()
                localDataSource.upsert(schedule.toEntity(planZoneId, now))
                if (BuildConfig.DEBUG) {
                    Log.d(WORKOUT_DB_TRACE_LOG_TAG, "upsert success clientId=${schedule.id}")
                }
            } catch (t: Throwable) {
                Log.e(WORKOUT_DB_TRACE_LOG_TAG, "upsert failed clientId=${schedule.id}", t)
                throw t
            }
        }
    }

    override fun observeWorkoutSchedules(): Flow<List<WorkoutSchedule>> {
        if (BuildConfig.DEBUG) {
            Log.d(WORKOUT_DB_TRACE_LOG_TAG, "observeAllSchedules subscribe")
        }
        return localDataSource.observeAllSchedules()
            .map { entities ->
                if (BuildConfig.DEBUG) {
                    Log.d(WORKOUT_DB_TRACE_LOG_TAG, "emit count=${entities.size} source=observeAllSchedules")
                }
                entities.map { it.toDomain(mappingZoneId) }
            }
            .flowOn(ioDispatcher)
    }

    override fun observeSchedulesInDayRange(startDay: Long, endDay: Long): Flow<List<WorkoutSchedule>> {
        if (BuildConfig.DEBUG) {
            Log.d(
                WORKOUT_DB_TRACE_LOG_TAG,
                "dayKey range subscribe ${formatEpochDayRangeForLog(startDay, endDay)}",
            )
        }
        return localDataSource.observeSchedulesInRange(startDay, endDay)
            .map { entities ->
                if (BuildConfig.DEBUG) {
                    Log.d(
                        WORKOUT_DB_TRACE_LOG_TAG,
                        "emit count=${entities.size} dayKey range=${formatEpochDayRangeForLog(startDay, endDay)}",
                    )
                }
                entities.map { it.toDomain(mappingZoneId) }
            }
            .flowOn(ioDispatcher)
    }

    override suspend fun getAllSchedules(): List<WorkoutSchedule> = withContext(ioDispatcher) {
        try {
            localDataSource.getAllSchedules().map { it.toDomain(mappingZoneId) }
        } catch (t: Throwable) {
            Log.e(WORKOUT_DB_TRACE_LOG_TAG, "getAllSchedules failed", t)
            throw t
        }
    }

    override suspend fun updateExecutionStatus(rowId: Long, status: WorkoutExecutionStatus) {
        withContext(ioDispatcher) {
            try {
                val now = System.currentTimeMillis()
                val rows = localDataSource.updateStatus(rowId, status.toStorageString(), now)
                when {
                    rows == 0 -> Log.w(
                        WORKOUT_DB_TRACE_LOG_TAG,
                        "updateStatus no row id=$rowId status=${status.toStorageString()}",
                    )
                    BuildConfig.DEBUG -> Log.d(
                        WORKOUT_DB_TRACE_LOG_TAG,
                        "updateStatus ok id=$rowId rows=$rows status=${status.toStorageString()}",
                    )
                }
            } catch (t: Throwable) {
                Log.e(
                    WORKOUT_DB_TRACE_LOG_TAG,
                    "updateStatus failed id=$rowId status=${status.toStorageString()}",
                    t,
                )
                throw t
            }
        }
    }
}
