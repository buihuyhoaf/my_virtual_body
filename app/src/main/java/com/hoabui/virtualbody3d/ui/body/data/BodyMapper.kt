package com.hoabui.virtualbody3d.ui.body.data

import com.hoabui.virtualbody3d.domain.model.nutrition.NutritionSummary

fun NutritionSummary.toCalorieGoalUiModel(): CalorieGoalUiModel {
    return CalorieGoalUiModel(
        intake = intake,
        burned = burned,
        intakeGoal = goal,
        burnGoal = goal
    )
}
