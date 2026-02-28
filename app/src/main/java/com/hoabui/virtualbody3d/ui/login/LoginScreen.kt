package com.hoabui.virtualbody3d.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.HorizontalDivider
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun LoginScreen(
    onSignIn: (email: String, password: String) -> Unit = { _, _ -> },
    onForgotPassword: () -> Unit = {},
    onSignUp: () -> Unit = {},
    onSignInWithGoogle: () -> Unit = {},
    onSignInWithApple: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    val colors = token.colors
    val spacing = token.spacing
    val typography = token.typography
    val loginTokens = token.login

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = spacing.xl, vertical = spacing.lg)
    ) {
        // Brand header: logo (Whitecat) on plum background
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(loginTokens.logoSize)
                    .clip(RoundedCornerShape(token.radius.md))
                    .background(colors.primary),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(R.drawable.ic_logo_whitecat),
                    contentDescription = null,
                    modifier = Modifier.size(loginTokens.logoSize * 0.75f),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.height(spacing.xs))
        }

        Spacer(modifier = Modifier.height(spacing.xl))

        // Welcome back
        Text(
            text = stringResource(R.string.login_welcome_back),
            style = typography.headlineMedium,
            color = colors.textPrimary
        )
        Spacer(modifier = Modifier.height(spacing.xl))

        // Email
        Text(
            text = stringResource(R.string.login_email),
            style = typography.labelLarge,
            color = colors.textSecondary
        )
        Spacer(modifier = Modifier.height(spacing.xs))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    stringResource(R.string.login_email_placeholder),
                    style = typography.bodyMedium,
                    color = colors.textMuted
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(loginTokens.inputCornerRadius),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.primary,
                unfocusedBorderColor = colors.borderSubtle,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = colors.primary,
                focusedTextColor = colors.textPrimary,
                unfocusedTextColor = colors.textPrimary,
                focusedLeadingIconColor = colors.textSecondary,
                unfocusedLeadingIconColor = colors.textSecondary
            )
        )

        Spacer(modifier = Modifier.height(spacing.lg))

        // Password
        Text(
            text = stringResource(R.string.login_password),
            style = typography.labelLarge,
            color = colors.textSecondary
        )
        Spacer(modifier = Modifier.height(spacing.xs))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    stringResource(R.string.login_password_placeholder),
                    style = typography.bodyMedium,
                    color = colors.textMuted
                )
            },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = colors.textMuted
                    )
                }
            },
            shape = RoundedCornerShape(loginTokens.inputCornerRadius),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colors.primary,
                unfocusedBorderColor = colors.borderSubtle,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = colors.primary,
                focusedTextColor = colors.textPrimary,
                unfocusedTextColor = colors.textPrimary,
                focusedTrailingIconColor = colors.primary,
                unfocusedTrailingIconColor = colors.textMuted
            )
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = onForgotPassword,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
            ) {
                Text(
                    text = stringResource(R.string.login_forgot_password),
                    style = typography.labelSmall,
                    color = colors.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.lg))

        // Sign in button
        androidx.compose.material3.Button(
            onClick = {
                focusManager.clearFocus()
                onSignIn(email, password)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(loginTokens.primaryButtonHeight),
            shape = RoundedCornerShape(loginTokens.ctaCornerRadius),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = colors.primary,
                contentColor = colors.onPrimary
            ),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
        ) {
            Text(
                text = stringResource(R.string.login_sign_in),
                style = typography.labelLarge
            )
        }

        // Divider: or continue with
        Spacer(modifier = Modifier.height(spacing.xl))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = colors.borderSubtle
            )
            Text(
                text = stringResource(R.string.login_or_continue_with),
                style = typography.bodySmall,
                color = colors.textMuted,
                modifier = Modifier.padding(horizontal = spacing.md)
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = colors.borderSubtle
            )
        }
        Spacer(modifier = Modifier.height(spacing.lg))

        // Social: Google
        androidx.compose.material3.OutlinedButton(
            onClick = onSignInWithGoogle,
            modifier = Modifier
                .fillMaxWidth()
                .height(loginTokens.socialButtonHeight),
            shape = RoundedCornerShape(loginTokens.ctaCornerRadius),
            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                contentColor = colors.primary
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.primary)
        ) {
            SocialIcon(drawableResId = R.drawable.ic_google)
            Spacer(modifier = Modifier.size(spacing.md))
            Text(
                text = stringResource(R.string.login_continue_google),
                style = typography.labelLarge
            )
        }
        Spacer(modifier = Modifier.height(spacing.md))
        // Social: Apple
        androidx.compose.material3.OutlinedButton(
            onClick = onSignInWithApple,
            modifier = Modifier
                .fillMaxWidth()
                .height(loginTokens.socialButtonHeight),
            shape = RoundedCornerShape(loginTokens.ctaCornerRadius),
            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                contentColor = colors.primary
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.primary)
        ) {
            SocialIcon(drawableResId = R.drawable.ic_apple, tint = colors.primary)
            Spacer(modifier = Modifier.size(spacing.md))
            Text(
                text = stringResource(R.string.login_continue_apple),
                style = typography.labelLarge
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Footer: Don't have an account? Sign up
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.login_no_account),
                style = typography.bodySmall,
                color = colors.textMuted
            )
            TextButton(
                onClick = onSignUp,
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = spacing.xxs, vertical = 0.dp)
            ) {
                Text(
                    text = stringResource(R.string.login_sign_up),
                    style = typography.labelLarge,
                    color = colors.primary
                )
            }
        }
    }
}

@Composable
private fun SocialIcon(
    drawableResId: Int,
    tint: Color? = null,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.Image(
        painter = painterResource(drawableResId),
        contentDescription = null,
        modifier = modifier.size(20.dp),
        colorFilter = tint?.let { ColorFilter.tint(it) }
    )
}
