package com.hoabui.virtualbody3d.data.repository

import com.hoabui.virtualbody3d.data.local.BodyScanResultLocalDataSource
import com.hoabui.virtualbody3d.data.mapper.toDomain
import com.hoabui.virtualbody3d.domain.model.body.BodyScanResult
import com.hoabui.virtualbody3d.domain.repository.BodyScanResultRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class BodyScanResultRepositoryImpl @Inject constructor(
    private val localDataSource: BodyScanResultLocalDataSource
) : BodyScanResultRepository {
    override fun observeBodyData(): Flow<BodyScanResult> =
        localDataSource.observeBodyScanResult().map { it.toDomain() }
}
