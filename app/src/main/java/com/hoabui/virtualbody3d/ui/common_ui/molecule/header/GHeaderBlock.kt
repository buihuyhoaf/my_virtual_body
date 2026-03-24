package com.hoabui.virtualbody3d.ui.common_ui.molecule.header

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.GymTheme

/**
 * Page-level title + optional subtitle block.
 * Use [GSectionHeader] for inline section titles with optional action links.
 * Use [GActionCardRow] for clickable rows with leading icons.
 */
@Composable
fun GHeaderBlock(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    contentAlignment: Alignment.Horizontal = Alignment.Start,
) {
    val token = GymTheme.token
    Column(
        modifier = modifier,
        horizontalAlignment = contentAlignment,
        verticalArrangement = Arrangement.spacedBy(token.spacing.xxs),
    ) {
        GText(
            text = title,
            style = token.typography.titleMedium,
            color = token.colors.textPrimary,
        )
        if (subtitle != null) {
            GText(
                text = subtitle,
                style = token.typography.bodyMedium,
                color = token.colors.textSecondary,
            )
        }
    }
}
