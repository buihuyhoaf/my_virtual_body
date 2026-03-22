package com.hoabui.virtualbody3d.domain.repository

import com.hoabui.virtualbody3d.domain.model.body.BodyScanResult

/** Nguồn dữ liệu body duy nhất (tổng quan + báo cáo scan). */
interface BodyScanResultRepository {
    fun getBodyData(): BodyScanResult
}
