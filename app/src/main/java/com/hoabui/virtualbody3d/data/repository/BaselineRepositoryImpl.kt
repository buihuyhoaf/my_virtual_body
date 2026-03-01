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
            weight = "72.5 kg",
            bodyFatPercent = "18.2%",
            muscleMass = "32.1 kg",
            bmi = "22.4",
            rawLines = listOf("Weight: 72.5 kg", "Body fat: 18.2%", "Muscle: 32.1 kg", "BMI: 22.4")
        )
    }

    companion object {
        private const val SIMULATED_UPLOAD_MS = 1500L
        private const val SIMULATED_OCR_MS = 2000L
    }
}
