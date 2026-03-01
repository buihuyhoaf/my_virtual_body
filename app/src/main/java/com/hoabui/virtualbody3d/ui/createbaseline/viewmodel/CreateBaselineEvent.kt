package com.hoabui.virtualbody3d.ui.createbaseline.viewmodel

/**
 * One-off events for Create Baseline flow (e.g. navigation).
 */
sealed class CreateBaselineEvent {
    data object NavigateHome : CreateBaselineEvent()
}