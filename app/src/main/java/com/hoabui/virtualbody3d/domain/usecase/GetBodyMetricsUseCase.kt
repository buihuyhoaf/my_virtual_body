package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.BodyMetrics
import com.hoabui.virtualbody3d.domain.repository.BodyRepository
import javax.inject.Inject

class GetBodyMetricsUseCase @Inject constructor(
    private val bodyRepository: BodyRepository
) {
    operator fun invoke(): BodyMetrics = bodyRepository.getBodyMetrics()
}
