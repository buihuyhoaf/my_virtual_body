package com.hoabui.virtualbody3d.ui.createbaseline

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.hoabui.virtualbody3d.ui.createbaseline.component.CreateBaselineActionsSection
import com.hoabui.virtualbody3d.ui.createbaseline.component.CreateBaselineLoadingOverlay
import com.hoabui.virtualbody3d.ui.createbaseline.component.CreateBaselinePreviewDialog
import com.hoabui.virtualbody3d.ui.createbaseline.component.CreateBaselineReviewBottomSheet
import com.hoabui.virtualbody3d.ui.createbaseline.component.CreateBaselineViewfinderSection
import com.hoabui.virtualbody3d.ui.createbaseline.viewmodel.CreateBaselineEvent
import com.hoabui.virtualbody3d.ui.createbaseline.viewmodel.CreateBaselineUiState
import com.hoabui.virtualbody3d.ui.createbaseline.viewmodel.CreateBaselineViewModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBaselineScreen(
    modifier: Modifier = Modifier,
    onComplete: () -> Unit,
    viewModel: CreateBaselineViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val reviewState by viewModel.reviewState.collectAsStateWithLifecycle()
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
        permissionLauncher.launch(android.Manifest.permission.CAMERA)
        onDispose { }
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is CreateBaselineEvent.NavigateHome -> onComplete()
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.surface)
                .padding(horizontal = spacing.xl)
        ) {
            CreateBaselineViewfinderSection(
                modifier = Modifier.weight(1f),
                colors = colors,
                radius = token.radius,
                createBaselineTokens = token.createBaseline,
                showCamera = state !is CreateBaselineUiState.PreviewReady && state !is CreateBaselineUiState.ReviewExtracted && state !is CreateBaselineUiState.Error,
                captureTrigger = captureTrigger,
                onImageCaptured = if (state is CreateBaselineUiState.Idle || state is CreateBaselineUiState.CameraActive) viewModel::onImageCaptured else null,
                onCaptureError = if (state is CreateBaselineUiState.Idle || state is CreateBaselineUiState.CameraActive) viewModel::onCaptureError else null
            )
            Spacer(modifier = Modifier.height(spacing.md))
            CreateBaselineActionsSection(
                colors = colors,
                spacing = spacing,
                radius = token.radius,
                createBaselineTokens = token.createBaseline,
                onboardingTokens = token.onboarding,
                elevation = token.elevation,
                typography = token.typography,
                buttonsEnabled = state !is CreateBaselineUiState.Processing && state !is CreateBaselineUiState.Uploading,
                onCapture = viewModel::requestCapture,
                onUpload = {
                    pickMedia.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            )
            Spacer(modifier = Modifier.height(spacing.xl))
        }

        when (state) {
            is CreateBaselineUiState.Processing -> CreateBaselineLoadingOverlay(message = stringResource(R.string.loading_processing))
            is CreateBaselineUiState.Uploading -> CreateBaselineLoadingOverlay(message = stringResource(R.string.loading_uploading))
            is CreateBaselineUiState.OcrLoading -> CreateBaselineLoadingOverlay(message = stringResource(R.string.loading_extracting))
            else -> { }
        }

        when (val s = state) {
            is CreateBaselineUiState.PreviewReady -> CreateBaselinePreviewDialog(
                file = s.file,
                onCancel = viewModel::onPreviewCancel,
                onUpload = { viewModel.onConfirmUpload(s.file) }
            )
            is CreateBaselineUiState.ReviewExtracted -> {
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
            is CreateBaselineUiState.Error -> CreateBaselineErrorSnackbarOrDialog(
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

