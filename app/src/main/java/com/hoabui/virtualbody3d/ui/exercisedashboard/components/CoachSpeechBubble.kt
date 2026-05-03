package com.hoabui.virtualbody3d.ui.exercisedashboard.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun CoachSpeechBubble(
    text: String,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    val dash = token.dashboardExercise
    val shape = RoundedCornerShape(token.radius.pill)
    Surface(
        modifier = modifier.fillMaxWidth(dash.coachBubbleMaxWidthFraction),
        shape = shape,
        color = token.colors.surface,
    ) {
        Box(
            modifier = Modifier
                .border(
                    width = dash.coachBubbleBorderWidth,
                    color = token.colors.borderSubtle,
                    shape = shape,
                )
                .padding(
                    horizontal = dash.coachSpeechHorizontalPadding,
                    vertical = dash.coachSpeechVerticalPadding,
                ),
        ) {
            GText(
                text = text,
                modifier = Modifier.fillMaxWidth(),
                style = token.typography.bodySmall,
                color = token.colors.textPrimary,
                textAlign = TextAlign.Center,
            )
        }
    }
}
