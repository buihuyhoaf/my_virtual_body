package com.hoabui.virtualbody3d.data.local

import com.hoabui.virtualbody3d.data.local.db.ProgressTimelineDao
import com.hoabui.virtualbody3d.data.local.db.toProgressSnapshotDto
import com.hoabui.virtualbody3d.data.model.ProgressSnapshotDto
import com.hoabui.virtualbody3d.di.IoDispatcher
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

@Singleton
class ProgressTimelineLocalDataSource @Inject constructor(
    private val progressTimelineDao: ProgressTimelineDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    fun observeSnapshots(): Flow<List<ProgressSnapshotDto>> =
        progressTimelineDao.observeAllByDate()
            .map { list -> list.map { it.toProgressSnapshotDto() } }
            .flowOn(ioDispatcher)
}
