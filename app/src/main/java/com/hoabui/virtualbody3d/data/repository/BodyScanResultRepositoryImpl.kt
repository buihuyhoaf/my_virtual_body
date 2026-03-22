package com.hoabui.virtualbody3d.data.repository

import com.hoabui.virtualbody3d.data.local.BodyScanResultLocalDataSource
import com.hoabui.virtualbody3d.data.mapper.toDomain
import com.hoabui.virtualbody3d.domain.model.body.BodyScanResult
import com.hoabui.virtualbody3d.domain.repository.BodyScanResultRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BodyScanResultRepositoryImpl @Inject constructor(
    private val localDataSource: BodyScanResultLocalDataSource
) : BodyScanResultRepository {
    override fun getBodyData(): BodyScanResult {
        return localDataSource.getBodyScanResult().toDomain()
    }
}
