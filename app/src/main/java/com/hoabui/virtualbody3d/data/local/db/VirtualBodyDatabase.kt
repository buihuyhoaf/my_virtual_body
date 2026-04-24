package com.hoabui.virtualbody3d.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        WorkoutScheduleEntity::class,
        WorkoutSessionEntity::class,
        ExerciseEntity::class,
        ProgressSnapshotEntity::class,
        NutritionSummaryEntity::class,
        BodyScanResultEntity::class,
    ],
    version = 3,
    exportSchema = true,
)
@TypeConverters(RoomStringListTypeConverter::class)
abstract class VirtualBodyDatabase : RoomDatabase() {
    abstract fun workoutScheduleDao(): WorkoutScheduleDao
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun progressTimelineDao(): ProgressTimelineDao
    abstract fun nutritionSummaryDao(): NutritionSummaryDao
    abstract fun bodyScanResultDao(): BodyScanResultDao
}
