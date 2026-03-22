package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.baseline.ExtractedData
import com.hoabui.virtualbody3d.domain.repository.BaselineRepository
import javax.inject.Inject

/**
 * Saves the confirmed baseline data (reviewed/edited metrics) for progress tracking.
 */
class SaveBaselineUseCase @Inject constructor(
    private val baselineRepository: BaselineRepository
) {
    suspend operator fun invoke(data: ExtractedData) {
        baselineRepository.saveBaseline(data)
    }
}
