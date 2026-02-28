package com.hoabui.virtualbody3d.ui.login.viewmodel

sealed interface LoginEvent {
    data object NavigateHome : LoginEvent
    data class ShowError(val message: String) : LoginEvent
}
