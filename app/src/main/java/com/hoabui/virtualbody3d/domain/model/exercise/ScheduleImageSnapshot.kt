package com.hoabui.virtualbody3d.domain.model.exercise

import com.hoabui.virtualbody3d.domain.model.common.ImageSource

/**
 * Maps [ImageSource] to nullable Room columns on [WorkoutSchedule] / [WorkoutScheduleEntity].
 * URL column holds network URLs or `content://` / `file://` strings for Coil.
 */
fun ImageSource.toScheduleImageSnapshot(): Pair<String?, String?> = when (this) {
    is ImageSource.Network -> url to null
    is ImageSource.LocalResource -> null to name
    is ImageSource.ContentUri -> uriString to null
}
