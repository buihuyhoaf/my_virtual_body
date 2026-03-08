package com.hoabui.virtualbody3d.ui.camera.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.hoabui.virtualbody3d.core.base.BaseViewModel
import com.hoabui.virtualbody3d.domain.model.AnalysisType
import com.hoabui.virtualbody3d.domain.model.ExtractedData
import com.hoabui.virtualbody3d.domain.usecase.AnalyzeImageUseCase
import com.hoabui.virtualbody3d.domain.usecase.PrepareImageUseCase
import com.hoabui.virtualbody3d.domain.usecase.SaveBaselineUseCase
import com.hoabui.virtualbody3d.domain.usecase.UploadImageUseCase
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
class CameraCaptureViewModel @Inject constructor(
    private val prepareImageUseCase: PrepareImageUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val analyzeImageUseCase: AnalyzeImageUseCase,
) : BaseViewModel<CameraCaptureUiState, CameraCaptureEvent>(CameraCaptureUiState.CameraActive) {

    init {
        logState(CameraCaptureUiState.CameraActive, "init")
    }

    /**
     * Log state transition for debugging pipeline:
     * CameraActive → ConfirmPhoto → PreProcessing → Uploading → Analyzing → ReviewExtracted | Error
     */
    private fun logState(state: CameraCaptureUiState, from: String = "") {
        val stateLabel = when (state) {
            is CameraCaptureUiState.CameraActive -> "CameraActive"
            is CameraCaptureUiState.ConfirmPhoto -> "ConfirmPhoto(uri=${state.photoUri.lastPathSegment?.takeLast(20) ?: "?"})"
            is CameraCaptureUiState.PreProcessing -> "PreProcessing"
            is CameraCaptureUiState.Uploading -> "Uploading"
            is CameraCaptureUiState.Analyzing -> "Analyzing(type=${state.type})"
            is CameraCaptureUiState.ReviewExtracted -> "ReviewExtracted"
            is CameraCaptureUiState.Error -> "Error(message=${state.message.take(50)})"
        }
        val trigger = if (from.isNotEmpty()) " [from $from]" else ""
        Log.d(TAG, "[STATE] → $stateLabel$trigger")
    }

    private val _captureTrigger = MutableStateFlow(0)
    val captureTrigger: StateFlow<Int> = _captureTrigger.asStateFlow()

    private val _reviewState = MutableStateFlow<ReviewState?>(null)
    val reviewState: StateFlow<ReviewState?> = _reviewState.asStateFlow()

    private val _pendingReviewFile = MutableStateFlow<File?>(null)
    val pendingReviewFile: StateFlow<File?> = _pendingReviewFile.asStateFlow()

    /** Survives recomposition so we only navigate to scan result once per ReviewExtracted. */
    private val _hasNavigatedToScanResult = MutableStateFlow(false)
    val hasNavigatedToScanResult: StateFlow<Boolean> = _hasNavigatedToScanResult.asStateFlow()

    fun markHasNavigatedToScanResult() {
        _hasNavigatedToScanResult.value = true
    }

    fun clearHasNavigatedToScanResult() {
        _hasNavigatedToScanResult.value = false
    }

    /** Call when user returns from BodyScanResult so UI shows camera again (state was still ReviewExtracted). */
    fun resetToCameraAfterReturnFromScanResult() {
        Log.d(TAG, "[ACTION] resetToCameraAfterReturnFromScanResult")
        _pendingReviewFile.value = null
        logState(CameraCaptureUiState.CameraActive, "resetToCameraAfterReturnFromScanResult")
        updateState { CameraCaptureUiState.CameraActive }
    }

    /** User taps "Capture Photo" button → request a capture (camera content will take picture). */
    fun requestCapture() {
        Log.d(TAG, "[ACTION] requestCapture (trigger=${_captureTrigger.value + 1})")
        _captureTrigger.value += 1
    }

    /**
     * Called when the user confirms the photo (Use Photo).
     * Pipeline: ConfirmPhoto → PreProcessing → Uploading → Analyzing → ReviewExtracted (or Error).
     * @param file The captured/picked image file (or already-processed file when [alreadyProcessed] is true).
     * @param alreadyProcessed If true, skip preparation and go straight to upload → analyze.
     */
    fun onImageCaptured(file: File, alreadyProcessed: Boolean = false) {
        Log.d(TAG, "[ACTION] onImageCaptured(file=${file.name}, alreadyProcessed=$alreadyProcessed)")
        _captureTrigger.value = 0
        val photoUri = Uri.fromFile(file)
        logState(CameraCaptureUiState.ConfirmPhoto(photoUri), "onImageCaptured(alreadyProcessed=$alreadyProcessed)")
        updateState { CameraCaptureUiState.ConfirmPhoto(photoUri) }
        if (alreadyProcessed) {
            viewModelScope.launch { runUploadAndAnalyzePipeline(file, AnalysisType.OCR) }
            return
        }
        viewModelScope.launch { runFullPipeline(file, AnalysisType.OCR) }
    }

    /**
     * Called when the user has selected an image from the gallery.
     * PreProcessing → on success sets [pendingReviewFile] and CameraActive (user sees review).
     */
    fun onImagePicked(uri: Uri?) {
        if (uri == null) return
        Log.d(TAG, "[ACTION] onImagePicked(uri=${uri.lastPathSegment?.takeLast(30)})")
        viewModelScope.launch {
            logState(CameraCaptureUiState.PreProcessing, "onImagePicked")
            updateState { CameraCaptureUiState.PreProcessing }
            runCatching {
                withContext(Dispatchers.IO) { prepareImageUseCase(uri) }
            }.fold(
                onSuccess = { file ->
                    _pendingReviewFile.value = file
                    logState(CameraCaptureUiState.CameraActive, "onImagePicked")
                    updateState { CameraCaptureUiState.CameraActive }
                },
                onFailure = { e ->
                    val err = CameraCaptureUiState.Error(e.message ?: "Processing failed")
                    logState(err, "onImagePicked")
                    updateState { err }
                }
            )
        }
    }

    /**
     * Runs the full pipeline: PreProcessing → Uploading → Analyzing → ReviewExtracted.
     * Entry: photo confirmed as [file] (from camera).
     */
    private suspend fun runFullPipeline(file: File, analysisType: AnalysisType) {
        logState(CameraCaptureUiState.PreProcessing, "runFullPipeline")
        updateState { CameraCaptureUiState.PreProcessing }
        val prepared = runCatching {
            withContext(Dispatchers.IO) { prepareImageUseCase(file) }
        }.fold(
            onSuccess = { it },
            onFailure = { e ->
                val err = CameraCaptureUiState.Error(e.message ?: "Failed to process image")
                logState(err, "runFullPipeline")
                updateState { err }
                return
            }
        )
        runUploadAndAnalyzePipeline(prepared, analysisType)
    }

    /**
     * Runs upload → analyze and updates state to ReviewExtracted or Error.
     */
    private suspend fun runUploadAndAnalyzePipeline(processedFile: File, analysisType: AnalysisType) {
        logState(CameraCaptureUiState.Uploading, "runUploadAndAnalyzePipeline")
        updateState { CameraCaptureUiState.Uploading }
        val uploaded = runCatching {
            withContext(Dispatchers.IO) { uploadImageUseCase(processedFile) }
        }.fold(
            onSuccess = { it },
            onFailure = { e ->
                val err = CameraCaptureUiState.Error(e.message ?: "Upload failed")
                logState(err, "runUploadAndAnalyzePipeline")
                updateState { err }
                return
            }
        )

        logState(CameraCaptureUiState.Analyzing(analysisType), "runUploadAndAnalyzePipeline")
        updateState { CameraCaptureUiState.Analyzing(analysisType) }
        runCatching {
            withContext(Dispatchers.IO) {
                analyzeImageUseCase(uploaded.imageUrl, analysisType)
            }
        }.fold(
            onSuccess = { data ->
                _pendingReviewFile.value = null
                _hasNavigatedToScanResult.value = false
                logState(CameraCaptureUiState.ReviewExtracted(data), "runUploadAndAnalyzePipeline")
                updateState { CameraCaptureUiState.ReviewExtracted(data) }
                _reviewState.value = ReviewState(
                    originalData = data,
                    editableData = data,
                    isModified = false,
                    isValid = isExtractedDataValid(data),
                    isLoading = false
                )
            },
            onFailure = { e ->
                val err = CameraCaptureUiState.Error(e.message ?: "Analysis failed")
                logState(err, "runUploadAndAnalyzePipeline")
                updateState { err }
            }
        )
    }

    /**
     * Called when the camera has finished capturing a photo (before user taps Send).
     * Transitions to ConfirmPhoto so the user can review and then confirm or retake.
     */
    fun onPhotoCaptured(file: File) {
        _captureTrigger.value = 0
        val photoUri = Uri.fromFile(file)
        Log.d(TAG, "[ACTION] onPhotoCaptured(file=${file.name})")
        logState(CameraCaptureUiState.ConfirmPhoto(photoUri), "onPhotoCaptured")
        updateState { CameraCaptureUiState.ConfirmPhoto(photoUri) }
    }

    /** Called when capture completes and review is shown (so camera does not re-trigger). */
    fun resetCaptureTrigger() {
        _captureTrigger.value = 0
    }

    /** User taps Retake in same-screen review → clear pending file and return to CameraActive. */
    fun onClearReview() {
        Log.d(TAG, "[ACTION] onClearReview")
        _pendingReviewFile.value = null
        logState(CameraCaptureUiState.CameraActive, "onClearReview")
        updateState { CameraCaptureUiState.CameraActive }
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


    /** User dismisses review without confirming → back to CameraActive. */
    fun onReviewDismiss() {
        Log.d(TAG, "[ACTION] onReviewDismiss")
        _reviewState.value = null
        logState(CameraCaptureUiState.CameraActive, "onReviewDismiss")
        updateState { CameraCaptureUiState.CameraActive }
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

    /** Clear error state → back to CameraActive. */
    fun onErrorDismiss() {
        Log.d(TAG, "[ACTION] onErrorDismiss")
        logState(CameraCaptureUiState.CameraActive, "onErrorDismiss")
        updateState { CameraCaptureUiState.CameraActive }
    }

    /** Called when camera capture fails (e.g. takePicture error). */
    fun onCaptureError(message: String) {
        _captureTrigger.value = 0
        Log.d(TAG, "[ACTION] onCaptureError(message=$message)")
        val err = CameraCaptureUiState.Error(message)
        logState(err, "onCaptureError")
        updateState { err }
    }

    companion object {
        private const val TAG = "CameraCaptureVM"
    }
}
