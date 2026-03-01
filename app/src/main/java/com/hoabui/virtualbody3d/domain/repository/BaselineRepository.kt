package com.hoabui.virtualbody3d.domain.repository

import com.hoabui.virtualbody3d.domain.model.ExtractedData
import java.io.File

/**
 * Repository for baseline image upload and OCR extraction.
 */
interface BaselineRepository {

    /**
     * Uploads the optimized image file. Simulates network delay.
     * @throws Exception on upload failure
     */
    suspend fun uploadImage(file: File)

    /**
     * Extracts baseline data from the image (OCR simulation).
     * @return ExtractedData with parsed metrics
     * @throws Exception on extraction failure
     */
    suspend fun extractData(file: File): ExtractedData
}
