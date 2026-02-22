package com.hoabui.virtualbody3d.ui.calendar.extensions

import java.time.DayOfWeek

fun DayOfWeek.toCalendarOffset(): Int {
    val sundayFirst = listOf(
        DayOfWeek.SUNDAY,
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY
    )
    return sundayFirst.indexOf(this)
}
