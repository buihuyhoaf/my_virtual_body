package com.hoabui.virtualbody3d.ui.login.component

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButton
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButtonVariant
import com.hoabui.virtualbody3d.ui.theme.tokens.GymToken

/**
 * Footer row: "Don't have an account?" + Sign up link.
 */
@Composable
fun LoginFooter(
    modifier: Modifier = Modifier,
    token: GymToken,
    onSignUp: () -> Unit
) {
    Log.d("LoginPerf", "LoginFooter recompose at ${System.currentTimeMillis()}")
    val colors = token.colors
    val typography = token.typography

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        GText(
            text = stringResource(R.string.login_no_account),
            style = typography.labelLarge,
            color = colors.textBlack
        )
        GButton(
            text = stringResource(R.string.login_sign_up),
            onClick = onSignUp,
            variant = GButtonVariant.Ghost
        )
    }
}
