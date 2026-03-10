package com.hoabui.virtualbody3d.ui.mealcapture

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.core.base.UiState
import com.hoabui.virtualbody3d.domain.model.AnalysisType
import com.hoabui.virtualbody3d.ui.camera.CameraCaptureScreenContent
import com.hoabui.virtualbody3d.ui.camera.viewmodel.CameraCaptureUiState
import com.hoabui.virtualbody3d.ui.camera.viewmodel.CameraCaptureViewModel
import com.hoabui.virtualbody3d.ui.components.UiStateContent
import com.hoabui.virtualbody3d.ui.createbaseline.component.ChatGPTThinkingCard
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import java.io.File

/**
 * Camera page used as the first page in the MealCapture vertical pager.
 * Wraps [CameraCaptureScreenContent] and exposes a callback when the user confirms
 * using a photo so that meal analysis can be triggered.
 */
@Composable
fun CameraPage(
    modifier: Modifier = Modifier,
    viewModel: CameraCaptureViewModel = hiltViewModel(),
    onUsePhoto: (File) -> Unit
) {
    val screenState by viewModel.state.collectAsStateWithLifecycle()
    val state = when (screenState) {
        is UiState.Success -> (screenState as UiState.Success<CameraCaptureUiState>).data
        else -> CameraCaptureUiState.CameraActive
    }
    val pendingReviewFile by viewModel.pendingReviewFile.collectAsStateWithLifecycle(initialValue = null)
    val captureTrigger by viewModel.captureTrigger.collectAsStateWithLifecycle(initialValue = 0)
    val token = GymTheme.token

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

    LaunchedEffect(state) {
        if (state is CameraCaptureUiState.ReviewExtracted) {
            viewModel.onReviewDismiss()
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
                        // Reuse existing pipeline behavior (OCR) and also surface file to caller.
                        viewModel.onImageCaptured(file, alreadyProcessed = (file == pendingReviewFile))
                        onUsePhoto(file)
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
                AlertDialog(
                    onDismissRequest = viewModel::onErrorDismiss,
                    title = {
                        Text(
                            text = stringResource(R.string.error_dialog_title),
                            style = token.typography.titleLarge,
                            color = token.colors.textPrimary
                        )
                    },
                    text = {
                        Text(
                            text = message,
                            style = token.typography.bodyMedium,
                            color = token.colors.textPrimary
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = viewModel::onErrorDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = token.colors.primary),
                            shape = RoundedCornerShape(token.radius.lg),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = token.elevation.level0)
                        ) {
                            Text(
                                text = stringResource(R.string.error_ok),
                                style = token.typography.titleMedium
                            )
                        }
                    },
                    shape = RoundedCornerShape(token.radius.lg)
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
                        onUsePhoto(file)
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

