package com.hoabui.virtualbody3d.domain.repository

import com.hoabui.virtualbody3d.domain.model.body.ProgressSnapshot

/** Timeline tiến độ (ảnh + chỉ số theo thời gian). */
interface ProgressTimelineRepository {
    fun getSnapshots(): List<ProgressSnapshot>
}
