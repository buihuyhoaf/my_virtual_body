package com.hoabui.virtualbody3d.data.repository

import com.hoabui.virtualbody3d.data.local.BodyLocalDataSource
import com.hoabui.virtualbody3d.data.mapper.toDomain
import com.hoabui.virtualbody3d.domain.model.BodyMetrics
import com.hoabui.virtualbody3d.domain.repository.BodyRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BodyRepositoryImpl @Inject constructor(
    private val localDataSource: BodyLocalDataSource
) : BodyRepository {
    override fun getBodyMetrics(): BodyMetrics {
        return localDataSource.getBodyMetrics().toDomain()
    }
}
