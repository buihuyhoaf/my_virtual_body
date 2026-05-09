package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.ui.exerciselibrary.catalog.ExerciseLibraryCatalogGrouped
import com.hoabui.virtualbody3d.ui.exerciselibrary.util.mapGroupedToCatalogGrouped
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@ActivityRetainedScoped
class ObserveExerciseCatalogUseCase @Inject constructor(
    private val getExerciseLibraryUseCase: GetExerciseLibraryUseCase,
) {

    private var isCollectionStarted = false

    private val _catalogGroupedByRegion =
        MutableStateFlow<ExerciseLibraryCatalogGrouped>(persistentMapOf())
    val catalogGroupedByRegion: StateFlow<ExerciseLibraryCatalogGrouped> =
        _catalogGroupedByRegion.asStateFlow()

    private val _catalogExercisesById = MutableStateFlow<Map<String, Exercise>>(emptyMap())
    val catalogExercisesById: StateFlow<Map<String, Exercise>> = _catalogExercisesById.asStateFlow()

    fun startCollection(scope: CoroutineScope, onCatalogError: (Throwable) -> Unit) {
        if (isCollectionStarted) return
        isCollectionStarted = true

        getExerciseLibraryUseCase()
            .onEach { grouped ->
                _catalogExercisesById.value = grouped.values.flatten().associateBy { it.id }
                _catalogGroupedByRegion.value = mapGroupedToCatalogGrouped(grouped)
            }
            .catch { onCatalogError(it) }
            .launchIn(scope)
    }
}
