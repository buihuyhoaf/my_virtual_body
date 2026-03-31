package com.hoabui.virtualbody3d.ui.common_ui.atom.button

import android.content.res.Configuration
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hoabui.virtualbody3d.ui.common_ui.atom.icon.GIcon
import com.hoabui.virtualbody3d.ui.theme.GymTheme

/**
 * Design-system floating action button: circular container, primary colors, tokenized elevation.
 * Content should use [GIcon] with default tint so it follows [androidx.compose.material3.LocalContentColor].
 */
@Composable
fun GFloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val token = GymTheme.token
    val fabSize = token.bodyAnalysis.exerciseLibraryWorkoutPlanFabSize
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(fabSize),
        shape = RoundedCornerShape(token.radius.pill),
        containerColor = token.colors.primary,
        contentColor = token.colors.onPrimary,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = token.elevation.level3,
            pressedElevation = token.elevation.level2,
            focusedElevation = token.elevation.level3,
            hoveredElevation = token.elevation.level3,
        ),
        content = content,
    )
}

@Preview(showBackground = true, name = "GFloatingActionButton — Light")
@Composable
private fun PreviewGFabLight() {
    GymTheme {
        val token = GymTheme.token
        GFloatingActionButton(onClick = {}) {
            GIcon(
                imageVector = Icons.Filled.CalendarMonth,
                contentDescription = null,
                modifier = Modifier.size(token.bodyAnalysis.exerciseLibraryWorkoutPlanFabIconSize),
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "GFloatingActionButton — Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PreviewGFabDark() {
    GymTheme(darkTheme = true) {
        val token = GymTheme.token
        GFloatingActionButton(onClick = {}) {
            GIcon(
                imageVector = Icons.Filled.CalendarMonth,
                contentDescription = null,
                modifier = Modifier.size(token.bodyAnalysis.exerciseLibraryWorkoutPlanFabIconSize),
            )
        }
    }
}
