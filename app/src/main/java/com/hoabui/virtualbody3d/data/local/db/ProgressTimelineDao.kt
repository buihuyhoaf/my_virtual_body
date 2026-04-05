package com.hoabui.virtualbody3d.data.local.db

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressTimelineDao {
    @Query("SELECT * FROM progress_snapshots ORDER BY date_iso ASC")
    fun observeAllByDate(): Flow<List<ProgressSnapshotEntity>>
}
