package com.hoabui.virtualbody3d.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.hoabui.virtualbody3d.core.extensions.rememberBottomBarItemState
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.tokens.GymToken

data class BottomBarItemState(
    val backgroundColor: Color,
    val contentColor: Color,
    val scale: Float
)

@Composable
private fun RowScope.BottomBarItem(
    destination: AppDestination,
    selected: Boolean,
    onClick: () -> Unit,
    token: GymToken,
) {
    val bodyToken = token.bodyAnalysis
    val interactionSource = remember { MutableInteractionSource() }
    val itemState = rememberBottomBarItemState(
        selected = selected,
        interactionSource = interactionSource,
        token = token
    )

    Column(
        modifier = Modifier
            .weight(1f)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(bodyToken.bottomBarIconContainerSize)
                .scale(itemState.scale)
                .background(
                    color = itemState.backgroundColor,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            destination.iconResId?.let { iconId ->
                Icon(
                    painter = painterResource(id = iconId),
                    contentDescription = stringResource(destination.labelResId),
                    tint = itemState.contentColor,
                    modifier = Modifier.size(bodyToken.bottomBarIconSize)
                )
            }
        }

        Spacer(modifier = Modifier.height(bodyToken.bottomBarLabelTopSpacing))

        Text(
            text = stringResource(destination.labelResId),
            style = token.typography.labelMedium,
            color = itemState.contentColor,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

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
            .navigationBarsPadding(),
        shape = RectangleShape,
        color = token.colors.dashboardFloatingNavBackground,
        shadowElevation = token.card.elevation
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = bodyToken.dashboardFloatingNavHorizontalPadding,
                    vertical = token.spacing.md
                ),
            horizontalArrangement = Arrangement.spacedBy(token.spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppDestination.bottomBarDestinations.forEach { destination ->
                val selected = currentDestination
                    ?.hierarchy
                    ?.any { it.route == destination.route } == true
                BottomBarItem(
                    destination = destination,
                    selected = selected,
                    token = token,
                    onClick = {
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}
