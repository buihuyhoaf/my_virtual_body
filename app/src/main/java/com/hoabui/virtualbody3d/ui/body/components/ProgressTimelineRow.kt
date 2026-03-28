package com.hoabui.virtualbody3d.ui.body.components

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.Barbell
import com.adamglin.phosphoricons.regular.Drop
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.body.data.ProgressSnapshotUiModel
import com.hoabui.virtualbody3d.ui.common_ui.atom.surface.GSurface
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import java.util.Locale
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

    GSurface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(token.radius.lg),
        color = token.colors.surfaceSubtle.copy(alpha = bodyToken.timelineRowSurfaceAlpha),
        shadowElevation = 0.dp,
    ) {
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
                flingBehavior = PagerDefaults.flingBehavior(
                    state = pagerState,
                    snapPositionalThreshold = bodyToken.timelinePagerSnapPositionalThreshold,
                ),
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
    val kg = stringResource(R.string.body_unit_kg)
    val placeholder = stringResource(R.string.progress_timeline_metric_placeholder)

    // Weight uses localized string — must stay in @Composable scope (not inside remember { }).
    val weightText = item.weight?.let { wv ->
        stringResource(R.string.progress_timeline_weight_kg, wv, kg)
    } ?: placeholder

    // Fat / muscle: pure formatting, stable across scroll for this item.
    val fatText = remember(item.bodyFat, placeholder) {
        item.bodyFat?.let { fv -> String.format(Locale.ENGLISH, "%.1f%%", fv) } ?: placeholder
    }
    val muscleText = remember(item.muscleMass, kg, placeholder) {
        item.muscleMass?.let { mv -> String.format(Locale.ENGLISH, "%.1f%s", mv, kg) } ?: placeholder
    }

    Column(
        modifier = modifier
            .width(bodyToken.timelineItemWidth)
            .graphicsLayer {
                val distanceFraction =
                    abs(pagerState.getOffsetDistanceInPages(pageIndex)).coerceIn(0f, 1f)
                alpha = if (distanceFraction < 0.5f) 1f else bodyToken.timelineUnselectedItemAlpha
            }
            .clickable(
                onClick = {
                    scope.launch { pagerState.animateScrollToPage(pageIndex) }
                }
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(token.spacing.xxs),
    ) {
        Box(
            modifier = Modifier.height(bodyToken.timelineDateSlotHeight),
            contentAlignment = Alignment.Center,
        ) {
            GText(
                text = item.date,
                style = token.typography.labelSmall,
                color = token.colors.textSecondary,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }

        AvatarNode(
            imageUrl = item.imageUrl,
            imageRes = R.drawable.body_unsplash,
            pageIndex = pageIndex,
            pagerState = pagerState,
        )

        GText(
            text = weightText,
            style = token.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = token.colors.primary,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )

        TimelineSecondaryMetricGrid(
            fatText = fatText,
            muscleText = muscleText,
            modifier = Modifier.fillMaxWidth(),
        )

        TimelineDot(
            pagerState = pagerState,
            pageIndex = pageIndex,
            modifier = Modifier.size(bodyToken.timelineDotSize)
        )
    }
}
@Composable
private fun TimelineSecondaryMetricGrid(
    fatText: String,
    muscleText: String,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    val iconColor = token.colors.textPrimary
    val textColor = token.colors.textPrimary
    val labelStyle = token.typography.labelSmall

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(token.spacing.xxs),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(token.spacing.xxxs),
        ) {
            Icon(
                imageVector = PhosphorIcons.Regular.Drop,
                contentDescription = null,
                modifier = Modifier.size(bodyToken.timelineSecondaryMetricIconSize),
                tint = iconColor,
            )
            GText(
                text = fatText,
                style = labelStyle,
                color = textColor,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(token.spacing.xxxs),
        ) {
            Icon(
                imageVector = PhosphorIcons.Regular.Barbell,
                contentDescription = null,
                modifier = Modifier.size(bodyToken.timelineSecondaryMetricIconSize),
                tint = iconColor,
            )
            GText(
                text = muscleText,
                style = labelStyle,
                color = textColor,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun AvatarNode(
    imageUrl: String?,
    imageRes: Int?,
    pageIndex: Int,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    val avatarSize = bodyToken.timelineAvatarSquareSize
    val shape = RoundedCornerShape(bodyToken.timelineAvatarCornerRadius)

    Box(
        modifier = modifier
            .size(avatarSize)
            .graphicsLayer {
                val distanceFraction =
                    abs(pagerState.getOffsetDistanceInPages(pageIndex)).coerceIn(0f, 1f)
                val s = if (distanceFraction < 0.5f) bodyToken.timelineAvatarSelectedScale else 1f
                scaleX = s
                scaleY = s
            }
            .clip(shape)
            .background(token.colors.surfaceSubtle)
            .drawWithContent {
                drawContent()
                val distanceFraction =
                    abs(pagerState.getOffsetDistanceInPages(pageIndex)).coerceIn(0f, 1f)
                if (distanceFraction < 0.5f) {
                    val strokePx = bodyToken.timelineAvatarSelectedBorderWidth.toPx()
                    val cornerPx = bodyToken.timelineAvatarCornerRadius.toPx()
                    drawRoundRect(
                        color = token.colors.primary,
                        topLeft = Offset.Zero,
                        size = this.size,
                        cornerRadius = CornerRadius(cornerPx, cornerPx),
                        style = Stroke(width = strokePx),
                    )
                }
            }
    ) {
        if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = painterResource(imageRes ?: R.drawable.body_unsplash),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape),
                contentScale = ContentScale.Crop
            )
        }
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
            val distanceFraction =
                abs(pagerState.getOffsetDistanceInPages(pageIndex)).coerceIn(0f, 1f)
            val nearCenter = distanceFraction < 0.5f
            val color = if (nearCenter) token.colors.primary else token.colors.borderStrong
            val r = size.minDimension / 2f
            drawCircle(
                color = color,
                radius = r,
                center = Offset(size.width / 2f, size.height / 2f)
            )
        }
    )
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
    Canvas(modifier = modifier) {
        val strokePx = thickness.toPx()
        val splitFraction = if (itemCount <= 1) {
            0.5f
        } else {
            (pagerState.currentPage + pagerState.currentPageOffsetFraction) /
                (itemCount - 1).coerceAtLeast(1)
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
