package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.body.ProgressSnapshot
import com.hoabui.virtualbody3d.domain.repository.ProgressTimelineRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetProgressTimelineUseCase @Inject constructor(
    private val progressTimelineRepository: ProgressTimelineRepository
) {
    operator fun invoke(): Flow<List<ProgressSnapshot>> = flow {
        emit(progressTimelineRepository.getSnapshots())
    }
}
