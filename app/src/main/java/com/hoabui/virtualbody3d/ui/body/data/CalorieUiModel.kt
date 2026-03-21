package com.hoabui.virtualbody3d.ui.body.data

import androidx.compose.runtime.Immutable

@Immutable
data class CalorieUiModel(
    val intake: Int,
    val burned: Int,
    val intakeGoal: Int,
    val burnGoal: Int
) {
    val intakeProgress: Float =
        if (intakeGoal == 0) 0f
        else (intake.toFloat() / intakeGoal).coerceIn(0f, 1f)

    val burnedProgress: Float =
        if (burnGoal == 0) 0f
        else (burned.toFloat() / burnGoal).coerceIn(0f, 1f)

    val net: Int = intake - burned
    val deficit: Int = intakeGoal - net
}

