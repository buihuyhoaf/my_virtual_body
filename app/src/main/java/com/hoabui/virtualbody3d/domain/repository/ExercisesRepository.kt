package com.hoabui.virtualbody3d.domain.repository

import com.hoabui.virtualbody3d.domain.model.exercise.FeedExercise
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface ExercisesRepository {
    fun getExercises(): Flow<List<FeedExercise>>

    /** Scheduled / planned exercises for the given calendar day. */
    fun getExercisesByDay(day: LocalDate): Flow<List<FeedExercise>>
}
