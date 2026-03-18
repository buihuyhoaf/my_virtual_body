package com.hoabui.virtualbody3d.ui.body.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoabui.virtualbody3d.core.utils.Constants.BODY_METRICS_PANEL_INDEX
import com.hoabui.virtualbody3d.core.utils.Constants.CALORIES_TODAY_PANEL_INDEX
import com.hoabui.virtualbody3d.core.utils.Constants.PANEL_PAGE_COUNT
import com.hoabui.virtualbody3d.domain.model.BodyScanResult
import com.hoabui.virtualbody3d.domain.model.PromoBanner
import com.hoabui.virtualbody3d.ui.body.components.CaloriesTodayPanel
import com.hoabui.virtualbody3d.ui.body.components.HeroSection
import com.hoabui.virtualbody3d.ui.body.components.IncommingExercisesRow
import com.hoabui.virtualbody3d.ui.body.components.NutritionCard
import com.hoabui.virtualbody3d.ui.body.components.PromoBannerCarousel
import com.hoabui.virtualbody3d.ui.body.components.SupplementsRow
import com.hoabui.virtualbody3d.ui.body.data.FavoriteExerciseUiItem
import com.hoabui.virtualbody3d.ui.body.data.NutritionSummaryUiState
import com.hoabui.virtualbody3d.ui.body.data.SupplementUiItem
import com.hoabui.virtualbody3d.ui.body.data.toPromoBannerItem
import com.hoabui.virtualbody3d.ui.body.state.BodyUiState
import com.hoabui.virtualbody3d.ui.body.state.toUiState
import com.hoabui.virtualbody3d.ui.body.viewmodel.BodyViewModel
import com.hoabui.virtualbody3d.ui.body.viewmodel.FavoriteExercisesViewModel
import com.hoabui.virtualbody3d.ui.body.viewmodel.SupplementsViewModel
import com.hoabui.virtualbody3d.ui.components.UiStateContent
import com.hoabui.virtualbody3d.ui.mealcapture.MealPageUiModel
import com.hoabui.virtualbody3d.ui.scanresult.MetricsPanel
import com.hoabui.virtualbody3d.ui.theme.GymTheme


@Composable
fun HomeScreen(
    onViewBodyDetailClick: () -> Unit = {},
    onNavigateToExerciseLibrary: (() -> Unit)? = null,
    viewModel: BodyViewModel = hiltViewModel(),
    favoriteExercisesViewModel: FavoriteExercisesViewModel = hiltViewModel(),
    supplementsViewModel: SupplementsViewModel = hiltViewModel()
) {
    val screenState by viewModel.state.collectAsStateWithLifecycle()
    val favoriteExercises by favoriteExercisesViewModel.exercises.collectAsStateWithLifecycle()
    val supplements by supplementsViewModel.supplements.collectAsStateWithLifecycle()

    UiStateContent(
        state = screenState,
        modifier = Modifier.fillMaxSize(),
        successContent = { mod, data ->
            HomeContent(
                modifier = mod,
                scanResult = data.scanResult,
                nutritionToday = data.nutritionToday,
                favoriteExercises = favoriteExercises,
                supplements = supplements,
                onViewBodyDetailClick = onViewBodyDetailClick,
                onNavigateToExerciseLibrary = onNavigateToExerciseLibrary,
                promoBanners = data.promoBanners
            )
        }
    )
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    scanResult: BodyScanResult?,
    nutritionToday: NutritionSummaryUiState,
    favoriteExercises: List<FavoriteExerciseUiItem>,
    supplements: List<SupplementUiItem>,
    onViewBodyDetailClick: () -> Unit = {},
    onNavigateToExerciseLibrary: (() -> Unit)? = null,
    promoBanners: List<PromoBanner> = emptyList()
) {
    val token = GymTheme.token
    val configuration = LocalConfiguration.current
    val contentHeight = configuration.screenHeightDp.dp
    val uiState = scanResult?.toUiState() ?: BodyUiState()
    val bodyScore = ((uiState.bmiScalePosition ?: 0.76f) * 100f).toInt().coerceIn(0, 100)
    val bannerItems = remember(promoBanners) { promoBanners.map { it.toPromoBannerItem() } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(token.spacing.md),
        verticalArrangement = Arrangement.spacedBy(token.spacing.md)
    ) {
//        PromoBannerCarousel(
//            banners = bannerItems,
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(contentHeight * 0.1f)
//        )
        IncommingExercisesRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(contentHeight * 0.17f),
            exercises = favoriteExercises,
            onAddExerciseClick = { /* TODO: navigate or show add flow */ },
            onSeeMoreClick = onNavigateToExerciseLibrary
        )
        NutritionCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(contentHeight * 0.1f),
            summary = nutritionToday
        )
        HeroSection(
            modifier = Modifier
                .fillMaxWidth()
                .height(contentHeight * 0.45f),
            uiState = uiState,
            bodyScore = bodyScore,
            onViewBodyDetailClick = onViewBodyDetailClick,
        )

//        SupplementsRow(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(contentHeight * 0.17f),
//            supplements = supplements
//        )
    }
}


@Composable
private fun PanelSlider(
    modifier: Modifier = Modifier,
    meals: List<MealPageUiModel>,
    scanResult: BodyScanResult?
) {
    val token = GymTheme.token
    val pagerState = rememberPagerState(pageCount = { PANEL_PAGE_COUNT }, initialPage = 0)

    Box(modifier = modifier) {
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = true
        ) { page ->
            when (page) {
                CALORIES_TODAY_PANEL_INDEX -> CaloriesTodayPanel(
                    modifier = Modifier.fillMaxSize(),
                    meals = meals
                )
                BODY_METRICS_PANEL_INDEX -> MetricsPanel(
                    modifier = Modifier.fillMaxSize(),
                    scanResult = scanResult
                )
                else -> { }
            }
        }
        PanelPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = token.spacing.xs, end = token.spacing.md)
        )
    }
}

@Composable
private fun PanelPagerIndicator(
    pagerState: PagerState,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(token.spacing.xxs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(PANEL_PAGE_COUNT) { index ->
            val isSelected = pagerState.currentPage == index
            Box(
                modifier = Modifier
                    .padding(horizontal = token.spacing.xxs)
                    .size(if (isSelected) 8.dp else 6.dp)
                    .background(
                        color = if (isSelected) token.colors.primary else token.colors.textSecondary.copy(alpha = 0.4f),
                        shape = CircleShape
                    )
            )
        }
    }
}
