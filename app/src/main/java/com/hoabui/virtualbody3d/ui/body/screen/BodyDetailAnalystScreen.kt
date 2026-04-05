package com.hoabui.virtualbody3d.ui.body.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.molecule.topbar.GTopBar
import com.hoabui.virtualbody3d.ui.common_ui.molecule.topbar.GTopBarBackIcon
import com.hoabui.virtualbody3d.ui.common_ui.organism.scaffold.GScaffold
import com.hoabui.virtualbody3d.ui.body.viewmodel.BodyViewModel
import com.hoabui.virtualbody3d.ui.components.UiStateContent
import com.hoabui.virtualbody3d.ui.theme.GymTheme

/**
 * Body detail analyst screen. Placeholder content for now.
 * Navigated from the home body section.
 */
@Composable
fun BodyDetailAnalystScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BodyViewModel = hiltViewModel()
) {
    val screenState by viewModel.state.collectAsStateWithLifecycle()

    UiStateContent(
        state = screenState,
        modifier = modifier,
        successContent = { mod, data ->
            val token = GymTheme.token
            GScaffold(
                modifier = mod,
                topBar = {
                    GTopBar(
                        title = stringResource(R.string.body_detail_analyst_title),
                        windowInsets = WindowInsets(0),
                        navigationIcon = { GTopBarBackIcon(onBack = onBack) }
                    )
                }
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(token.spacing.md),
                    contentAlignment = Alignment.Center
                ) {
                    GText(
                        text = stringResource(R.string.body_detail_analyst_placeholder),
                        style = token.typography.bodyMedium,
                        color = token.colors.textSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    )
}
