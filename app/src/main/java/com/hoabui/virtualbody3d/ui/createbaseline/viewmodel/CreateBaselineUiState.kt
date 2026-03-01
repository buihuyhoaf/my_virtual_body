package com.hoabui.virtualbody3d.ui.createbaseline.viewmodel

import com.hoabui.virtualbody3d.domain.model.ExtractedData
import java.io.File

sealed class CreateBaselineUiState {
    /** Initial state: user sees Capture and Upload from Gallery buttons. */
    data object Idle : CreateBaselineUiState()
    /** Camera preview is active; user can take photo or pick from gallery. */
    data object CameraActive : CreateBaselineUiState()
    data object Processing : CreateBaselineUiState()
    data class PreviewReady(val file: File, val fromCamera: Boolean = false) : CreateBaselineUiState()
    data object Uploading : CreateBaselineUiState()
    data object OcrLoading : CreateBaselineUiState()
    data class ReviewExtracted(val data: ExtractedData) : CreateBaselineUiState()
    data class Error(val message: String) : CreateBaselineUiState()
}