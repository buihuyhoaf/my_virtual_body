package com.hoabui.virtualbody3d.data.repository

import com.hoabui.virtualbody3d.data.local.ProgressTimelineLocalDataSource
import com.hoabui.virtualbody3d.data.mapper.toDomain
import com.hoabui.virtualbody3d.domain.model.body.ProgressSnapshot
import com.hoabui.virtualbody3d.domain.repository.ProgressTimelineRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgressTimelineRepositoryImpl @Inject constructor(
    private val localDataSource: ProgressTimelineLocalDataSource
) : ProgressTimelineRepository {
    override fun getSnapshots(): List<ProgressSnapshot> =
        localDataSource.getSnapshots().toDomain()
}
