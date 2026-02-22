package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.BodyDashboard
import com.hoabui.virtualbody3d.domain.repository.BodyDashboardRepository
import javax.inject.Inject

class GetBodyDashboardDataUseCase @Inject constructor(
    private val bodyDashboardRepository: BodyDashboardRepository
) {
    operator fun invoke(): BodyDashboard = bodyDashboardRepository.getBodyDashboard()
}
