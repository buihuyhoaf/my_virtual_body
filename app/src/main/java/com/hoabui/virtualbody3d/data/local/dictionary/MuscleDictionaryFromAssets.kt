package com.hoabui.virtualbody3d.data.local.dictionary

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hoabui.virtualbody3d.domain.model.exercise.Muscle
import com.hoabui.virtualbody3d.domain.model.exercise.MuscleDictionary
import com.hoabui.virtualbody3d.domain.model.exercise.RegionBody
import com.hoabui.virtualbody3d.domain.model.exercise.RegionGroup
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MuscleDictionaryFromAssets @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson,
) : MuscleDictionary {

    private val decoded: DecodedDictionary by lazy { loadAndValidateDictionary() }

    override fun getBodyForMuscle(muscle: Muscle): RegionBody =
        decoded.bodyByMuscle[muscle]
            ?: error("No region body found for muscle: ${muscle.wireKey}")

    override fun getGroupForBody(body: RegionBody): RegionGroup =
        decoded.groupByBody[body]
            ?: error("No region group found for body: ${body.wireKey}")

    override fun allBodies(regionGroup: RegionGroup): Set<RegionBody> =
        decoded.bodiesByGroup[regionGroup].orEmpty()

    override fun allMuscles(regionGroup: RegionGroup, body: RegionBody): Set<Muscle> =
        decoded.musclesByGroupAndBody[regionGroup].orEmpty()[body].orEmpty()

    private fun loadAndValidateDictionary(): DecodedDictionary {
        val raw = context.assets.open(MUSCLE_DICTIONARY_ASSET_PATH).use { input ->
            input.bufferedReader().readText()
        }
        val type = object : TypeToken<Map<String, Map<String, List<String>>>>() {}.type
        val parsed: Map<String, Map<String, List<String>>> = gson.fromJson(raw, type)
        val bodyByMuscle = linkedMapOf<Muscle, RegionBody>()
        val groupByBody = linkedMapOf<RegionBody, RegionGroup>()
        val bodiesByGroup = linkedMapOf<RegionGroup, MutableSet<RegionBody>>()
        val musclesByGroupAndBody = linkedMapOf<RegionGroup, MutableMap<RegionBody, Set<Muscle>>>()
        parsed.forEach { (groupRaw, bodyMap) ->
            val group = RegionGroup.fromWireKeyStrict(groupRaw)
            val bodySet = bodiesByGroup.getOrPut(group) { linkedSetOf() }
            val muscleMap = musclesByGroupAndBody.getOrPut(group) { linkedMapOf() }
            bodyMap.forEach { (bodyRaw, musclesRaw) ->
                val body = RegionBody.fromWireKeyStrict(bodyRaw)
                val previousGroup = groupByBody.putIfAbsent(body, group)
                check(previousGroup == null || previousGroup == group) {
                    "Region body ${body.wireKey} belongs to multiple groups: ${previousGroup?.wireKey}, ${group.wireKey}"
                }
                bodySet.add(body)
                val muscles = musclesRaw.map(Muscle::fromWireKeyStrict).toSet()
                check(muscles.isNotEmpty()) { "Body ${body.wireKey} must define at least one muscle" }
                muscleMap[body] = muscles
                muscles.forEach { muscle ->
                    val existingBody = bodyByMuscle.putIfAbsent(muscle, body)
                    check(existingBody == null || existingBody == body) {
                        "Muscle ${muscle.wireKey} belongs to multiple bodies: ${existingBody?.wireKey}, ${body.wireKey}"
                    }
                }
            }
        }
        return DecodedDictionary(
            bodyByMuscle = bodyByMuscle,
            groupByBody = groupByBody,
            bodiesByGroup = bodiesByGroup.mapValues { it.value.toSet() },
            musclesByGroupAndBody = musclesByGroupAndBody.mapValues { entry ->
                entry.value.toMap()
            },
        )
    }

    private data class DecodedDictionary(
        val bodyByMuscle: Map<Muscle, RegionBody>,
        val groupByBody: Map<RegionBody, RegionGroup>,
        val bodiesByGroup: Map<RegionGroup, Set<RegionBody>>,
        val musclesByGroupAndBody: Map<RegionGroup, Map<RegionBody, Set<Muscle>>>,
    )
}

private const val MUSCLE_DICTIONARY_ASSET_PATH = "muscle_dictionary.json"
