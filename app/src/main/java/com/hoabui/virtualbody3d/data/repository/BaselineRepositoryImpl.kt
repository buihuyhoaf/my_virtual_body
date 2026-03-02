package com.hoabui.virtualbody3d.data.repository

import com.hoabui.virtualbody3d.domain.model.ExtractedData
import com.hoabui.virtualbody3d.domain.repository.BaselineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BaselineRepositoryImpl @Inject constructor() : BaselineRepository {

    override suspend fun uploadImage(file: File) = withContext(Dispatchers.IO) {
        delay(SIMULATED_UPLOAD_MS)
        if (!file.exists()) throw IllegalStateException("File no longer exists")
    }

    override suspend fun extractData(file: File): ExtractedData = withContext(Dispatchers.IO) {
        delay(SIMULATED_OCR_MS)
        if (!file.exists()) throw IllegalStateException("File no longer exists")
        ExtractedData(
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
    }

    override suspend fun saveBaseline(data: ExtractedData) = withContext(Dispatchers.IO) {
        delay(500) // Simulate persist
        // TODO: persist to local DB or API
    }

    companion object {
        private const val SIMULATED_UPLOAD_MS = 1500L
        private const val SIMULATED_OCR_MS = 2000L
    }
}
