package com.hoabui.virtualbody3d.data.repository

import com.hoabui.virtualbody3d.domain.model.AnalysisType
import com.hoabui.virtualbody3d.domain.model.ExtractedData
import com.hoabui.virtualbody3d.domain.model.UploadedImage
import com.hoabui.virtualbody3d.domain.repository.BaselineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BaselineRepositoryImpl @Inject constructor() : BaselineRepository {

    override suspend fun uploadImage(file: File): UploadedImage = withContext(Dispatchers.IO) {
        delay(SIMULATED_UPLOAD_MS)
        if (!file.exists()) throw IllegalStateException("File no longer exists")
        val imageId = UUID.randomUUID().toString()
        val imageUrl = "https://api.example.com/images/$imageId"
        UploadedImage(imageId = imageId, imageUrl = imageUrl)
    }

    override suspend fun analyzeImage(imageUrl: String, type: AnalysisType): ExtractedData =
        withContext(Dispatchers.IO) {
            delay(SIMULATED_ANALYSIS_MS)
            when (type) {
                AnalysisType.OCR -> ExtractedData(
                    weight = "72.5",
                    bodyFatPercent = "18.2",
                    muscleMass = "32.1",
                    bmi = "22.4",
                    bodyFatMass = "13.2",
                    fatFreeMass = "59.3",
                    bmr = "1680",
                    visceralFatLevel = "5",
                    rawLines = listOf("Weight: 72.5 kg", "Body fat: 18.2%", "Muscle: 32.1 kg", "BMI: 22.4")
                )
                AnalysisType.MEAL -> ExtractedData(
                    weight = "",
                    bodyFatPercent = "",
                    muscleMass = "",
                    bmi = "",
                    bodyFatMass = "",
                    fatFreeMass = "",
                    bmr = "",
                    visceralFatLevel = "",
                    rawLines = listOf("Meal recognized: placeholder")
                )
            }
        }

    override suspend fun saveBaseline(data: ExtractedData) = withContext(Dispatchers.IO) {
        delay(500) // Simulate persist
        // TODO: persist to local DB or API
    }

    companion object {
        private const val SIMULATED_UPLOAD_MS = 1500L
        private const val SIMULATED_ANALYSIS_MS = 2000L
    }
}
