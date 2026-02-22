package com.hoabui.virtualbody3d.ui.calendar.state

enum class DailyItemType {
    Meal,
    Activity
}

data class DailyItem(
    val id: String,
    val title: String,
    val type: DailyItemType,
    val thumbnailResId: Int
)
