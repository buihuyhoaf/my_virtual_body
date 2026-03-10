package com.hoabui.virtualbody3d.ui.login.viewmodel

import com.hoabui.virtualbody3d.core.base.UiState
import com.hoabui.virtualbody3d.core.base.UiStateViewModel
import com.hoabui.virtualbody3d.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : UiStateViewModel<LoginUiState, LoginEvent>() {

    init {
        setSuccess(LoginUiState())
    }

    fun onEmailChanged(email: String) {
        updateSuccess { copy(email = email) }
    }

    fun onPasswordChanged(password: String) {
        updateSuccess { copy(password = password) }
    }

    fun onTogglePasswordVisible() {
        updateSuccess { copy(passwordVisible = !passwordVisible) }
    }

    fun login() {
        val data = (state.value as? UiState.Success<LoginUiState>)?.data ?: return
        launchSafely {
            updateSuccess { copy(isLoading = true) }
            loginUseCase(data.email, data.password)
            sendEvent(LoginEvent.NavigateHome)
            updateSuccess { copy(isLoading = false) }
        }
    }

    override fun onError(throwable: Throwable) {
        updateSuccess { copy(isLoading = false) }
        sendEvent(LoginEvent.ShowError(throwable.message ?: "Unknown error"))
    }
}
