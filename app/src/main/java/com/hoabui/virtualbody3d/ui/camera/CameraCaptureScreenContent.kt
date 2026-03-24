package com.hoabui.virtualbody3d.ui.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import com.hoabui.virtualbody3d.ui.common_ui.atom.progress.GCircularProgress
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.hoabui.virtualbody3d.ui.camera.component.CameraViewfinderSection
import com.hoabui.virtualbody3d.ui.camera.component.CaptureControlsRow
import com.hoabui.virtualbody3d.ui.camera.component.ReviewControlRow
import com.hoabui.virtualbody3d.ui.camera.viewmodel.CameraCaptureUiState
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.tokens.semantic.SemanticColorTokens
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

private const val PREVIEW_WIDTH_FRACTION = 0.92f
private val PREVIEW_ASPECT_RATIO = 3f / 4f

/**
 * Shared screen content for camera capture. UI is driven by [state]:
 * - **CameraActive**: Camera preview + [CaptureControlsRow] (Gallery | Capture | Rotate).
 * - **ConfirmPhoto** (and loading states): Captured image + [ReviewControlRow] (X | Send).
 *
 * Preview card: ~92% width, 3:4 aspect ratio, rounded corners. Button row anchored to bottom.
 *
 * [externalReviewFile]: file to show in review (e.g. from gallery); [capturedFileForReview] is used when state is ConfirmPhoto after camera capture.
 */
@Composable
fun CameraCaptureScreenContent(
    modifier: Modifier = Modifier,
    state: CameraCaptureUiState,
    showCamera: Boolean = true,
    captureTrigger: Int = 0,
    onImageCaptured: (File) -> Unit,
    onCaptureError: (String) -> Unit,
    buttonsEnabled: Boolean = true,
    onCapture: () -> Unit,
    onUpload: () -> Unit,
    reviewStateEnabled: Boolean = false,
    /** Called when camera capture completes with [File]; use to e.g. transition to ConfirmPhoto. */
    onCaptureCompletedForReview: ((File) -> Unit)? = null,
    externalReviewFile: File? = null,
    onClearReviewFile: (() -> Unit)? = null
) {
    val token = GymTheme.token
    val colors = token.colors
    val spacing = token.spacing

    var useBackCamera by remember { mutableStateOf(true) }
    var capturedFileForReview by remember { mutableStateOf<File?>(null) }
    val onSwitchCamera: () -> Unit = { useBackCamera = !useBackCamera }

    val showReviewContent = state is CameraCaptureUiState.ConfirmPhoto
    val fileInReview: File? = if (showReviewContent) externalReviewFile ?: capturedFileForReview else null

    val onCapturedFromCamera: (File) -> Unit = {
        if (reviewStateEnabled) {
            capturedFileForReview = it
            onCaptureCompletedForReview?.invoke(it)
        } else {
            onImageCaptured(it)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(spacing.md)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(PREVIEW_WIDTH_FRACTION)
                        .aspectRatio(PREVIEW_ASPECT_RATIO)
                        .clip(RoundedCornerShape(token.radius.lg))
                        .background(colors.surface)
                ) {
                    if (showReviewContent && fileInReview != null) {
                        CapturedImageContent(file = fileInReview, colors = colors)
                    } else {
                        CameraViewfinderSection(
                            modifier = Modifier.fillMaxSize(),
                            token = token,
                            showCamera = showCamera,
                            captureTrigger = captureTrigger,
                            onImageCaptured = onCapturedFromCamera,
                            onCaptureError = onCaptureError,
                            useBackCamera = useBackCamera,
                            onSwitchCamera = onSwitchCamera
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = spacing.md, end = spacing.md, bottom = spacing.md)
            ) {
                when {
                    state is CameraCaptureUiState.CameraActive -> CaptureControlsRow(
                        token = token,
                        buttonsEnabled = buttonsEnabled,
                        onGallery = onUpload,
                        onCapture = onCapture,
                        onRotate = onSwitchCamera
                    )
                    showReviewContent -> ReviewControlRow(
                        token = token,
                        buttonsEnabled = buttonsEnabled,
                        onBackToCapture = {
                            capturedFileForReview = null
                            onClearReviewFile?.invoke()
                        },
                        onConfirmPhoto = {
                            fileInReview?.let { onImageCaptured(it) }
                            capturedFileForReview = null
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CapturedImageContent(
    file: File,
    colors: SemanticColorTokens
) {
    val token = GymTheme.token
    var bitmap by remember(file) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(file) {
        bitmap = withContext(Dispatchers.IO) {
            runCatching { BitmapFactory.decodeFile(file.absolutePath) }.getOrNull()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        bitmap?.asImageBitmap()?.let { imageBitmap ->
            Image(
                bitmap = imageBitmap,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(token.radius.lg)),
                contentScale = ContentScale.Crop
            )
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                GCircularProgress()
            }
        }
    }
}
