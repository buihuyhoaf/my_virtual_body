package com.hoabui.virtualbody3d.domain.model.exercise

typealias RegionBodyMuscleSelectionMap = Map<RegionGroup, Map<RegionBody, Set<Muscle>>>

interface MuscleDictionary {
    fun getBodyForMuscle(muscle: Muscle): RegionBody
    fun getGroupForBody(body: RegionBody): RegionGroup
    fun getGroupForMuscle(muscle: Muscle): RegionGroup = getGroupForBody(getBodyForMuscle(muscle))
    fun allBodies(regionGroup: RegionGroup): Set<RegionBody>
    fun allMuscles(regionGroup: RegionGroup, body: RegionBody): Set<Muscle>
}
