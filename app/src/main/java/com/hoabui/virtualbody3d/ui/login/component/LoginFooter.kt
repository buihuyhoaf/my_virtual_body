package com.hoabui.virtualbody3d.ui.login.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.R
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
    val colors = token.colors
    val spacing = token.spacing
    val typography = token.typography

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.login_no_account),
            style = typography.labelLarge,
            color = colors.textBlack
        )
        TextButton(
            onClick = onSignUp,
            contentPadding = PaddingValues(horizontal = spacing.xxs)
        ) {
            Text(
                text = stringResource(R.string.login_sign_up),
                style = typography.headlineSmall,
                color = colors.textBlack
            )
        }
    }
}
