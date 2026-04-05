package com.hoabui.virtualbody3d.ui.body.data

import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.R

/** UI model for an exercise card on the body home row (name, reps/sets, optional image, optional trend e.g. "+2.5kg"). */
data class ExerciseUiItem(
    val name: String,
    val reps: Int,
    val sets: Int,
    val imageResId: Int = R.drawable.body_unsplash,
    val trendText: String? = null
)

@Immutable
data class CalorieGoalUiModel(
    val intake: Int = 0,
    val burned: Int = 0,
    val intakeGoal: Int = 0,
    val burnGoal: Int = 0
) {
    val intakeProgress: Float
        get() = if (intakeGoal == 0) 0f else (intake.toFloat() / intakeGoal).coerceAtLeast(0f)

    val burnedProgress: Float
        get() = if (burnGoal == 0) 0f else (burned.toFloat() / burnGoal).coerceAtLeast(0f)

    val net: Int
        get() = intake - burned

    val deficit: Int
        get() = intakeGoal - net
}
