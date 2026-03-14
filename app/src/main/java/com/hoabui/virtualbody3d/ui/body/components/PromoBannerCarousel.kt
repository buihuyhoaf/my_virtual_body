package com.hoabui.virtualbody3d.ui.body.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.ui.body.data.PromoBannerItem
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.tokens.GymToken
import kotlinx.coroutines.delay


/**
 * Reusable horizontal carousel of promotional or informational banners.
 *
 * Displays full-width rounded cards that support image or gradient backgrounds
 * and optional overlay title/subtitle. Auto-slides to the next banner after a short
 * interval (looping), and pauses while the user drags or swipes; manual swipe remains
 * enabled. Uses a pager indicator (dots) below the banner.
 *
 * Uses [GymTheme.token] for radius, spacing, colors, and typography.
 * Designed to be lightweight and reusable across the app (e.g. Body Analysis,
 * workouts, nutrition).
 *
 * @param banners List of [PromoBannerItem] to display.
 * @param modifier Modifier for the carousel container.
 * @param bannerHeight Height of each banner card (default medium fitness-app style).
 * @param autoSlideIntervalMs Interval in ms between auto slides (0 to disable).
 * @param pauseAfterUserInteractionMs How long to pause auto-slide after user interaction (ms).
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PromoBannerCarousel(
    banners: List<PromoBannerItem>,
    modifier: Modifier = Modifier,
    bannerHeight: Dp = 120.dp,
    autoSlideIntervalMs: Long = 3000L,
    pauseAfterUserInteractionMs: Long = 4000L
) {
    if (banners.isEmpty()) return

    val token = GymTheme.token
    val pagerState = rememberPagerState(
        pageCount = { banners.size },
        initialPage = 0
    )
    var autoSlidePaused by remember { mutableStateOf(false) }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.isScrollInProgress }.collect { inProgress ->
            if (inProgress) {
                autoSlidePaused = true
            } else {
                delay(pauseAfterUserInteractionMs)
                autoSlidePaused = false
            }
        }
    }

    LaunchedEffect(banners.size, autoSlideIntervalMs) {
        if (banners.size <= 1 || autoSlideIntervalMs <= 0L) return@LaunchedEffect
        while (true) {
            delay(autoSlideIntervalMs)
            if (autoSlidePaused) continue
            val next = (pagerState.currentPage + 1) % banners.size
            pagerState.animateScrollToPage(next)
        }
    }

    val currentPage by remember {
        derivedStateOf { pagerState.currentPage }
    }

    Box(
        modifier = modifier.fillMaxWidth(),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(bannerHeight),
            userScrollEnabled = true,
            key = { index -> index }
        ) { page ->
            val item = banners[page]
            PromoBannerCard(
                item = item,
                modifier = Modifier.fillMaxSize(),
                bannerHeight = bannerHeight
            )
        }

        PromoBannerPagerIndicator(
            pageCount = banners.size,
            currentPage = currentPage,
            modifier = Modifier
                .fillMaxWidth().align(Alignment.BottomCenter),
            token = token,
        )
    }
}

@Composable
private fun PromoBannerCard(
    item: PromoBannerItem,
    modifier: Modifier = Modifier,
    bannerHeight: Dp
) {
    val token = GymTheme.token
    val shape = RoundedCornerShape(token.radius.md)
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .clip(shape)
            .then(
                if (item.onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = item.onClick
                    )
                } else Modifier
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
        ) {
            when {
                item.backgroundImageRes != null -> {
                    Image(
                        painter = painterResource(item.backgroundImageRes),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(shape),
                        contentScale = ContentScale.Crop
                    )
                }
                !item.backgroundGradientColors.isNullOrEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(item.backgroundGradientColors)
                            )
                    )
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(token.colors.primarySoft)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.15f),
                                Color.Black.copy(alpha = 0.5f)
                            )
                        )
                    )
            )
        }
    }
}

@Composable
private fun PromoBannerPagerIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
    token: GymToken
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = currentPage == index
            Box(
                modifier = Modifier
                    .padding(horizontal = token.spacing.xxs)
                    .size(if (isSelected) 8.dp else 6.dp)
                    .background(
                        color = Color.White,
                        shape = CircleShape
                    )
            )
        }
    }
}
