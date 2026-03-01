package com.hoabui.virtualbody3d.ui.createbaseline.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.hoabui.virtualbody3d.ui.createbaseline.CreateBaselineCameraContent
import com.hoabui.virtualbody3d.ui.theme.tokens.RadiusTokens
import java.io.File
import com.hoabui.virtualbody3d.ui.theme.tokens.component.CreateBaselineTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.semantic.SemanticColorTokens

@Composable
fun CreateBaselineViewfinderSection(
    modifier: Modifier,
    colors: SemanticColorTokens,
    radius: RadiusTokens,
    createBaselineTokens: CreateBaselineTokens,
    showCamera: Boolean = true,
    captureTrigger: Int = 0,
    onImageCaptured: ((File) -> Unit)? = null,
    onCaptureError: ((String) -> Unit)? = null
) {
    val density = LocalDensity.current
    val cornerSizePx = with(density) { createBaselineTokens.cornerMarkerSize.toPx() }
    val strokeWidthPx = with(density) { createBaselineTokens.cornerStrokeWidth.toPx() }
    val borderWidthPx = with(density) { createBaselineTokens.borderWidth.toPx() }
    val pathEffect = PathEffect.dashPathEffect(
        floatArrayOf(
            createBaselineTokens.dashedBorderDashLength,
            createBaselineTokens.dashedBorderGapLength
        ),
        0f
    )

    val cameraAspectRatio = if (showCamera) 9f / 16f else createBaselineTokens.viewfinderAspectRatio

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(cameraAspectRatio)
            .clip(RoundedCornerShape(radius.xl))
    ) {
        if (showCamera) {
            CreateBaselineCameraContent(
                modifier = Modifier.fillMaxSize(),
                captureTrigger = captureTrigger,
                onImageCaptured = onImageCaptured,
                onCaptureError = onCaptureError
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.surfaceSubtle)
            )
        }
        if (!showCamera) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawDashedBorder(
                        borderWidthPx = borderWidthPx,
                        color = createBaselineTokens.viewfinderBorder,
                        cornerRadiusPx = with(density) { radius.xl.toPx() },
                        pathEffect = pathEffect
                    )
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(createBaselineTokens.guidePadding)
                    .border(
                        width = createBaselineTokens.borderWidth,
                        color = createBaselineTokens.guideBorder,
                        shape = RoundedCornerShape(radius.lg)
                    )
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cw = size.width
                    val ch = size.height
                    val cs = cornerSizePx.coerceAtMost(cw / 2f).coerceAtMost(ch / 2f)
                    val sw = strokeWidthPx
                    val primary = colors.primary
                    drawLine(
                        color = primary,
                        start = Offset(0f, 0f),
                        end = Offset(cs, 0f),
                        strokeWidth = sw
                    )
                    drawLine(
                        color = primary,
                        start = Offset(0f, 0f),
                        end = Offset(0f, cs),
                        strokeWidth = sw
                    )
                    drawLine(
                        color = primary,
                        start = Offset(cw, 0f),
                        end = Offset(cw - cs, 0f),
                        strokeWidth = sw
                    )
                    drawLine(
                        color = primary,
                        start = Offset(cw, 0f),
                        end = Offset(cw, cs),
                        strokeWidth = sw
                    )
                    drawLine(
                        color = primary,
                        start = Offset(0f, ch),
                        end = Offset(0f, ch - cs),
                        strokeWidth = sw
                    )
                    drawLine(
                        color = primary,
                        start = Offset(0f, ch),
                        end = Offset(cs, ch),
                        strokeWidth = sw
                    )
                    drawLine(
                        color = primary,
                        start = Offset(cw, ch),
                        end = Offset(cw, ch - cs),
                        strokeWidth = sw
                    )
                    drawLine(
                        color = primary,
                        start = Offset(cw, ch),
                        end = Offset(cw - cs, ch),
                        strokeWidth = sw
                    )
                }
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(createBaselineTokens.gradientHeight)
                    .background(
                        Brush.verticalGradient(
                            listOf(createBaselineTokens.gradientStart, createBaselineTokens.gradientEnd)
                        )
                    )
            )
        }
    }
}

private fun Modifier.drawDashedBorder(
    borderWidthPx: Float,
    color: androidx.compose.ui.graphics.Color,
    cornerRadiusPx: Float,
    pathEffect: PathEffect
): Modifier = this.then(
    Modifier.drawBehind {
        drawRoundRect(
            color = color,
            style = Stroke(
                width = borderWidthPx,
                pathEffect = pathEffect
            ),
            topLeft = Offset.Zero,
            size = Size(size.width, size.height),
            cornerRadius = CornerRadius(cornerRadiusPx)
        )
    }
)
