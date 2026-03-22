package com.hoabui.virtualbody3d.data.repository

import com.hoabui.virtualbody3d.data.local.InitialSetupLocalDataSource
import com.hoabui.virtualbody3d.data.mapper.toDomain
import com.hoabui.virtualbody3d.data.remote.ApiService
import com.hoabui.virtualbody3d.data.remote.InitialSetupRequestDto
import com.hoabui.virtualbody3d.domain.model.onboarding.InitialSetupStep
import com.hoabui.virtualbody3d.domain.repository.InitialSetupRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InitialSetupRepositoryImpl @Inject constructor(
    private val localDataSource: InitialSetupLocalDataSource,
    private val apiService: ApiService
) : InitialSetupRepository {

    override suspend fun submitInitialSetup(
        reflectionIntentId: String,
        focusIds: List<String>,
        focusAreaIds: List<String>
    ) {
        val body = InitialSetupRequestDto(
            reflectionIntentId = reflectionIntentId,
            focusIds = focusIds,
            focusAreaIds = focusAreaIds
        )
        val response = apiService.submitInitialSetup(body)
        if (!response.isSuccessful) {
            throw Exception("Initial setup submit failed: ${response.code()}")
        }
    }

    override suspend fun getSteps(): List<InitialSetupStep> =
        localDataSource.getSteps().map { it.toDomain() }
}
