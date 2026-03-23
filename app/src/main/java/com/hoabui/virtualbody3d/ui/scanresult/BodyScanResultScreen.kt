package com.hoabui.virtualbody3d.ui.scanresult

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.body.BodyScanResult
import com.hoabui.virtualbody3d.ui.body.state.BodyScreenState
import com.hoabui.virtualbody3d.ui.body.components.HeroSection
import com.hoabui.virtualbody3d.ui.body.state.toUiState
import com.hoabui.virtualbody3d.ui.body.viewmodel.BodyViewModel
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButton
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButtonVariant
import com.hoabui.virtualbody3d.ui.common_ui.organism.scaffold.GScaffold
import com.hoabui.virtualbody3d.ui.components.UiStateContent
import com.hoabui.virtualbody3d.ui.theme.GymTheme

/**
 * Read-only body analysis result screen (InBody-style).
 * HeroSection + MetricsPanel + bottom actions (Bắt đầu, Quay lại).
 */
@Composable
fun BodyScanResultScreen(
    modifier: Modifier = Modifier,
    viewModel: BodyViewModel = hiltViewModel(),
    onBeginClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val screenState by viewModel.state.collectAsStateWithLifecycle()

    UiStateContent(
        state = screenState,
        modifier = modifier,
        successContent = { mod, data ->
            BodyScanResultScreenContent(
                modifier = mod,
                scanResult = data.scanResult,
                onBeginClick = onBeginClick,
                onBackClick = onBackClick
            )
        }
    )
}

@Composable
fun BodyScanResultScreenContent(
    modifier: Modifier = Modifier,
    scanResult: BodyScanResult,
    onBeginClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val token = GymTheme.token

    val uiState = scanResult.toUiState()
    val bodyScore = ((uiState.bmiScalePosition ?: 0.76f) * 100f).toInt().coerceIn(0, 100)

    GScaffold(modifier = modifier) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(token.spacing.md)
        ) {
            HeroSection(
                modifier = Modifier.weight(0.45f),
                uiState = uiState,
                bodyScore = bodyScore,
            )
            MetricsPanel(
                modifier = Modifier.weight(0.45f),
                scanResult = scanResult
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = token.spacing.lg),
                horizontalArrangement = Arrangement.spacedBy(token.spacing.md)
            ) {
                GButton(
                    text = stringResource(R.string.body_scan_result_back),
                    onClick = onBackClick,
                    modifier = Modifier.weight(1f),
                    variant = GButtonVariant.Outlined
                )
                GButton(
                    text = stringResource(R.string.initial_setup_begin),
                    onClick = onBeginClick,
                    modifier = Modifier.weight(1f),
                    variant = GButtonVariant.Primary
                )
            }
        }
    }
}

