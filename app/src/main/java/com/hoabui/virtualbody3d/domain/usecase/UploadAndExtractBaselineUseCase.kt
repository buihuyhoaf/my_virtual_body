package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.ExtractedData
import com.hoabui.virtualbody3d.domain.repository.BaselineRepository
import java.io.File
import javax.inject.Inject

/**
 * Uploads the baseline image and extracts data (OCR). Uses [BaselineRepository] internally.
 */
class UploadAndExtractBaselineUseCase @Inject constructor(
    private val baselineRepository: BaselineRepository
) {
    /**
     * Uploads [file] then extracts baseline data.
     * @return [ExtractedData] on success
     * @throws Exception on upload or extraction failure
     */
    suspend operator fun invoke(file: File): ExtractedData {
        baselineRepository.uploadImage(file)
        return baselineRepository.extractData(file)
    }
}
