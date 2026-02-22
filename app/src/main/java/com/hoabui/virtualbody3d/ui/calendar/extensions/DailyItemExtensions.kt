package com.hoabui.virtualbody3d.ui.calendar.extensions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.ui.graphics.vector.ImageVector
import com.hoabui.virtualbody3d.ui.calendar.state.DailyItem
import com.hoabui.virtualbody3d.ui.calendar.state.DailyItemType
import kotlin.math.absoluteValue

fun DailyItem.toIcon(): ImageVector {
    return when (type) {
        DailyItemType.Meal -> Icons.Default.LocalDining
        DailyItemType.Activity -> Icons.AutoMirrored.Filled.DirectionsRun
    }
}

fun DailyItem.estimatedKcal(): Int? {
    if (type != DailyItemType.Meal) return null
    return 220 + (id.hashCode().absoluteValue % 280)
}
