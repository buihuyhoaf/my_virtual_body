package com.hoabui.virtualbody3d.di

import com.hoabui.virtualbody3d.domain.repository.AuthRepository
import com.hoabui.virtualbody3d.domain.repository.BodyNutritionSummaryRepository
import com.hoabui.virtualbody3d.domain.repository.BodyScanResultRepository
import com.hoabui.virtualbody3d.domain.repository.MealRepository
import com.hoabui.virtualbody3d.domain.repository.PromoBannerRepository
import com.hoabui.virtualbody3d.domain.repository.MessageRepository
import com.hoabui.virtualbody3d.domain.repository.UserInfoRepository
import com.hoabui.virtualbody3d.domain.usecase.AnalyzeMealImageUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetMealDaysUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetMealsByDayUseCase
import com.hoabui.virtualbody3d.domain.repository.FavoriteExerciseRepository
import com.hoabui.virtualbody3d.domain.repository.SupplementRepository
import com.hoabui.virtualbody3d.domain.usecase.GetBodyDataUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetFavoriteExercisesUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetSupplementsUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetCaloriesTodayUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetPromoBannersUseCase
import com.hoabui.virtualbody3d.domain.usecase.LoginUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetMessageThreadsUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetMessageDetailUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetUserInfoUseCase
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
    fun provideGetPromoBannersUseCase(
        promoBannerRepository: PromoBannerRepository
    ): GetPromoBannersUseCase {
        return GetPromoBannersUseCase(promoBannerRepository)
    }

    @Provides
    fun provideGetMessageThreadsUseCase(
        messageRepository: MessageRepository
    ): GetMessageThreadsUseCase {
        return GetMessageThreadsUseCase(messageRepository)
    }

    @Provides
    fun provideGetMessageDetailUseCase(
        messageRepository: MessageRepository
    ): GetMessageDetailUseCase {
        return GetMessageDetailUseCase(messageRepository)
    }

    @Provides
    fun provideGetChatUserUseCase(
        userInfoRepository: UserInfoRepository
    ): GetUserInfoUseCase {
        return GetUserInfoUseCase(userInfoRepository)
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
    fun provideGetFavoriteExercisesUseCase(
        favoriteExerciseRepository: FavoriteExerciseRepository
    ): GetFavoriteExercisesUseCase {
        return GetFavoriteExercisesUseCase(favoriteExerciseRepository)
    }

    @Provides
    fun provideGetSupplementsUseCase(
        supplementRepository: SupplementRepository
    ): GetSupplementsUseCase {
        return GetSupplementsUseCase(supplementRepository)
    }
}
