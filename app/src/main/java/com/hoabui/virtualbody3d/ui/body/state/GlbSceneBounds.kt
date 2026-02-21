package com.hoabui.virtualbody3d.ui.body.state

data class GlbSceneBounds(
    val minX: Float,
    val minY: Float,
    val minZ: Float,
    val maxX: Float,
    val maxY: Float,
    val maxZ: Float
) {
    val width: Float get() = maxX - minX
    val height: Float get() = maxY - minY
    val depth: Float get() = maxZ - minZ
    val centerY: Float get() = (minY + maxY) / 2f
}
