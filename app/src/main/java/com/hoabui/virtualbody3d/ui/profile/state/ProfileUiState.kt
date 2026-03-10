package com.hoabui.virtualbody3d.ui.profile.state

import androidx.compose.runtime.Immutable

@Immutable
data class ProfileUiState(
    val displayName: String = "",
    val email: String = "",
    val inBodyScore: Int? = null,
    val scoreLabel: String = "",
    val lastScanDate: String = "",
    val hasScanData: Boolean = false
)
