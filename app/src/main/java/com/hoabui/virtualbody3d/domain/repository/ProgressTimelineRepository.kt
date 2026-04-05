package com.hoabui.virtualbody3d.domain.repository

import com.hoabui.virtualbody3d.domain.model.body.ProgressSnapshot
import kotlinx.coroutines.flow.Flow

/** Timeline tiến độ (ảnh + chỉ số theo thời gian). */
interface ProgressTimelineRepository {
    fun observeSnapshots(): Flow<List<ProgressSnapshot>>
}
