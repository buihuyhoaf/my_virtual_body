package com.hoabui.virtualbody3d.ui.login.component

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButton
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButtonVariant
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GIconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.field.GTextField
import com.hoabui.virtualbody3d.ui.login.viewmodel.LoginUiState
import com.hoabui.virtualbody3d.ui.theme.tokens.GymToken

/**
 * Login form: welcome title, email field, password field, forgot password link.
 */
@Composable
fun LoginForm(
    modifier: Modifier = Modifier,
    token: GymToken,
    state: LoginUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onTogglePasswordVisible: () -> Unit,
    onForgotPassword: () -> Unit
) {
    Log.d("LoginPerf", "LoginForm recompose at ${System.currentTimeMillis()}")
    val colors = token.colors
    val spacing = token.spacing
    val typography = token.typography
    val loginTokens = token.login

    Column(modifier = modifier.fillMaxWidth()) {
        GText(
            text = stringResource(R.string.login_welcome_back),
            style = typography.headlineLarge,
            color = colors.textBlack
        )
        Spacer(modifier = Modifier.height(spacing.xl))

        GText(
            text = stringResource(R.string.login_email),
            style = typography.headlineSmall,
            color = colors.textBlack
        )
        Spacer(modifier = Modifier.height(spacing.xs))
        GTextField(
            value = state.email,
            onValueChange = onEmailChanged,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { if (it.isFocused) Log.d("LoginPerf", "Email field FOCUSED at ${System.currentTimeMillis()}") },
            placeholder = stringResource(R.string.login_email_placeholder),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(spacing.lg))

        GText(
            text = stringResource(R.string.login_password),
            style = typography.headlineSmall,
            color = colors.textBlack
        )
        Spacer(modifier = Modifier.height(spacing.xs))
        GTextField(
            value = state.password,
            onValueChange = onPasswordChanged,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { if (it.isFocused) Log.d("LoginPerf", "Password field FOCUSED at ${System.currentTimeMillis()}") },
            placeholder = stringResource(R.string.login_password_placeholder),
            singleLine = true,
            visualTransformation = if (state.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                GIconButton(onClick = onTogglePasswordVisible) {
                    Icon(
                        imageVector = if (state.passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (state.passwordVisible) "Hide password" else "Show password",
                        tint = colors.textPlaceholder
                    )
                }
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            GButton(
                text = stringResource(R.string.login_forgot_password),
                onClick = onForgotPassword,
                variant = GButtonVariant.Ghost,
            )
        }
    }
}
