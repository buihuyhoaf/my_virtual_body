package com.hoabui.virtualbody3d.di

import com.hoabui.virtualbody3d.data.repository.BodyRepositoryImpl
import com.hoabui.virtualbody3d.data.repository.BodyDashboardRepositoryImpl
import com.hoabui.virtualbody3d.domain.repository.BodyDashboardRepository
import com.hoabui.virtualbody3d.domain.repository.BodyRepository
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
    abstract fun bindBodyRepository(
        bodyRepositoryImpl: BodyRepositoryImpl
    ): BodyRepository

    @Binds
    @Singleton
    abstract fun bindBodyDashboardRepository(
        bodyDashboardRepositoryImpl: BodyDashboardRepositoryImpl
    ): BodyDashboardRepository
}
