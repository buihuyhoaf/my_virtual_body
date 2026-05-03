package com.hoabui.virtualbody3d.ui.exercisedashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.MetricLabelText
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.MetricValueText
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun DashboardMetricRow(
    kcalDisplay: Int,
    durationMinutesDisplay: Int,
    modifier: Modifier = Modifier,
) {
    val token = GymTheme.token
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        DashboardMetricSlot(
            value = "${kcalDisplay.coerceAtLeast(0)}",
            label = stringResource(R.string.exercise_dashboard_metric_kcal),
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(token.spacing.md))
        DashboardMetricSlot(
            value = "${durationMinutesDisplay.coerceAtLeast(0)}",
            label = stringResource(R.string.exercise_dashboard_metric_min),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun DashboardMetricSlot(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        MetricValueText(text = value)
        MetricLabelText(text = label)
    }
}
