package com.hoabui.virtualbody3d.ui.body.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.body.components.DashboardPanel
import com.hoabui.virtualbody3d.ui.body.components.HeroSection
import com.hoabui.virtualbody3d.ui.body.screen.BodyModelPreview
import com.hoabui.virtualbody3d.ui.body.state.BodyDashboardUiState
import com.hoabui.virtualbody3d.ui.body.state.BodyRegion
import com.hoabui.virtualbody3d.ui.body.state.BodyUiState
import com.hoabui.virtualbody3d.ui.body.viewmodel.BodyViewModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import java.time.LocalDate

@Composable
fun BodyAnalysisRoute(
    viewModel: BodyViewModel = hiltViewModel()
) {
    val screenState by viewModel.screenState.collectAsStateWithLifecycle()
    BodyAnalysisScreen(
        modifier = Modifier.fillMaxSize(),
        uiState = screenState.uiState,
        dashboardUiState = screenState.dashboardUiState,
        selectedDate = screenState.selectedDate ?: LocalDate.now(),
        selectedRegion = screenState.selectedRegion,
        onRegionSelected = viewModel::onRegionSelected
    )
}

@Composable
fun BodyAnalysisScreen(
    modifier: Modifier = Modifier,
    uiState: BodyUiState,
    dashboardUiState: BodyDashboardUiState,
    selectedDate: LocalDate,
    selectedRegion: BodyRegion?,
    onRegionSelected: (BodyRegion) -> Unit
) {
    val token = GymTheme.token
    val bodyScore = ((uiState.bmiScalePosition ?: 0.76f) * 100f).toInt().coerceIn(0, 100)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(token.spacing.md)
    ) {
        HeroSection(
            modifier = Modifier.weight(0.5f),
            uiState = uiState,
            bodyScore = bodyScore
        )
        BodyRegionRow(
            modifier = Modifier.weight(0.12f),
            selectedRegion = selectedRegion,
            onRegionSelected = onRegionSelected
        )
        DashboardPanel(
            modifier = Modifier.weight(0.38f),
            selectedDate = selectedDate,
            nutritionSummary = dashboardUiState.nutrition,
            meals = dashboardUiState.meals
        )
    }
}

@Composable
private fun BodyRegionRow(
    modifier: Modifier = Modifier,
    selectedRegion: BodyRegion?,
    onRegionSelected: (BodyRegion) -> Unit
) {
    val token = GymTheme.token
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(token.spacing.md),
        contentPadding = PaddingValues(horizontal = token.spacing.xs)
    ) {
        items(
            items = BodyRegion.entries,
            key = { it.name }
        ) { region ->
            BodyRegionItem(
                region = region,
                isSelected = selectedRegion == region,
                onClick = { onRegionSelected(region) }
            )
        }
    }
}

@Composable
private fun BodyRegionItem(
    region: BodyRegion,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    val surfaceColor = if (isSelected) token.colors.primarySoft else token.colors.surfaceOverlay
    val borderColor = if (isSelected) token.colors.primary else token.colors.surfaceBorder

    Surface(
        modifier = Modifier
            .width(bodyToken.bodyRegionItemWidth)
            .height(bodyToken.bodyRegionItemHeight)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(token.radius.lg),
        color = surfaceColor,
        border = androidx.compose.foundation.BorderStroke(
            width = token.bodyAnalysis.topBarBorderWidth,
            color = borderColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(token.spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(token.spacing.xs)
        ) {
            Box(
                modifier = Modifier
                    .size(bodyToken.bodyRegionPlaceholderSize)
                    .background(
                        color = token.colors.dashboardMealImageBackground,
                        shape = RoundedCornerShape(token.radius.md)
                    ),
                contentAlignment = Alignment.Center
            ) {
                BodyModelPreview(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(token.radius.md))
                )
            }
            Text(
                text = when (region) {
                    BodyRegion.UpperBody -> stringResource(R.string.body_region_upper_body)
                    BodyRegion.Core -> stringResource(R.string.body_region_core)
                    BodyRegion.Glutes -> stringResource(R.string.body_region_glutes)
                    BodyRegion.Thighs -> stringResource(R.string.body_region_thighs)
                    BodyRegion.Arms -> stringResource(R.string.body_region_arms)
                },
                style = token.typography.labelMedium,
                color = if (isSelected) token.colors.primary else token.colors.textSecondary
            )
        }
    }
}
