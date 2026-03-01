package com.hoabui.virtualbody3d.data.repository

import android.content.Context
import com.hoabui.virtualbody3d.data.remote.InitialSetupRequestDto
import com.hoabui.virtualbody3d.data.remote.ApiService
import com.hoabui.virtualbody3d.domain.model.InitialSetupOption
import com.hoabui.virtualbody3d.domain.model.InitialSetupStep
import com.hoabui.virtualbody3d.domain.repository.InitialSetupRepository
import com.hoabui.virtualbody3d.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InitialSetupRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
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

    override suspend fun getSteps(): List<InitialSetupStep> = listOf(
        InitialSetupStep(
            question = context.getString(R.string.initial_setup_step1_title),
            options = listOf(
                InitialSetupOption("observe", context.getString(R.string.initial_setup_step1_option_observe)),
                InitialSetupOption("posture", context.getString(R.string.initial_setup_step1_option_posture)),
                InitialSetupOption("awareness", context.getString(R.string.initial_setup_step1_option_awareness)),
                InitialSetupOption("exploring", context.getString(R.string.initial_setup_step1_option_exploring))
            ),
            isMultiSelect = false
        ),
        InitialSetupStep(
            question = context.getString(R.string.initial_setup_step2_title),
            options = listOf(
                InitialSetupOption("muscle", context.getString(R.string.initial_setup_step2_muscle)),
                InitialSetupOption("fat_loss", context.getString(R.string.initial_setup_step2_fat_loss)),
                InitialSetupOption("posture", context.getString(R.string.initial_setup_step2_posture)),
                InitialSetupOption("health", context.getString(R.string.initial_setup_step2_health)),
                InitialSetupOption("performance", context.getString(R.string.initial_setup_step2_performance)),
                InitialSetupOption("observation", context.getString(R.string.initial_setup_step2_observation))
            ),
            isMultiSelect = true
        ),
        InitialSetupStep(
            question = context.getString(R.string.initial_setup_step3_title),
            options = listOf(
                InitialSetupOption("posture", context.getString(R.string.initial_setup_step3_posture), "accessibility_new"),
                InitialSetupOption("upper_body", context.getString(R.string.initial_setup_step3_upper_body), "fitness_center"),
                InitialSetupOption("core", context.getString(R.string.initial_setup_step3_core), "straighten"),
                InitialSetupOption("lower_body", context.getString(R.string.initial_setup_step3_lower_body), "directions_run"),
                InitialSetupOption("full_body", context.getString(R.string.initial_setup_step3_full_body), "self_improvement")
            ),
            isMultiSelect = true
        ),
        InitialSetupStep(
            question = context.getString(R.string.initial_setup_step4_title),
            subtitle = context.getString(R.string.initial_setup_step4_subtitle),
            options = emptyList(),
            isMultiSelect = false
        )
    )
}
