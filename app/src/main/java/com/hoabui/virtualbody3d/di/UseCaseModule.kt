package com.hoabui.virtualbody3d.di

import com.hoabui.virtualbody3d.domain.repository.BodyRepository
import com.hoabui.virtualbody3d.domain.repository.BodyDashboardRepository
import com.hoabui.virtualbody3d.domain.usecase.GetBodyDashboardDataUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetBodyMetricsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideGetBodyMetricsUseCase(
        bodyRepository: BodyRepository
    ): GetBodyMetricsUseCase {
        return GetBodyMetricsUseCase(bodyRepository)
    }

    @Provides
    fun provideGetBodyDashboardDataUseCase(
        bodyDashboardRepository: BodyDashboardRepository
    ): GetBodyDashboardDataUseCase {
        return GetBodyDashboardDataUseCase(bodyDashboardRepository)
    }
}
