package com.hoabui.virtualbody3d.ui.common_ui.atom.surface

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.tokens.component.GSurfaceTreatment

/**
 * Decorative surface for badges, chips, and non-interactive containers.
 * For clickable surfaces, use [GCard] instead.
 *
 * @param treatment [GSurfaceTreatment.Standard] applies a subtle border (when [border] is null),
 * inner radial depth, and optional hero rim/elevation for [GSurfaceTreatment.Hero].
 * [GSurfaceTreatment.Flat] skips premium fills for dense or legacy layouts.
 * @param shadowElevation When null, uses hero elevation for [GSurfaceTreatment.Hero], else [level0][com.hoabui.virtualbody3d.ui.theme.tokens.ElevationTokens.level0].
 */
@Composable
fun GSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(GymTheme.token.radius.sm),
    color: Color = GymTheme.token.colors.surfaceSubtle,
    border: BorderStroke? = null,
    shadowElevation: Dp? = null,
    treatment: GSurfaceTreatment = GSurfaceTreatment.Standard,
    content: @Composable BoxScope.() -> Unit,
) {
    val token = GymTheme.token
    val surfaceTok = token.surface
    val resolvedShadow = shadowElevation ?: when (treatment) {
        GSurfaceTreatment.Hero -> surfaceTok.heroShadowElevation
        else -> token.elevation.level0
    }
    val effectiveBorder = when {
        border != null -> border
        treatment == GSurfaceTreatment.Flat || !surfaceTok.applyDefaultSubtleBorder -> null
        treatment == GSurfaceTreatment.Hero -> null
        else -> BorderStroke(token.borderWidth.hairline, token.colors.borderSubtle)
    }
    val rimBrush = Brush.linearGradient(
        colors = listOf(
            token.colors.primary.copy(alpha = surfaceTok.gradientRimAlphaHigh),
            token.colors.borderSubtle.copy(alpha = surfaceTok.gradientRimAlphaLow),
        ),
    )
    val borderModifier = if (treatment == GSurfaceTreatment.Hero) {
        Modifier.border(
            width = token.borderWidth.hairline,
            brush = rimBrush,
            shape = shape,
        )
    } else {
        Modifier
    }
    Surface(
        modifier = modifier.then(borderModifier),
        shape = shape,
        color = color,
        border = effectiveBorder,
        shadowElevation = resolvedShadow,
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .gymPremiumInnerRadialDepth(
                    enabled = treatment != GSurfaceTreatment.Flat,
                    token = token,
                ),
            content = content,
        )
    }
}

@Preview(showBackground = true, name = "GSurface — Standard")
@Composable
private fun PreviewGSurfaceStandard() {
    GymTheme {
        val token = GymTheme.token
        GSurface(
            modifier = Modifier.padding(token.spacing.md),
            treatment = GSurfaceTreatment.Standard,
        ) {
            GText(
                modifier = Modifier.padding(token.spacing.md),
                text = "Standard surface",
                style = token.typography.bodyMedium,
            )
        }
    }
}

@Preview(showBackground = true, name = "GSurface — Hero")
@Composable
private fun PreviewGSurfaceHero() {
    GymTheme {
        val token = GymTheme.token
        GSurface(
            modifier = Modifier.padding(token.spacing.md),
            treatment = GSurfaceTreatment.Hero,
        ) {
            GText(
                modifier = Modifier.padding(token.spacing.md),
                text = "Hero rim + elevation",
                style = token.typography.bodyMedium,
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "GSurface — Dark Standard",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewGSurfaceDark() {
    GymTheme(darkTheme = true) {
        val token = GymTheme.token
        GSurface(
            modifier = Modifier.padding(token.spacing.md),
            treatment = GSurfaceTreatment.Standard,
        ) {
            GText(
                modifier = Modifier.padding(token.spacing.md),
                text = "Dark mode",
                style = token.typography.bodyMedium,
            )
        }
    }
}
