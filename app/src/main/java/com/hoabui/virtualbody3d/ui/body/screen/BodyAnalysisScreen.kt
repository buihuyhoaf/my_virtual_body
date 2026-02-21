package com.hoabui.virtualbody3d.ui.body.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoabui.virtualbody3d.core.extensions.formatMeasurement
import com.hoabui.virtualbody3d.core.extensions.formatPercent
import com.hoabui.virtualbody3d.ui.body.state.BodyTab
import com.hoabui.virtualbody3d.ui.body.state.BodyUiState
import com.hoabui.virtualbody3d.ui.body.viewmodel.BodyViewModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun BodyAnalysisRoute(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit = {},
    viewModel: BodyViewModel = hiltViewModel()
) {
    val screenState = viewModel.screenState.collectAsStateWithLifecycle().value
    BodyAnalysisScreen(
        uiState = screenState.uiState,
        selectedTab = screenState.selectedTab,
        title = screenState.title,
        onBackClick = onBackClick,
        onSettingsClick = onSettingsClick,
        onTabSelected = viewModel::onTabSelected,
        modifier = Modifier.fillMaxSize()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyAnalysisScreen(
    uiState: BodyUiState,
    selectedTab: BodyTab,
    title: String,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onTabSelected: (BodyTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    val bodyScore = ((uiState.bmiScalePosition ?: 0.76f) * 100f).toInt().coerceIn(0, 100)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            BodyTopBar(
                title = title,
                onBackClick = onBackClick,
                onSettingsClick = onSettingsClick,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(
                        horizontal = bodyToken.topBarHorizontalPadding,
                        vertical = bodyToken.topBarVerticalPadding
                    )
            )
        },
        bottomBar = {
            BodyBottomBar(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            BodyPreviewSection(
                uiState = uiState,
                bodyScore = bodyScore,
                modifier = Modifier
                    .weight(0.6f)
            )
            AnalysisPanel(
                uiState = uiState,
                bodyScore = bodyScore,
                modifier = Modifier
                    .weight(0.4f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BodyTopBar(
    title: String,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = bodyToken.topBarInnerHorizontalPadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(token.radius.lg),
            color = token.colors.surfaceOverlay,
            border = androidx.compose.foundation.BorderStroke(
                bodyToken.topBarBorderWidth,
                token.colors.surfaceBorder
            )
        ) {
            IconButton(onClick = onBackClick, modifier = Modifier.size(bodyToken.topBarIconSize)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Surface(
            shape = RoundedCornerShape(token.radius.lg),
            color = token.colors.primary,
            shadowElevation = bodyToken.topBarActionElevation
        ) {
            IconButton(onClick = onSettingsClick, modifier = Modifier.size(bodyToken.topBarIconSize)) {
                Icon(
                    imageVector = Icons.Default.PersonSearch,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun BodyPreviewSection(
    uiState: BodyUiState,
    bodyScore: Int,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    Box(
        modifier = modifier
            .background(
                brush = Brush.radialGradient(
                    center = Offset(0.5f, 0.5f),
                    radius = 1.2f,
                    colors = listOf(token.colors.primarySoft, MaterialTheme.colorScheme.surface)
                )
            )
    ) {
        BodyModelPreview(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = bodyToken.previewModelTopPadding)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            token.colors.backgroundTransparent,
                            token.colors.backgroundScrim
                        )
                    )
                )
        )
        BodyScoreChip(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    top = bodyToken.scoreChipTopPadding,
                    start = bodyToken.metricChipSidePadding
                ),
            score = bodyScore
        )
        FloatingMetricChip(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    top = bodyToken.metricChipFirstRowTopPadding,
                    start = bodyToken.metricChipSidePadding
                ),
            icon = Icons.Default.Straighten,
            value = uiState.height.formatMeasurement(uiState.heightUnit)
        )
        FloatingMetricChip(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    top = bodyToken.metricChipSecondRowTopPadding,
                    start = bodyToken.metricChipSidePadding
                ),
            icon = Icons.Default.MonitorWeight,
            value = uiState.weight.formatMeasurement(uiState.weightUnit)
        )
        FloatingMetricChip(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    top = bodyToken.scoreChipTopPadding,
                    end = bodyToken.metricChipSidePadding
                ),
            icon = Icons.Default.Opacity,
            value = uiState.bodyFat.formatPercent()
        )
        FloatingMetricChip(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    top = bodyToken.metricChipFirstRowTopPadding,
                    end = bodyToken.metricChipSidePadding
                ),
            icon = Icons.Default.FitnessCenter,
            value = uiState.muscleMass.formatMeasurement(uiState.muscleMassUnit)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = bodyToken.previewTrackBottomPadding)
                .widthIn(max = bodyToken.previewTrackMaxWidth)
                .height(bodyToken.previewTrackHeight)
                .background(
                    token.colors.previewTrack,
                    RoundedCornerShape(token.radius.sm)
                )
        )
    }
}

@Composable
private fun BodyBottomBar(
    selectedTab: BodyTab,
    onTabSelected: (BodyTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(token.controlPanel.panelHeight)
            .background(MaterialTheme.colorScheme.surface)
            .border(bodyToken.bottomBarBorderWidth, token.colors.outlineSoft)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = token.controlPanel.horizontalPadding),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BodyTab.entries.forEach { tab ->
                val selected = selectedTab == tab
                Column(
                    modifier = Modifier
                        .clickable { onTabSelected(tab) }
                        .padding(bodyToken.bottomBarItemPadding)
                        .then(
                            if (selected) Modifier
                                .background(
                                    token.colors.primarySelected,
                                    RoundedCornerShape(token.radius.lg)
                                )
                                .padding(
                                    horizontal = bodyToken.bottomBarSelectedHorizontalPadding,
                                    vertical = bodyToken.bottomBarSelectedVerticalPadding
                                )
                            else Modifier
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = stringResource(tab.labelResId),
                        tint = if (selected) token.colors.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(bodyToken.bottomBarLabelTopSpacing))
                    Text(
                        text = stringResource(tab.labelResId),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = if (selected) token.colors.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
