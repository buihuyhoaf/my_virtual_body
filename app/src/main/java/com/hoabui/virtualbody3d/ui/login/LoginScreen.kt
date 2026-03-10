package com.hoabui.virtualbody3d.ui.login

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoabui.virtualbody3d.core.base.UiState
import com.hoabui.virtualbody3d.ui.components.UiStateContent
import com.hoabui.virtualbody3d.ui.login.component.LoginFooter
import com.hoabui.virtualbody3d.ui.login.component.LoginForm
import com.hoabui.virtualbody3d.ui.login.component.LoginLogo
import com.hoabui.virtualbody3d.ui.login.component.LoginSignInButton
import com.hoabui.virtualbody3d.ui.login.component.LoginSocialSection
import com.hoabui.virtualbody3d.ui.login.viewmodel.LoginEvent
import com.hoabui.virtualbody3d.ui.login.viewmodel.LoginUiState
import com.hoabui.virtualbody3d.ui.login.viewmodel.LoginViewModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),
    onSignIn: (email: String, password: String) -> Unit = { _, _ -> },
    onForgotPassword: () -> Unit = {},
    onSignUp: () -> Unit = {},
    onSignInWithGoogle: () -> Unit = {},
    onSignInWithApple: () -> Unit = {},
) {
    Log.d("LoginPerf", "LoginScreen recompose at ${System.currentTimeMillis()}")
    val token = GymTheme.token
    val colors = token.colors
    val spacing = token.spacing

    val screenState by viewModel.state.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is LoginEvent.NavigateHome -> {
                    val data = (viewModel.state.value as? UiState.Success<LoginUiState>)?.data
                    if (data != null) onSignIn(data.email, data.password)
                }
                is LoginEvent.ShowError -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    UiStateContent(
        state = screenState,
        modifier = modifier.fillMaxSize(),
        loadingContent = { mod ->
            Box(
                modifier = mod
                    .fillMaxSize()
                    .background(colors.surface)
            )
        },
        errorContent = { mod, message ->
            Box(
                modifier = mod
                    .fillMaxSize()
                    .background(colors.surface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = message,
                    color = token.colors.textSecondary
                )
            }
        },
        successContent = { mod, data ->
            Column(
                modifier = mod
                    .fillMaxSize()
                    .background(colors.surface)
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .windowInsetsPadding(WindowInsets.ime)
                    .padding(horizontal = spacing.xl, vertical = spacing.lg)
            ) {
                LoginLogo(token = token)
                Spacer(modifier = Modifier.height(spacing.xl))
                LoginForm(
                    token = token,
                    state = data,
                    onEmailChanged = viewModel::onEmailChanged,
                    onPasswordChanged = viewModel::onPasswordChanged,
                    onTogglePasswordVisible = viewModel::onTogglePasswordVisible,
                    onForgotPassword = onForgotPassword
                )
                Spacer(modifier = Modifier.height(spacing.lg))
                LoginSignInButton(
                    token = token,
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.login()
                    }
                )
                Spacer(modifier = Modifier.height(spacing.xl))
                LoginSocialSection(
                    token = token,
                    onSignInWithGoogle = onSignInWithGoogle,
                    onSignInWithApple = onSignInWithApple
                )
                Spacer(modifier = Modifier.weight(1f))
                LoginFooter(token = token, onSignUp = onSignUp)
            }
        }
    )
}
