package com.hoabui.virtualbody3d.ui.login.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.PaddingValues
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.login.viewmodel.LoginUiState
import com.hoabui.virtualbody3d.ui.theme.outlinedTextFieldColors
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
    val colors = token.colors
    val spacing = token.spacing
    val typography = token.typography
    val loginTokens = token.login

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.login_welcome_back),
            style = typography.headlineLarge,
            color = colors.textBlack
        )
        Spacer(modifier = Modifier.height(spacing.xl))

        Text(
            text = stringResource(R.string.login_email),
            style = typography.headlineSmall,
            color = colors.textBlack
        )
        Spacer(modifier = Modifier.height(spacing.xs))
        OutlinedTextField(
            value = state.email,
            onValueChange = onEmailChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    stringResource(R.string.login_email_placeholder),
                    style = typography.bodyLarge,
                    color = colors.textPlaceholder
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(loginTokens.inputCornerRadius),
            colors = outlinedTextFieldColors(colors)
        )
        Spacer(modifier = Modifier.height(spacing.lg))

        Text(
            text = stringResource(R.string.login_password),
            style = typography.headlineSmall,
            color = colors.textBlack
        )
        Spacer(modifier = Modifier.height(spacing.xs))
        OutlinedTextField(
            value = state.password,
            onValueChange = onPasswordChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    stringResource(R.string.login_password_placeholder),
                    style = typography.bodyLarge,
                    color = colors.textPlaceholder
                )
            },
            singleLine = true,
            visualTransformation = if (state.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = onTogglePasswordVisible) {
                    Icon(
                        imageVector = if (state.passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (state.passwordVisible) "Hide password" else "Show password",
                        tint = colors.textPlaceholder
                    )
                }
            },
            shape = RoundedCornerShape(loginTokens.inputCornerRadius),
            colors = outlinedTextFieldColors(colors)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = onForgotPassword,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = stringResource(R.string.login_forgot_password),
                    style = typography.labelLarge,
                    color = colors.textBlack
                )
            }
        }
    }
}
