package com.hoabui.virtualbody3d.ui.initialsetup.viewmodel

import com.hoabui.virtualbody3d.domain.model.InitialSetupStep

data class InitialSetupUiState(
    val steps: List<InitialSetupStep> = emptyList(),
    val isLoading: Boolean = false,
    val currentStep: Int = 0,
    val selectedStep0Index: Int = 0,
    val selectedStep1Indices: Set<Int> = emptySet(),
    val selectedStep2Indices: Set<Int> = emptySet()
) {
    val totalSteps: Int get() = steps.size
    val currentStepData: InitialSetupStep? get() = steps.getOrNull(currentStep)

    /** True khi step hiện tại đã có ít nhất một lựa chọn (hoặc step không có options). */
    val isNextEnabled: Boolean
        get() = when (currentStep) {
            0 -> true
            1 -> selectedStep1Indices.isNotEmpty()
            2 -> selectedStep2Indices.isNotEmpty()
            else -> true
        }
}
