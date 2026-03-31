package com.hoabui.virtualbody3d.ui.common_ui.atom.icon

import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Design-system wrapper for Material 3 [Icon].
 *
 * [tint] defaults to [LocalContentColor.current] so icons follow surrounding content
 * (e.g. [androidx.compose.material3.Surface] `contentColor`) unless overridden.
 */
@Composable
fun GIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current,
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier,
        tint = tint,
    )
}
