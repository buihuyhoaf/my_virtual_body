package com.hoabui.virtualbody3d.ui.exerciselibrary.data

import androidx.annotation.DrawableRes
import com.hoabui.virtualbody3d.R

/**
 * Resource entry names (drawable) for the focus-muscle strip. Used in UI state; resolved to ids in
 * [rememberFocusMusclesStripDrawableResIds].
 */
const val FOCUS_STRIP_DEFAULT_QUADRANT_NAME: String = "back_normal"
const val FOCUS_STRIP_FRONT_UPPER_EMPTY_NAME: String = "chest_normal"

/**
 * [android.content.res.Resources.getIdentifier] input for a strip art resource id.
 */
fun focusStripDrawableNameOrFallback(@DrawableRes res: Int): String = when (res) {
    R.drawable.chest_normal -> "chest_normal"
    R.drawable.chest_full_chest -> "chest_full_chest"
    R.drawable.chest_upper_pectoralis -> "chest_upper_pectoralis"
    R.drawable.chest_lower_pectoralis -> "chest_lower_pectoralis"
    R.drawable.back_normal -> "back_normal"
    R.drawable.back_infraspinatus -> "back_infraspinatus"
    R.drawable.back_latissimus_dorsi -> "back_latissimus_dorsi"
    R.drawable.back_middle_trapezius -> "back_middle_trapezius"
    R.drawable.back_infraspinatus_middle_trapezius -> "back_infraspinatus_middle_trapezius"
    R.drawable.back_infraspinatus_latissimus_dorsi -> "back_infraspinatus_latissimus_dorsi"
    R.drawable.back_latissimus_dorsi_middle_trapezius -> "back_latissimus_dorsi_middle_trapezius"
    R.drawable.back_full_back -> "back_full_back"
    R.drawable.belly_full_belly -> "belly_full_belly"
    R.drawable.belly_external_obliques -> "belly_external_obliques"
    R.drawable.belly_rectus_abdominis -> "belly_rectus_abdominis"
    else -> FOCUS_STRIP_DEFAULT_QUADRANT_NAME
}
