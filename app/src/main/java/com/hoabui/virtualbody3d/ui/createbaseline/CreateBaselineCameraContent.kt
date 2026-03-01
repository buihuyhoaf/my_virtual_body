package com.hoabui.virtualbody3d.ui.createbaseline

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import java.io.File

/**
 * CameraX Preview + ImageCapture for Create Baseline.
 * Camera stays bound while this composable is in composition; unbind only in [DisposableEffect] onDispose (e.g. when a dialog is shown).
 * Capture is triggered by the "Capture Photo" button via [captureTrigger]; when [captureTrigger] > 0 and callbacks are set, takePicture() runs.
 */
@Composable
fun CreateBaselineCameraContent(
    modifier: Modifier = Modifier,
    captureTrigger: Int = 0,
    onImageCaptured: ((File) -> Unit)? = null,
    onCaptureError: ((String) -> Unit)? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val token = GymTheme.token
    val previewView = remember { PreviewView(context) }
    var useBackCamera by remember { mutableStateOf(true) }
    var imageCaptureRef by remember { mutableStateOf<ImageCapture?>(null) }
    val showShutter = onImageCaptured != null && onCaptureError != null

    fun takePicture() {
        val imageCapture = imageCaptureRef ?: run {
            onCaptureError?.invoke("Camera not ready")
            return
        }
        val file = File(context.cacheDir, "capture_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    onImageCaptured?.invoke(file)
                }
                override fun onError(exception: ImageCaptureException) {
                    onCaptureError?.invoke(exception.message ?: "Capture failed")
                }
            }
        )
    }

    DisposableEffect(lifecycleOwner, useBackCamera) {
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
            val selector = if (useBackCamera) CameraSelector.DEFAULT_BACK_CAMERA else CameraSelector.DEFAULT_FRONT_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    selector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                onCaptureError?.invoke(e.message ?: "Camera bind failed")
            }
        }, ContextCompat.getMainExecutor(context))

        onDispose {
            imageCaptureRef = null
            try {
                ProcessCameraProvider.getInstance(context).get().unbindAll()
            } catch (_: Exception) { }
        }
    }

    if (showShutter && captureTrigger > 0) {
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
                }
            }
        )
        IconButton(
            onClick = { useBackCamera = !useBackCamera },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .background(token.colors.surface.copy(alpha = 0.8f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Outlined.SwitchCamera,
                contentDescription = null,
                tint = token.colors.primary
            )
        }
    }
}
