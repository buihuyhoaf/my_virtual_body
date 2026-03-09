package com.hoabui.virtualbody3d.ui.scanresult

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.core.base.UiState
import com.hoabui.virtualbody3d.domain.model.BodyScanResult
import com.hoabui.virtualbody3d.ui.body.state.BodyScreenState
import com.hoabui.virtualbody3d.ui.body.components.HeroSection
import com.hoabui.virtualbody3d.ui.body.state.toUiState
import com.hoabui.virtualbody3d.ui.body.viewmodel.BodyViewModel
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
    val token = GymTheme.token
    val screenState by viewModel.state.collectAsStateWithLifecycle()

    if (screenState is UiState.Loading) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(token.colors.background)
        )
    } else if (screenState is UiState.Error) {
        val errorState = screenState as UiState.Error
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(token.colors.background),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = errorState.message,
                color = token.colors.textSecondary
            )
        }
    } else if (screenState is UiState.Success<*>) {
        val successState = screenState as UiState.Success<BodyScreenState>
        BodyScanResultScreenContent(
            modifier = modifier,
            scanResult = successState.data.scanResult,
            onBeginClick = onBeginClick,
            onBackClick = onBackClick
        )
    }
}

@Composable
fun BodyScanResultScreenContent(
    modifier: Modifier = Modifier,
    scanResult: BodyScanResult,
    onBeginClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val token = GymTheme.token
    val onboardingTokens = token.onboarding
    val colors = token.colors
    val radius = token.radius
    val elevation = token.elevation

    val uiState = scanResult.toUiState()
    val bodyScore = ((uiState.bmiScalePosition ?: 0.76f) * 100f).toInt().coerceIn(0, 100)

    Column(
        modifier = modifier
            .fillMaxSize()
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
            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier
                    .weight(1f)
                    .height(onboardingTokens.primaryButtonHeight),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = colors.primary
                ),
                shape = RoundedCornerShape(radius.lg),
                border = BorderStroke(1.dp, colors.primary)
            ) {
                Text(
                    text = stringResource(R.string.body_scan_result_back),
                    style = token.typography.titleMedium
                )
            }
            Button(
                onClick = onBeginClick,
                modifier = Modifier
                    .weight(1f)
                    .height(onboardingTokens.primaryButtonHeight),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    disabledContainerColor = colors.surfaceSubtle,
                    disabledContentColor = colors.textMuted
                ),
                shape = RoundedCornerShape(radius.lg),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = elevation.level0)
            ) {
                Text(
                    text = stringResource(R.string.initial_setup_begin),
                    style = token.typography.titleMedium
                )
            }
        }
    }
}

