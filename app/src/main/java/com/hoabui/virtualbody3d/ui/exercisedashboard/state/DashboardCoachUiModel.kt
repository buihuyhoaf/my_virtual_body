package com.hoabui.virtualbody3d.ui.exercisedashboard.state

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable

@Immutable
data class DashboardCoachUiModel(
    val speechText: String,
    @DrawableRes val coachImageRes: Int,
) {
    companion object {
        fun placeholder(@DrawableRes imageRes: Int, speech: String) =
            DashboardCoachUiModel(speechText = speech, coachImageRes = imageRes)
    }
}
