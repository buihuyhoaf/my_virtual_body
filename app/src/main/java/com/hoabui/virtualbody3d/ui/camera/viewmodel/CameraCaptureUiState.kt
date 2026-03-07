package com.hoabui.virtualbody3d.ui.camera.viewmodel

import android.net.Uri
import com.hoabui.virtualbody3d.domain.model.AnalysisType
import com.hoabui.virtualbody3d.domain.model.ExtractedData

/**
 * UI state pipeline for camera capture, upload, and AI analysis:
 *
 * CameraActive → ConfirmPhoto → PreProcessing → Uploading → Analyzing → ReviewExtracted
 *                                                                              ↓
 * Error ←—————————————————————————————————————————————————————————————————————
 */
sealed class CameraCaptureUiState {
    /** Camera preview is visible; user can capture or pick from gallery. */
    data object CameraActive : CameraCaptureUiState()

    /** User is reviewing the captured/picked image; can retake or confirm (Use Photo). */
    data class ConfirmPhoto(val photoUri: Uri) : CameraCaptureUiState()

    /** Local image processing (resize, rotation, compression) before upload. */
    data object PreProcessing : CameraCaptureUiState()

    /** Image is being uploaded to the backend. */
    data object Uploading : CameraCaptureUiState()

    /** Backend is analyzing the image (OCR or meal recognition). */
    data class Analyzing(val type: AnalysisType) : CameraCaptureUiState()

    /** Analysis complete; show review screen for user to confirm or edit. */
    data class ReviewExtracted(val data: ExtractedData) : CameraCaptureUiState()

    /** A step in the pipeline failed; show error with retry option. */
    data class Error(val message: String) : CameraCaptureUiState()
}
