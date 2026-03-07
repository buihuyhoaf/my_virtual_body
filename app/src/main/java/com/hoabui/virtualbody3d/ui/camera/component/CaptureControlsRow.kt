package com.hoabui.virtualbody3d.ui.camera.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.outlined.SwitchCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.hoabui.virtualbody3d.ui.theme.tokens.GymToken

/**
 * Bottom control row for Capture state: [ Gallery ]  [ Shutter ]  [ Rotate ].
 */
@Composable
fun CaptureControlsRow(
    token: GymToken,
    buttonsEnabled: Boolean = true,
    onGallery: () -> Unit,
    onCapture: () -> Unit,
    onRotate: () -> Unit
) {
    val colors = token.colors
    val cameraToken = token.camera
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            IconButton(
                onClick = onGallery,
                enabled = buttonsEnabled,
                modifier = Modifier
                    .size(cameraToken.secondaryButtonSize)
                    .clip(CircleShape)
                    .background(colors.surfaceOverlay)
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = null,
                    modifier = Modifier.size(cameraToken.secondaryIconSize),
                    tint = colors.primary
                )
            }
        }

        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(cameraToken.primaryButtonSize)
                    .clip(CircleShape)
                    .border(cameraToken.primaryButtonBorderWidth, colors.primary, CircleShape)
                    .background(Color.White)
                    .clickable(
                        enabled = buttonsEnabled,
                        onClick = onCapture,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    )
            )
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterEnd
        ) {
            IconButton(
                onClick = onRotate,
                enabled = buttonsEnabled,
                modifier = Modifier
                    .size(cameraToken.secondaryButtonSize)
                    .clip(CircleShape)
                    .background(colors.surfaceOverlay)
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
