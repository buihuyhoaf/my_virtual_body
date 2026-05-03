package com.hoabui.virtualbody3d.ui.exerciselibrary.catalog

import android.net.Uri
import androidx.compose.runtime.Immutable
import com.hoabui.virtualbody3d.domain.model.common.ImageSource
import com.hoabui.virtualbody3d.domain.repository.ResourceProvider

/**
 * Immutable Coil input for exercise library cards (skippable vs [Any] model).
 */
@Immutable
sealed interface ExerciseLibraryCardImage {
    @Immutable
    data class NetworkUrl(val url: String) : ExerciseLibraryCardImage

    @Immutable
    data class LocalDrawableName(val resourceName: String) : ExerciseLibraryCardImage

    @Immutable
    data class ContentUriString(val uriString: String) : ExerciseLibraryCardImage
}

fun ImageSource.toExerciseLibraryCardImage(): ExerciseLibraryCardImage = when (this) {
    is ImageSource.Network -> ExerciseLibraryCardImage.NetworkUrl(url)
    is ImageSource.LocalResource -> ExerciseLibraryCardImage.LocalDrawableName(name)
    is ImageSource.ContentUri -> ExerciseLibraryCardImage.ContentUriString(uriString)
}

fun ExerciseLibraryCardImage.toCoilModel(resourceProvider: ResourceProvider): Any? = when (this) {
    is ExerciseLibraryCardImage.NetworkUrl -> url
    is ExerciseLibraryCardImage.LocalDrawableName -> resourceProvider.drawableResId(resourceName)
    is ExerciseLibraryCardImage.ContentUriString -> Uri.parse(uriString)
}
