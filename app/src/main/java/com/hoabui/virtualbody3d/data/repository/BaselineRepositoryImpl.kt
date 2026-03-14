package com.hoabui.virtualbody3d.data.repository

import com.hoabui.virtualbody3d.data.local.BaselineLocalDataSource
import com.hoabui.virtualbody3d.data.mapper.toDomain
import com.hoabui.virtualbody3d.domain.model.AnalysisType
import com.hoabui.virtualbody3d.domain.model.ExtractedData
import com.hoabui.virtualbody3d.domain.model.UploadedImage
import com.hoabui.virtualbody3d.domain.repository.BaselineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BaselineRepositoryImpl @Inject constructor(
    private val localDataSource: BaselineLocalDataSource
) : BaselineRepository {

    override suspend fun uploadImage(file: File): UploadedImage =
        localDataSource.uploadImage(file).toDomain()

    override suspend fun analyzeImage(imageUrl: String, type: AnalysisType): ExtractedData =
        localDataSource.analyzeImage(imageUrl, type).toDomain()

    override suspend fun saveBaseline(data: ExtractedData) = withContext(Dispatchers.IO) {
        delay(500) // Simulate persist
        // TODO: persist to local DB or API
    }
}
