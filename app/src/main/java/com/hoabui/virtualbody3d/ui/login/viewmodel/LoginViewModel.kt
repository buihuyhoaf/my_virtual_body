package com.hoabui.virtualbody3d.ui.login.viewmodel

import com.hoabui.virtualbody3d.core.base.BaseViewModel
import com.hoabui.virtualbody3d.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : BaseViewModel<LoginUiState, LoginEvent>() {

    override fun initialState(): LoginUiState = LoginUiState()

    fun onEmailChanged(email: String) {
        updateState { copy(email = email) }
    }

    fun onPasswordChanged(password: String) {
        updateState { copy(password = password) }
    }

    fun onTogglePasswordVisible() {
        updateState { copy(passwordVisible = !passwordVisible) }
    }

    fun login() {
        val email = state.value.email
        val password = state.value.password
        launchSafely {
            updateState { copy(isLoading = true) }
            loginUseCase(email, password)
            sendEvent(LoginEvent.NavigateHome)
            updateState { copy(isLoading = false) }
        }
    }

    override fun defaultError(throwable: Throwable) {
        updateState { copy(isLoading = false) }
        sendEvent(LoginEvent.ShowError(throwable.message ?: "Unknown error"))
    }
}
