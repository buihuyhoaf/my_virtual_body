package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.body.BodyScanResult
import com.hoabui.virtualbody3d.domain.repository.BodyScanResultRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/** Use case lấy dữ liệu body (một nguồn: tổng quan + báo cáo scan). */
class GetBodyDataUseCase @Inject constructor(
    private val bodyRepository: BodyScanResultRepository
) {
    operator fun invoke(): Flow<BodyScanResult> =
        bodyRepository.observeBodyData()
}
