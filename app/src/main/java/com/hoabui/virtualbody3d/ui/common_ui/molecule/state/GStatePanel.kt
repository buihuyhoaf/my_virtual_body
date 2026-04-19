package com.hoabui.virtualbody3d.ui.common_ui.molecule.state

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButton
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.GymTheme

/**
 * Unified panel for Empty, Error, and Success states.
 *
 * Layout: centred column with optional icon, title, optional subtitle, and optional CTA button.
 * Fills its parent via [fillMaxSize].
 *
 * SuccessOverlay is intentionally NOT migrated to this component because it has
 * tightly-coupled entrance animations (scale + alpha via [Animatable]) that would
 * over-engineer this generic molecule.
 *
 * @param title Primary state message.
 * @param subtitle Optional secondary line shown below [title].
 * @param icon Optional composable slot for an icon or illustration above [title].
 * @param actionText Label for the optional CTA button. Requires [onActionClick].
 * @param onActionClick Called when the CTA button is tapped.
 */
@Composable
fun GStatePanel(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: (@Composable () -> Unit)? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    val token = GymTheme.token
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(token.radius.lg)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(token.spacing.xs),
        ) {
            if (icon != null) {
                icon()
            }
            GText(
                text = title,
                textAlign = TextAlign.Center,
                style = token.typography.titleMedium,
                color = token.colors.textPrimary,
            )
            if (subtitle != null) {
                GText(
                    text = subtitle,
                    textAlign = TextAlign.Center,
                    style = token.typography.bodyMedium,
                    color = token.colors.textSecondary,
                )
            }
            if (actionText != null && onActionClick != null) {
                GButton(
                    text = actionText,
                    onClick = onActionClick,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "GStatePanel — light")
@Composable
private fun PreviewGStatePanelLight() {
    GymTheme {
        val token = GymTheme.token
        GStatePanel(
            title = "No messages yet",
            subtitle = "Start a conversation to see it here.",
            icon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = token.colors.textSecondary,
                )
            },
            actionText = "Refresh",
            onActionClick = {},
        )
    }
}

@Preview(
    showBackground = true,
    name = "GStatePanel — dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewGStatePanelDark() {
    GymTheme(darkTheme = true) {
        val token = GymTheme.token
        GStatePanel(
            title = "Something went wrong",
            subtitle = "Please try again.",
            icon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = token.colors.textSecondary,
                )
            },
        )
    }
}
