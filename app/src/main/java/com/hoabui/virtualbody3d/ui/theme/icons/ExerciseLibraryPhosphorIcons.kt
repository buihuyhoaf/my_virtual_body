package com.hoabui.virtualbody3d.ui.theme.icons

import androidx.compose.ui.graphics.vector.ImageVector
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Fill
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.fill.Barbell
import com.adamglin.phosphoricons.fill.CheckCircle
import com.adamglin.phosphoricons.fill.MapPin
import com.adamglin.phosphoricons.fill.Wrench
import com.adamglin.phosphoricons.regular.Calendar
import com.adamglin.phosphoricons.regular.Clock
import com.adamglin.phosphoricons.regular.MagnifyingGlass
import com.adamglin.phosphoricons.regular.Plus
import com.adamglin.phosphoricons.regular.Timer
import com.adamglin.phosphoricons.regular.X

/**
 * Central Phosphor [ImageVector] references for Exercise Library (no Material icons).
 */
object ExerciseLibraryPhosphorIcons {
    val listToggleNotInCart: ImageVector get() = PhosphorIcons.Regular.Plus
    val listToggleInCart: ImageVector get() = PhosphorIcons.Fill.CheckCircle
    val cartRemove: ImageVector get() = PhosphorIcons.Regular.X
    val bookingSheetClose: ImageVector get() = PhosphorIcons.Regular.X
    val bookingCalendar: ImageVector get() = PhosphorIcons.Regular.Calendar
    val bookingClock: ImageVector get() = PhosphorIcons.Regular.Clock
    val bookingMapPin: ImageVector get() = PhosphorIcons.Fill.MapPin
    val search: ImageVector get() = PhosphorIcons.Regular.MagnifyingGlass
    val cartScheduleDate: ImageVector get() = PhosphorIcons.Regular.Calendar
    val cartScheduleTime: ImageVector get() = PhosphorIcons.Regular.Clock
    val cartDurationTimer: ImageVector get() = PhosphorIcons.Regular.Timer
    val workoutPlanFab: ImageVector get() = PhosphorIcons.Regular.Calendar
    val addSuccess: ImageVector get() = PhosphorIcons.Fill.CheckCircle
    val detailBodyRegion: ImageVector get() = PhosphorIcons.Fill.MapPin
    val detailEquipment: ImageVector get() = PhosphorIcons.Fill.Wrench
    val detailCategory: ImageVector get() = PhosphorIcons.Fill.Barbell
    val filterChipSelected: ImageVector get() = PhosphorIcons.Fill.CheckCircle
}
