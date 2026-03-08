package com.hoabui.virtualbody3d.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun AppBottomBar(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = token.spacing.md)
            .navigationBarsPadding(),
        shape = RoundedCornerShape(token.radius.lg),
        color = token.colors.dashboardFloatingNavBackground,
        border = androidx.compose.foundation.BorderStroke(
            width = bodyToken.topBarBorderWidth,
            color = token.colors.dashboardFloatingNavBorder
        ),
        shadowElevation = token.card.elevation
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = bodyToken.dashboardFloatingNavHorizontalPadding,
                    vertical = bodyToken.dashboardFloatingNavVerticalPadding
                ),
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppDestination.bottomBarDestinations.forEach { destination ->
                val selected = currentDestination
                    ?.hierarchy
                    ?.any { it.route == destination.route } == true
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                        .padding(bodyToken.bottomBarItemPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(bodyToken.bottomBarLabelTopSpacing)
                ) {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = stringResource(destination.labelResId),
                        tint = if (selected) token.colors.primary else token.colors.textSecondary
                    )
                    Text(
                        text = stringResource(destination.labelResId),
                        style = token.typography.labelMedium,
                        color = if (selected) token.colors.primary else token.colors.textSecondary,
                        maxLines = 2,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
