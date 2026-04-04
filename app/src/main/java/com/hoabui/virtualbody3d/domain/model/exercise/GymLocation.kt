package com.hoabui.virtualbody3d.domain.model.exercise

import androidx.compose.runtime.Immutable

/**
 * Training facility selectable when booking a session.
 */
@Immutable
data class GymLocation(
    val id: String,
    val displayName: String,
)
