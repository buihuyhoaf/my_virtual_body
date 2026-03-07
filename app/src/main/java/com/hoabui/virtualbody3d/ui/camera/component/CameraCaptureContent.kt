package com.hoabui.virtualbody3d.ui.camera.component

import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SwitchCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Popup
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.hoabui.virtualbody3d.ui.theme.tokens.GymToken
import java.io.File

/**
 * Reusable CameraX composable: Preview, ImageCapture, and front/back camera switch.
 * Use for Baseline Scan, Meal Capture, or any feature that needs to capture a photo.
 *
 * - Binds/unbinds via [DisposableEffect] (lifecycle); capture is triggered by [captureTrigger].
 * - When [captureTrigger] > 0, takes a picture and invokes [onImageCaptured] with the saved file.
 * - When [useBackCamera] and [onSwitchCamera] are provided, camera is controlled externally and
 *   the built-in switch button is hidden (e.g. for Locket-style overlay UI).
 */
@Composable
fun CameraCaptureContent(
    modifier: Modifier = Modifier,
    token: GymToken,
    captureTrigger: Int = 0,
    onImageCaptured: (File) -> Unit,
    onCaptureError: (String) -> Unit,
    useBackCamera: Boolean? = null,
    onSwitchCamera: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    var useBackCameraInternal by remember { mutableStateOf(true) }
    val useBack = useBackCamera ?: useBackCameraInternal
    val switchCamera: () -> Unit = onSwitchCamera ?: { useBackCameraInternal = !useBackCameraInternal }
    var imageCaptureRef by remember { mutableStateOf<ImageCapture?>(null) }

    fun takePicture() {
        val imageCapture = imageCaptureRef ?: run {
            onCaptureError("Camera not ready")
            return
        }
        val file = File(context.cacheDir, "capture_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    onImageCaptured(file)
                }
                override fun onError(exception: ImageCaptureException) {
                    onCaptureError(exception.message ?: "Capture failed")
                }
            }
        )
    }

    DisposableEffect(lifecycleOwner, useBack) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }
            val imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()
            imageCaptureRef = imageCapture
            val selector = if (useBack) CameraSelector.DEFAULT_BACK_CAMERA else CameraSelector.DEFAULT_FRONT_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    selector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                onCaptureError(e.message ?: "Camera bind failed")
            }
        }, ContextCompat.getMainExecutor(context))

        onDispose {
            imageCaptureRef = null
            try {
                ProcessCameraProvider.getInstance(context).get().unbindAll()
            } catch (_: Exception) { }
        }
    }

    if (captureTrigger > 0) {
        LaunchedEffect(captureTrigger) {
            takePicture()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { _ ->
                previewView.apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
            }
        )
        if (onSwitchCamera == null) {
            val colors = token.colors
            val cameraToken = token.camera
            IconButton(
                onClick = switchCamera,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(token.spacing.md)
                    .size(cameraToken.secondaryButtonSize)
                    .background(
                        colors.surfaceOverlay,
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Outlined.SwitchCamera,
                    contentDescription = null,
                    modifier = Modifier.size(cameraToken.secondaryIconSize),
                    tint = colors.primary
                )
            }
        }
    }
}
