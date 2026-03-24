package com.hoabui.virtualbody3d.ui.body.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import com.hoabui.virtualbody3d.ui.common_ui.atom.surface.GSurface
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.core.extensions.drawAvatarBackdrop
import com.hoabui.virtualbody3d.core.extensions.timelineAvatarLayer
import com.hoabui.virtualbody3d.core.extensions.timelineItemLayer
import com.hoabui.virtualbody3d.ui.body.data.ProgressSnapshotUiModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.font.InterFontFamily
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.math.abs


@Composable
fun ProgressTimelineRow(
    items: List<ProgressSnapshotUiModel>,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    onItemClick: (Int) -> Unit = {}
) {
    if (items.isEmpty()) return

    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    val safeIndex = selectedIndex.coerceIn(0, items.lastIndex)
    val topPadding = contentPadding.calculateTopPadding()
    val bottomPadding = contentPadding.calculateBottomPadding()
    val verticalPadding = if (topPadding == 0.dp && bottomPadding == 0.dp) token.spacing.md else topPadding

    val pagerState = rememberPagerState(
        initialPage = safeIndex,
        pageCount = { items.size }
    )

    LaunchedEffect(safeIndex, items.size) {
        val target = safeIndex.coerceIn(0, items.lastIndex)
        if (pagerState.currentPage != target) {
            pagerState.animateScrollToPage(target)
        }
    }

    LaunchedEffect(pagerState, items.size) {
        if (items.isEmpty()) return@LaunchedEffect
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect { page ->
                val p = page.coerceIn(0, items.lastIndex)
                onItemClick(p)
            }
    }

    Box(modifier = modifier.fillMaxWidth()) {
        TimelineRowAmbientBackground(
            cornerRadius = token.radius.lg,
            modifier = Modifier.fillMaxSize()
        )

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val horizontalPad =
                ((maxWidth - bodyToken.timelineItemWidth) / 2f).coerceAtLeast(0.dp)
            val centerContentPadding = PaddingValues(
                start = horizontalPad,
                end = horizontalPad,
                top = verticalPadding,
                bottom = bottomPadding
            )

            TimelineSegmentedLine(
                pagerState = pagerState,
                itemCount = items.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(bodyToken.timelineLineThickness)
                    .offset(y = bodyToken.timelineLineOffsetY + centerContentPadding.calculateTopPadding()),
                primaryColor = token.colors.primary,
                dashedColor = token.colors.borderSubtle,
                thickness = bodyToken.timelineLineThickness
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = centerContentPadding,
                pageSize = PageSize.Fixed(bodyToken.timelineItemWidth),
                pageSpacing = bodyToken.timelineItemSpacing,
                verticalAlignment = Alignment.Top,
                key = { index -> "${items[index].date}-${items[index].imageUrl}-$index" },
            ) { page ->
                TimelineItem(
                    item = items[page],
                    pageIndex = page,
                    pagerState = pagerState,
                )
            }
        }
    }
}



@Composable
private fun TimelineItem(
    item: ProgressSnapshotUiModel,
    pageIndex: Int,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .width(bodyToken.timelineItemWidth)
            .timelineItemLayer(pagerState = pagerState, pageIndex = pageIndex)
            .clickable(
                onClick = {
                    scope.launch { pagerState.animateScrollToPage(pageIndex) }
                }
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier.height(bodyToken.timelineDateSlotHeight),
            contentAlignment = Alignment.Center
        ) {
            GText(
                text = item.date,
                style = token.typography.labelSmall,
                color = token.colors.textSecondary,
                textAlign = TextAlign.Center
            )
        }

        Box(modifier = Modifier.height(bodyToken.timelineDateToAvatarGap))

        Box(
            modifier = Modifier.timelineAvatarLayer(pagerState = pagerState, pageIndex = pageIndex)
        ) {
            AvatarNode(
                imageUrl = item.imageUrl,
                imageRes = R.drawable.body_unsplash,
                weight = item.weight,
                bodyFat = item.bodyFat,
                muscleMass = item.muscleMass,
                scale = 1f,
                avatarSize = bodyToken.timelineAvatarSquareSize,
                cornerRadius = bodyToken.timelineAvatarCornerRadius,
                pagerState = pagerState,
                pageIndex = pageIndex,
            )
        }

        Box(modifier = Modifier.height(bodyToken.timelineAvatarToDotGap))

        TimelineDot(
            pagerState = pagerState,
            pageIndex = pageIndex,
            modifier = Modifier.size(bodyToken.timelineDotSize)
        )
    }
}

@Composable
private fun TimelineDot(
    pagerState: PagerState,
    pageIndex: Int,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    Box(
        modifier = modifier.drawBehind {
            val distanceFraction = abs(pagerState.getOffsetDistanceInPages(pageIndex)).coerceIn(0f, 1f)
            val isNearCenter = distanceFraction < 0.5f
            val color = if (isNearCenter) token.colors.primary else token.colors.borderStrong
            val r = size.minDimension / 2f
            drawCircle(color = color, radius = r, center = Offset(size.width / 2f, size.height / 2f))
        }
    )
}

@Composable
private fun TimelineRowAmbientBackground(
    cornerRadius: Dp,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(token.colors.surfaceSubtle.copy(alpha = 0.35f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(token.spacing.lg)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            token.colors.primary.copy(alpha = 0.12f),
                            token.colors.surface.copy(alpha = 0.02f),
                            token.colors.primarySoft.copy(alpha = 0.1f)
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            token.colors.primary.copy(alpha = 0.06f),
                            Color.Transparent
                        ),
                        center = Offset(0.3f, 0.2f),
                        radius = 800f
                    )
                )
        )
    }
}

@Composable
private fun TimelineSegmentedLine(
    pagerState: PagerState,
    itemCount: Int,
    primaryColor: Color,
    dashedColor: Color,
    thickness: Dp,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val strokePx = with(density) { thickness.toPx() }
    Canvas(modifier = modifier) {
        val splitFraction = if (itemCount <= 1) {
            0.5f
        } else {
            (pagerState.currentPage + pagerState.currentPageOffsetFraction) / (itemCount - 1).coerceAtLeast(1)
        }
        val splitX = splitFraction.coerceIn(0f, 1f) * size.width
        val y = size.height / 2f
        val w = size.width

        drawLine(
            color = primaryColor,
            start = Offset(0f, y),
            end = Offset(splitX, y),
            strokeWidth = strokePx
        )
        drawLine(
            color = dashedColor,
            start = Offset(splitX, y),
            end = Offset(w, y),
            strokeWidth = strokePx,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
        )
    }
}

@Composable
private fun AvatarNode(
    imageUrl: String?,
    imageRes: Int?,
    weight: Float?,
    bodyFat: Float?,
    muscleMass: Float?,
    scale: Float,
    avatarSize: Dp,
    cornerRadius: Dp,
    pagerState: PagerState,
    pageIndex: Int,
) {
    val token = GymTheme.token
    val density = LocalDensity.current
    val primary = token.colors.primary
    val primarySoft = token.colors.primarySoft
    val glowPadding = token.spacing.xs
    val glowOuterSize = avatarSize + glowPadding * 2
    val borderWidth = token.spacing.xxxs
    val avatarShape = RoundedCornerShape(cornerRadius)
    val frameSize = avatarSize + borderWidth * 2

    val kg = stringResource(R.string.body_unit_kg)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(token.spacing.xxxs)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(glowOuterSize)
                .drawBehind {
                    val isNearCenter = abs(pagerState.getOffsetDistanceInPages(pageIndex)) < 0.5f
                    drawAvatarBackdrop(
                        isSelected = isNearCenter,
                        glowOuterSize = Size(size.width, size.height),
                        frameSidePx = with(density) { frameSize.toPx() },
                        borderWidthPx = with(density) { borderWidth.toPx() },
                        cornerRadiusPx = with(density) { cornerRadius.toPx() },
                        primary = primary,
                        primarySoft = primarySoft,
                        surfaceElevated = token.colors.surfaceElevated,
                    )
                }
        ) {
            Box(
                modifier = Modifier
                    .size(frameSize)
                    .graphicsLayer {
                        val isNearCenter = abs(pagerState.getOffsetDistanceInPages(pageIndex)) < 0.5f
                        val shadowElevationPx = with(density) {
                            if (isNearCenter) (token.elevation.level2 * 8f).toPx() else token.elevation.level1.toPx()
                        }
                        scaleX = scale
                        scaleY = scale
                        shadowElevation = shadowElevationPx
                        shape = avatarShape
                        clip = false
                    }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(borderWidth)
                        .clip(avatarShape)
                        .drawBehind {
                            val isNearCenter = abs(pagerState.getOffsetDistanceInPages(pageIndex)) < 0.5f
                            val rPx = cornerRadius.toPx()
                            drawRoundRect(
                                color = if (isNearCenter) primary.copy(alpha = 0.1f) else token.colors.surfaceSubtle,
                                topLeft = Offset.Zero,
                                size = size,
                                cornerRadius = CornerRadius(rPx, rPx)
                            )
                        }
                ) {
                    if (!imageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(avatarShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(imageRes ?: R.drawable.body_unsplash),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(avatarShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                AvatarTopLeftFloatingMetricChips(
                    bodyFat = bodyFat,
                    muscleMass = muscleMass,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = -(token.spacing.xxxs + token.spacing.xs / 2), y = -token.spacing.xxxs)
                )
            }
        }
        AvatarFloatingMetricChip(
            value = weight,
            unit = kg,
            isPercentSuffix = false
        )
    }
}



@Composable
private fun AvatarTopLeftFloatingMetricChips(
    bodyFat: Float?,
    muscleMass: Float?,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    val pct = stringResource(R.string.body_unit_percent)
    val kg = stringResource(R.string.body_unit_kg)
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(token.spacing.xxxs)
    ) {
        AvatarFloatingMetricChip(
            value = bodyFat,
            unit = pct,
            isPercentSuffix = true
        )
        AvatarFloatingMetricChip(
            value = muscleMass,
            unit = kg,
            isPercentSuffix = false
        )
    }
}

@Composable
private fun AvatarFloatingMetricChip(
    value: Float?,
    unit: String,
    isPercentSuffix: Boolean
) {
    val token = GymTheme.token
    val numberPart = value?.let { "%.1f".format(it) } ?: "—"
    val unitMuted = token.colors.textPrimary.copy(alpha = 0.5f)

    GSurface(
        shape = RoundedCornerShape(token.radius.sm),
        color = token.colors.surfaceElevated.copy(alpha = 0.94f),
        shadowElevation = token.card.elevation,
        border = BorderStroke(
            width = token.spacing.xxxs,
            color = token.colors.borderSubtle.copy(alpha = 0.45f)
        ),
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = token.spacing.xs,
                vertical = token.spacing.xxxs
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            GText(
                text = numberPart,
                style = token.typography.labelMedium.copy(
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold
                ),
                color = token.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (value != null) {
                GText(
                    text = if (isPercentSuffix) unit else " $unit",
                    style = token.typography.labelSmall.copy(fontFamily = InterFontFamily),
                    color = unitMuted,
                    maxLines = 1
                )
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
private fun ProgressTimelineRowPreview() {
    GymTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(GymTheme.token.colors.background)
        ) {
            ProgressTimelineRow(
                items = remember {
                    listOf(
                        ProgressSnapshotUiModel(
                            date = "Mar 1",
                            imageUrl = null,
                            weight = 75.0f,
                            bodyFat = 20.0f,
                            muscleMass = 32.4f
                        ),
                        ProgressSnapshotUiModel(
                            date = "Mar 5",
                            imageUrl = null,
                            weight = 74.2f,
                            bodyFat = 19.5f,
                            muscleMass = 32.7f
                        ),
                        ProgressSnapshotUiModel(
                            date = "Mar 10",
                            imageUrl = null,
                            weight = 73.5f,
                            bodyFat = 19.0f,
                            muscleMass = 33.0f
                        ),
                        ProgressSnapshotUiModel(
                            date = "Mar 15",
                            imageUrl = null,
                            weight = 72.8f,
                            bodyFat = 18.6f,
                            muscleMass = 33.2f
                        ),
                        ProgressSnapshotUiModel(
                            date = "Mar 20",
                            imageUrl = null,
                            weight = 72.0f,
                            bodyFat = 18.2f,
                            muscleMass = 33.6f
                        )
                    )
                },
                selectedIndex = 3
            )
        }
    }
}
