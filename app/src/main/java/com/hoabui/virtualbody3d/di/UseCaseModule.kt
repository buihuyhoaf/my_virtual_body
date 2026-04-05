package com.hoabui.virtualbody3d.di

import com.hoabui.virtualbody3d.domain.repository.AuthRepository
import com.hoabui.virtualbody3d.domain.repository.BodyNutritionSummaryRepository
import com.hoabui.virtualbody3d.domain.repository.BodyScanResultRepository
import com.hoabui.virtualbody3d.domain.repository.MealRepository
import com.hoabui.virtualbody3d.domain.repository.ProgressTimelineRepository
import com.hoabui.virtualbody3d.domain.usecase.AnalyzeMealImageUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetMealDaysUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetMealsByDayUseCase
import com.hoabui.virtualbody3d.domain.repository.ExercisesRepository
import com.hoabui.virtualbody3d.domain.usecase.GetBodyDataUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetExerciseLibraryUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetCaloriesTodayUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetProgressTimelineUseCase
import com.hoabui.virtualbody3d.domain.usecase.LoginUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideLoginUseCase(
        authRepository: AuthRepository
    ): LoginUseCase {
        return LoginUseCase(authRepository)
    }

    @Provides
    fun provideGetBodyDataUseCase(
        bodyRepository: BodyScanResultRepository
    ): GetBodyDataUseCase {
        return GetBodyDataUseCase(bodyRepository)
    }

    @Provides
    fun provideGetCaloriesTodayUseCase(
        bodyNutritionSummaryRepository: BodyNutritionSummaryRepository
    ): GetCaloriesTodayUseCase {
        return GetCaloriesTodayUseCase(bodyNutritionSummaryRepository)
    }

    @Provides
    fun provideGetProgressTimelineUseCase(
        progressTimelineRepository: ProgressTimelineRepository
    ): GetProgressTimelineUseCase {
        return GetProgressTimelineUseCase(progressTimelineRepository)
    }

    @Provides
    fun provideAnalyzeMealImageUseCase(
        mealRepository: MealRepository
    ): AnalyzeMealImageUseCase {
        return AnalyzeMealImageUseCase(mealRepository)
    }

    @Provides
    fun provideGetMealDaysUseCase(
        mealRepository: MealRepository
    ): GetMealDaysUseCase {
        return GetMealDaysUseCase(mealRepository)
    }

    @Provides
    fun provideGetMealsByDayUseCase(
        mealRepository: MealRepository
    ): GetMealsByDayUseCase {
        return GetMealsByDayUseCase(mealRepository)
    }

    @Provides
    fun provideGetExerciseLibraryUseCase(
        exercisesRepository: ExercisesRepository
    ): GetExerciseLibraryUseCase {
        return GetExerciseLibraryUseCase(exercisesRepository)
    }
}
