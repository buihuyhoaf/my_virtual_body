package com.hoabui.virtualbody3d.domain.repository

import com.hoabui.virtualbody3d.domain.model.AnalysisType
import com.hoabui.virtualbody3d.domain.model.ExtractedData
import com.hoabui.virtualbody3d.domain.model.UploadedImage
import java.io.File

/**
 * Repository for image upload, AI analysis, and baseline persistence.
 */
interface BaselineRepository {

    /**
     * Uploads the prepared image file to the backend.
     * @return [UploadedImage] with imageId and imageUrl for analysis
     * @throws Exception on upload failure
     */
    suspend fun uploadImage(file: File): UploadedImage

    /**
     * Sends the uploaded image to the backend for AI analysis (OCR or meal recognition).
     * @param imageUrl URL of the uploaded image
     * @param type Kind of analysis (OCR or MEAL)
     * @return [ExtractedData] with parsed metrics or meal data
     * @throws Exception on analysis failure
     */
    suspend fun analyzeImage(imageUrl: String, type: AnalysisType): ExtractedData

    /**
     * Saves the confirmed baseline data (e.g. to local DB or API).
     * @throws Exception on save failure
     */
    suspend fun saveBaseline(data: ExtractedData)
}
