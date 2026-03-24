package com.hoabui.virtualbody3d.ui.messages.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hoabui.virtualbody3d.ui.common_ui.molecule.state.GStatePanel

@Composable
fun MessagesErrorState(
    message: String,
    modifier: Modifier = Modifier
) {
    GStatePanel(
        title = message,
        modifier = modifier,
    )
}
