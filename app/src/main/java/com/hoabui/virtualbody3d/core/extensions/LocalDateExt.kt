package com.hoabui.virtualbody3d.core.extensions

import java.time.DayOfWeek
import java.time.LocalDate

fun LocalDate.toVietnameseTopBarDate(): String {
    val dayName = when (dayOfWeek) {
        DayOfWeek.MONDAY -> "thứ Hai"
        DayOfWeek.TUESDAY -> "thứ Ba"
        DayOfWeek.WEDNESDAY -> "thứ Tư"
        DayOfWeek.THURSDAY -> "thứ Năm"
        DayOfWeek.FRIDAY -> "thứ Sáu"
        DayOfWeek.SATURDAY -> "thứ Bảy"
        DayOfWeek.SUNDAY -> "Chủ Nhật"
    }

    return "$year $dayName ${dayOfMonth}."
}

