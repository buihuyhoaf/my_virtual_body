package com.hoabui.virtualbody3d.ui.exercisedashboard.viewmodel

import android.content.Context
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.core.base.UiStateViewModel
import com.hoabui.virtualbody3d.domain.model.exercise.dashboard.ExerciseDashboardLastSessionRecap
import com.hoabui.virtualbody3d.domain.model.exercise.dashboard.ExerciseLibraryWeekStripDay
import com.hoabui.virtualbody3d.domain.usecase.ObserveExerciseDashboardLastSessionUseCase
import com.hoabui.virtualbody3d.domain.usecase.ObserveExerciseLibraryWeekStripUseCase
import com.hoabui.virtualbody3d.ui.exercisedashboard.DashboardCategoryTiles
import com.hoabui.virtualbody3d.ui.exercisedashboard.state.DashboardAchievementUiModel
import com.hoabui.virtualbody3d.ui.exercisedashboard.state.DashboardCoachUiModel
import com.hoabui.virtualbody3d.ui.exercisedashboard.state.ExerciseDashboardUiState
import com.hoabui.virtualbody3d.ui.exercisedashboard.state.ExerciseLibraryWeekStripUiState
import com.hoabui.virtualbody3d.ui.exercisedashboard.state.WeekStripDayUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Clock
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@HiltViewModel
class ExerciseDashboardViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val observeExerciseDashboardLastSessionUseCase: ObserveExerciseDashboardLastSessionUseCase,
    private val observeExerciseLibraryWeekStripUseCase: ObserveExerciseLibraryWeekStripUseCase,
) : UiStateViewModel<ExerciseDashboardUiState, Unit>() {

    init {
        launchSafely {
            val locale = Locale.getDefault()
            val heatmapFlow: Flow<ExerciseLibraryWeekStripUiState> =
                observeExerciseLibraryWeekStripUseCase()
                    .map { days ->
                        ExerciseLibraryWeekStripUiState.Loaded(days.toWeekStripUi(locale))
                            as ExerciseLibraryWeekStripUiState
                    }
                    .catch {
                        emit(
                            ExerciseLibraryWeekStripUiState.Error(
                                it.message
                                    ?: appContext.getString(R.string.exercise_library_weekly_heatmap_error),
                            ),
                        )
                    }
            combine(observeExerciseDashboardLastSessionUseCase(), heatmapFlow) { recap, heatmapState ->
                ExerciseDashboardUiState(
                    achievement = recap?.toAchievementUiModel(),
                    coach = coachUiModel(),
                    categories = DashboardCategoryTiles.categories,
                    heatmap = heatmapState,
                )
            }.collect { setSuccess(it) }
        }
    }

    private fun coachUiModel(): DashboardCoachUiModel {
        val zone = Clock.systemDefaultZone()
        val hour = zone.instant().atZone(zone.zone).toLocalTime().hour
        val res = when (hour) {
            in 5..11 -> R.string.exercise_dashboard_coach_morning
            in 12..16 -> R.string.exercise_dashboard_coach_afternoon
            in 17..23 -> R.string.exercise_dashboard_coach_evening
            else -> R.string.exercise_dashboard_coach_default
        }
        return DashboardCoachUiModel(
            speechText = appContext.getString(res),
            coachImageRes = R.drawable.whitecat,
        )
    }

    private fun ExerciseDashboardLastSessionRecap.toAchievementUiModel(): DashboardAchievementUiModel =
        DashboardAchievementUiModel(
            anchorEpochDay = anchorDate.toEpochDay(),
            exerciseTitlesLine = exerciseTitlesJoined,
            totalKcal = totalKcalRounded,
            durationMinutes = durationMinutes,
        )

    private fun List<ExerciseLibraryWeekStripDay>.toWeekStripUi(locale: Locale) =
        map { day ->
            val local = LocalDate.ofEpochDay(day.epochDay)
            WeekStripDayUiModel(
                dayAbbrev = local.dayOfWeek.getDisplayName(TextStyle.NARROW, locale),
                densityLevel = day.densityLevel.coerceIn(0, 3),
            )
        }.toPersistentList()

    override fun onError(throwable: Throwable) {
        setError(throwable.message ?: "Unknown error")
    }
}
