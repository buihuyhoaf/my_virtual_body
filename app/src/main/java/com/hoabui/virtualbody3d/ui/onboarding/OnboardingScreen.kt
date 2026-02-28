package com.hoabui.virtualbody3d.ui.onboarding

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.tokens.component.OnboardingTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.semantic.SemanticColorTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.SpacingTokens
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    val colors = token.colors
    val spacing = token.spacing
    val typography = token.typography
    val onboardingTokens = token.onboarding
    val scope = rememberCoroutineScope()

    val pagerState = rememberPagerState(
        pageCount = { OnboardingPage.count },
        initialPage = OnboardingPage.Slide1.pageIndex
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = spacing.xl, vertical = spacing.lg)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            userScrollEnabled = true
        ) { page ->
            when (OnboardingPage.fromIndex(page)) {
                OnboardingPage.Slide1 -> OnboardingSlide1(
                    colors = colors,
                    spacing = spacing,
                    typography = typography,
                    onboardingTokens = onboardingTokens
                )
                OnboardingPage.Slide2 -> OnboardingSlide2(
                    colors = colors,
                    spacing = spacing,
                    typography = typography,
                    onboardingTokens = onboardingTokens
                )
                OnboardingPage.Slide3 -> OnboardingSlide3(
                    colors = colors,
                    spacing = spacing,
                    typography = typography,
                    onboardingTokens = onboardingTokens
                )
                null -> { /* fallback */ }
            }
        }

        OnboardingPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = spacing.md),
            tokens = onboardingTokens,
            selectedColor = colors.primary,
            unselectedColor = colors.borderStrong.copy(alpha = 0.5f)
        )

        val isLastPage = pagerState.currentPage == OnboardingPage.Slide3.pageIndex
        Button(
            onClick = {
                if (isLastPage) {
                    onComplete()
                } else {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            page = pagerState.currentPage + 1,
                            animationSpec = tween(durationMillis = 300, easing = LinearEasing)
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(onboardingTokens.primaryButtonHeight),
            colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
            shape = MaterialTheme.shapes.large,
            elevation = ButtonDefaults.buttonElevation(defaultElevation = token.elevation.level0)
        ) {
            Text(
                text = if (isLastPage) stringResource(R.string.onboarding_start) else stringResource(R.string.onboarding_next),
                style = typography.titleMedium
            )
        }
    }
}

@Composable
private fun OnboardingSlide1(
    colors: SemanticColorTokens,
    spacing: SpacingTokens,
    typography: androidx.compose.material3.Typography,
    onboardingTokens: OnboardingTokens
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(onboardingTokens.illustrationBodyWidth, onboardingTokens.illustrationBodyHeight)) {
                val w = size.width
                val strokeWidth = 1.5.dp.toPx()
                val plum = colors.textSecondary
                drawCircle(
                    color = plum,
                    radius = 15.dp.toPx(),
                    center = Offset(w / 2f, 40.dp.toPx()),
                    style = Stroke(width = strokeWidth)
                )
                val path = Path().apply {
                    moveTo(w * 0.29f, 140.dp.toPx())
                    lineTo(w * 0.29f, 100.dp.toPx())
                    lineTo(w / 2f, 115.dp.toPx())
                    lineTo(w * 0.71f, 100.dp.toPx())
                    lineTo(w * 0.71f, 140.dp.toPx())
                    lineTo(w * 0.71f, 200.dp.toPx())
                    lineTo(w * 0.71f, 240.dp.toPx())
                }
                drawPath(path, plum, style = Stroke(width = strokeWidth))
                drawLine(plum, Offset(w * 0.21f, 240.dp.toPx()), Offset(w * 0.21f, 300.dp.toPx()), strokeWidth)
                drawLine(plum, Offset(w * 0.38f, 240.dp.toPx()), Offset(w * 0.38f, 300.dp.toPx()), strokeWidth)
                drawLine(plum, Offset(w * 0.62f, 240.dp.toPx()), Offset(w * 0.62f, 300.dp.toPx()), strokeWidth)
                drawLine(plum, Offset(w * 0.79f, 240.dp.toPx()), Offset(w * 0.79f, 300.dp.toPx()), strokeWidth)
            }
        }
        Spacer(modifier = Modifier.height(spacing.md))
        Column(
            modifier = Modifier.padding(bottom = spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.onboarding_slide1_title),
                style = typography.headlineMedium,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(spacing.xs))
            Text(
                text = stringResource(R.string.onboarding_slide1_subtitle),
                style = typography.bodyLarge,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = spacing.md)
            )
        }
    }
}

@Composable
private fun OnboardingSlide2(
    colors: SemanticColorTokens,
    spacing: SpacingTokens,
    typography: androidx.compose.material3.Typography,
    onboardingTokens: OnboardingTokens
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = spacing.xl),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(onboardingTokens.illustrationScannerSize)) {
                val w = size.width
                val strokeWidth = 1.5.dp.toPx()
                val plum = colors.textSecondary.copy(alpha = alpha * 0.5f)
                drawRect(plum, topLeft = Offset(60.dp.toPx(), 40.dp.toPx()), size = androidx.compose.ui.geometry.Size(2.dp.toPx(), 160.dp.toPx()))
                drawRect(plum, topLeft = Offset(w / 2f - 1.dp.toPx(), 20.dp.toPx()), size = androidx.compose.ui.geometry.Size(2.dp.toPx(), 200.dp.toPx()))
                drawRect(plum, topLeft = Offset(180.dp.toPx(), 60.dp.toPx()), size = androidx.compose.ui.geometry.Size(2.dp.toPx(), 120.dp.toPx()))
                drawLine(plum, Offset(40.dp.toPx(), 80.dp.toPx()), Offset(200.dp.toPx(), 80.dp.toPx()), strokeWidth)
                drawLine(plum, Offset(40.dp.toPx(), 160.dp.toPx()), Offset(200.dp.toPx(), 160.dp.toPx()), strokeWidth)
                drawCircle(color = plum, radius = 40.dp.toPx(), center = Offset(w / 2f, w / 2f), style = Stroke(width = 1.dp.toPx()))
                drawCircle(color = plum, radius = 2.dp.toPx(), center = Offset(w / 2f, w / 2f))
            }
        }
        Column(
            modifier = Modifier.padding(bottom = spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.onboarding_slide2_title),
                style = typography.headlineMedium,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(spacing.xs))
            Text(
                text = stringResource(R.string.onboarding_slide2_subtitle),
                style = typography.bodyLarge,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = spacing.md)
            )
        }
    }
}

@Composable
private fun OnboardingSlide3(
    colors: SemanticColorTokens,
    spacing: SpacingTokens,
    typography: androidx.compose.material3.Typography,
    onboardingTokens: OnboardingTokens
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = spacing.lg),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(onboardingTokens.illustrationJournalWidth, onboardingTokens.illustrationJournalHeight)) {
                val strokeWidth = 2.dp.toPx()
                val thinStroke = 1.5.dp.toPx()
                val plum = colors.textSecondary.copy(alpha = 0.4f)
                val path = Path().apply {
                    moveTo(40.dp.toPx(), 90.dp.toPx())
                    quadraticTo(70.dp.toPx(), 85.dp.toPx(), 90.dp.toPx(), 60.dp.toPx())
                    quadraticTo(110.dp.toPx(), 35.dp.toPx(), 140.dp.toPx(), 30.dp.toPx())
                    quadraticTo(160.dp.toPx(), 50.dp.toPx(), 180.dp.toPx(), 70.dp.toPx())
                    lineTo(200.dp.toPx(), 65.dp.toPx())
                    lineTo(210.dp.toPx(), 55.dp.toPx())
                }
                drawPath(path, plum, style = Stroke(width = strokeWidth))
                drawRect(plum, topLeft = Offset(60.dp.toPx(), 20.dp.toPx()), size = androidx.compose.ui.geometry.Size(120.dp.toPx(), 80.dp.toPx()), style = Stroke(width = thinStroke))
                drawLine(plum, Offset(75.dp.toPx(), 40.dp.toPx()), Offset(145.dp.toPx(), 40.dp.toPx()), thinStroke)
                drawLine(plum, Offset(75.dp.toPx(), 55.dp.toPx()), Offset(125.dp.toPx(), 55.dp.toPx()), thinStroke)
                drawLine(plum, Offset(75.dp.toPx(), 70.dp.toPx()), Offset(165.dp.toPx(), 70.dp.toPx()), thinStroke)
            }
        }
        Column(
            modifier = Modifier.padding(bottom = spacing.xs),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.onboarding_slide3_title),
                style = typography.headlineMedium,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(spacing.xs))
            Text(
                text = stringResource(R.string.onboarding_slide3_subtitle),
                style = typography.bodyLarge,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = spacing.md)
            )
        }
        Text(
            text = stringResource(R.string.onboarding_disclaimer),
            style = typography.labelSmall,
            color = colors.textSecondary.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = spacing.lg)
        )
    }
}
