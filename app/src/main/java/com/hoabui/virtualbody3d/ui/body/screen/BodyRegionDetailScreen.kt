package com.hoabui.virtualbody3d.ui.body.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.body.BodyScanResult
import com.hoabui.virtualbody3d.ui.body.components.BalanceCard
import com.hoabui.virtualbody3d.ui.body.components.HealthIndicatorsCard
import com.hoabui.virtualbody3d.ui.body.components.MuscleCompositionCard
import com.hoabui.virtualbody3d.ui.body.components.PerformanceCard
import com.hoabui.virtualbody3d.ui.body.components.RealLifeApplicationsCard
import com.hoabui.virtualbody3d.ui.body.components.RecommendedExercisesSection
import com.hoabui.virtualbody3d.ui.body.components.StaticHeroSection
import com.hoabui.virtualbody3d.ui.common_ui.molecule.topbar.GTopBar
import com.hoabui.virtualbody3d.ui.common_ui.molecule.topbar.GTopBarBackIcon
import com.hoabui.virtualbody3d.ui.common_ui.organism.scaffold.GScaffold
import com.hoabui.virtualbody3d.ui.body.state.BodyRegion
import com.hoabui.virtualbody3d.ui.body.state.BodyUiState
import com.hoabui.virtualbody3d.ui.body.state.toUiState
import com.hoabui.virtualbody3d.ui.body.viewmodel.BodyViewModel
import com.hoabui.virtualbody3d.ui.components.UiStateContent
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun BodyRegionDetailScreen(
    regionName: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BodyViewModel = hiltViewModel()
) {
    val displayNameRes = BodyRegion.valueOf(regionName).displayNameRes
    val screenState by viewModel.state.collectAsStateWithLifecycle()

    UiStateContent(
        state = screenState,
        modifier = modifier,
        successContent = { mod, data ->
            GScaffold(
                modifier = mod,
                topBar = {
                    GTopBar(
                        title = stringResource(displayNameRes),
                        windowInsets = WindowInsets(0),
                        navigationIcon = { GTopBarBackIcon(onBack = onBack) }
                    )
                }
            ) { padding ->
                BodyRegionDetailScreenContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    scanResult = data.scanResult,
                    displayName = stringResource(displayNameRes)
                )
            }
        }
    )
}

@Composable
fun BodyRegionDetailScreenContent(
    modifier: Modifier = Modifier,
    scanResult: BodyScanResult,
    displayName: String
) {
    val token = GymTheme.token
    val uiState: BodyUiState = scanResult.toUiState()
    val bodyScore = ((uiState.bmiScalePosition ?: 0.76f) * 100f).toInt().coerceIn(0, 100)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(token.spacing.lg)
    ) {
        // 1. 3D Muscle Viewer: Static hero section (ảnh tĩnh/snapshot)
        item {
            StaticHeroSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(horizontal = token.spacing.md),
                uiState = uiState,
                bodyScore = bodyScore
            )
        }

        // 2. Muscle Metrics Section
        item {
            GText(
                modifier = Modifier.padding(horizontal = token.spacing.md),
                text = stringResource(R.string.body_region_detail_muscle_metrics),
                style = token.typography.titleMedium,
                color = token.colors.textPrimary,
                fontWeight = FontWeight.Medium
            )
        }
        item {
            MuscleCompositionCard(
                modifier = Modifier.padding(horizontal = token.spacing.md)
            )
        }
        item {
            PerformanceCard(
                modifier = Modifier.padding(horizontal = token.spacing.md)
            )
        }
        item {
            BalanceCard(
                modifier = Modifier.padding(horizontal = token.spacing.md)
            )
        }
        item {
            HealthIndicatorsCard(
                modifier = Modifier.padding(horizontal = token.spacing.md)
            )
        }

        // 3. Recommended Exercises Section
        item {
            GText(
                modifier = Modifier.padding(horizontal = token.spacing.md),
                text = stringResource(R.string.body_region_detail_recommended_exercises),
                style = token.typography.titleMedium,
                color = token.colors.textPrimary,
                fontWeight = FontWeight.Medium
            )
        }
        item {
            RecommendedExercisesSection(
                regionDisplayName = displayName,
                modifier = Modifier.padding(horizontal = token.spacing.md)
            )
        }

        // 4. Real-life Applications Section
        item {
            GText(
                modifier = Modifier.padding(horizontal = token.spacing.md),
                text = stringResource(R.string.body_region_detail_real_life_applications),
                style = token.typography.titleMedium,
                color = token.colors.textPrimary,
                fontWeight = FontWeight.Medium
            )
        }
        item {
            RealLifeApplicationsCard(
                modifier = Modifier.padding(horizontal = token.spacing.md)
            )
        }

        item {
            Spacer(modifier = Modifier.height(token.spacing.xl))
        }
    }
}



