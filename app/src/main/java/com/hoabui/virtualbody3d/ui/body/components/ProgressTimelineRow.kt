package com.hoabui.virtualbody3d.ui.body.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import kotlin.math.abs
import kotlin.math.roundToInt

@Immutable
data class ProgressSnapshotUiModel(
    val date: String,
    val imageUrl: String?,
    val weight: Float?,
    val bodyFat: Float?,
    val muscleMass: Float?,
    val delta: Float?
)

enum class MetricType {
    WEIGHT,
    BODY_FAT
}

@Composable
fun ProgressTimelineRow(
    items: List<ProgressSnapshotUiModel>,
    selectedIndex: Int,
    metricType: MetricType,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    onItemClick: (Int) -> Unit = {}
) {
    if (items.isEmpty()) return

    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    val listState = rememberLazyListState()
    val safeIndex = selectedIndex.coerceIn(0, items.lastIndex)
    val density = LocalDensity.current
    val topPadding = contentPadding.calculateTopPadding()
    val bottomPadding = contentPadding.calculateBottomPadding()
    val verticalPadding = if (topPadding == 0.dp && bottomPadding == 0.dp) token.spacing.md else topPadding
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val viewportWidthPx = listState.layoutInfo.viewportSize.width
        val itemWidthPx = with(density) { bodyToken.timelineItemWidth.roundToPx() }
        val centerContentPadding = remember(maxWidth, bodyToken.timelineItemWidth, verticalPadding) {
            PaddingValues(
                start = ((maxWidth - bodyToken.timelineItemWidth) / 2f).coerceAtLeast(token.spacing.md),
                end = ((maxWidth - bodyToken.timelineItemWidth) / 2f).coerceAtLeast(token.spacing.md),
                top = verticalPadding,
                bottom = verticalPadding
            )
        }

        val snapLayoutInfoProvider = remember(listState) {
            SnapLayoutInfoProvider(
                lazyListState = listState,
                snapPosition = SnapPosition.Center
            )
        }
        val snapFlingBehavior = rememberSnapFlingBehavior(snapLayoutInfoProvider)
        val flingBehavior = remember(
            listState,
            snapFlingBehavior,
            bodyToken.timelineItemWidth,
            bodyToken.timelineItemSpacing,
            items.size
        ) {
            PagerLikeFlingBehavior(
                lazyListState = listState,
                delegate = snapFlingBehavior,
                itemExtentPx = with(density) { (bodyToken.timelineItemWidth + bodyToken.timelineItemSpacing).toPx() },
                maxItemsPerFling = 2,
                itemCount = items.size
            )
        }

        LaunchedEffect(safeIndex, bodyToken.timelineItemWidth, density, viewportWidthPx) {
            val centerOffset = ((viewportWidthPx - itemWidthPx) / 2f).roundToInt()
            listState.animateScrollToItem(
                index = safeIndex,
                scrollOffset = -centerOffset
            )
        }

        TimelineLine(
            modifier = Modifier
                .fillMaxWidth()
                .height(bodyToken.timelineLineThickness)
                .offset(y = bodyToken.timelineLineOffsetY + centerContentPadding.calculateTopPadding()),
            color = token.colors.borderSubtle,
            thickness = bodyToken.timelineLineThickness
        )

        LazyRow(
            state = listState,
            flingBehavior = flingBehavior,
            horizontalArrangement = Arrangement.spacedBy(bodyToken.timelineItemSpacing),
            contentPadding = centerContentPadding
        ) {
            itemsIndexed(
                items = items,
                key = { index, item -> "${item.date}-${item.imageUrl}-${index}" },
                contentType = { _, _ -> "progress_timeline_item" }
            ) { index, item ->
                val distanceFraction by rememberItemDistanceFromCenter(listState = listState, index = index)
                TimelineItem(
                    item = item,
                    metricType = metricType,
                    isSelected = index == safeIndex,
                    distanceFraction = distanceFraction,
                    onClick = { onItemClick(index) }
                )
            }
        }
    }
}

@Composable
fun TimelineLine(
    modifier: Modifier = Modifier,
    color: Color,
    thickness: Dp
) {
    Canvas(modifier = modifier) {
        val y = size.height / 2f
        drawLine(
            color = color,
            start = androidx.compose.ui.geometry.Offset(0f, y),
            end = androidx.compose.ui.geometry.Offset(size.width, y),
            strokeWidth = thickness.toPx()
        )
    }
}

@Composable
fun TimelineItem(
    item: ProgressSnapshotUiModel,
    metricType: MetricType,
    isSelected: Boolean,
    distanceFraction: Float,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    val gradientScale = lerp(0.86f, 1f, 1f - distanceFraction)
    val gradientAlpha = lerp(0.45f, 1f, 1f - distanceFraction)
    val itemScale by animateFloatAsState(
        targetValue = if (isSelected) 1f else gradientScale,
        animationSpec = spring(),
        label = "timeline-item-scale"
    )
    val itemAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else gradientAlpha,
        animationSpec = spring(),
        label = "timeline-item-alpha"
    )
    val avatarScale by animateFloatAsState(
        targetValue = if (isSelected) 1.14f else 1f,
        animationSpec = spring(),
        label = "timeline-avatar-scale"
    )
    val trendColor = trendColor(delta = item.delta, colors = token.colors)

    Column(
        modifier = modifier
            .width(bodyToken.timelineItemWidth)
            .graphicsLayer {
                scaleX = itemScale
                scaleY = itemScale
                alpha = itemAlpha
            }
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier.height(bodyToken.timelineDateSlotHeight),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = item.date,
                style = token.typography.labelSmall,
                color = token.colors.textSecondary,
                textAlign = TextAlign.Center
            )
        }

        Box(modifier = Modifier.height(bodyToken.timelineDateToAvatarGap))

        AvatarNode(
            imageUrl = item.imageUrl,
            isSelected = isSelected,
            scale = avatarScale,
            avatarSize = bodyToken.timelineAvatarSize,
            placeholderIconSize = bodyToken.timelinePlaceholderIconSize
        )

        Box(modifier = Modifier.height(bodyToken.timelineAvatarToDotGap))

        Box(
            modifier = Modifier
                .size(bodyToken.timelineDotSize)
                .background(
                    color = trendColor,
                    shape = CircleShape
                )
        )

        Box(modifier = Modifier.height(bodyToken.timelineDotToMetricGap))

        Row(
            horizontalArrangement = Arrangement.spacedBy(token.spacing.xxs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatPrimaryMetric(item = item, metricType = metricType),
                style = token.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = token.colors.textPrimary,
                textAlign = TextAlign.Center
            )

            DeltaChip(delta = item.delta, color = trendColor)
        }
    }
}

@Composable
private fun AvatarNode(
    imageUrl: String?,
    isSelected: Boolean,
    scale: Float,
    avatarSize: Dp,
    placeholderIconSize: Dp
) {
    val token = GymTheme.token
    val glowColor = token.colors.primary.copy(alpha = 0.22f)

    Surface(
        modifier = Modifier
            .size(avatarSize)
            .scale(scale)
            .graphicsLayer {
                shadowElevation = if (isSelected) {
                    (token.elevation.level2 * 6f).toPx()
                } else {
                    token.elevation.level1.toPx()
                }
                shape = CircleShape
                clip = false
            },
        shape = CircleShape,
        color = token.colors.surfaceElevated,
        border = if (isSelected) {
            BorderStroke(width = token.spacing.xxxs, color = token.colors.primary)
        } else {
            null
        }
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(if (isSelected) glowColor else token.colors.surfaceSubtle),
            contentAlignment = Alignment.Center
        ) {
            if (!imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Progress snapshot image",
                    modifier = Modifier
                        .size(avatarSize)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "No snapshot image",
                    tint = token.colors.textMuted,
                    modifier = Modifier.size(placeholderIconSize)
                )
            }
        }
    }
}

@Composable
private fun DeltaChip(
    delta: Float?,
    color: Color
) {
    val token = GymTheme.token
    Surface(
        shape = CircleShape,
        color = color.copy(alpha = 0.18f)
    ) {
        Text(
            text = formatDelta(delta),
            style = token.typography.labelSmall,
            color = color,
            modifier = Modifier.padding(
                horizontal = token.spacing.xs,
                vertical = token.spacing.xxxs
            )
        )
    }
}

private fun formatPrimaryMetric(item: ProgressSnapshotUiModel, metricType: MetricType): String {
    return when (metricType) {
        MetricType.WEIGHT -> item.weight?.let { "${"%.1f".format(it)} kg" } ?: "--"
        MetricType.BODY_FAT -> item.bodyFat?.let { "${"%.1f".format(it)}%" } ?: "--"
    }
}

private fun formatDelta(delta: Float?): String {
    if (delta == null) return "--"
    if (delta == 0f) return "0.0"
    val arrow = if (delta > 0f) "↑" else "↓"
    val sign = if (delta > 0f) "+" else "-"
    return "$sign${"%.1f".format(abs(delta))} $arrow"
}

private fun trendColor(
    delta: Float?,
    colors: com.hoabui.virtualbody3d.ui.theme.tokens.semantic.SemanticColorTokens
): Color {
    return when {
        delta == null || delta == 0f -> colors.textMuted
        // For quick scan body-progress snapshots, decrease is treated as improvement.
        delta < 0f -> colors.calorieDeficitPositive
        else -> colors.error
    }
}

@Composable
private fun rememberItemDistanceFromCenter(
    listState: LazyListState,
    index: Int
) = remember(listState, index) {
    derivedStateOf {
        val layoutInfo = listState.layoutInfo
        val item = layoutInfo.visibleItemsInfo.firstOrNull { it.index == index } ?: return@derivedStateOf 1f
        distanceFraction(layoutInfo = layoutInfo, itemInfo = item)
    }
}

private fun distanceFraction(
    layoutInfo: LazyListLayoutInfo,
    itemInfo: LazyListItemInfo
): Float {
    val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2f
    val itemCenter = itemInfo.offset + (itemInfo.size / 2f)
    val distance = abs(itemCenter - viewportCenter)
    val maxDistance = (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset) / 2f
    if (maxDistance <= 0f) return 1f
    return (distance / maxDistance).coerceIn(0f, 1f)
}

private fun lerp(start: Float, end: Float, fraction: Float): Float {
    return start + (end - start) * fraction
}

private class PagerLikeFlingBehavior(
    private val lazyListState: LazyListState,
    private val delegate: FlingBehavior,
    private val itemExtentPx: Float,
    private val maxItemsPerFling: Int,
    private val itemCount: Int
) : FlingBehavior {
    override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
        val currentCenteredIndex = lazyListState.layoutInfo.visibleItemsInfo.minByOrNull {
            abs((it.offset + it.size / 2f) - (lazyListState.layoutInfo.viewportEndOffset / 2f))
        }?.index ?: lazyListState.firstVisibleItemIndex

        val itemJump = when {
            initialVelocity > itemExtentPx * 8f -> maxItemsPerFling
            initialVelocity < -itemExtentPx * 8f -> -maxItemsPerFling
            initialVelocity > 0f -> 1
            initialVelocity < 0f -> -1
            else -> 0
        }

        if (itemJump == 0) {
            return delegate.run { performFling(initialVelocity = initialVelocity) }
        }

        val targetIndex = (currentCenteredIndex + itemJump).coerceIn(0, itemCount - 1)
        lazyListState.animateScrollToItem(targetIndex)
        return 0f
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
                        ProgressSnapshotUiModel("Mar 1", null, 75.0f, 20.0f, 32.4f, null),
                        ProgressSnapshotUiModel("Mar 5", null, 74.2f, 19.5f, 32.7f, -0.8f),
                        ProgressSnapshotUiModel("Mar 10", null, 73.5f, 19.0f, 33.0f, -0.7f),
                        ProgressSnapshotUiModel("Mar 15", null, 72.8f, 18.6f, 33.2f, -0.7f),
                        ProgressSnapshotUiModel("Mar 20", null, 72.0f, 18.2f, 33.6f, -0.8f)
                    )
                },
                selectedIndex = 3,
                metricType = MetricType.WEIGHT
            )
        }
    }
}
