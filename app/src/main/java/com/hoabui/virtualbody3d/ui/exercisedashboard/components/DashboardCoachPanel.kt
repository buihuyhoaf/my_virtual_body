package com.hoabui.virtualbody3d.ui.exercisedashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.hoabui.virtualbody3d.ui.exercisedashboard.state.DashboardCoachUiModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun DashboardCoachPanel(
    coach: DashboardCoachUiModel,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val dash = token.dashboardExercise
    Column(
        modifier = modifier.fillMaxHeight().fillMaxWidth().padding(end = dash.middleSectionHorizontalSpacing),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        CoachSpeechBubble(
            text = coach.speechText,
            modifier = Modifier.padding(bottom = token.spacing.sm),
        )
        Spacer(modifier = Modifier.height(token.spacing.xs))
        AsyncImage(
            model = coach.coachImageRes,
            contentDescription = coach.speechText,
            modifier = Modifier
                .weight(1f, fill = true)
                .fillMaxWidth(),
            contentScale = ContentScale.Fit,
        )
    }
}
