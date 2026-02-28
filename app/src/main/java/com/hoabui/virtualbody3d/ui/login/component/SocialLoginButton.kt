package com.hoabui.virtualbody3d.ui.login.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.BorderStroke
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
    val colors = token.colors
    val spacing = token.spacing
    val typography = token.typography
    val loginTokens = token.login

    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(loginTokens.socialButtonHeight),
        shape = RoundedCornerShape(loginTokens.ctaCornerRadius),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary),
        border = BorderStroke(1.dp, colors.primary)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            SocialIcon(
                modifier = Modifier,
                drawableResId = drawableResId,
                tint = iconTint
            )
            Spacer(modifier = Modifier.size(spacing.xs))
            Text(
                text = stringResource(textResId),
                style = typography.headlineSmall,
                color = colors.textBlack
            )
        }
    }
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
