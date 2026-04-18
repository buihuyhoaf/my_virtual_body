package com.hoabui.virtualbody3d.data.local.db

import androidx.room.Embedded
import androidx.room.Relation

data class WorkoutLogExerciseWithDetails(
    @Embedded val exercise: WorkoutLogExerciseEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "exerciseLogId",
    )
    val sets: List<WorkoutLogSetEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "exerciseLogId",
    )
    val energy: List<WorkoutLogEnergyEntity>,
)

data class WorkoutLogSessionWithExercises(
    @Embedded val session: WorkoutLogSessionEntity,
    @Relation(
        entity = WorkoutLogExerciseEntity::class,
        parentColumn = "id",
        entityColumn = "sessionId",
    )
    val exercises: List<WorkoutLogExerciseWithDetails>,
)
