package com.hoabui.virtualbody3d.ui.exerciselibrary.data

import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.exercise.Exercise
import com.hoabui.virtualbody3d.domain.model.exercise.Muscle
import com.hoabui.virtualbody3d.domain.model.exercise.MuscleDictionary
import com.hoabui.virtualbody3d.domain.model.exercise.RegionBodyMuscleSelectionMap
import com.hoabui.virtualbody3d.domain.model.exercise.RegionBody
import com.hoabui.virtualbody3d.domain.model.exercise.RegionGroup
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

/** Four drawable names in strip order: front-upper, back-upper, front-lower, back-lower. */
fun focusMusclesStripImageNamesForCartExercises(
    cartExerciseIds: List<String>,
    exercisesById: Map<String, Exercise>,
    muscleDictionary: MuscleDictionary,
    clickSelectionMap: RegionBodyMuscleSelectionMap = emptyMap(),
    defaultQuadrantName: String = FOCUS_STRIP_DEFAULT_QUADRANT_NAME,
): ImmutableList<String> {
    if (cartExerciseIds.isEmpty()) {
        return if (clickSelectionMap.isEmpty()) {
            emptyFocusMusclesStripImageNames()
        } else {
            focusMusclesStripImageNamesForSelectionMap(
                selectionMap = clickSelectionMap,
                muscleDictionary = muscleDictionary,
                defaultQuadrantName = defaultQuadrantName,
            )
        }
    }
    val cartSelectionMap = buildSelectionMapFromCartExercises(
        cartExerciseIds = cartExerciseIds,
        exercisesById = exercisesById,
        muscleDictionary = muscleDictionary,
    )
    return focusMusclesStripImageNamesForSelectionMap(
        selectionMap = mergeSelectionMaps(cartSelectionMap, clickSelectionMap),
        muscleDictionary = muscleDictionary,
        defaultQuadrantName = defaultQuadrantName,
    )
}

fun focusMusclesStripImageNamesForSelectionMap(
    selectionMap: RegionBodyMuscleSelectionMap,
    muscleDictionary: MuscleDictionary,
    defaultQuadrantName: String = FOCUS_STRIP_DEFAULT_QUADRANT_NAME,
): ImmutableList<String> {
    val normalized = normalizeSelectionMap(
        selectionMap = selectionMap,
        muscleDictionary = muscleDictionary,
    )
    if (normalized.isEmpty()) return emptyFocusMusclesStripImageNames()
    return listOf(
        quadrantImageName(
            regionGroup = RegionGroup.UpperFront,
            bodySelection = normalized[RegionGroup.UpperFront].orEmpty(),
            muscleDictionary = muscleDictionary,
            defaultQuadrantName = FOCUS_STRIP_FRONT_UPPER_EMPTY_NAME,
        ),
        quadrantImageName(
            regionGroup = RegionGroup.UpperBack,
            bodySelection = normalized[RegionGroup.UpperBack].orEmpty(),
            muscleDictionary = muscleDictionary,
            defaultQuadrantName = defaultQuadrantName,
        ),
        quadrantImageName(
            regionGroup = RegionGroup.LowerFront,
            bodySelection = normalized[RegionGroup.LowerFront].orEmpty(),
            muscleDictionary = muscleDictionary,
            defaultQuadrantName = defaultQuadrantName,
        ),
        quadrantImageName(
            regionGroup = RegionGroup.LowerBack,
            bodySelection = normalized[RegionGroup.LowerBack].orEmpty(),
            muscleDictionary = muscleDictionary,
            defaultQuadrantName = defaultQuadrantName,
        ),
    ).toPersistentList()
}

private fun buildSelectionMapFromCartExercises(
    cartExerciseIds: List<String>,
    exercisesById: Map<String, Exercise>,
    muscleDictionary: MuscleDictionary,
): RegionBodyMuscleSelectionMap {
    val selection = linkedMapOf<RegionGroup, MutableMap<RegionBody, MutableSet<Muscle>>>()
    cartExerciseIds.forEach { exerciseId ->
        val exercise = exercisesById[exerciseId] ?: return@forEach
        exercise.focusMuscles.forEach { muscle ->
            val group = muscleDictionary.getGroupForMuscle(muscle)
            val body = muscleDictionary.getBodyForMuscle(muscle)
            selection
                .getOrPut(group) { linkedMapOf() }
                .getOrPut(body) { linkedSetOf() }
                .add(muscle)
        }
    }
    return selection.mapValues { (_, bodyMap) ->
        bodyMap.mapValues { (_, muscles) -> muscles.toSet() }
    }
}

private fun normalizeSelectionMap(
    selectionMap: RegionBodyMuscleSelectionMap,
    muscleDictionary: MuscleDictionary,
): RegionBodyMuscleSelectionMap {
    val normalized = linkedMapOf<RegionGroup, Map<RegionBody, Set<Muscle>>>()
    selectionMap.forEach { (group, bodyMap) ->
        val allowedBodies = muscleDictionary.allBodies(group)
        if (allowedBodies.isEmpty()) return@forEach
        val normalizedBodies = linkedMapOf<RegionBody, Set<Muscle>>()
        bodyMap.forEach { (body, selectedMuscles) ->
            if (body !in allowedBodies) return@forEach
            val allowedMuscles = muscleDictionary.allMuscles(group, body)
            val filtered = selectedMuscles.filterTo(linkedSetOf()) { it in allowedMuscles }
            if (filtered.isNotEmpty()) {
                normalizedBodies[body] = filtered
            }
        }
        if (normalizedBodies.isNotEmpty()) {
            normalized[group] = normalizedBodies
        }
    }
    return normalized
}

private fun mergeSelectionMaps(
    fromCart: RegionBodyMuscleSelectionMap,
    fromClicks: RegionBodyMuscleSelectionMap,
): RegionBodyMuscleSelectionMap {
    if (fromCart.isEmpty()) return fromClicks
    if (fromClicks.isEmpty()) return fromCart
    val merged = linkedMapOf<RegionGroup, MutableMap<RegionBody, MutableSet<Muscle>>>()
    listOf(fromCart, fromClicks).forEach { source ->
        source.forEach { (group, bodyMap) ->
            val targetBodies = merged.getOrPut(group) { linkedMapOf() }
            bodyMap.forEach { (body, muscles) ->
                targetBodies.getOrPut(body) { linkedSetOf() }.addAll(muscles)
            }
        }
    }
    return merged.mapValues { (_, bodyMap) ->
        bodyMap.mapValues { (_, muscles) -> muscles.toSet() }
    }
}

private fun quadrantImageName(
    regionGroup: RegionGroup,
    bodySelection: Map<RegionBody, Set<Muscle>>,
    muscleDictionary: MuscleDictionary,
    defaultQuadrantName: String,
): String {
    if (bodySelection.isEmpty()) return defaultQuadrantName
    regionGroupFullImageName(regionGroup)?.let { fullName ->
        val isFullGroup = muscleDictionary
            .allBodies(regionGroup)
            .all { body ->
                val required = muscleDictionary.allMuscles(regionGroup, body)
                required.isNotEmpty() && required.all { it in bodySelection[body].orEmpty() }
            }
        if (isFullGroup) return fullName
    }
    for (body in muscleDictionary.allBodies(regionGroup)) {
        val selected = bodySelection[body].orEmpty()
        if (selected.isEmpty()) continue
        bodyFullImageName(body)?.let { fullBodyImage ->
            val required = muscleDictionary.allMuscles(regionGroup, body)
            if (required.isNotEmpty() && required.all { it in selected }) {
                return fullBodyImage
            }
        }
    }
    return when (regionGroup) {
        RegionGroup.UpperFront -> frontUpperImageFromSelection(bodySelection)
        RegionGroup.UpperBack -> backUpperImageFromSelection(bodySelection)
        RegionGroup.LowerFront,
        RegionGroup.LowerBack,
        -> defaultQuadrantName
    }
}

private fun frontUpperImageFromSelection(bodySelection: Map<RegionBody, Set<Muscle>>): String {
    val frontFallbackRes = R.drawable.chest_normal
    val chest = bodySelection[RegionBody.Chest].orEmpty().map { it.wireKey }.toSet()
    val belly = bodySelection[RegionBody.Belly].orEmpty().map { it.wireKey }.toSet()
    return when {
        chest.isNotEmpty() -> {
            val c = ChestFocusStripDrawableMapper.drawableResForChestFocusMuscles(chest) ?: frontFallbackRes
            focusStripDrawableNameOrFallback(c)
        }
        belly.isNotEmpty() -> {
            val b = BellyFocusStripDrawableMapper.drawableResForBellyFocusMuscles(belly) ?: frontFallbackRes
            focusStripDrawableNameOrFallback(b)
        }
        else -> focusStripDrawableNameOrFallback(frontFallbackRes)
    }
}

private fun backUpperImageFromSelection(bodySelection: Map<RegionBody, Set<Muscle>>): String {
    val fallback = R.drawable.back_normal
    val backTokens = bodySelection[RegionBody.Back].orEmpty().map { it.wireKey }.toList()
    if (backTokens.isEmpty()) return focusStripDrawableNameOrFallback(fallback)
    val d = BackFocusStripDrawableMapper.drawableResForBackFocusMuscles(backTokens) ?: fallback
    return focusStripDrawableNameOrFallback(d)
}

private fun bodyFullImageName(regionBody: RegionBody): String? = when (regionBody) {
    RegionBody.Chest -> "chest_full_chest"
    else -> null
}

private fun regionGroupFullImageName(regionGroup: RegionGroup): String? = when (regionGroup) {
    RegionGroup.UpperBack -> "back_full_back"
    else -> null
}

fun emptyFocusMusclesStripImageNames(): ImmutableList<String> = persistentListOf(
    FOCUS_STRIP_FRONT_UPPER_EMPTY_NAME,
    FOCUS_STRIP_DEFAULT_QUADRANT_NAME,
    FOCUS_STRIP_DEFAULT_QUADRANT_NAME,
    FOCUS_STRIP_DEFAULT_QUADRANT_NAME,
)
