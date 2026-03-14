package com.hoabui.virtualbody3d.data.mapper

import com.hoabui.virtualbody3d.data.model.NutritionSummaryDto
import com.hoabui.virtualbody3d.domain.model.NutritionSummary

fun NutritionSummaryDto.toDomain(): NutritionSummary {
    return NutritionSummary(
        intake = intake,
        burned = burned,
        goal = goal
    )
}
