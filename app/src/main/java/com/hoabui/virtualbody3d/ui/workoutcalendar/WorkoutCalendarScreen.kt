package com.hoabui.virtualbody3d.ui.workoutcalendar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.core.extensions.toWorkoutCalendarLineUiModel
import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarExerciseLineUiModel
import com.hoabui.virtualbody3d.ui.common_ui.molecule.topbar.GTopBar
import com.hoabui.virtualbody3d.ui.common_ui.molecule.topbar.GTopBarBackIcon
import com.hoabui.virtualbody3d.ui.common_ui.organism.scaffold.GScaffold
import com.hoabui.virtualbody3d.ui.common_ui.organism.workoutcalendar.WorkoutCalendarDayExerciseListOrganism
import com.hoabui.virtualbody3d.ui.common_ui.organism.workoutcalendar.WorkoutCalendarMonthGridOrganism
import com.hoabui.virtualbody3d.ui.components.UiStateContent
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.workoutcalendar.model.buildMonthGridCells
import com.hoabui.virtualbody3d.ui.workoutcalendar.viewmodel.WorkoutCalendarContent
import com.hoabui.virtualbody3d.ui.workoutcalendar.viewmodel.WorkoutCalendarViewModel
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun WorkoutCalendarScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WorkoutCalendarViewModel = hiltViewModel(),
) {
    val zoneId = ZoneId.systemDefault()
    val today = LocalDate.now(zoneId)
    val screen by viewModel.state.collectAsStateWithLifecycle()
    GScaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            GTopBar(
                title = stringResource(R.string.workout_calendar_title),
                windowInsets = WindowInsets(0),
                navigationIcon = {
                    GTopBarBackIcon(onBack = onBack)
                },
            )
        },
    ) { padding ->
        UiStateContent(
            state = screen,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            successContent = { mod, data ->
                WorkoutCalendarSuccessContent(
                    modifier = mod,
                    data = data,
                    today = today,
                    viewModel = viewModel,
                )
            },
        )
    }
}

@Composable
private fun WorkoutCalendarSuccessContent(
    modifier: Modifier,
    data: WorkoutCalendarContent,
    today: LocalDate,
    viewModel: WorkoutCalendarViewModel,
) {
    val cal = GymTheme.token.workoutCalendar
    val resources = LocalResources.current
    val cells = buildMonthGridCells(
        yearMonth = data.visibleYearMonth,
        selected = data.selectedDate,
        today = today,
        summaries = data.summariesByEpochDay,
    )
    val lineUiModels: List<WorkoutCalendarExerciseLineUiModel> =
        data.dayLines.map { line -> line.toWorkoutCalendarLineUiModel(resources) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = cal.screenHorizontalPadding,
                vertical = cal.screenVerticalPadding,
            ),
    ) {
        WorkoutCalendarMonthGridOrganism(
            yearMonth = data.visibleYearMonth,
            cells = cells,
            onPreviousMonth = {
                viewModel.onVisibleMonthChanged(data.visibleYearMonth.minusMonths(1))
            },
            onNextMonth = {
                viewModel.onVisibleMonthChanged(data.visibleYearMonth.plusMonths(1))
            },
            onDayClick = viewModel::onDaySelected,
        )
        Spacer(modifier = Modifier.height(cal.sectionGapMajor))
        WorkoutCalendarDayExerciseListOrganism(
            selectedDate = data.selectedDate,
            lines = lineUiModels,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .weight(1f),
        )
    }
}
