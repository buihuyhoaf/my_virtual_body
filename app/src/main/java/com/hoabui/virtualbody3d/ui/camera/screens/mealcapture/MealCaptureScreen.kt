package com.hoabui.virtualbody3d.ui.camera.screens.mealcapture

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
import com.hoabui.virtualbody3d.ui.camera.CameraCaptureScreenContent
import com.hoabui.virtualbody3d.domain.model.AnalysisType
import com.hoabui.virtualbody3d.ui.camera.viewmodel.CameraCaptureUiState
import com.hoabui.virtualbody3d.ui.camera.viewmodel.CameraCaptureViewModel
import com.hoabui.virtualbody3d.ui.camera.screens.createbaseline.component.ChatGPTThinkingCard
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun MealCaptureScreen(
    modifier: Modifier = Modifier,
    viewModel: CameraCaptureViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
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

    Box(modifier = modifier.fillMaxSize()) {
        CameraCaptureScreenContent(
            modifier = Modifier.fillMaxSize(),
            state = state,
            showCamera = state is CameraCaptureUiState.CameraActive,
            captureTrigger = captureTrigger,
            onImageCaptured = { file ->
                viewModel.onImageCaptured(file, alreadyProcessed = (file == pendingReviewFile))
            },
            onCaptureError = viewModel::onCaptureError,
            buttonsEnabled = state is CameraCaptureUiState.CameraActive || state is CameraCaptureUiState.ConfirmPhoto,
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
            visible = state is CameraCaptureUiState.PreProcessing ||
                state is CameraCaptureUiState.Uploading ||
                state is CameraCaptureUiState.Analyzing,
            message = when (state) {
                is CameraCaptureUiState.PreProcessing -> stringResource(R.string.loading_processing)
                is CameraCaptureUiState.Uploading -> stringResource(R.string.loading_uploading)
                is CameraCaptureUiState.Analyzing -> when ((state as CameraCaptureUiState.Analyzing).type) {
                    AnalysisType.OCR -> stringResource(R.string.loading_extracting)
                    AnalysisType.MEAL -> stringResource(R.string.loading_analyzing_meal)
                }
                else -> ""
            }
        )

        when (val s = state) {
            is CameraCaptureUiState.Error -> AlertDialog(
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
                        text = s.message,
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
            else -> { }
        }
    }
}
