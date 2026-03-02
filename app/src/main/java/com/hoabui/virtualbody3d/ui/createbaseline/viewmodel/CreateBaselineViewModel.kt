package com.hoabui.virtualbody3d.ui.createbaseline.viewmodel

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.hoabui.virtualbody3d.core.base.BaseViewModel
import com.hoabui.virtualbody3d.domain.model.ExtractedData
import com.hoabui.virtualbody3d.domain.usecase.ImageProcessingUseCase
import com.hoabui.virtualbody3d.domain.usecase.SaveBaselineUseCase
import com.hoabui.virtualbody3d.domain.usecase.UploadAndExtractBaselineUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CreateBaselineViewModel @Inject constructor(
    private val imageProcessingUseCase: ImageProcessingUseCase,
    private val uploadAndExtractBaselineUseCase: UploadAndExtractBaselineUseCase,
    private val saveBaselineUseCase: SaveBaselineUseCase
) : BaseViewModel<CreateBaselineUiState, CreateBaselineEvent>(CreateBaselineUiState.Idle) {

    private val _captureTrigger = MutableStateFlow(0)
    val captureTrigger: StateFlow<Int> = _captureTrigger.asStateFlow()

    private val _reviewState = MutableStateFlow<ReviewState?>(null)
    val reviewState: StateFlow<ReviewState?> = _reviewState.asStateFlow()

    /** User taps "Capture Photo" button → request a capture (camera content will take picture). */
    fun requestCapture() {
        _captureTrigger.value += 1
    }

    /** User taps Capture in Idle → switch to camera mode. */
    fun onCaptureClick() {
        updateState { CreateBaselineUiState.CameraActive }
    }

    /**
     * Called when the user has captured an image (CameraX saved to temp file).
     * Emits Processing then runs unified pipeline on IO; emits PreviewReady(optimizedFile, fromCamera=true) or Error.
     */
    fun onImageCaptured(rawFile: File) {
        _captureTrigger.value = 0
        viewModelScope.launch {
            updateState { CreateBaselineUiState.Processing }
            runCatching {
                withContext(Dispatchers.IO) {
                    imageProcessingUseCase.process(rawFile)
                }
            }.fold(
                onSuccess = { file ->
                    if (file != null) {
                        updateState { CreateBaselineUiState.PreviewReady(file, fromCamera = true) }
                    } else {
                        updateState { CreateBaselineUiState.Error("Failed to process image") }
                    }
                },
                onFailure = { e ->
                    updateState {
                        CreateBaselineUiState.Error(e.message ?: "Processing failed")
                    }
                }
            )
        }
    }

    /**
     * Called when the user has selected an image from the gallery (PickVisualMedia result).
     * Runs processing on IO and emits PreviewReady(file, fromCamera=false) or Error.
     */
    fun onImagePicked(uri: Uri?) {
        if (uri == null) return
        viewModelScope.launch {
            updateState { CreateBaselineUiState.Processing }
            runCatching {
                withContext(Dispatchers.IO) {
                    imageProcessingUseCase.process(uri)
                }
            }.fold(
                onSuccess = { file ->
                    if (file != null) {
                        updateState { CreateBaselineUiState.PreviewReady(file, fromCamera = false) }
                    } else {
                        updateState { CreateBaselineUiState.Error("Failed to process image") }
                    }
                },
                onFailure = { e ->
                    updateState {
                        CreateBaselineUiState.Error(e.message ?: "Processing failed")
                    }
                }
            )
        }
    }

    /** User cancels the preview dialog → return to CameraActive if from camera, else Idle. */
    fun onPreviewCancel() {
        updateState {
            when (this) {
                is CreateBaselineUiState.PreviewReady -> if (fromCamera) CreateBaselineUiState.CameraActive else CreateBaselineUiState.Idle
                else -> CreateBaselineUiState.Idle
            }
        }
    }

    /** User confirms upload in preview dialog → upload then OCR. */
    fun onConfirmUpload(file: File) {
        viewModelScope.launch {
            updateState { CreateBaselineUiState.Uploading }
            runCatching {
                uploadAndExtractBaselineUseCase(file)
            }.fold(
                onSuccess = { data ->
                    updateState { CreateBaselineUiState.ReviewExtracted(data) }
                    _reviewState.value = ReviewState(
                        originalData = data,
                        editableData = data,
                        isModified = false,
                        isValid = isExtractedDataValid(data),
                        isLoading = false
                    )
                },
                onFailure = { e ->
                    updateState { CreateBaselineUiState.Error(e.message ?: "Upload failed") }
                }
            )
        }
    }

    /** Updates a single metric in the review sheet. */
    fun updateReviewField(metric: ReviewMetric, value: String) {
        val current = _reviewState.value ?: return
        val updated = when (metric) {
            ReviewMetric.WEIGHT -> current.editableData.copy(weight = value)
            ReviewMetric.BODY_FAT_PERCENT -> current.editableData.copy(bodyFatPercent = value)
            ReviewMetric.MUSCLE_MASS -> current.editableData.copy(muscleMass = value)
            ReviewMetric.BMI -> current.editableData.copy(bmi = value)
            ReviewMetric.BODY_FAT_MASS -> current.editableData.copy(bodyFatMass = value)
            ReviewMetric.FAT_FREE_MASS -> current.editableData.copy(fatFreeMass = value)
            ReviewMetric.BMR -> current.editableData.copy(bmr = value)
            ReviewMetric.VISCERAL_FAT_LEVEL -> current.editableData.copy(visceralFatLevel = value)
        }
        _reviewState.value = current.copy(
            editableData = updated,
            isModified = updated != current.originalData,
            isValid = isExtractedDataValid(updated)
        )
    }

    /** User confirms baseline in review → save then navigate Home (clear backstack). */
    fun onConfirmBaseline() {
        val state = _reviewState.value ?: return
        if (!state.isValid || state.isLoading) return
        viewModelScope.launch {
            _reviewState.value = state.copy(isLoading = true)
            runCatching {
                withContext(Dispatchers.IO) {
                    saveBaselineUseCase(state.editableData)
                }
            }.fold(
                onSuccess = {
                    _reviewState.value = null
                    updateState { CreateBaselineUiState.Idle }
                    sendEvent(CreateBaselineEvent.NavigateHome)
                },
                onFailure = { e ->
                    _reviewState.value = state.copy(isLoading = false)
                    updateState { CreateBaselineUiState.Error(e.message ?: "Save failed") }
                }
            )
        }
    }

    /** User dismisses review without confirming → back to Idle. */
    fun onReviewDismiss() {
        _reviewState.value = null
        updateState { CreateBaselineUiState.Idle }
    }

    private fun isExtractedDataValid(data: ExtractedData): Boolean {
        fun isNonEmptyNumeric(s: String): Boolean {
            val trimmed = s.trim()
            if (trimmed.isEmpty()) return false
            return trimmed.toDoubleOrNull() != null
        }
        return isNonEmptyNumeric(data.weight) &&
            isNonEmptyNumeric(data.bodyFatPercent) &&
            isNonEmptyNumeric(data.muscleMass) &&
            isNonEmptyNumeric(data.bmi) &&
            isNonEmptyNumeric(data.bodyFatMass) &&
            isNonEmptyNumeric(data.fatFreeMass) &&
            isNonEmptyNumeric(data.bmr) &&
            isNonEmptyNumeric(data.visceralFatLevel)
    }

    /** Clear error state. */
    fun onErrorDismiss() {
        updateState { CreateBaselineUiState.Idle }
    }

    /** Called when camera capture fails (e.g. takePicture error). */
    fun onCaptureError(message: String) {
        _captureTrigger.value = 0
        updateState { CreateBaselineUiState.Error(message) }
    }
}