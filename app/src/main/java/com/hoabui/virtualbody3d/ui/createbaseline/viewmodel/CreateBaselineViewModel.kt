package com.hoabui.virtualbody3d.ui.createbaseline.viewmodel

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.hoabui.virtualbody3d.core.base.BaseViewModel
import com.hoabui.virtualbody3d.domain.usecase.ImageProcessingUseCase
import com.hoabui.virtualbody3d.domain.usecase.UploadAndExtractBaselineUseCase
import com.hoabui.virtualbody3d.ui.createbaseline.viewmodel.CreateBaselineEvent
import com.hoabui.virtualbody3d.ui.createbaseline.viewmodel.CreateBaselineUiState
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
    private val uploadAndExtractBaselineUseCase: UploadAndExtractBaselineUseCase
) : BaseViewModel<CreateBaselineUiState, CreateBaselineEvent>(CreateBaselineUiState.Idle) {

    private val _captureTrigger = MutableStateFlow(0)
    val captureTrigger: StateFlow<Int> = _captureTrigger.asStateFlow()

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
                onSuccess = { data -> updateState { CreateBaselineUiState.ReviewExtracted(data) } },
                onFailure = { e ->
                    updateState { CreateBaselineUiState.Error(e.message ?: "Upload failed") }
                }
            )
        }
    }

    /** User confirms baseline in review → navigate Home (clear backstack). */
    fun onConfirmBaseline() {
        sendEvent(CreateBaselineEvent.NavigateHome)
    }

    /** User dismisses review without confirming → back to Idle. */
    fun onReviewDismiss() {
        updateState { CreateBaselineUiState.Idle }
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