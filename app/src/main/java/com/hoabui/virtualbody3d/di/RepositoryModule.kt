package com.hoabui.virtualbody3d.di

import com.hoabui.virtualbody3d.data.repository.AuthRepositoryImpl
import com.hoabui.virtualbody3d.data.repository.BaselineRepositoryImpl
import com.hoabui.virtualbody3d.data.repository.BodyNutritionSummaryRepositoryImpl
import com.hoabui.virtualbody3d.data.repository.BodyScanResultRepositoryImpl
import com.hoabui.virtualbody3d.data.repository.InitialSetupRepositoryImpl
import com.hoabui.virtualbody3d.data.repository.FavoriteExerciseRepositoryImpl
import com.hoabui.virtualbody3d.data.repository.MealRepositoryImpl
import com.hoabui.virtualbody3d.data.repository.MessageRepositoryImpl
import com.hoabui.virtualbody3d.data.repository.PromoBannerRepositoryImpl
import com.hoabui.virtualbody3d.data.repository.ExerciseRepositoryImpl
import com.hoabui.virtualbody3d.data.repository.SupplementRepositoryImpl
import com.hoabui.virtualbody3d.data.repository.UserInfoRepositoryImpl
import com.hoabui.virtualbody3d.domain.repository.AuthRepository
import com.hoabui.virtualbody3d.domain.repository.BaselineRepository
import com.hoabui.virtualbody3d.domain.repository.BodyNutritionSummaryRepository
import com.hoabui.virtualbody3d.domain.repository.BodyScanResultRepository
import com.hoabui.virtualbody3d.domain.repository.InitialSetupRepository
import com.hoabui.virtualbody3d.domain.repository.FavoriteExerciseRepository
import com.hoabui.virtualbody3d.domain.repository.MealRepository
import com.hoabui.virtualbody3d.domain.repository.MessageRepository
import com.hoabui.virtualbody3d.domain.repository.PromoBannerRepository
import com.hoabui.virtualbody3d.domain.repository.ExerciseRepository
import com.hoabui.virtualbody3d.domain.repository.SupplementRepository
import com.hoabui.virtualbody3d.domain.repository.UserInfoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindBodyScanResultRepository(
        bodyScanResultRepositoryImpl: BodyScanResultRepositoryImpl
    ): BodyScanResultRepository

    @Binds
    @Singleton
    abstract fun bindBodyNutritionSummaryRepository(
        bodyNutritionSummaryRepository: BodyNutritionSummaryRepositoryImpl
    ): BodyNutritionSummaryRepository

    @Binds
    @Singleton
    abstract fun bindInitialSetupRepository(
        initialSetupRepositoryImpl: InitialSetupRepositoryImpl
    ): InitialSetupRepository

    @Binds
    @Singleton
    abstract fun bindBaselineRepository(
        baselineRepositoryImpl: BaselineRepositoryImpl
    ): BaselineRepository

    @Binds
    @Singleton
    abstract fun bindPromoBannerRepository(
        promoBannerRepositoryImpl: PromoBannerRepositoryImpl
    ): PromoBannerRepository

    @Binds
    @Singleton
    abstract fun bindMessageRepository(
        messageRepositoryImpl: MessageRepositoryImpl
    ): MessageRepository

    @Binds
    @Singleton
    abstract fun bindUserInfoRepository(
        userInfoRepositoryImpl: UserInfoRepositoryImpl
    ): UserInfoRepository

    @Binds
    @Singleton
    abstract fun bindMealRepository(
        mealRepositoryImpl: MealRepositoryImpl
    ): MealRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteExerciseRepository(
        favoriteExerciseRepositoryImpl: FavoriteExerciseRepositoryImpl
    ): FavoriteExerciseRepository

    @Binds
    @Singleton
    abstract fun bindSupplementRepository(
        supplementRepositoryImpl: SupplementRepositoryImpl
    ): SupplementRepository

    @Binds
    @Singleton
    abstract fun bindExerciseRepository(
        exerciseRepositoryImpl: ExerciseRepositoryImpl
    ): ExerciseRepository
}
