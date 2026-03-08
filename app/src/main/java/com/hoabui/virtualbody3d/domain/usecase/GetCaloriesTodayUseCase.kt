package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.BodyDashboard
import com.hoabui.virtualbody3d.domain.repository.BodyDashboardRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetCaloriesTodayUseCase @Inject constructor(
    private val bodyDashboardRepository: BodyDashboardRepository
) {
    operator fun invoke(): Flow<BodyDashboard> = flow {
        emit(bodyDashboardRepository.getBodyDashboard())
    }
}
