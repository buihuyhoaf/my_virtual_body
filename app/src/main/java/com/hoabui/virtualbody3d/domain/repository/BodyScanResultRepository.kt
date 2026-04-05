package com.hoabui.virtualbody3d.domain.repository

import com.hoabui.virtualbody3d.domain.model.body.BodyScanResult
import kotlinx.coroutines.flow.Flow

/** Nguồn dữ liệu body duy nhất (tổng quan + báo cáo scan). */
interface BodyScanResultRepository {
    fun observeBodyData(): Flow<BodyScanResult>
}
