package com.hoabui.virtualbody3d.ui.body.components

import android.net.Uri
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import com.hoabui.virtualbody3d.ui.common_ui.atom.progress.GProgressBar
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.hoabui.virtualbody3d.ui.mealcapture.MealMacroGroup
import com.hoabui.virtualbody3d.ui.mealcapture.MealPageUiModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.tokens.semantic.SemanticColorTokens
import kotlin.math.abs
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/**
 * Tiêu đề + [LazyRow] tự cuộn (marquee) vòng lặp vô hạn; tạm dừng khi user chạm hoặc đang kéo.
 * Thẻ giữa viewport được phóng nhẹ (1.05x); hai mép có fade theo [fadeBackgroundColor].
 *
 * Dữ liệu hiển thị là [MealPageUiModel] do [com.hoabui.virtualbody3d.ui.mealcapture.MealsViewModel]
 * cập nhật sau khi tải ngày (GetMealDaysUseCase) và bữa ăn theo ngày (GetMealsByDayUseCase), ví dụ [com.hoabui.virtualbody3d.ui.mealcapture.MealsViewModel.mealsForToday].
 */
@Composable
fun DailyMealsAutoRow(
    meals: List<MealPageUiModel>,
    modifier: Modifier = Modifier,
    title: String = "Today's Meals",
    fadeBackgroundColor: Color? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 0.dp),
    onItemClick: (MealPageUiModel) -> Unit = {},
) {
    if (meals.isEmpty()) return

    val token = GymTheme.token
    val fadeBg = fadeBackgroundColor ?: token.colors.background
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val edgeFadePx = with(density) { 28.dp.toPx() }

    val repeatFactor = 48
    val displayItems = remember(meals, repeatFactor) {
        List(meals.size * repeatFactor) { idx -> meals[idx % meals.size] }
    }

    var fingerDown by remember { mutableStateOf(false) }
    var scrollInProgress by remember { mutableStateOf(false) }
    val paused = fingerDown || scrollInProgress

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }.collect { scrollInProgress = it }
    }

    LaunchedEffect(meals, paused, displayItems.size) {
        if (meals.isEmpty()) return@LaunchedEffect
        val cycle = meals.size
        while (isActive) {
            if (!paused) {
                listState.animateScrollBy(
                    value = 2.5f,
                    animationSpec = tween(durationMillis = 28, easing = LinearEasing),
                )
                val first = listState.firstVisibleItemIndex
                if (first >= cycle * 2) {
                    listState.scrollToItem(
                        index = first - cycle,
                        scrollOffset = listState.firstVisibleItemScrollOffset,
                    )
                }
            } else {
                delay(32L)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(token.spacing.xs),
    ) {
        GText(
            text = title,
            style = token.typography.titleMedium,
            color = token.colors.textPrimary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = token.spacing.md),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(104.dp)
                .pointerInputFingerPauseDaily { fingerDown = it },
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
                                colors = listOf(fadeBg, Color.Transparent),
                                startX = 0f,
                                endX = edgeFadePx,
                            ),
                            topLeft = Offset.Zero,
                            size = Size(edgeFadePx, h),
                        )
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, fadeBg),
                                startX = 0f,
                                endX = edgeFadePx,
                            ),
                            topLeft = Offset(w - edgeFadePx, 0f),
                            size = Size(edgeFadePx, h),
                        )
                    },
                contentPadding = contentPadding,
                horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                itemsIndexed(
                    items = displayItems,
                    key = { index, item -> "${item.id}-$index" },
                ) { index, item ->
                    DailyMealCard(
                        item = item,
                        index = index,
                        listState = listState,
                        onClick = { onItemClick(item) },
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyMealCard(
    item: MealPageUiModel,
    index: Int,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val token = GymTheme.token
    val shape = RoundedCornerShape(token.radius.md)
    val macroColor = macroColorFor(item.dominantMacro, token.colors)

    val targetScale by remember(listState, index) {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visible = layoutInfo.visibleItemsInfo.find { it.index == index }
                ?: return@derivedStateOf 1f
            val halfViewport =
                (layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset) / 2f
            if (halfViewport <= 0f) return@derivedStateOf 1f
            val viewportCenter =
                (layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset) / 2f
            val itemCenter = visible.offset + visible.size / 2f
            val distance = abs(itemCenter - viewportCenter)
            val t = 1f - (distance / halfViewport).coerceIn(0f, 1f)
            1f + t * 0.05f
        }
    }

    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(durationMillis = 120, easing = LinearEasing),
        label = "dailyMealCardScale",
    )

    Surface(
        onClick = onClick,
        modifier = modifier
            .width(228.dp)
            .graphicsLayerDaily(scale = animatedScale),
        shape = shape,
        color = token.colors.surfaceElevated,
        shadowElevation = 0.dp,
        tonalElevation = token.elevation.level1,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = token.spacing.xs, vertical = token.spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(token.colors.surfaceSubtle),
                    contentAlignment = Alignment.Center,
                ) {
                    if (item.imageUri != Uri.EMPTY) {
                        AsyncImage(
                            model = item.imageUri,
                            contentDescription = item.title,
                            modifier = Modifier
                                .fillMaxHeight()
                                .size(52.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(token.spacing.xxxs),
                ) {
                    GText(
                        text = item.title,
                        style = token.typography.titleSmall,
                        color = token.colors.textPrimary,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    GText(
                        text = "${item.caloriesKcal} kcal",
                        style = token.typography.labelLarge,
                        color = macroColor,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                    )
                }
            }
            GProgressBar(
                progress = 1f,
                modifier = Modifier.fillMaxWidth(),
                indicatorColor = macroColor,
                trackColor = token.colors.borderSubtle.copy(alpha = 0.35f),
                height = 3.dp,
            )
        }
    }
}

private fun macroColorFor(
    group: MealMacroGroup,
    colors: SemanticColorTokens,
): Color = when (group) {
    MealMacroGroup.Protein -> colors.calorieBurned
    MealMacroGroup.Carb -> colors.calorieIntake
    MealMacroGroup.Fat -> colors.primary
}

private fun Modifier.graphicsLayerDaily(scale: Float): Modifier =
    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }

private fun Modifier.pointerInputFingerPauseDaily(onFingerDown: (Boolean) -> Unit): Modifier =
    this.pointerInput(Unit) {
        awaitEachGesture {
            awaitFirstDown()
            onFingerDown(true)
            waitForUpOrCancellation()
            onFingerDown(false)
        }
    }

@Preview(showBackground = true)
@Composable
private fun DailyMealsAutoRowPreview() {
    GymTheme(darkTheme = true) {
        DailyMealsAutoRow(
            meals = listOf(
                MealPageUiModel(
                    id = "1",
                    imageUri = Uri.EMPTY,
                    title = "Grilled salmon bowl",
                    caloriesKcal = 420,
                    caloriesText = "420 kcal",
                    macroSummaryText = "",
                    rawLines = emptyList(),
                    dominantMacro = MealMacroGroup.Protein,
                ),
                MealPageUiModel(
                    id = "2",
                    imageUri = Uri.EMPTY,
                    title = "Oatmeal & berries",
                    caloriesKcal = 320,
                    caloriesText = "320 kcal",
                    macroSummaryText = "",
                    rawLines = emptyList(),
                    dominantMacro = MealMacroGroup.Carb,
                ),
                MealPageUiModel(
                    id = "3",
                    imageUri = Uri.EMPTY,
                    title = "Avocado toast",
                    caloriesKcal = 280,
                    caloriesText = "280 kcal",
                    macroSummaryText = "",
                    rawLines = emptyList(),
                    dominantMacro = MealMacroGroup.Fat,
                ),
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(GymTheme.token.colors.background)
                .padding(vertical = GymTheme.token.spacing.xs),
        )
    }
}
