package com.hoabui.virtualbody3d.data.local

import com.hoabui.virtualbody3d.data.local.db.WorkoutLogDao
import com.hoabui.virtualbody3d.data.local.db.WorkoutLogEnergyEntity
import com.hoabui.virtualbody3d.data.local.db.WorkoutLogExerciseEntity
import com.hoabui.virtualbody3d.data.local.db.WorkoutLogSetEntity
import com.hoabui.virtualbody3d.data.local.db.WorkoutLogSessionEntity
import com.hoabui.virtualbody3d.data.local.db.WorkoutLogSessionWithExercises
import com.hoabui.virtualbody3d.di.IoDispatcher
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

@Singleton
class WorkoutLogLocalDataSource @Inject constructor(
    private val workoutLogDao: WorkoutLogDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    fun observeSessionsByDay(dayKey: String): Flow<List<WorkoutLogSessionWithExercises>> =
        workoutLogDao.observeSessionsByDay(dayKey).flowOn(ioDispatcher)

    suspend fun insertFullSession(
        session: WorkoutLogSessionEntity,
        exercises: List<WorkoutLogExerciseEntity>,
        sets: List<WorkoutLogSetEntity>,
        energy: List<WorkoutLogEnergyEntity>,
    ) = withContext(ioDispatcher) {
        workoutLogDao.insertFullSession(
            session = session,
            exercises = exercises,
            sets = sets,
            energy = energy,
        )
    }
}
