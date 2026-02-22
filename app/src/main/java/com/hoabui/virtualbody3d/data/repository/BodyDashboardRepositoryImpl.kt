package com.hoabui.virtualbody3d.data.repository

import com.hoabui.virtualbody3d.data.local.BodyDashboardLocalDataSource
import com.hoabui.virtualbody3d.data.mapper.toDomain
import com.hoabui.virtualbody3d.domain.model.BodyDashboard
import com.hoabui.virtualbody3d.domain.repository.BodyDashboardRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BodyDashboardRepositoryImpl @Inject constructor(
    private val localDataSource: BodyDashboardLocalDataSource
) : BodyDashboardRepository {
    override fun getBodyDashboard(): BodyDashboard {
        return localDataSource.getBodyDashboard().toDomain()
    }
}
