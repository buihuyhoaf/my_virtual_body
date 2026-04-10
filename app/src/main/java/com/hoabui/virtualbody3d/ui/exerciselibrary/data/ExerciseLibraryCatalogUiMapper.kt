package com.hoabui.virtualbody3d.ui.exerciselibrary.data

import android.content.Context
import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.ui.exerciselibrary.model.toExerciseLibraryCardImage
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryCatalogEntryUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.ExerciseLibraryCatalogState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toPersistentMap
import javax.inject.Inject

/**
 * Maps domain catalog output to UI-only [ExerciseLibraryCatalogState] at the screen boundary.
 */
class ExerciseLibraryCatalogUiMapper @Inject constructor(
    @ApplicationContext private val appContext: Context,
) {

    fun mapGroupedToCatalogState(grouped: Map<BodyRegion, List<Exercise>>): ExerciseLibraryCatalogState =
        ExerciseLibraryCatalogState(
            catalogGroupedByRegion = grouped
                .mapValues { (_, exercises) ->
                    exercises.map { it.toExerciseLibraryCatalogEntryUiModel() }.toImmutableList()
                }
                .toPersistentMap(),
        )

    private fun Exercise.toExerciseLibraryCatalogEntryUiModel(): ExerciseLibraryCatalogEntryUiModel =
        ExerciseLibraryCatalogEntryUiModel(
            id = id,
            name = name,
            category = category,
            equipment = equipment,
            bodyRegion = bodyRegion,
            measurementMode = measurementMode,
            image = image.toExerciseLibraryCardImage(),
            libraryCardStaticSubtitle = exerciseLibraryCardStaticSubtitle(appContext),
        )
}
