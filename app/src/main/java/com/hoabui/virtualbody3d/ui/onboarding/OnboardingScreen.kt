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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
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
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButton
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.tokens.component.OnboardingTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.semantic.SemanticColorTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.SpacingTokens
import com.hoabui.virtualbody3d.ui.common_ui.organism.scaffold.GScaffold
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    onComplete: () -> Unit
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

    GScaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = colors.surface
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
            GButton(
                text = if (isLastPage) stringResource(R.string.onboarding_start) else stringResource(R.string.onboarding_next),
                onClick = {
                    if (isLastPage) {
                        onComplete()
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(
                                page = pagerState.currentPage + 1,
                                animationSpec = tween(
                                    durationMillis = token.motion.duration.standard,
                                    easing = token.motion.easing.standard
                                )
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
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
                val unit = onboardingTokens.illustrationBaseUnit.toPx()
                val fine = onboardingTokens.illustrationFineUnit.toPx()
                val strokeWidth = onboardingTokens.illustrationStrokeStandard.toPx()
                val plum = colors.textSecondary
                drawCircle(
                    color = plum,
                    radius = unit - fine,
                    center = Offset(w / 2f, unit * 2f),
                    style = Stroke(width = strokeWidth)
                )
                val path = Path().apply {
                    moveTo(w * 0.29f, unit * 7f)
                    lineTo(w * 0.29f, unit * 5f)
                    lineTo(w / 2f, unit * 5f + fine * 3f)
                    lineTo(w * 0.71f, unit * 5f)
                    lineTo(w * 0.71f, unit * 7f)
                    lineTo(w * 0.71f, unit * 10f)
                    lineTo(w * 0.71f, unit * 12f)
                }
                drawPath(path, plum, style = Stroke(width = strokeWidth))
                drawLine(plum, Offset(w * 0.21f, unit * 12f), Offset(w * 0.21f, unit * 15f), strokeWidth)
                drawLine(plum, Offset(w * 0.38f, unit * 12f), Offset(w * 0.38f, unit * 15f), strokeWidth)
                drawLine(plum, Offset(w * 0.62f, unit * 12f), Offset(w * 0.62f, unit * 15f), strokeWidth)
                drawLine(plum, Offset(w * 0.79f, unit * 12f), Offset(w * 0.79f, unit * 15f), strokeWidth)
            }
        }
        Spacer(modifier = Modifier.height(spacing.md))
        Column(
            modifier = Modifier.padding(bottom = spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GText(
                text = stringResource(R.string.onboarding_slide1_title),
                style = typography.headlineMedium,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(spacing.xs))
            GText(
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
            animation = tween(
                durationMillis = GymTheme.token.motion.duration.long * 3,
                easing = GymTheme.token.motion.easing.standard
            ),
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
                val unit = onboardingTokens.illustrationBaseUnit.toPx()
                val strokeWidth = onboardingTokens.illustrationStrokeStandard.toPx()
                val thinStroke = onboardingTokens.illustrationStrokeThin.toPx()
                val boldStroke = onboardingTokens.illustrationStrokeBold.toPx()
                val plum = colors.textSecondary.copy(alpha = alpha * 0.5f)
                drawRect(plum, topLeft = Offset(unit * 3f, unit * 2f), size = androidx.compose.ui.geometry.Size(boldStroke, unit * 8f))
                drawRect(plum, topLeft = Offset(w / 2f - thinStroke, unit), size = androidx.compose.ui.geometry.Size(boldStroke, unit * 10f))
                drawRect(plum, topLeft = Offset(unit * 9f, unit * 3f), size = androidx.compose.ui.geometry.Size(boldStroke, unit * 6f))
                drawLine(plum, Offset(unit * 2f, unit * 4f), Offset(unit * 10f, unit * 4f), strokeWidth)
                drawLine(plum, Offset(unit * 2f, unit * 8f), Offset(unit * 10f, unit * 8f), strokeWidth)
                drawCircle(color = plum, radius = unit * 2f, center = Offset(w / 2f, w / 2f), style = Stroke(width = thinStroke))
                drawCircle(color = plum, radius = boldStroke, center = Offset(w / 2f, w / 2f))
            }
        }
        Column(
            modifier = Modifier.padding(bottom = spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GText(
                text = stringResource(R.string.onboarding_slide2_title),
                style = typography.headlineMedium,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(spacing.xs))
            GText(
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
                val unit = onboardingTokens.illustrationBaseUnit.toPx()
                val strokeWidth = onboardingTokens.illustrationStrokeBold.toPx()
                val thinStroke = onboardingTokens.illustrationStrokeStandard.toPx()
                val plum = colors.textSecondary.copy(alpha = 0.4f)
                val path = Path().apply {
                    moveTo(unit * 2f, unit * 4.5f)
                    quadraticTo(unit * 3.5f, unit * 4.25f, unit * 4.5f, unit * 3f)
                    quadraticTo(unit * 5.5f, unit * 1.75f, unit * 7f, unit * 1.5f)
                    quadraticTo(unit * 8f, unit * 2.5f, unit * 9f, unit * 3.5f)
                    lineTo(unit * 10f, unit * 3.25f)
                    lineTo(unit * 10.5f, unit * 2.75f)
                }
                drawPath(path, plum, style = Stroke(width = strokeWidth))
                drawRect(
                    plum,
                    topLeft = Offset(unit * 3f, unit),
                    size = androidx.compose.ui.geometry.Size(unit * 6f, unit * 4f),
                    style = Stroke(width = thinStroke)
                )
                drawLine(plum, Offset(unit * 3.75f, unit * 2f), Offset(unit * 7.25f, unit * 2f), thinStroke)
                drawLine(plum, Offset(unit * 3.75f, unit * 2.75f), Offset(unit * 6.25f, unit * 2.75f), thinStroke)
                drawLine(plum, Offset(unit * 3.75f, unit * 3.5f), Offset(unit * 8.25f, unit * 3.5f), thinStroke)
            }
        }
        Column(
            modifier = Modifier.padding(bottom = spacing.xs),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GText(
                text = stringResource(R.string.onboarding_slide3_title),
                style = typography.headlineMedium,
                color = colors.textPrimary
            )
            Spacer(modifier = Modifier.height(spacing.xs))
            GText(
                text = stringResource(R.string.onboarding_slide3_subtitle),
                style = typography.bodyLarge,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = spacing.md)
            )
        }
        GText(
            text = stringResource(R.string.onboarding_disclaimer),
            style = typography.labelSmall,
            color = colors.textSecondary.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = spacing.lg)
        )
    }
}
