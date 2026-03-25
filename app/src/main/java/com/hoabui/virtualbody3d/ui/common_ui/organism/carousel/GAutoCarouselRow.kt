package com.hoabui.virtualbody3d.ui.common_ui.organism.carousel

import android.content.res.Configuration
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.abs

data class GCarouselUiModel<T>(
    val items: List<T>,
    val rowHeight: Dp,
    val itemSpacing: Dp,
    val contentPadding: PaddingValues = PaddingValues(0.dp),
    val fadeBackgroundColor: Color,
    val fadeWidth: Dp = 28.dp,
    val repeatFactor: Int = 48,
    val autoScrollStepPx: Float = 2.5f,
    val autoScrollTickMs: Int = 28,
    val pausedTickMs: Long = 32L,
    val centerScaleEnabled: Boolean = true,
    val centerScaleFactor: Float = 0.05f,
    val centerScaleAnimMs: Int = 120,
)

@Composable
fun <T> GAutoCarouselRow(
    uiModel: GCarouselUiModel<T>,
    keyFactory: (index: Int, item: T) -> Any,
    modifier: Modifier = Modifier,
    itemContent: @Composable (T) -> Unit,
) {
    if (uiModel.items.isEmpty()) return

    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val edgeFadePx = with(density) { uiModel.fadeWidth.toPx() }
    val displayItems = remember(uiModel.items, uiModel.repeatFactor) {
        List(uiModel.items.size * uiModel.repeatFactor) { idx -> uiModel.items[idx % uiModel.items.size] }
    }

    var fingerDown by remember { mutableStateOf(false) }
    var scrollInProgress by remember { mutableStateOf(false) }
    val paused = fingerDown || scrollInProgress

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }.collect { scrollInProgress = it }
    }

    LaunchedEffect(uiModel.items, paused, displayItems.size) {
        val cycle = uiModel.items.size
        while (isActive) {
            if (!paused) {
                listState.animateScrollBy(
                    value = uiModel.autoScrollStepPx,
                    animationSpec = tween(durationMillis = uiModel.autoScrollTickMs, easing = LinearEasing),
                )
                val first = listState.firstVisibleItemIndex
                if (first >= cycle * 2) {
                    listState.scrollToItem(
                        index = first - cycle,
                        scrollOffset = listState.firstVisibleItemScrollOffset,
                    )
                }
            } else {
                delay(uiModel.pausedTickMs)
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(uiModel.rowHeight)
            .pointerInputPauseOnTouch { fingerDown = it },
    ) {
        LazyRow(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .drawWithContent {
                    drawContent()
                    val w = size.width
                    val h = size.height
                    drawRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(uiModel.fadeBackgroundColor, Color.Transparent),
                            startX = 0f,
                            endX = edgeFadePx,
                        ),
                        topLeft = Offset.Zero,
                        size = Size(edgeFadePx, h),
                    )
                    drawRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, uiModel.fadeBackgroundColor),
                            startX = 0f,
                            endX = edgeFadePx,
                        ),
                        topLeft = Offset(w - edgeFadePx, 0f),
                        size = Size(edgeFadePx, h),
                    )
                },
            contentPadding = uiModel.contentPadding,
            horizontalArrangement = Arrangement.spacedBy(uiModel.itemSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            itemsIndexed(
                items = displayItems,
                key = keyFactory,
            ) { index, item ->
                val scale = rememberCenterScale(
                    listState = listState,
                    index = index,
                    enabled = uiModel.centerScaleEnabled,
                    factor = uiModel.centerScaleFactor,
                    animMs = uiModel.centerScaleAnimMs,
                )
                Box(
                    modifier = Modifier.graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    },
                ) {
                    itemContent(item)
                }
            }
        }
    }
}

@Composable
private fun rememberCenterScale(
    listState: LazyListState,
    index: Int,
    enabled: Boolean,
    factor: Float,
    animMs: Int,
): Float {
    if (!enabled) return 1f
    val targetScale by remember(listState, index, factor) {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visible = layoutInfo.visibleItemsInfo.find { it.index == index } ?: return@derivedStateOf 1f
            val halfViewport = (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset) / 2f
            if (halfViewport <= 0f) return@derivedStateOf 1f
            val viewportCenter = (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2f
            val itemCenter = visible.offset + visible.size / 2f
            val distance = abs(itemCenter - viewportCenter)
            val t = 1f - (distance / halfViewport).coerceIn(0f, 1f)
            1f + t * factor
        }
    }
    val animated by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(durationMillis = animMs, easing = LinearEasing),
        label = "gAutoCarouselScale",
    )
    return animated
}

private fun Modifier.pointerInputPauseOnTouch(onFingerDown: (Boolean) -> Unit): Modifier =
    this.pointerInput(Unit) {
        awaitEachGesture {
            awaitFirstDown()
            onFingerDown(true)
            waitForUpOrCancellation()
            onFingerDown(false)
        }
    }

@Preview(showBackground = true, name = "GAutoCarouselRow - Light")
@Composable
private fun PreviewGAutoCarouselLight() {
    GymTheme {
        val token = GymTheme.token
        val demo = listOf(
            token.colors.primary,
            token.colors.calorieBurned,
            token.colors.calorieIntake,
            token.colors.primarySoft,
        )
        GAutoCarouselRow(
            uiModel = GCarouselUiModel(
                items = demo,
                rowHeight = 120.dp,
                itemSpacing = token.spacing.xs,
                fadeBackgroundColor = token.colors.background,
            ),
            keyFactory = { index, _ -> index },
            modifier = Modifier.background(token.colors.background),
        ) { color ->
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(color, RoundedCornerShape(token.radius.md)),
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "GAutoCarouselRow - Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewGAutoCarouselDark() {
    GymTheme(darkTheme = true) {
        val token = GymTheme.token
        val demo = listOf(
            token.colors.primary,
            token.colors.calorieBurned,
            token.colors.calorieIntake,
            token.colors.primarySoft,
        )
        GAutoCarouselRow(
            uiModel = GCarouselUiModel(
                items = demo,
                rowHeight = 120.dp,
                itemSpacing = token.spacing.xs,
                fadeBackgroundColor = token.colors.background,
            ),
            keyFactory = { index, _ -> index },
            modifier = Modifier.background(token.colors.background),
        ) { color ->
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(color, RoundedCornerShape(token.radius.md)),
            )
        }
    }
}

