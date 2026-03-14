package com.hoabui.virtualbody3d.data.local

import android.content.Context
import com.hoabui.virtualbody3d.data.model.InitialSetupOptionDto
import com.hoabui.virtualbody3d.data.model.InitialSetupStepDto
import com.hoabui.virtualbody3d.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InitialSetupLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getSteps(): List<InitialSetupStepDto> = listOf(
        InitialSetupStepDto(
            question = context.getString(R.string.initial_setup_step1_title),
            options = listOf(
                InitialSetupOptionDto("observe", context.getString(R.string.initial_setup_step1_option_observe)),
                InitialSetupOptionDto("posture", context.getString(R.string.initial_setup_step1_option_posture)),
                InitialSetupOptionDto("awareness", context.getString(R.string.initial_setup_step1_option_awareness)),
                InitialSetupOptionDto("exploring", context.getString(R.string.initial_setup_step1_option_exploring))
            ),
            isMultiSelect = false
        ),
        InitialSetupStepDto(
            question = context.getString(R.string.initial_setup_step2_title),
            options = listOf(
                InitialSetupOptionDto("muscle", context.getString(R.string.initial_setup_step2_muscle)),
                InitialSetupOptionDto("fat_loss", context.getString(R.string.initial_setup_step2_fat_loss)),
                InitialSetupOptionDto("posture", context.getString(R.string.initial_setup_step2_posture)),
                InitialSetupOptionDto("health", context.getString(R.string.initial_setup_step2_health)),
                InitialSetupOptionDto("performance", context.getString(R.string.initial_setup_step2_performance)),
                InitialSetupOptionDto("observation", context.getString(R.string.initial_setup_step2_observation))
            ),
            isMultiSelect = true
        ),
        InitialSetupStepDto(
            question = context.getString(R.string.initial_setup_step3_title),
            options = listOf(
                InitialSetupOptionDto("posture", context.getString(R.string.initial_setup_step3_posture), "accessibility_new"),
                InitialSetupOptionDto("upper_body", context.getString(R.string.initial_setup_step3_upper_body), "fitness_center"),
                InitialSetupOptionDto("core", context.getString(R.string.initial_setup_step3_core), "straighten"),
                InitialSetupOptionDto("lower_body", context.getString(R.string.initial_setup_step3_lower_body), "directions_run"),
                InitialSetupOptionDto("full_body", context.getString(R.string.initial_setup_step3_full_body), "self_improvement")
            ),
            isMultiSelect = true
        ),
        InitialSetupStepDto(
            question = context.getString(R.string.initial_setup_step4_title),
            subtitle = context.getString(R.string.initial_setup_step4_subtitle),
            options = emptyList(),
            isMultiSelect = false
        )
    )
}
