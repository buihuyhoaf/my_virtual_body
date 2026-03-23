package com.hoabui.virtualbody3d.ui.login.component

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButton
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
    GButton(
        text = stringResource(R.string.login_sign_in),
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
    )
}
