package com.hoabui.virtualbody3d.ui.exerciselibrary.data

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.collections.immutable.ImmutableList

/**
 * Resolves [imageNames] (drawable **resource entry** names) to ids for
 * [com.hoabui.virtualbody3d.ui.exerciselibrary.components.ExerciseLibraryFocusMusclesSection].
 * The strip always uses four names in order: front-upper, back-upper, front-lower, back-lower.
 * Unknown names fall back to [FOCUS_STRIP_DEFAULT_QUADRANT_NAME].
 */
@Composable
fun rememberFocusMusclesStripDrawableResIds(
    imageNames: ImmutableList<String>,
): List<Int> {
    val context = LocalContext.current
    return remember(imageNames) {
        focusMusclesStripDrawableResIds(
            resources = context.resources,
            packageName = context.packageName,
            imageNames = imageNames,
        )
    }
}

fun focusMusclesStripDrawableResIds(
    resources: Resources,
    packageName: String,
    imageNames: List<String>,
): List<Int> {
    val def = FOCUS_STRIP_DEFAULT_QUADRANT_NAME
    val resolve: (String) -> Int = { name ->
        val id = resources.getIdentifier(name, "drawable", packageName)
        if (id != 0) {
            id
        } else {
            resources.getIdentifier(def, "drawable", packageName)
        }
    }
    return imageNames.map { resolve(it) }
}
