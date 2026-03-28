package com.hoabui.virtualbody3d.ui.body.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.hoabui.virtualbody3d.domain.model.common.ImageSource
import com.hoabui.virtualbody3d.ui.common_ui.atom.progress.GProgressBar
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.common_ui.image.LocalResourceProvider
import com.hoabui.virtualbody3d.ui.common_ui.image.toImageModel
import com.hoabui.virtualbody3d.ui.common_ui.organism.carousel.GAutoCarouselRow
import com.hoabui.virtualbody3d.ui.common_ui.organism.carousel.GCarouselUiModel
import com.hoabui.virtualbody3d.ui.mealcapture.MealMacroGroup
import com.hoabui.virtualbody3d.ui.mealcapture.MealPageUiModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.tokens.semantic.SemanticColorTokens

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
    contentPadding: PaddingValues = PaddingValues(),
    onItemClick: (MealPageUiModel) -> Unit = {},
) {
    if (meals.isEmpty()) return

    val token = GymTheme.token
    val fadeBg = fadeBackgroundColor ?: token.colors.background

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
        GAutoCarouselRow(
            uiModel = GCarouselUiModel(
                items = meals,
                rowHeight = token.meal.carouselRowHeight,
                itemSpacing = token.spacing.xs,
                contentPadding = contentPadding,
                fadeBackgroundColor = fadeBg,
            ),
            keyFactory = { index, item -> "${item.id}-$index" },
            modifier = Modifier.fillMaxWidth(),
        ) { item ->
            DailyMealCard(
                item = item,
                onClick = { onItemClick(item) },
            )
        }
    }
}

@Composable
private fun DailyMealCard(
    item: MealPageUiModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    val resourceProvider = LocalResourceProvider.current
    val token = GymTheme.token
    val shape = RoundedCornerShape(token.radius.md)
    val macroColor = macroColorFor(item.dominantMacro, token.colors)

    Surface(
        onClick = onClick,
        modifier = modifier
            .width(token.meal.cardWidth),
        shape = shape,
        color = token.colors.surfaceElevated,
        shadowElevation = token.borderWidth.none,
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
                        .size(token.meal.imageSize)
                        .clip(CircleShape)
                        .background(token.colors.surfaceSubtle),
                    contentAlignment = Alignment.Center,
                ) {
                    val model = remember(item.image) { item.image.toImageModel(resourceProvider) }
                    if (model != null) {
                        AsyncImage(
                            model = model,
                            contentDescription = item.title,
                            modifier = Modifier
                                .fillMaxHeight()
                                .size(token.meal.imageSize)
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
                height = token.meal.macroProgressHeight,
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

@Preview(showBackground = true)
@Composable
private fun DailyMealsAutoRowPreview() {
    GymTheme(darkTheme = true) {
        DailyMealsAutoRow(
            meals = listOf(
                MealPageUiModel(
                    id = "1",
                    image = ImageSource.LocalResource("body_unsplash"),
                    title = "Grilled salmon bowl",
                    caloriesKcal = 420,
                    caloriesText = "420 kcal",
                    macroSummaryText = "",
                    rawLines = emptyList(),
                    dominantMacro = MealMacroGroup.Protein,
                ),
                MealPageUiModel(
                    id = "2",
                    image = ImageSource.LocalResource("body_unsplash"),
                    title = "Oatmeal & berries",
                    caloriesKcal = 320,
                    caloriesText = "320 kcal",
                    macroSummaryText = "",
                    rawLines = emptyList(),
                    dominantMacro = MealMacroGroup.Carb,
                ),
                MealPageUiModel(
                    id = "3",
                    image = ImageSource.LocalResource("body_unsplash"),
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
