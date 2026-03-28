package com.hoabui.virtualbody3d.ui.common_ui.atom.chip

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.GymTheme

// ─────────────────────────────────────────────────────────────────────────────
// GFilterChip
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Tokenized filter chip atom that bridges M3's [FilterChip] with [GymTheme].
 *
 * M3's `FilterChip` defaults to Material baseline purple tones for the selected state.
 * `GFilterChip` overrides all colour roles with `GymTheme.token.colors` so the chip
 * automatically matches the token palette (sage primary) in both light and dark modes.
 *
 * ### Colour contract
 * | State              | Container           | Label / Icon           | Border             |
 * |--------------------|---------------------|------------------------|--------------------|
 * | Selected           | `colors.primary`    | `colors.onPrimary`     | `colors.primary`   |
 * | Unselected         | `colors.surfaceSubtle` | `colors.textSecondary` | `colors.borderStrong` |
 * | Disabled selected  | `colors.primary` @ 0.38f | `colors.onPrimary` @ 0.38f | — |
 * | Disabled unselected| `colors.surfaceSubtle` @ 0.38f | `colors.textMuted` | — |
 *
 * ### Leading icon
 * When [leadingIcon] is `null` and [selected] = `true`, M3's `FilterChip` renders its
 * built-in animated checkmark. Pass a composable to override with a custom icon.
 *
 * @param label Chip label text. Rendered with [labelStyle] or `token.typography.labelMedium` by default.
 * @param selected Whether the chip is in the selected (active) state.
 * @param onSelectedChange Called with the new selected value on click.
 * @param leadingIcon Optional slot. When `null` and [selected] = `true`, M3 shows a checkmark.
 * @param labelStyle Typography for the chip label; defaults to `token.typography.labelMedium`.
 * @param enabled When `false`, the chip ignores clicks and renders at reduced opacity.
 */
@Composable
fun GFilterChip(
    label: String,
    selected: Boolean,
    onSelectedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
    labelStyle: TextStyle? = null,
) {
    val token = GymTheme.token
    val colors = token.colors
    val resolvedLabelStyle = labelStyle ?: token.typography.labelMedium

    FilterChip(
        selected = selected,
        onClick = { onSelectedChange(!selected) },
        label = {
            GText(
                text = label,
                style = resolvedLabelStyle,
                // Color will be inherited from FilterChip's LocalContentColor;
                // GText's explicit color parameter is not set here so M3 state
                // colours (selected/disabled) propagate automatically.
                color = if (selected) colors.onPrimary else colors.textSecondary,
            )
        },
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon,
        colors = FilterChipDefaults.filterChipColors(
            // ── Unselected ────────────────────────────────────────────────────
            containerColor = colors.surfaceSubtle,
            labelColor = colors.textSecondary,
            iconColor = colors.textSecondary,
            // ── Selected ──────────────────────────────────────────────────────
            selectedContainerColor = colors.primary,
            selectedLabelColor = colors.onPrimary,
            selectedLeadingIconColor = colors.onPrimary,
            // ── Disabled unselected ───────────────────────────────────────────
            disabledContainerColor = colors.surfaceSubtle.copy(alpha = 0.38f),
            disabledLabelColor = colors.textMuted.copy(alpha = 0.38f),
            disabledLeadingIconColor = colors.textMuted.copy(alpha = 0.38f),
            // ── Disabled selected ─────────────────────────────────────────────
            disabledSelectedContainerColor = colors.primary.copy(alpha = 0.38f),
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = enabled,
            selected = selected,
            borderColor = colors.borderStrong,
            selectedBorderColor = Color.Transparent,
            disabledBorderColor = colors.borderStrong.copy(alpha = 0.38f),
            disabledSelectedBorderColor = Color.Transparent,
            borderWidth = token.spacing.dividerThickness,
            selectedBorderWidth = token.spacing.dividerThickness,
        ),
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Preview(showBackground = true, name = "GFilterChip — States")
@Composable
private fun PreviewGFilterChipStates() {
    GymTheme {
        val token = GymTheme.token
        Column(
            modifier = Modifier.padding(token.spacing.md),
            verticalArrangement = Arrangement.spacedBy(token.spacing.md),
        ) {
            // Enabled states
            GText(
                text = "ENABLED",
                style = token.typography.labelSmall,
                color = token.colors.textMuted,
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(token.spacing.xs)) {
                GFilterChip(
                    label = "Unselected",
                    selected = false,
                    onSelectedChange = {},
                )
                GFilterChip(
                    label = "Selected",
                    selected = true,
                    onSelectedChange = {},
                )
                GFilterChip(
                    label = "With icon",
                    selected = true,
                    onSelectedChange = {},
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                        )
                    },
                )
                GFilterChip(
                    label = "Unselected icon",
                    selected = false,
                    onSelectedChange = {},
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                        )
                    },
                )
            }

            // Disabled states
            GText(
                text = "DISABLED",
                style = token.typography.labelSmall,
                color = token.colors.textMuted,
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(token.spacing.xs)) {
                GFilterChip(
                    label = "Disabled unselected",
                    selected = false,
                    onSelectedChange = {},
                    enabled = false,
                )
                GFilterChip(
                    label = "Disabled selected",
                    selected = true,
                    onSelectedChange = {},
                    enabled = false,
                )
            }

            // Interactive toggle demo
            GText(
                text = "INTERACTIVE",
                style = token.typography.labelSmall,
                color = token.colors.textMuted,
            )
            val difficulties = listOf("Beginner", "Intermediate", "Advanced")
            val selections = remember { mutableStateOf(setOf("Beginner")) }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(token.spacing.xs)) {
                difficulties.forEach { difficulty ->
                    val isSelected = difficulty in selections.value
                    GFilterChip(
                        label = difficulty,
                        selected = isSelected,
                        onSelectedChange = { selected ->
                            selections.value = if (selected) {
                                selections.value + difficulty
                            } else {
                                selections.value - difficulty
                            }
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Preview(
    showBackground = true,
    name = "GFilterChip — Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewGFilterChipDark() {
    GymTheme(darkTheme = true) {
        val token = GymTheme.token
        var selected by remember { mutableStateOf(false) }
        FlowRow(
            modifier = Modifier.padding(token.spacing.md),
            horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
        ) {
            GFilterChip(
                label = "Dark unselected",
                selected = false,
                onSelectedChange = {},
            )
            GFilterChip(
                label = "Dark selected",
                selected = true,
                onSelectedChange = {},
            )
            GFilterChip(
                label = "Toggle me",
                selected = selected,
                onSelectedChange = { selected = it },
            )
        }
    }
}
