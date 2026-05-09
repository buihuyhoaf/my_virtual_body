package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.BodyRegion
import com.hoabui.virtualbody3d.domain.model.exercise.EquipmentType
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.ExerciseCategory
import com.hoabui.virtualbody3d.domain.model.exercise.isValidForSessionConfirm
import com.hoabui.virtualbody3d.ui.exerciselibrary.cart.ExerciseDraft
import com.hoabui.virtualbody3d.ui.exerciselibrary.catalog.ExerciseLibraryCatalogGrouped
import com.hoabui.virtualbody3d.ui.exerciselibrary.catalog.LibraryPresentationSlice
import com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibraryCartManager
import com.hoabui.virtualbody3d.ui.exerciselibrary.manager.ExerciseLibrarySearchManager
import com.hoabui.virtualbody3d.ui.exerciselibrary.mapper.ExerciseLibraryUiMapper
import com.hoabui.virtualbody3d.ui.exerciselibrary.presentation.exerciseLibrarySectionRebuildKey
import com.hoabui.virtualbody3d.ui.exerciselibrary.presentation.mergeExerciseLibraryPresentation
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.ExerciseLibraryUiState
import com.hoabui.virtualbody3d.ui.exerciselibrary.util.toLibraryCartDraft
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@ActivityRetainedScoped
class ObserveExerciseLibraryUiStateUseCase @Inject constructor(
    private val observeExerciseCatalogUseCase: ObserveExerciseCatalogUseCase,
    private val searchManager: ExerciseLibrarySearchManager,
    private val cartManager: ExerciseLibraryCartManager,
    private val exerciseLibraryUiMapper: ExerciseLibraryUiMapper,
) {

    private val emptyLibrarySlice = LibraryPresentationSlice(
        sections = persistentListOf(),
        exerciseMeasurementById = persistentMapOf(),
        isAddToSessionEnabled = false,
    )

    private lateinit var librarySliceFlow: StateFlow<LibraryPresentationSlice>
    private lateinit var mergedScreenStateFlow: StateFlow<ExerciseLibraryUiState>

    fun observe(scope: CoroutineScope): StateFlow<ExerciseLibraryUiState> {
        if (::mergedScreenStateFlow.isInitialized) {
            return mergedScreenStateFlow
        }

        val catalogGroupedByRegion = observeExerciseCatalogUseCase.catalogGroupedByRegion
        val catalogExercisesById = observeExerciseCatalogUseCase.catalogExercisesById

        val filterCatalogCartExercise = combine(
            combine(
                searchManager.searchQuery,
                searchManager.selectedExerciseCategory,
                searchManager.selectedBodyRegions,
                searchManager.selectedEquipment,
                cartManager.itemDrafts,
            ) { q, c, r, e, d -> FilterCatalogDraftPart(q, c, r, e, d) },
            combine(
                cartManager.draftOrder,
                cartManager.activeExerciseId,
                cartManager.isCartExpanded,
                catalogGroupedByRegion,
                catalogExercisesById,
            ) { o, a, exp, cat, exById ->
                CartCatalogExercisePart(o, a, exp, cat, exById)
            },
        ) { f, c -> f to c }

        val baseStateFlow = filterCatalogCartExercise
            .map { pair ->
                val f = pair.first
                val c = pair.second
                ExerciseLibraryUiState(
                    searchQuery = f.q,
                    selectedExerciseCategory = f.cat,
                    selectedBodyRegions = f.regions,
                    selectedEquipment = f.equipment,
                    itemDrafts = f.itemDrafts,
                    draftOrder = c.order,
                    activeExerciseId = c.activeId,
                    isCartExpanded = c.expanded,
                    catalogGroupedByRegion = c.catalog,
                    catalogExercisesById = c.exercisesById,
                    libraryList = LibraryPresentationSlice(),
                )
            }
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ExerciseLibraryUiState(),
            )

        librarySliceFlow = baseStateFlow
            .distinctUntilChanged { a, b ->
                exerciseLibrarySectionRebuildKey(a.catalogGroupedByRegion, a) ==
                    exerciseLibrarySectionRebuildKey(b.catalogGroupedByRegion, b)
            }
            .map { state ->
                val slice = exerciseLibraryUiMapper.mapLibraryPresentation(state)
                val cart = state.toLibraryCartDraft()
                val isEnabled =
                    cart.itemDrafts.isNotEmpty() &&
                        cart.isValidForSessionConfirm(slice.exerciseMeasurementById)
                slice.copy(isAddToSessionEnabled = isEnabled)
            }
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyLibrarySlice,
            )

        mergedScreenStateFlow = combine(
            librarySliceFlow,
            baseStateFlow,
        ) { librarySlice, base ->
            mergeExerciseLibraryPresentation(
                base = base,
                library = librarySlice,
            )
        }
            .distinctUntilChanged()
            .stateIn(
                scope = scope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = mergeExerciseLibraryPresentation(
                    base = ExerciseLibraryUiState(),
                    library = emptyLibrarySlice,
                ),
            )

        return mergedScreenStateFlow
    }

    fun mergedUiState(): ExerciseLibraryUiState {
        check(::mergedScreenStateFlow.isInitialized) {
            "observe(scope) must be called before reading merged UI state"
        }
        return mergedScreenStateFlow.value
    }

    fun snapshotForCartActions(): ExerciseLibraryUiState {
        check(::librarySliceFlow.isInitialized) {
            "observe(scope) must be called before cart snapshots"
        }
        return ExerciseLibraryUiState(
            searchQuery = searchManager.searchQuery.value,
            selectedExerciseCategory = searchManager.selectedExerciseCategory.value,
            selectedBodyRegions = searchManager.selectedBodyRegions.value,
            selectedEquipment = searchManager.selectedEquipment.value,
            itemDrafts = cartManager.itemDrafts.value,
            draftOrder = cartManager.draftOrder.value,
            activeExerciseId = cartManager.activeExerciseId.value,
            isCartExpanded = cartManager.isCartExpanded.value,
            catalogGroupedByRegion = observeExerciseCatalogUseCase.catalogGroupedByRegion.value,
            catalogExercisesById = observeExerciseCatalogUseCase.catalogExercisesById.value,
            libraryList = LibraryPresentationSlice(
                exerciseMeasurementById = librarySliceFlow.value.exerciseMeasurementById,
            ),
        )
    }
}

private data class FilterCatalogDraftPart(
    val q: String,
    val cat: ExerciseCategory?,
    val regions: ImmutableSet<BodyRegion>?,
    val equipment: EquipmentType?,
    val itemDrafts: ImmutableMap<String, ExerciseDraft>,
)

private data class CartCatalogExercisePart(
    val order: ImmutableList<String>,
    val activeId: String?,
    val expanded: Boolean,
    val catalog: ExerciseLibraryCatalogGrouped,
    val exercisesById: Map<String, Exercise>,
)
