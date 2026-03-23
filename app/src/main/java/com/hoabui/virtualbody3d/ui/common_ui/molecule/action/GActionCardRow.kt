package com.hoabui.virtualbody3d.ui.common_ui.molecule.action

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.GymTheme

// ─────────────────────────────────────────────────────────────────────────────
// GActionCardRow
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Tappable settings / action row molecule.
 *
 * Replaces `SettingsRow` in [com.hoabui.virtualbody3d.ui.profile.ProfileScreen] and
 * all hand-rolled action list rows in exercise and body dashboard cards.
 *
 * ### Layout
 * ```
 * ┌────────────────── Surface (fillMaxWidth, token shape) ──────────────────────┐
 * │ [Icon]  Title text                            [trailing slot or chevron →]  │
 * │         Subtitle text (optional)                                             │
 * └─────────────────────────────────────────────────────────────────────────────┘
 * ```
 *
 * ### Interaction contract
 * - When [onClick] is non-null, the row is interactive: the [Surface] registers a
 *   click, the semantics role is set to [Role.Button], and a ripple is applied.
 * - When [onClick] is null, the row renders as a static (non-interactive) surface.
 *
 * ### Trailing slot
 * The default trailing slot is the `KeyboardArrowRight` chevron (tinted `textSecondary`).
 * Pass a custom [trailing] composable to replace it — e.g. a `Switch`, a badge, or nothing.
 * To suppress the trailing slot entirely, pass `trailing = {}`.
 *
 * @param title Primary row label. Rendered with `token.typography.bodyLarge`.
 * @param icon Optional leading [ImageVector] sized to 24 dp, tinted `token.colors.textSecondary`.
 * @param subtitle Optional secondary text below [title]. Rendered with `token.typography.bodySmall`.
 * @param onClick Tap handler. When `null`, the row is non-interactive.
 * @param trailing Optional slot after the title column. Defaults to a right-chevron icon.
 */
@Composable
fun GActionCardRow(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = DefaultTrailingChevron,
) {
    val token = GymTheme.token

    val interactionModifier = if (onClick != null) {
        Modifier.semantics { role = Role.Button }
    } else {
        Modifier
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(interactionModifier),
        onClick = onClick ?: {},
        enabled = onClick != null,
        color = token.colors.surface,
        shape = RoundedCornerShape(token.radius.sm),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = token.spacing.md, vertical = token.spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(token.spacing.md),
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = token.colors.textSecondary,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                GText(
                    text = title,
                    style = token.typography.bodyLarge,
                    color = token.colors.textPrimary,
                )
                if (subtitle != null) {
                    GText(
                        text = subtitle,
                        style = token.typography.bodySmall,
                        color = token.colors.textSecondary,
                    )
                }
            }
            trailing?.invoke()
        }
    }
}

// Default trailing content — chevron icon. Declared as a top-level val so it
// can be used as a default parameter without triggering "composable invocation
// inside non-composable" issues at call sites.
private val DefaultTrailingChevron: @Composable () -> Unit = {
    val token = GymTheme.token
    Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = null,
        modifier = Modifier.size(24.dp),
        tint = token.colors.textSecondary,
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "GActionCardRow — default chevron")
@Composable
private fun PreviewDefault() {
    GymTheme {
        val token = GymTheme.token
        Column(
            modifier = Modifier.padding(token.spacing.md),
            verticalArrangement = Arrangement.spacedBy(token.spacing.xxs),
        ) {
            GActionCardRow(
                title = "Thông tin cá nhân",
                icon = Icons.Default.Person,
                onClick = {},
            )
            GActionCardRow(
                title = "Thông báo",
                icon = Icons.Default.Notifications,
                onClick = {},
            )
            GActionCardRow(
                title = "Bảo mật",
                icon = Icons.Default.Lock,
                subtitle = "Mật khẩu & xác thực 2 bước",
                onClick = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "GActionCardRow — custom trailing + no icon")
@Composable
private fun PreviewCustomTrailing() {
    GymTheme {
        val token = GymTheme.token
        Column(
            modifier = Modifier.padding(token.spacing.md),
            verticalArrangement = Arrangement.spacedBy(token.spacing.xxs),
        ) {
            GActionCardRow(
                title = "Không có trailing",
                onClick = {},
                trailing = {},
            )
            GActionCardRow(
                title = "Non-interactive row",
                subtitle = "Không có sự kiện click",
                onClick = null,
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "GActionCardRow — dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewDark() {
    GymTheme(darkTheme = true) {
        val token = GymTheme.token
        Column(
            modifier = Modifier.padding(token.spacing.md),
            verticalArrangement = Arrangement.spacedBy(token.spacing.xxs),
        ) {
            GActionCardRow(
                title = "Cài đặt tài khoản",
                icon = Icons.Default.Person,
                onClick = {},
            )
            GActionCardRow(
                title = "Thông báo",
                icon = Icons.Default.Notifications,
                subtitle = "Bật tất cả thông báo",
                onClick = {},
            )
        }
    }
}
