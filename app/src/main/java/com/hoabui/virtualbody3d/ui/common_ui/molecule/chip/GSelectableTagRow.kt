package com.hoabui.virtualbody3d.ui.common_ui.molecule.chip

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hoabui.virtualbody3d.ui.common_ui.atom.chip.GFilterChip
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.GymTheme

// ─────────────────────────────────────────────────────────────────────────────
// Model
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Domain-free tag descriptor for [GSelectableTagRow].
 *
 * @param id Stable unique key used for selection state and list keying.
 * @param label Human-readable chip label.
 */
data class GTagOption(val id: String, val label: String)

// ─────────────────────────────────────────────────────────────────────────────
// GSelectableTagRow
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Selectable tag row molecule: wrapping [FlowRow] of [GFilterChip]s with an optional section label.
 *
 * Replaces `ExerciseFilterChips` in
 * [com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseLibraryComponents]
 * and all hand-rolled `FlowRow + FilterChip` groups.
 *
 * ### Behavior
 * - **Multi-select** (`singleSelect = false`): toggling a chip adds/removes its [GTagOption.id]
 *   from [selected]. Any number of chips may be active simultaneously.
 * - **Single-select** (`singleSelect = true`): selecting a chip deselects all others. Tapping
 *   an already-selected chip keeps it selected (at least one is always active).
 *
 * ### Layout
 * ```
 * [Title label]       ← optional, labelMedium / textSecondary
 * ┌──────── FlowRow (wraps at max width) ────────────────┐
 * │ [Chip A]  [Chip B]  [Chip C]                         │
 * │ [Chip D]  [Chip E]                                   │
 * └──────────────────────────────────────────────────────┘
 * ```
 *
 * @param options Full list of selectable tag options.
 * @param selected Set of [GTagOption.id] values that are currently selected.
 * @param onToggle Called with the toggled [GTagOption.id]. The caller is responsible for
 *   updating [selected]; this component is fully controlled.
 * @param title Optional section label rendered above the [FlowRow].
 * @param singleSelect When `true`, forces radio-button semantics (one active at a time).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GSelectableTagRow(
    options: List<GTagOption>,
    selected: Set<String>,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    singleSelect: Boolean = false,
) {
    val token = GymTheme.token
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(token.spacing.xxs),
    ) {
        if (title != null) {
            GText(
                text = title,
                style = token.typography.labelMedium,
                color = token.colors.textSecondary,
            )
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
            verticalArrangement = Arrangement.spacedBy(token.spacing.xxs),
        ) {
            options.forEach { option ->
                val isSelected = option.id in selected
                GFilterChip(
                    label = option.label,
                    selected = isSelected,
                    onSelectedChange = { nowSelected ->
                        if (singleSelect) {
                            // For single-select, always notify the new selection;
                            // the caller decides if de-selection is allowed.
                            if (nowSelected || !isSelected) onToggle(option.id)
                        } else {
                            onToggle(option.id)
                        }
                    },
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Previews
// ─────────────────────────────────────────────────────────────────────────────

private val previewBodyRegions = listOf(
    GTagOption("ALL", "Tất cả"),
    GTagOption("CHEST", "Ngực"),
    GTagOption("BACK", "Lưng"),
    GTagOption("LEGS", "Chân"),
    GTagOption("SHOULDERS", "Vai"),
    GTagOption("ARMS", "Tay"),
    GTagOption("CORE", "Lõi"),
)

private val previewDifficulty = listOf(
    GTagOption("BEGINNER", "Người mới"),
    GTagOption("INTERMEDIATE", "Trung cấp"),
    GTagOption("ADVANCED", "Nâng cao"),
)

@Preview(showBackground = true, name = "GSelectableTagRow — multi-select")
@Composable
private fun PreviewMultiSelect() {
    GymTheme {
        val token = GymTheme.token
        var selected by remember { mutableStateOf(setOf("CHEST", "BACK")) }
        GSelectableTagRow(
            options = previewBodyRegions,
            selected = selected,
            onToggle = { id ->
                selected = if (id in selected) selected - id else selected + id
            },
            title = "Vùng cơ",
            modifier = Modifier.padding(token.spacing.md),
        )
    }
}

@Preview(showBackground = true, name = "GSelectableTagRow — single-select")
@Composable
private fun PreviewSingleSelect() {
    GymTheme {
        val token = GymTheme.token
        var selected by remember { mutableStateOf(setOf("BEGINNER")) }
        GSelectableTagRow(
            options = previewDifficulty,
            selected = selected,
            onToggle = { id -> selected = setOf(id) },
            title = "Độ khó",
            singleSelect = true,
            modifier = Modifier.padding(token.spacing.md),
        )
    }
}

@Preview(
    showBackground = true,
    name = "GSelectableTagRow — dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewDark() {
    GymTheme(darkTheme = true) {
        val token = GymTheme.token
        var selected by remember { mutableStateOf(setOf("CHEST")) }
        Column(
            modifier = Modifier.padding(token.spacing.md),
            verticalArrangement = Arrangement.spacedBy(token.spacing.md),
        ) {
            GSelectableTagRow(
                options = previewBodyRegions,
                selected = selected,
                onToggle = { id ->
                    selected = if (id in selected) selected - id else selected + id
                },
                title = "Vùng cơ",
            )
        }
    }
}
