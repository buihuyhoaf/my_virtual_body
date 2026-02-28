package com.hoabui.virtualbody3d.ui.login

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoabui.virtualbody3d.ui.login.component.LoginFooter
import com.hoabui.virtualbody3d.ui.login.component.LoginForm
import com.hoabui.virtualbody3d.ui.login.component.LoginLogo
import com.hoabui.virtualbody3d.ui.login.component.LoginSignInButton
import com.hoabui.virtualbody3d.ui.login.component.LoginSocialSection
import com.hoabui.virtualbody3d.ui.login.viewmodel.LoginEvent
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
    val token = GymTheme.token
    val colors = token.colors
    val spacing = token.spacing

    val state by viewModel.state.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is LoginEvent.NavigateHome -> onSignIn(state.email, state.password)
                is LoginEvent.ShowError -> Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surface)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = spacing.xl, vertical = spacing.lg)
    ) {
        LoginLogo(token = token)
        Spacer(modifier = Modifier.height(spacing.xl))
        LoginForm(
            token = token,
            state = state,
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
