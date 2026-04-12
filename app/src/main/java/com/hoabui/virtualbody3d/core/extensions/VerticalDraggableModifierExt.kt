package com.hoabui.virtualbody3d.core.extensions

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.verticalDrag
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker

data class VerticalDragResult(
    val totalDragY: Float,
    val velocityY: Float,
)

fun Modifier.verticalDraggable(
    enabled: Boolean = true,
    onDragStart: () -> Unit = {},
    onDrag: (dragAmountY: Float) -> Unit,
    onDragEnd: (VerticalDragResult) -> Unit = {},
): Modifier = if (!enabled) {
    this
} else {
    pointerInput(Unit) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false)
            val velocityTracker = VelocityTracker()
            var totalDragY = 0f
            onDragStart()
            verticalDrag(down.id) { change ->
                val dragAmount = change.positionChange().y
                totalDragY += dragAmount
                onDrag(dragAmount)
                velocityTracker.addPosition(change.uptimeMillis, change.position)
                change.consume()
            }
            onDragEnd(
                VerticalDragResult(
                    totalDragY = totalDragY,
                    velocityY = velocityTracker.calculateVelocity().y,
                ),
            )
        }
    }
}
