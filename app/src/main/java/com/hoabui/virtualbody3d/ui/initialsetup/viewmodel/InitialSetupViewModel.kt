package com.hoabui.virtualbody3d.ui.initialsetup.viewmodel

import com.hoabui.virtualbody3d.core.base.UiState
import com.hoabui.virtualbody3d.core.base.UiStateViewModel
import com.hoabui.virtualbody3d.domain.repository.InitialSetupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class InitialSetupViewModel @Inject constructor(
    private val initialSetupRepository: InitialSetupRepository
) : UiStateViewModel<InitialSetupUiState, InitialSetupEvent>() {

    init {
        loadSteps()
    }

    private fun loadSteps() {
        launchSafely {
            setLoading()
            val steps = initialSetupRepository.getSteps()
            setSuccess(InitialSetupUiState(steps = steps))
        }
    }

    fun onStep0OptionSelected(index: Int) {
        updateSuccess { copy(selectedStep0Index = index) }
    }

    fun onStep1ToggleOption(index: Int) {
        updateSuccess {
            val next = if (selectedStep1Indices.contains(index)) selectedStep1Indices - index else selectedStep1Indices + index
            copy(selectedStep1Indices = next)
        }
    }

    fun onStep2ToggleOption(index: Int) {
        updateSuccess {
            val next = if (selectedStep2Indices.contains(index)) selectedStep2Indices - index else selectedStep2Indices + index
            copy(selectedStep2Indices = next)
        }
    }

    fun onSkip() {
        val data = (state.value as? UiState.Success<InitialSetupUiState>)?.data ?: return
        val step = data.currentStep
        if (step < data.totalSteps - 1) {
            updateSuccess { copy(currentStep = step + 1) }
        }
    }

    fun onPrimaryClick() {
        val data = (state.value as? UiState.Success<InitialSetupUiState>)?.data ?: return
        val step = data.currentStep
        val total = data.totalSteps
        if (step == total - 1) {
            submitAndComplete()
        } else {
            updateSuccess { copy(currentStep = step + 1) }
        }
    }

    private fun submitAndComplete() {
        launchSafely {
            val s = (state.value as? UiState.Success<InitialSetupUiState>)?.data ?: return@launchSafely
            val steps = s.steps
            updateSuccess { copy(isLoading = true) }
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
            updateSuccess { copy(isLoading = false) }
            sendEvent(InitialSetupEvent.Complete)
        }
    }

    override fun onError(throwable: Throwable) {
        updateSuccess { copy(isLoading = false) }
    }
}
