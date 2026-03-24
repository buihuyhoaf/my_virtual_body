package com.hoabui.virtualbody3d.ui.messages.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.molecule.state.GStatePanel

@Composable
fun MessagesEmptyState(modifier: Modifier = Modifier) {
    GStatePanel(
        title = stringResource(R.string.messages_empty_title),
        subtitle = stringResource(R.string.messages_empty_subtitle),
        modifier = modifier,
    )
}
