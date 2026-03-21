package com.hoabui.virtualbody3d.ui.body.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import com.hoabui.virtualbody3d.ui.body.data.CalorieUiModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme


@Composable fun CalorieDualRingChart(
    intakeProgress: Float,
    burnedProgress: Float,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    val bodyToken = token.bodyAnalysis

    Canvas(modifier = modifier) {
        val strokeWidthPx = bodyToken.dashboardCalorieRingOuterStrokeWidth.toPx()
        val gapPx = bodyToken.dashboardCalorieRingGap.toPx()
        val halfStroke = strokeWidthPx / 2f

        val side = minOf(size.width, size.height)
        val left = (size.width - side) / 2f
        val top = (size.height - side) / 2f

        val outerRect = Rect(
            left = left + halfStroke,
            top = top + halfStroke,
            right = left + side - halfStroke,
            bottom = top + side - halfStroke
        )

        val innerRect = Rect(
            left = outerRect.left + strokeWidthPx + gapPx,
            top = outerRect.top + strokeWidthPx + gapPx,
            right = outerRect.right - strokeWidthPx - gapPx,
            bottom = outerRect.bottom - strokeWidthPx - gapPx
        )

        // Outer ring (intake)
        drawArc(
            color = token.colors.calorieRingTrack,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(outerRect.left, outerRect.top),
            size = Size(outerRect.width, outerRect.height),
            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
        )
        drawArc(
            color = token.colors.calorieIntake,
            startAngle = -90f,
            sweepAngle = 360f * intakeProgress.coerceIn(0f, 1f),
            useCenter = false,
            topLeft = Offset(outerRect.left, outerRect.top),
            size = Size(outerRect.width, outerRect.height),
            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
        )

        // Inner ring (burned)
        drawArc(
            color = token.colors.calorieRingTrack,
            startAngle = -90f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(innerRect.left, innerRect.top),
            size = Size(innerRect.width, innerRect.height),
            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
        )
        drawArc(
            color = token.colors.calorieBurned,
            startAngle = -90f,
            sweepAngle = 360f * burnedProgress.coerceIn(0f, 1f),
            useCenter = false,
            topLeft = Offset(innerRect.left, innerRect.top),
            size = Size(innerRect.width, innerRect.height),
            style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
        )
    }
}

