package com.hoabui.virtualbody3d.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.hoabui.virtualbody3d.ui.theme.tokens.component.OnboardingTokens

/**
 * Page indicator dots for HorizontalPager, following the pattern from
 * [Android custom page indicator](https://developer.android.com/develop/ui/compose/quick-guides/content/custom-page-indicator).
 * Uses design tokens for dot size and spacing.
 */
@Composable
fun OnboardingPagerIndicator(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    tokens: OnboardingTokens,
    selectedColor: Color,
    unselectedColor: Color
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pagerState.pageCount) { index ->
            val isSelected = pagerState.currentPage == index
            Row(
                modifier = Modifier.padding(horizontal = tokens.dotGap / 2),
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(tokens.dotSize)
                        .background(
                            color = if (isSelected) selectedColor else unselectedColor,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}
