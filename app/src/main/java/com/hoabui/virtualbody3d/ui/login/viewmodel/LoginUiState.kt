package com.hoabui.virtualbody3d.ui.login.viewmodel

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val isLoading: Boolean = false
)
