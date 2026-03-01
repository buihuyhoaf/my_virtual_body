package com.hoabui.virtualbody3d.ui.initialsetup.viewmodel

import com.hoabui.virtualbody3d.core.base.BaseViewModel
import com.hoabui.virtualbody3d.domain.repository.InitialSetupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class InitialSetupViewModel @Inject constructor(
    private val initialSetupRepository: InitialSetupRepository
) : BaseViewModel<InitialSetupUiState, InitialSetupEvent>(InitialSetupUiState()) {

    init {
        loadSteps()
    }

    private fun loadSteps() {
        launchSafely {
            updateState { copy(isLoading = true) }
            val steps = initialSetupRepository.getSteps()
            updateState {
                copy(
                    steps = steps,
                    isLoading = false
                )
            }
        }
    }

    fun onStep0OptionSelected(index: Int) {
        updateState { copy(selectedStep0Index = index) }
    }

    fun onStep1ToggleOption(index: Int) {
        updateState {
            val next = if (selectedStep1Indices.contains(index)) selectedStep1Indices - index else selectedStep1Indices + index
            copy(selectedStep1Indices = next)
        }
    }

    fun onStep2ToggleOption(index: Int) {
        updateState {
            val next = if (selectedStep2Indices.contains(index)) selectedStep2Indices - index else selectedStep2Indices + index
            copy(selectedStep2Indices = next)
        }
    }

    fun onSkip() {
        val step = state.value.currentStep
        if (step < state.value.totalSteps - 1) {
            updateState { copy(currentStep = step + 1) }
        }
    }

    fun onPrimaryClick() {
        val step = state.value.currentStep
        val total = state.value.totalSteps
        if (step == total - 1) {
            submitAndComplete()
        } else {
            updateState { copy(currentStep = step + 1) }
        }
    }

    private fun submitAndComplete() {
        launchSafely {
            val s = state.value
            val steps = s.steps
            updateState { copy(isLoading = true) }
            if (steps.size >= 3) {
                val reflectionIntentId = steps[0].options.getOrNull(s.selectedStep0Index)?.id ?: ""
                val focusIds = s.selectedStep1Indices.mapNotNull { idx -> steps[1].options.getOrNull(idx)?.id }
                val focusAreaIds = s.selectedStep2Indices.mapNotNull { idx -> steps[2].options.getOrNull(idx)?.id }
                try {
                    initialSetupRepository.submitInitialSetup(reflectionIntentId, focusIds, focusAreaIds)
                } catch (_: Exception) {
                    // TODO: API hiện trả fail, sau khi backend sửa thì chỉ sendEvent(Complete) khi API success; bỏ delay và luôn navigate khi success.
                }
            }
            delay(200)
            updateState { copy(isLoading = false) }
            sendEvent(InitialSetupEvent.Complete)
        }
    }

    override fun defaultError(throwable: Throwable) {
        updateState { copy(isLoading = false) }
    }
}
