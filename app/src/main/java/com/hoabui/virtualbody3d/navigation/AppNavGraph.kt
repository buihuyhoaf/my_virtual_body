package com.hoabui.virtualbody3d.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hoabui.virtualbody3d.ui.body.screen.BodyAnalysisRoute
import com.hoabui.virtualbody3d.ui.body.viewmodel.BodyViewModel
import com.hoabui.virtualbody3d.ui.calendar.screen.CalendarScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    sharedViewModel: BodyViewModel,
    modifier: Modifier = Modifier
) {
    val screenState by sharedViewModel.screenState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = AppDestination.startDestination.route,
        modifier = modifier
    ) {
        composable(route = AppDestination.Home.route) {
            BodyAnalysisRoute(viewModel = sharedViewModel)
        }
        composable(route = AppDestination.Add.route) {
            AppDestinationPlaceholder(labelResId = AppDestination.Add.labelResId)
        }
        composable(route = AppDestination.Calendar.route) {
            CalendarScreen(
                months = screenState.calendarMonths,
                selectedDate = screenState.selectedDate,
                dailyItemsByDate = screenState.dailyItemsByDate,
                onDateSelected = sharedViewModel::onDateSelected,
                onLoadMoreMonths = sharedViewModel::loadMoreCalendarMonths
            )
        }
    }
}

@Composable
private fun AppDestinationPlaceholder(labelResId: Int) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = stringResource(labelResId))
    }
}
