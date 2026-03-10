package com.hoabui.virtualbody3d.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.RowScope
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.body.state.BodyRegion
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AppTopBar(
    modifier: Modifier = Modifier,
    onNotificationClick: () -> Unit
) {
    val token = GymTheme.token
    val now = LocalTime.now()
    val dateText = LocalDate.now().format(
        DateTimeFormatter.ofPattern("yyyy EEE MMM dd", Locale.ENGLISH)
    )
    val isDayTime = now.hour in AppTopBarDefaults.dayStartHour until AppTopBarDefaults.dayEndHour
    val greeting = if (isDayTime) {
        stringResource(R.string.analysis_dashboard_greeting_day)
    } else {
        stringResource(R.string.analysis_dashboard_greeting_night)
    }
    val greetingIcon = if (isDayTime) Icons.Default.WbSunny else Icons.Default.NightsStay

    Row(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(
                start = token.spacing.md,
                end = token.spacing.md,
                bottom = token.spacing.md
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(token.spacing.xxs)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = greetingIcon,
                    contentDescription = null,
                    tint = token.colors.primary
                )
                Text(
                    text = greeting,
                    style = token.typography.titleMedium
                )
            }
            Text(
                text = dateText,
                style = token.typography.bodyMedium,
                color = token.colors.textSecondary
            )
        }
        Surface(
            shape = RoundedCornerShape(token.radius.md),
            color = token.colors.surfaceOverlay,
            border = androidx.compose.foundation.BorderStroke(
                width = token.bodyAnalysis.topBarBorderWidth,
                color = token.colors.surfaceBorder
            )
        ) {
            IconButton(
                onClick = onNotificationClick,
                modifier = Modifier.size(token.bodyAnalysis.topBarIconSize)
            ) {
                BadgedBox(
                    badge = {
                        Badge(
                            containerColor = token.colors.error,
                            contentColor = token.colors.surface
                        ) {
                            Text(
                                text = AppTopBarDefaults.notificationBadgeCount,
                                style = token.typography.labelMedium
                            )
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = stringResource(R.string.analysis_dashboard_notifications),
                        tint = token.colors.textPrimary
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
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = token.spacing.xxs,
                end = token.spacing.md,
                bottom = token.spacing.md
            ),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.body_region_detail_back),
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
