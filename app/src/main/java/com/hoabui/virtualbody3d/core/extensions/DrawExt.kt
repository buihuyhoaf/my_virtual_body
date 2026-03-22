package com.hoabui.virtualbody3d.core.extensions

import android.graphics.BlurMaskFilter
import android.graphics.LinearGradient
import android.graphics.RectF
import android.graphics.Shader
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import kotlin.math.min
import android.graphics.Paint as AndroidPaint

fun DrawScope.drawAvatarBackdrop(
    isSelected: Boolean,
    glowOuterSize: Size,
    frameSidePx: Float,
    borderWidthPx: Float,
    cornerRadiusPx: Float,
    primary: Color,
    primarySoft: Color,
    surfaceElevated: Color,
) {
    val cx = glowOuterSize.width / 2f
    val cy = glowOuterSize.height / 2f
    val left = cx - frameSidePx / 2f
    val top = cy - frameSidePx / 2f
    val rect = RectF(left, top, left + frameSidePx, top + frameSidePx)
    val rx = min(cornerRadiusPx + borderWidthPx, frameSidePx / 2f)
    val native = drawContext.canvas.nativeCanvas

    if (isSelected) {
        val glowPaint = AndroidPaint().apply {
            isAntiAlias = true
            color = primary.copy(alpha = 0.45f).toArgb()
            maskFilter = BlurMaskFilter(28f, BlurMaskFilter.Blur.NORMAL)
        }
        native.drawRoundRect(rect, rx, rx, glowPaint)
    }

    val innerRect = RectF(
        left + borderWidthPx,
        top + borderWidthPx,
        left + frameSidePx - borderWidthPx,
        top + frameSidePx - borderWidthPx
    )
    val innerR = min(cornerRadiusPx, innerRect.width() / 2f)

    val fillPaint = AndroidPaint().apply {
        isAntiAlias = true
        color = surfaceElevated.toArgb()
    }
    native.drawRoundRect(innerRect, innerR, innerR, fillPaint)

    val borderPaint = AndroidPaint().apply {
        isAntiAlias = true
        style = AndroidPaint.Style.STROKE
        strokeWidth = borderWidthPx
        shader = LinearGradient(
            left,
            top,
            left + frameSidePx,
            top + frameSidePx,
            intArrayOf(primary.toArgb(), primarySoft.toArgb(), primary.copy(alpha = 0.85f).toArgb()),
            floatArrayOf(0f, 0.5f, 1f),
            Shader.TileMode.CLAMP
        )
    }
    native.drawRoundRect(rect, rx, rx, borderPaint)
}