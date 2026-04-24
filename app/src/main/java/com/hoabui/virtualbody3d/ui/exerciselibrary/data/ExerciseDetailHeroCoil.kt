package com.hoabui.virtualbody3d.ui.exerciselibrary.data

import android.content.Context
import com.hoabui.virtualbody3d.data.mapper.exerciseNameToDrawableBasenameCandidates
import com.hoabui.virtualbody3d.domain.repository.ResourceProvider
import com.hoabui.virtualbody3d.ui.exerciselibrary.model.ExerciseLibraryCardImage
import com.hoabui.virtualbody3d.ui.exerciselibrary.model.toCoilModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseDetailSheetUiModel

private const val ExerciseDetailHeroGifAssetFolder = "gif"

private fun Context.hasAsset(path: String): Boolean = runCatching {
    assets.open(path).use { }
    true
}.getOrDefault(false)

/**
 * Prefer `assets/gif/{basename}.gif` (animated) when present; otherwise [ExerciseLibraryCardImage.toCoilModel].
 */
internal fun exerciseDetailHeroCoilModel(
    context: Context,
    detail: ExerciseDetailSheetUiModel,
    resourceProvider: ResourceProvider,
): Any? {
    for (basename in exerciseNameToDrawableBasenameCandidates(detail.name)) {
        val relative = "$ExerciseDetailHeroGifAssetFolder/$basename.gif"
        if (context.hasAsset(relative)) {
            return "file:///android_asset/$relative"
        }
    }
    return detail.heroImage.toCoilModel(resourceProvider)
}
