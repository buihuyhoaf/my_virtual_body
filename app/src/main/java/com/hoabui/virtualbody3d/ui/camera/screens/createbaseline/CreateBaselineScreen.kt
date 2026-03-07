package com.hoabui.virtualbody3d.ui.camera.screens.createbaseline

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.hoabui.virtualbody3d.ui.camera.screens.createbaseline.component.ChatGPTThinkingCard
import com.hoabui.virtualbody3d.ui.camera.screens.createbaseline.component.CreateBaselineReviewBottomSheet
import com.hoabui.virtualbody3d.domain.model.AnalysisType
import com.hoabui.virtualbody3d.ui.camera.viewmodel.CameraCaptureEvent
import com.hoabui.virtualbody3d.ui.camera.viewmodel.CameraCaptureUiState
import com.hoabui.virtualbody3d.ui.camera.viewmodel.CameraCaptureViewModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBaselineScreen(
    modifier: Modifier = Modifier,
    onComplete: () -> Unit,
    viewModel: CameraCaptureViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val reviewState by viewModel.reviewState.collectAsStateWithLifecycle()
    val pendingReviewFile by viewModel.pendingReviewFile.collectAsStateWithLifecycle(initialValue = null)
    val captureTrigger by viewModel.captureTrigger.collectAsStateWithLifecycle(initialValue = 0)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
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

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is CameraCaptureEvent.NavigateHome -> onComplete()
            }
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
            is CameraCaptureUiState.ReviewExtracted -> {
                if (reviewState != null) {
                    ModalBottomSheet(
                        onDismissRequest = viewModel::onReviewDismiss,
                        sheetState = sheetState
                    ) {
                        CreateBaselineReviewBottomSheet(
                            reviewState = reviewState!!,
                            onUpdateField = viewModel::updateReviewField,
                            onConfirm = viewModel::onConfirmBaseline,
                            onRetake = viewModel::onReviewDismiss,
                            modifier = Modifier.fillMaxHeight(0.9f)
                        )
                    }
                }
            }
            is CameraCaptureUiState.Error -> CreateBaselineErrorSnackbarOrDialog(
                message = s.message,
                onDismiss = viewModel::onErrorDismiss
            )
            else -> { }
        }
    }
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

