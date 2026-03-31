package com.hoabui.virtualbody3d.ui.common_ui.atom.dialog

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButton
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButtonVariant
import com.hoabui.virtualbody3d.ui.common_ui.atom.card.GCard
import com.hoabui.virtualbody3d.ui.common_ui.atom.icon.GIcon
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun GDialog(
    onDismissRequest: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: @Composable (() -> Unit)? = null,
    buttons: @Composable ColumnScope.() -> Unit,
    properties: DialogProperties = DialogProperties(
        usePlatformDefaultWidth = true,
        dismissOnBackPress = true,
        dismissOnClickOutside = true,
    ),
    useEntranceAnimation: Boolean = false,
) {
    val token = GymTheme.token
    val entranceAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(
            durationMillis = if (useEntranceAnimation) token.motion.duration.standard else 0,
            easing = token.motion.easing.standard,
        ),
        label = "g_dialog_entrance_alpha",
    )
    val cardModifier = if (useEntranceAnimation) {
        modifier.graphicsLayer { alpha = entranceAlpha }
    } else {
        modifier
    }
    Dialog(onDismissRequest = onDismissRequest, properties = properties) {
        GCard(
            modifier = cardModifier,
            shape = RoundedCornerShape(token.radius.lg),
            containerColor = token.colors.surface,
            contentModifier = Modifier.padding(token.spacing.lg),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (icon != null) {
                    icon()
                }
                GText(
                    text = title,
                    style = token.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                )
                if (description != null) {
                    Spacer(modifier = Modifier.height(token.spacing.sm))
                    GText(
                        text = description,
                        style = token.typography.bodyMedium,
                        color = token.colors.textSecondary,
                        textAlign = TextAlign.Center,
                    )
                }
                Spacer(modifier = Modifier.height(token.spacing.xl))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(token.spacing.sm),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    content = buttons,
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "GDialog")
@Composable
private fun PreviewGDialog() {
    GymTheme {
        val token = GymTheme.token
        GDialog(
            onDismissRequest = {},
            title = "Confirm action",
            description = "This cannot be undone.",
            icon = {
                GIcon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    modifier = Modifier.size(token.spacing.iconMedium),
                    tint = token.colors.primary,
                )
            },
            buttons = {
                GButton(
                    text = "Continue",
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                )
                GButton(
                    text = "Cancel",
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    variant = GButtonVariant.Outlined,
                )
            },
        )
    }
}

@Preview(
    showBackground = true,
    name = "GDialog — dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewGDialogDark() {
    GymTheme(darkTheme = true) {
        val token = GymTheme.token
        GDialog(
            onDismissRequest = {},
            title = "Confirm action",
            description = "This cannot be undone.",
            icon = {
                GIcon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    modifier = Modifier.size(token.spacing.iconMedium),
                    tint = token.colors.primary,
                )
            },
            buttons = {
                GButton(
                    text = "Continue",
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                )
                GButton(
                    text = "Cancel",
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    variant = GButtonVariant.Outlined,
                )
            },
        )
    }
}
