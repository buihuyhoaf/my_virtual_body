package com.hoabui.virtualbody3d.ui.common_ui.molecule.section

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.GymTheme

// ─────────────────────────────────────────────────────────────────────────────
// GSectionHeader
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Section header molecule: bold title row with optional trailing action button.
 *
 * Replaces the legacy [com.hoabui.virtualbody3d.ui.components.SectionTitle] and
 * all hand-rolled `Text + TextButton` header rows scattered across feature screens.
 *
 * ### Layout
 * ```
 * ┌──────────────────────────────── fillMaxWidth ───────────────────────────────┐
 * │ Title (weight 1, ellipsis)                          [Action text] (optional) │
 * └─────────────────────────────────────────────────────────────────────────────┘
 * ```
 *
 * ### Rules
 * - Title typography: `token.typography.titleMedium`, color `token.colors.textPrimary`.
 * - Action text typography: `token.typography.labelLarge`, color `token.colors.primary`.
 * - When [actionText] is `null`, the trailing slot is omitted entirely (no extra space allocated).
 * - [onActionClick] is ignored when [actionText] is `null`.
 *
 * @param title Section title string.
 * @param actionText Optional label for the trailing action button (e.g. "See more").
 * @param onActionClick Invoked when the action button is tapped. Must be non-null when
 *   [actionText] is non-null (assertion via [require] in debug; silent no-op in release).
 */
@Composable
fun GSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
) {
    val token = GymTheme.token
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (actionText != null) Arrangement.SpaceBetween else Arrangement.Start,
    ) {
        GText(
            text = title,
            style = token.typography.titleMedium,
            color = token.colors.textPrimary,
            maxLines = 1,
            modifier = Modifier.weight(1f, fill = false),
        )
        if (actionText != null) {
            TextButton(onClick = { onActionClick?.invoke() }) {
                GText(
                    text = actionText,
                    style = token.typography.labelLarge,
                    color = token.colors.primary,
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "GSectionHeader — title only")
@Composable
private fun PreviewTitleOnly() {
    GymTheme {
        GSectionHeader(
            title = "Bài tập sắp tới",
            modifier = Modifier.padding(GymTheme.token.spacing.md),
        )
    }
}

@Preview(showBackground = true, name = "GSectionHeader — with action")
@Composable
private fun PreviewWithAction() {
    GymTheme {
        GSectionHeader(
            title = "Nhóm cơ",
            actionText = "Xem thêm",
            onActionClick = {},
            modifier = Modifier.padding(GymTheme.token.spacing.md),
        )
    }
}

@Preview(
    showBackground = true,
    name = "GSectionHeader — dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewDark() {
    GymTheme(darkTheme = true) {
        Column(modifier = Modifier.padding(GymTheme.token.spacing.md)) {
            GSectionHeader(title = "Phân tích cơ thể")
            GSectionHeader(title = "Lịch sử", actionText = "Tất cả", onActionClick = {})
        }
    }
}
