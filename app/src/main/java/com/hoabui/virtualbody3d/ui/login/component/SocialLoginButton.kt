package com.hoabui.virtualbody3d.ui.login.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButton
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButtonVariant
import com.hoabui.virtualbody3d.ui.theme.tokens.GymToken

/**
 * Social login button (Google / Apple): icon + label, styled with design tokens.
 */
@Composable
fun SocialLoginButton(
    modifier: Modifier = Modifier,
    token: GymToken,
    onClick: () -> Unit,
    drawableResId: Int,
    textResId: Int,
    iconTint: Color? = null
) {
    GButton(
        text = stringResource(textResId),
        onClick = onClick,
        modifier = modifier,
        variant = GButtonVariant.Outlined,
        leadingIcon = { SocialIcon(drawableResId = drawableResId, tint = iconTint) }
    )
}

@Composable
private fun SocialIcon(
    modifier: Modifier = Modifier,
    drawableResId: Int,
    tint: Color? = null
) {
    Image(
        painter = painterResource(drawableResId),
        contentDescription = null,
        modifier = modifier.size(20.dp),
        colorFilter = tint?.let { ColorFilter.tint(it) }
    )
}
