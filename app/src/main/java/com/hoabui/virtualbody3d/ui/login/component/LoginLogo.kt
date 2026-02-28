package com.hoabui.virtualbody3d.ui.login.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.theme.tokens.GymToken

/**
 * Login brand header: logo image on primary (plum) rounded background.
 */
@Composable
fun LoginLogo(
    modifier: Modifier = Modifier,
    token: GymToken
) {
    val colors = token.colors
    val spacing = token.spacing
    val loginTokens = token.login

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(loginTokens.logoSize)
                .clip(RoundedCornerShape(token.radius.md))
                .background(colors.primary),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.drawable.whitecat),
                contentDescription = null,
                modifier = Modifier.size(loginTokens.logoSize * 0.85f),
                contentScale = ContentScale.Fit
            )
        }
        Spacer(modifier = Modifier.height(spacing.xs))
    }
}
