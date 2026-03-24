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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GIconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.hoabui.virtualbody3d.ui.theme.tokens.GymToken

/**
 * Bottom control row for Review state: [ X (back) ]  [ Send ]  (empty right).
 */
@Composable
fun ReviewControlRow(
    token: GymToken,
    buttonsEnabled: Boolean = true,
    onBackToCapture: () -> Unit,
    onConfirmPhoto: () -> Unit
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
            GIconButton(
                onClick = onBackToCapture,
                enabled = buttonsEnabled,
                modifier = Modifier
                    .size(cameraToken.secondaryButtonSize)
                    .clip(CircleShape)
                    .background(colors.surfaceOverlay)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
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
                        onClick = onConfirmPhoto,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = null,
                    modifier = Modifier.size(cameraToken.sendIconSize),
                    tint = colors.primary
                )
            }
        }

        Box(modifier = Modifier.weight(1f))
    }
}
