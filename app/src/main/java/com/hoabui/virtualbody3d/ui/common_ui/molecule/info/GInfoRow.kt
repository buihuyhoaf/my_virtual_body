package com.hoabui.virtualbody3d.ui.common_ui.molecule.info

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.GymTheme

// ─────────────────────────────────────────────────────────────────────────────
// GInfoRow
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Info row molecule: optional leading slot, a label, an expandable value, and an optional trailing slot.
 *
 * Replaces `ExerciseInfoRow` in [com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseDetailDialog]
 * and all hand-rolled label/value pairs scattered across Body Analysis and Profile feature cards.
 *
 * ### Layout
 * ```
 * ┌─────────────────────────────── fillMaxWidth ──────────────────────────────┐
 * │ [leading]  Label text   Value text (weight 1, ellipsis)  [trailing]       │
 * └───────────────────────────────────────────────────────────────────────────┘
 * ```
 *
 * ### Accessibility
 * By default the row is a merged semantics node so TalkBack announces
 * "[leading description] label value [trailing description]" as a single unit.
 * Pass [mergeDescendants] = `false` if the trailing slot needs its own focus node
 * (e.g., an interactive button).
 *
 * @param label Short descriptor (e.g. "Nhóm cơ chính").
 * @param value Resolved string value (e.g. "Ngực, Vai trước").
 * @param leading Optional composable slot at the start. Commonly an [Icon] sized
 *   to `token.spacing.md` (16 dp), tinted to `token.colors.textSecondary`.
 * @param trailing Optional composable slot at the end (e.g. a badge or action icon).
 * @param mergeDescendants Whether TalkBack merges all children into one node.
 */
@Composable
fun GInfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    mergeDescendants: Boolean = true,
) {
    val token = GymTheme.token
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (mergeDescendants) Modifier.semantics(mergeDescendants = true) {}
                else Modifier
            ),
        horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leading != null) {
            leading()
        }
        GText(
            text = label,
            style = token.typography.labelMedium,
            color = token.colors.textSecondary,
        )
        GText(
            text = value,
            style = token.typography.bodyMedium,
            color = token.colors.textPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false),
        )
        if (trailing != null) {
            trailing()
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "GInfoRow — label + value")
@Composable
private fun PreviewBasic() {
    GymTheme {
        GInfoRow(
            label = "Vùng cơ",
            value = "Ngực",
            modifier = Modifier.padding(GymTheme.token.spacing.md),
        )
    }
}

@Preview(showBackground = true, name = "GInfoRow — with leading icon")
@Composable
private fun PreviewWithIcon() {
    GymTheme {
        val token = GymTheme.token
        Column(
            modifier = Modifier.padding(token.spacing.md),
            verticalArrangement = Arrangement.spacedBy(token.spacing.xs),
        ) {
            GInfoRow(
                label = "Vùng cơ",
                value = "Ngực",
                leading = {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = null,
                        modifier = Modifier.size(token.spacing.md),
                        tint = token.colors.textSecondary,
                    )
                },
            )
            GInfoRow(
                label = "Dụng cụ",
                value = "Tạ đòn",
                leading = {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = null,
                        modifier = Modifier.size(token.spacing.md),
                        tint = token.colors.textSecondary,
                    )
                },
            )
            GInfoRow(
                label = "Cơ chính",
                value = "Ngực lớn, Vai trước, Tam đầu",
                leading = {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        modifier = Modifier.size(token.spacing.md),
                        tint = token.colors.textSecondary,
                    )
                },
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "GInfoRow — dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewDark() {
    GymTheme(darkTheme = true) {
        val token = GymTheme.token
        GInfoRow(
            label = "Cơ chính",
            value = "Lưng rộng, Nhị đầu",
            leading = {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    modifier = Modifier.size(token.spacing.md),
                    tint = token.colors.textSecondary,
                )
            },
            modifier = Modifier.padding(token.spacing.md),
        )
    }
}
