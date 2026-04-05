package com.hoabui.virtualbody3d.data.local.db

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BodyScanResultDao {
    @Query("SELECT * FROM body_scan_results WHERE id = 1")
    fun observeActive(): Flow<BodyScanResultEntity?>
}
