package com.hoabui.virtualbody3d.ui.mealcapture

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoabui.virtualbody3d.ui.camera.viewmodel.CameraCaptureViewModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import kotlinx.coroutines.android.awaitFrame
import java.io.File

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MealCaptureScreen(
    modifier: Modifier = Modifier,
    cameraViewModel: CameraCaptureViewModel = hiltViewModel(),
    mealViewModel: MealCaptureViewModel = hiltViewModel()
) {
    val mealPages by mealViewModel.mealPages.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 1 + mealPages.size }
    )

    val token = GymTheme.token

    // When a new meal page is added, automatically scroll to page 1 (latest meal).
    LaunchedEffect(mealPages.size) {
        if (mealPages.isNotEmpty()) {
            // Small delay to ensure pager is laid out before scroll
            awaitFrame()
            pagerState.animateScrollToPage(1)
        }
    }

    VerticalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize()
    ) { page ->
        if (page == 0) {
            CameraPage(
                modifier = Modifier.fillMaxSize(),
                viewModel = cameraViewModel,
                onUsePhoto = { file: File ->
                    mealViewModel.onMealImageConfirmed(file)
                }
            )
        } else {
            val meal = mealPages[page - 1]
            MealResultPage(
                meal = meal,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
