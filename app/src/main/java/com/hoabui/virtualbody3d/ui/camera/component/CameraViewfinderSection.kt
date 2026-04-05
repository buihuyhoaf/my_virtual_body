package com.hoabui.virtualbody3d.ui.camera.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.hoabui.virtualbody3d.ui.theme.tokens.GymToken
import java.io.File

/**
 * Camera viewfinder area: live preview or placeholder (solid background when no camera).
 * Used inside [CameraCaptureScreenContent].
 */
@Composable
fun CameraViewfinderSection(
    modifier: Modifier,
    token: GymToken,
    showCamera: Boolean = true,
    captureTrigger: Int = 0,
    onImageCaptured: (File) -> Unit = {},
    onCaptureError: (String) -> Unit = {},
    useBackCamera: Boolean? = null,
    onSwitchCamera: (() -> Unit)? = null
) {
    val cameraTokens = token.camera
    val radius = token.radius
    val colors = token.colors
    val cameraAspectRatio =
        if (showCamera) 9f / 16f else cameraTokens.placeholderViewfinderAspectRatio

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(radius.lg)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(cameraAspectRatio)
                .clip(RoundedCornerShape(radius.lg))
        ) {
            if (showCamera) {
                CameraCaptureContent(
                    modifier = Modifier.fillMaxSize(),
                    token = token,
                    captureTrigger = captureTrigger,
                    onImageCaptured = onImageCaptured,
                    onCaptureError = onCaptureError,
                    useBackCamera = useBackCamera,
                    onSwitchCamera = onSwitchCamera
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colors.surfaceSubtle)
                )
            }
        }
    }
}


