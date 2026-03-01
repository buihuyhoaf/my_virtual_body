package com.hoabui.virtualbody3d.ui.login.component

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.theme.tokens.GymToken

/**
 * Primary Sign in button.
 */
@Composable
fun LoginSignInButton(
    modifier: Modifier = Modifier,
    token: GymToken,
    onClick: () -> Unit
) {
    Log.d("LoginPerf", "LoginSignInButton recompose at ${System.currentTimeMillis()}")
    val colors = token.colors
    val typography = token.typography
    val loginTokens = token.login

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(loginTokens.primaryButtonHeight),
        shape = RoundedCornerShape(loginTokens.ctaCornerRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.primary,
            contentColor = colors.onPrimary
        )
    ) {
        Text(
            text = stringResource(R.string.login_sign_in),
            style = typography.headlineSmall
        )
    }
}
