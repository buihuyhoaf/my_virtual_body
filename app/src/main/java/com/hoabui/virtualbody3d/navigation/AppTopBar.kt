package com.hoabui.virtualbody3d.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Light
import com.adamglin.phosphoricons.light.Bell
import com.adamglin.phosphoricons.light.CaretLeft
import com.adamglin.phosphoricons.light.Moon
import com.adamglin.phosphoricons.light.Sun
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.core.extensions.toVietnameseTopBarDate
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun AppTopBar(
    modifier: Modifier = Modifier,
    onNotificationClick: () -> Unit
) {
    val token = GymTheme.token
    val now = LocalTime.now()
    val dateText = LocalDate.now().toVietnameseTopBarDate()
    val isDayTime = now.hour in AppTopBarDefaults.dayStartHour until AppTopBarDefaults.dayEndHour
    val greeting = if (isDayTime) {
        stringResource(R.string.analysis_dashboard_greeting_day)
    } else {
        stringResource(R.string.analysis_dashboard_greeting_night)
    }
    val greetingImageVector = if (isDayTime) {
        PhosphorIcons.Light.Sun
    } else {
        PhosphorIcons.Light.Moon
    }
    val iconSize = token.spacing.lg
    val topBarGradient = Brush.verticalGradient(
        colors = listOf(
            token.colors.background,
            token.colors.backgroundSubtleGradientEnd
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(brush = topBarGradient)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(
                    start = token.spacing.lg,
                    end = token.spacing.lg,
                    bottom = token.spacing.md
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(token.spacing.xxs)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(token.spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = greetingImageVector,
                        contentDescription = null,
                        modifier = Modifier.size(iconSize),
                        tint = token.colors.primary
                    )
                    Text(
                        text = greeting,
                        style = token.typography.titleMedium,
                        color = token.colors.textPrimary
                    )
                }
                Text(
                    text = dateText,
                    style = token.typography.bodySmall,
                    color = token.colors.textSecondary
                )
            }
            IconButton(onClick = onNotificationClick) {
                BadgedBox(
                    badge = {
                        Badge(
                            containerColor = token.colors.accent,
                            contentColor = token.colors.onAccent
                        ) {
                            Text(
                                text = AppTopBarDefaults.notificationBadgeCount,
                                style = token.typography.labelMedium,
                                color = token.colors.onAccent
                            )
                        }
                    }
                ) {
                    Icon(
                        imageVector = PhosphorIcons.Light.Bell,
                        contentDescription = stringResource(R.string.analysis_dashboard_notifications),
                        modifier = Modifier.size(iconSize),
                        tint = token.colors.textPrimarySoft
                    )
                }
            }
        }
    }
}

@Composable
fun AppTopBarBack(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val token = GymTheme.token
    val iconSize = token.spacing.lg
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = token.spacing.lg,
                end = token.spacing.lg,
                bottom = token.spacing.md
            ),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = PhosphorIcons.Light.CaretLeft,
                contentDescription = stringResource(R.string.body_region_detail_back),
                modifier = Modifier.size(iconSize),
                tint = token.colors.textPrimary
            )
        }
        content()
    }
}

private object AppTopBarDefaults {
    const val dayStartHour: Int = 6
    const val dayEndHour: Int = 18
    const val notificationBadgeCount: String = "3"
}
