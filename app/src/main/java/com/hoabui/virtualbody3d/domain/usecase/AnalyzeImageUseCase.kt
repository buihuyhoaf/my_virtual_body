package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.AnalysisType
import com.hoabui.virtualbody3d.domain.model.ExtractedData
import com.hoabui.virtualbody3d.domain.repository.BaselineRepository
import javax.inject.Inject

/**
 * Sends an uploaded image to the backend for AI analysis (OCR or meal recognition).
 */
class AnalyzeImageUseCase @Inject constructor(
    private val baselineRepository: BaselineRepository
) {
    /**
     * Analyzes the image at [imageUrl] with the given [type].
     * @return [ExtractedData] with parsed metrics or meal data
     * @throws Exception on analysis failure
     */
    suspend operator fun invoke(
        imageUrl: String,
        type: AnalysisType
    ): ExtractedData = baselineRepository.analyzeImage(imageUrl, type)
}
