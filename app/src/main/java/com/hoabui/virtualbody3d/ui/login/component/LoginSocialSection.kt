package com.hoabui.virtualbody3d.ui.login.component

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import com.hoabui.virtualbody3d.ui.common_ui.atom.divider.GDivider
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.theme.tokens.GymToken

/**
 * "or continue with" divider + Google & Apple buttons row.
 */
@Composable
fun LoginSocialSection(
    modifier: Modifier = Modifier,
    token: GymToken,
    onSignInWithGoogle: () -> Unit,
    onSignInWithApple: () -> Unit
) {
    Log.d("LoginPerf", "LoginSocialSection recompose at ${System.currentTimeMillis()}")
    val colors = token.colors
    val spacing = token.spacing
    val typography = token.typography

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GDivider(modifier = Modifier.weight(1f))
        GText(
            text = stringResource(R.string.login_or_continue_with),
            style = typography.labelLarge,
            color = colors.textBlack,
            modifier = Modifier.padding(horizontal = spacing.md)
        )
        GDivider(modifier = Modifier.weight(1f))
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = spacing.lg),
        horizontalArrangement = Arrangement.spacedBy(spacing.md)
    ) {
        SocialLoginButton(
            modifier = Modifier.weight(1f),
            token = token,
            onClick = onSignInWithGoogle,
            drawableResId = R.drawable.ic_google,
            textResId = R.string.login_continue_google
        )
        SocialLoginButton(
            modifier = Modifier.weight(1f),
            token = token,
            onClick = onSignInWithApple,
            drawableResId = R.drawable.ic_apple,
            textResId = R.string.login_continue_apple,
            iconTint = colors.primary
        )
    }
}
