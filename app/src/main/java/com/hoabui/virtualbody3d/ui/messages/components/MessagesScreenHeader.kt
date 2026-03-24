package com.hoabui.virtualbody3d.ui.messages.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.common_ui.molecule.header.GHeaderBlock
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun MessagesScreenHeader(
    modifier: Modifier = Modifier,
    showTitle: Boolean = false
) {
    val token = GymTheme.token
    if (showTitle) {
        GHeaderBlock(
            title = stringResource(R.string.messages_screen_title),
            subtitle = stringResource(R.string.messages_screen_subtitle),
            modifier = modifier.fillMaxWidth(),
        )
    } else {
        GText(
            text = stringResource(R.string.messages_screen_subtitle),
            style = token.typography.bodyMedium,
            color = token.colors.textSecondary,
            modifier = modifier.fillMaxWidth(),
        )
    }
}
