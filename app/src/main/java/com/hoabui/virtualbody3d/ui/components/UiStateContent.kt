package com.hoabui.virtualbody3d.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.common_ui.atom.progress.GCircularProgress
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hoabui.virtualbody3d.core.base.UiState
import com.hoabui.virtualbody3d.ui.theme.GymTheme

/**
 * Common wrapper that renders Loading / Error / Success UI from [UiState].
 * Use for screens that follow the full three-branch pattern.
 *
 * @param state Current [UiState] from ViewModel
 * @param modifier Modifier applied to the root (and passed to each branch)
 * @param loadingContent Default: full-screen background + [CircularProgressIndicator]
 * @param errorContent Default: full-screen background + centered [Text] with message
 * @param successContent Required: content when [UiState.Success] with [T] data
 */
@Composable
fun <T> UiStateContent(
    state: UiState<T>,
    modifier: Modifier = Modifier,
    loadingContent: @Composable (Modifier) -> Unit = { mod -> DefaultLoading(mod) },
    errorContent: @Composable (Modifier, String) -> Unit = { mod, message -> DefaultError(mod, message) },
    successContent: @Composable (Modifier, T) -> Unit
) {
    when (state) {
        is UiState.Loading -> loadingContent(modifier)
        is UiState.Error -> errorContent(modifier, state.message)
        is UiState.Success -> successContent(modifier, state.data)
    }
}

@Composable
private fun DefaultLoading(modifier: Modifier) {
    val token = GymTheme.token
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(token.colors.background),
        contentAlignment = Alignment.Center
    ) {
        GCircularProgress()
    }
}

@Composable
private fun DefaultError(modifier: Modifier, message: String) {
    val token = GymTheme.token
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(token.colors.background),
        contentAlignment = Alignment.Center
    ) {
        GText(
            text = message,
            color = token.colors.textSecondary
        )
    }
}
