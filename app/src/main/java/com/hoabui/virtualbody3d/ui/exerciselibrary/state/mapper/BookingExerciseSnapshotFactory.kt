package com.hoabui.virtualbody3d.ui.exerciselibrary.state.mapper

import android.content.Context
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseMeasurementMode
import com.hoabui.virtualbody3d.domain.model.exercise.parseCartDurationTotalSecondsForSummary
import com.hoabui.virtualbody3d.domain.model.exercise.parseCartStrengthSetsRepsForSummary
import com.hoabui.virtualbody3d.ui.exerciselibrary.model.toExerciseLibraryCardImage
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.BookingExerciseSummaryUi
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseDraft
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableList

fun buildBookingExerciseSnapshot(
    context: Context,
    draftOrder: List<String>,
    exercisesById: Map<String, Exercise>,
    itemDrafts: ImmutableMap<String, ExerciseDraft>,
    exerciseMeasurementById: ImmutableMap<String, ExerciseMeasurementMode>,
): ImmutableList<BookingExerciseSummaryUi> =
    draftOrder.mapIndexedNotNull { index, id ->
        val ex = exercisesById[id] ?: return@mapIndexedNotNull null
        val draft = itemDrafts[id] ?: ExerciseDraft()
        val mode = exerciseMeasurementById[id] ?: ex.measurementMode
        val parametersSummary = formatBookingExerciseParametersSummary(context, draft, mode)
        BookingExerciseSummaryUi(
            id = ex.id,
            title = ex.name,
            image = ex.image.toExerciseLibraryCardImage(),
            orderIndex = index,
            parametersSummary = parametersSummary,
        )
    }.toImmutableList()

private fun formatBookingExerciseParametersSummary(
    context: Context,
    draft: ExerciseDraft,
    mode: ExerciseMeasurementMode,
): String =
    when (mode) {
        ExerciseMeasurementMode.Strength -> {
            val pair = parseCartStrengthSetsRepsForSummary(draft.sets, draft.reps) ?: return ""
            context.getString(
                R.string.exercise_library_booking_param_strength,
                pair.first,
                pair.second,
            )
        }
        ExerciseMeasurementMode.Duration -> {
            val total = parseCartDurationTotalSecondsForSummary(draft.sets, draft.reps) ?: return ""
            val minutes = total / 60
            val seconds = total % 60
            if (seconds == 0) {
                context.resources.getQuantityString(
                    R.plurals.exercise_library_booking_param_duration_minutes,
                    minutes,
                    minutes,
                )
            } else {
                context.getString(
                    R.string.exercise_library_booking_param_duration_min_sec,
                    minutes,
                    seconds,
                )
            }
        }
    }
