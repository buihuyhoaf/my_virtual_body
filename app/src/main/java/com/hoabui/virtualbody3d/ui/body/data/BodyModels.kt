package com.hoabui.virtualbody3d.ui.body.data

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.body.components.PromoBannerCarousel

/** UI model for a favorite exercise card (name, last lifted weight, optional image, optional trend e.g. "+2.5kg"). */
data class FavoriteExerciseUiItem(
    val name: String,
    val exerciseVolume: String,
    val imageResId: Int = R.drawable.body_unsplash,
    val trendText: String? = null
)

/** UI model for a supplement card: image, name, and main nutrient/mineral. */
data class SupplementUiItem(
    val name: String,
    val nutrient: String,
    val imageResId: Int = R.drawable.body_unsplash
)

/**
 * Data for a single promotional or informational banner in [PromoBannerCarousel].
 *
 * @param title Optional overlay title text.
 * @param subtitle Optional overlay subtitle text.
 * @param onClick Optional action when the banner is tapped.
 * @param backgroundImageRes Optional drawable resource for the banner background image.
 * @param backgroundGradientColors Optional list of colors for a vertical gradient background.
 *   If both [backgroundImageRes] and [backgroundGradientColors] are null, the theme's
 *   [com.hoabui.virtualbody3d.ui.theme.tokens.semantic.SemanticColorTokens.primarySoft]
 *   is used as a fallback for readability.
 */
data class PromoBannerItem(
    val onClick: (() -> Unit)? = null,
    val backgroundImageRes: Int? = null,
    val backgroundImageResUrl : String? = null,
    val backgroundGradientColors: List<Color>? = null
)


@Immutable
data class NutritionSummaryUiState(
    val intake: Int = 0,
    val burned: Int = 0,
    val goal: Int = 1
)
