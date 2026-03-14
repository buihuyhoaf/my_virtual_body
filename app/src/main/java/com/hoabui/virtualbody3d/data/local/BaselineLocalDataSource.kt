package com.hoabui.virtualbody3d.data.local

import com.hoabui.virtualbody3d.data.model.ExtractedDataDto
import com.hoabui.virtualbody3d.data.model.UploadedImageDto
import com.hoabui.virtualbody3d.domain.model.AnalysisType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BaselineLocalDataSource @Inject constructor() {

    suspend fun uploadImage(file: File): UploadedImageDto = withContext(Dispatchers.IO) {
        delay(SIMULATED_UPLOAD_MS)
        if (!file.exists()) throw IllegalStateException("File no longer exists")
        val imageId = UUID.randomUUID().toString()
        val imageUrl = "https://api.example.com/images/$imageId"
        UploadedImageDto(imageId = imageId, imageUrl = imageUrl)
    }

    suspend fun analyzeImage(imageUrl: String, type: AnalysisType): ExtractedDataDto =
        withContext(Dispatchers.IO) {
            delay(SIMULATED_ANALYSIS_MS)
            check(type == AnalysisType.OCR) {
                "BaselineLocalDataSource only supports OCR analysis; meal analysis is handled by MealRepository."
            }
            ExtractedDataDto(
                weight = "72.5",
                bodyFatPercent = "18.2",
                muscleMass = "32.1",
                bmi = "22.4",
                bodyFatMass = "13.2",
                fatFreeMass = "59.3",
                bmr = "1680",
                visceralFatLevel = "5",
                rawLines = listOf(
                    "Weight: 72.5 kg",
                    "Body fat: 18.2%",
                    "Muscle: 32.1 kg",
                    "BMI: 22.4"
                )
            )
        }

    companion object {
        private const val SIMULATED_UPLOAD_MS = 1500L
        private const val SIMULATED_ANALYSIS_MS = 2000L
    }
}
