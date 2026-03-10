package com.hoabui.virtualbody3d.ui.createbaseline

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.core.base.UiState
import com.hoabui.virtualbody3d.ui.camera.CameraCaptureScreenContent
import com.hoabui.virtualbody3d.ui.createbaseline.component.ChatGPTThinkingCard
import com.hoabui.virtualbody3d.domain.model.AnalysisType
import com.hoabui.virtualbody3d.ui.camera.viewmodel.CameraCaptureUiState
import com.hoabui.virtualbody3d.ui.camera.viewmodel.CameraCaptureViewModel
import com.hoabui.virtualbody3d.ui.components.UiStateContent
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBaselineScreen(
    modifier: Modifier = Modifier,
    onNavigateToScanResult: () -> Unit = {},
    viewModel: CameraCaptureViewModel = hiltViewModel()
) {
    val screenState by viewModel.state.collectAsStateWithLifecycle()
    val state = when (screenState) {
        is UiState.Success -> (screenState as UiState.Success<CameraCaptureUiState>).data
        else -> CameraCaptureUiState.CameraActive
    }
    val pendingReviewFile by viewModel.pendingReviewFile.collectAsStateWithLifecycle(initialValue = null)
    val captureTrigger by viewModel.captureTrigger.collectAsStateWithLifecycle(initialValue = 0)
    // Nullable so we don't navigate on first frame when returning from BodyScanResult (initial would be false and trigger re-navigate).
    val hasNavigatedToScanResult by viewModel.hasNavigatedToScanResult.collectAsStateWithLifecycle(initialValue = null)
    val token = GymTheme.token
    val colors = token.colors
    val spacing = token.spacing

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { }

    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        viewModel.onImagePicked(uri)
    }

    DisposableEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
        onDispose { }
    }

    LaunchedEffect(state, hasNavigatedToScanResult) {
        when (state) {
            is CameraCaptureUiState.ReviewExtracted -> {
                when (hasNavigatedToScanResult) {
                    false -> {
                        viewModel.markHasNavigatedToScanResult()
                        onNavigateToScanResult()
                    }
                    true -> {
                        // User returned from BodyScanResult; state was still ReviewExtracted so camera was hidden. Reset to show CameraActive UI.
                        viewModel.resetToCameraAfterReturnFromScanResult()
                    }
                    null -> { /* Waiting for first emission, do nothing */ }
                }
            }
            else -> viewModel.clearHasNavigatedToScanResult()
        }
    }

    UiStateContent(
        state = screenState,
        modifier = modifier.fillMaxSize(),
        errorContent = { mod, message ->
            Box(modifier = mod.fillMaxSize()) {
                CameraCaptureScreenContent(
                    modifier = Modifier.fillMaxSize(),
                    state = CameraCaptureUiState.CameraActive,
                    showCamera = true,
                    captureTrigger = captureTrigger,
                    onImageCaptured = { file ->
                        viewModel.onImageCaptured(file, alreadyProcessed = (file == pendingReviewFile))
                    },
                    onCaptureError = viewModel::onCaptureError,
                    buttonsEnabled = true,
                    onCapture = viewModel::requestCapture,
                    onUpload = {
                        pickMedia.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    reviewStateEnabled = true,
                    onCaptureCompletedForReview = { file -> viewModel.onPhotoCaptured(file) },
                    externalReviewFile = pendingReviewFile,
                    onClearReviewFile = viewModel::onClearReview
                )
                CreateBaselineErrorSnackbarOrDialog(
                    message = message,
                    onDismiss = viewModel::onErrorDismiss
                )
            }
        },
        successContent = { mod, data ->
            Box(modifier = mod.fillMaxSize()) {
                CameraCaptureScreenContent(
                    modifier = Modifier.fillMaxSize(),
                    state = data,
                    showCamera = data is CameraCaptureUiState.CameraActive,
                    captureTrigger = captureTrigger,
                    onImageCaptured = { file ->
                        viewModel.onImageCaptured(file, alreadyProcessed = (file == pendingReviewFile))
                    },
                    onCaptureError = viewModel::onCaptureError,
                    buttonsEnabled = data is CameraCaptureUiState.CameraActive || data is CameraCaptureUiState.ConfirmPhoto,
                    onCapture = viewModel::requestCapture,
                    onUpload = {
                        pickMedia.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    reviewStateEnabled = true,
                    onCaptureCompletedForReview = { file -> viewModel.onPhotoCaptured(file) },
                    externalReviewFile = pendingReviewFile,
                    onClearReviewFile = viewModel::onClearReview
                )
                ChatGPTThinkingCard(
                    visible = data is CameraCaptureUiState.PreProcessing ||
                        data is CameraCaptureUiState.Uploading ||
                        data is CameraCaptureUiState.Analyzing,
                    message = when (data) {
                        is CameraCaptureUiState.PreProcessing -> stringResource(R.string.loading_processing)
                        is CameraCaptureUiState.Uploading -> stringResource(R.string.loading_uploading)
                        is CameraCaptureUiState.Analyzing -> when (data.type) {
                            AnalysisType.OCR -> stringResource(R.string.loading_extracting)
                            AnalysisType.MEAL -> stringResource(R.string.loading_analyzing_meal)
                        }
                        else -> ""
                    }
                )
            }
        }
    )
}

@Composable
private fun CreateBaselineErrorSnackbarOrDialog(
    message: String,
    onDismiss: () -> Unit
) {
    val token = GymTheme.token
    val colors = token.colors
    val typography = token.typography
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.error_dialog_title),
                style = typography.titleLarge,
                color = colors.textPrimary
            )
        },
        text = {
            Text(
                text = message,
                style = typography.bodyMedium,
                color = colors.textPrimary
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                shape = RoundedCornerShape(token.radius.lg),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = token.elevation.level0)
            ) {
                Text(
                    text = stringResource(R.string.error_ok),
                    style = typography.titleMedium
                )
            }
        },
        shape = RoundedCornerShape(token.radius.lg)
    )
}

