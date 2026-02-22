package com.hoabui.virtualbody3d.ui.calendar.model

import java.time.LocalDate
import java.time.YearMonth

data class MonthGridUiModel(
    val month: YearMonth,
    val cells: List<LocalDate?>
)
