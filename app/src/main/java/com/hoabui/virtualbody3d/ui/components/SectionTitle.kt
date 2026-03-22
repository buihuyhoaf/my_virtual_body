package com.hoabui.virtualbody3d.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.theme.GymTheme

/**
 * Common section title (H1 style): bold, large typography for screen sections,
 * with optional "Xem thêm" text button on the right.
 * Use above CalorieDualRingCard, HeroSection, IncommingExercisesRow, etc.
 *
 * @param textResId String resource for the section title
 * @param onSeeMoreClick When non-null, shows "Xem thêm" button on the right and invokes this on click
 */
@Composable
fun SectionTitle(
    @StringRes textResId: Int,
    modifier: Modifier = Modifier,
    onSeeMoreClick: (() -> Unit)? = null
) {
    val token = GymTheme.token
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (onSeeMoreClick != null) Arrangement.SpaceBetween else Arrangement.Start
    ) {
        Text(
            text = stringResource(textResId),
            style = token.typography.titleMedium,
            color = token.colors.textPrimary,
            modifier = Modifier.weight(1f, fill = false)
        )
        if (onSeeMoreClick != null) {
            TextButton(onClick = onSeeMoreClick) {
                Text(
                    text = stringResource(R.string.home_section_see_more),
                    style = token.typography.labelLarge,
                    color = token.colors.primary
                )
            }
        }
    }
}
