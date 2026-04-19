package com.hoabui.virtualbody3d.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        WorkoutScheduleEntity::class,
        WorkoutSessionEntity::class,
        WorkoutLogSessionEntity::class,
        WorkoutLogExerciseEntity::class,
        WorkoutLogSetEntity::class,
        WorkoutLogEnergyEntity::class,
        ExerciseEntity::class,
        ProgressSnapshotEntity::class,
        NutritionSummaryEntity::class,
        BodyScanResultEntity::class,
    ],
    version = 8,
    exportSchema = true,
)
abstract class VirtualBodyDatabase : RoomDatabase() {
    abstract fun workoutScheduleDao(): WorkoutScheduleDao
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun workoutLogDao(): WorkoutLogDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun progressTimelineDao(): ProgressTimelineDao
    abstract fun nutritionSummaryDao(): NutritionSummaryDao
    abstract fun bodyScanResultDao(): BodyScanResultDao
}
