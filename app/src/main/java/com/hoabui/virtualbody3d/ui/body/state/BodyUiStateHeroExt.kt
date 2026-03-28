package com.hoabui.virtualbody3d.ui.body.state

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun BodyUiState.heroBmiStatusLabel(): String = when (bmiCategory) {
    BodyBmiCategory.UNKNOWN -> stringResource(R.string.home_hero_bmi_analyzing)
    BodyBmiCategory.UNDERWEIGHT -> stringResource(R.string.analysis_under)
    BodyBmiCategory.NORMAL -> stringResource(R.string.analysis_normal)
    BodyBmiCategory.OVERWEIGHT -> stringResource(R.string.analysis_over)
    BodyBmiCategory.OBESE -> stringResource(R.string.analysis_obese)
}

@Composable
fun BodyUiState.heroBmiIndicatorColor(): Color {
    val colors = GymTheme.token.colors
    return when (bmiCategory) {
        BodyBmiCategory.UNKNOWN -> colors.primary
        BodyBmiCategory.NORMAL -> colors.success
        BodyBmiCategory.UNDERWEIGHT, BodyBmiCategory.OVERWEIGHT -> colors.warning
        BodyBmiCategory.OBESE -> colors.error
    }
}
