package com.hoabui.virtualbody3d.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.hoabui.virtualbody3d.data.local.db.BodyScanResultDao
import com.hoabui.virtualbody3d.data.local.db.ExerciseDao
import com.hoabui.virtualbody3d.data.local.db.MIGRATION_1_2
import com.hoabui.virtualbody3d.data.local.db.MIGRATION_3_4
import com.hoabui.virtualbody3d.data.local.db.MIGRATION_4_5
import com.hoabui.virtualbody3d.data.local.db.migration2To3
import com.hoabui.virtualbody3d.data.local.db.NutritionSummaryDao
import com.hoabui.virtualbody3d.data.local.db.ProgressTimelineDao
import com.hoabui.virtualbody3d.data.local.db.VirtualBodyDatabase
import com.hoabui.virtualbody3d.data.local.db.WorkoutScheduleDao
import com.hoabui.virtualbody3d.data.local.db.seed.DatabaseSeeder
import com.hoabui.virtualbody3d.data.local.db.WorkoutSessionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DB_NAME = "virtual_body.db"

    @Provides
    @Singleton
    fun provideVirtualBodyDatabase(
        @ApplicationContext context: Context,
        databaseSeeder: DatabaseSeeder,
    ): VirtualBodyDatabase =
        Room.databaseBuilder(
            context,
            VirtualBodyDatabase::class.java,
            DB_NAME,
        )
            .addMigrations(MIGRATION_1_2, migration2To3(databaseSeeder), MIGRATION_3_4, MIGRATION_4_5)
            .addCallback(databaseSeeder.roomCallback())
            .build()

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideWorkoutScheduleDao(db: VirtualBodyDatabase): WorkoutScheduleDao =
        db.workoutScheduleDao()

    @Provides
    @Singleton
    fun provideWorkoutSessionDao(db: VirtualBodyDatabase): WorkoutSessionDao =
        db.workoutSessionDao()

    @Provides
    @Singleton
    fun provideExerciseDao(db: VirtualBodyDatabase): ExerciseDao =
        db.exerciseDao()

    @Provides
    @Singleton
    fun provideProgressTimelineDao(db: VirtualBodyDatabase): ProgressTimelineDao =
        db.progressTimelineDao()

    @Provides
    @Singleton
    fun provideNutritionSummaryDao(db: VirtualBodyDatabase): NutritionSummaryDao =
        db.nutritionSummaryDao()

    @Provides
    @Singleton
    fun provideBodyScanResultDao(db: VirtualBodyDatabase): BodyScanResultDao =
        db.bodyScanResultDao()
}
