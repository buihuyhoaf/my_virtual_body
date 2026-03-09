package com.hoabui.virtualbody3d.di

import com.hoabui.virtualbody3d.domain.repository.AuthRepository
import com.hoabui.virtualbody3d.domain.repository.BodyDashboardRepository
import com.hoabui.virtualbody3d.domain.repository.BodyScanResultRepository
import com.hoabui.virtualbody3d.domain.repository.PromoBannerRepository
import com.hoabui.virtualbody3d.domain.repository.MessageRepository
import com.hoabui.virtualbody3d.domain.usecase.GetBodyDataUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetCaloriesTodayUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetPromoBannersUseCase
import com.hoabui.virtualbody3d.domain.usecase.LoginUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetMessageThreadsUseCase
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
        bodyDashboardRepository: BodyDashboardRepository
    ): GetCaloriesTodayUseCase {
        return GetCaloriesTodayUseCase(bodyDashboardRepository)
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
}
