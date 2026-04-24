package com.hoabui.virtualbody3d.domain.model.exercise

class TestMuscleDictionary : MuscleDictionary {
    private val hierarchy: Map<RegionGroup, Map<RegionBody, Set<Muscle>>> = mapOf(
        RegionGroup.UpperFront to mapOf(
            RegionBody.Chest to setOf(
                Muscle.UPPER_PECTORALIS,
                Muscle.MIDDLE_PECTORALIS,
                Muscle.LOWER_PECTORALIS,
                Muscle.PECTORALIS_MAJOR,
            ),
            RegionBody.Belly to setOf(
                Muscle.RECTUS_ABDOMINIS,
                Muscle.UPPER_ABS,
                Muscle.LOWER_ABS,
                Muscle.OBLIQUES,
                Muscle.TRANSVERSE_ABDOMINIS,
                Muscle.SERRATUS_ANTERIOR,
            ),
            RegionBody.ShouldersFront to setOf(Muscle.ANTERIOR_DELTOID, Muscle.LATERAL_DELTOID),
            RegionBody.ArmsFront to setOf(Muscle.BICEPS_BRACHII, Muscle.BRACHIALIS, Muscle.BRACHIORADIALIS),
        ),
        RegionGroup.UpperBack to mapOf(
            RegionBody.Back to setOf(
                Muscle.LATISSIMUS_DORSI,
                Muscle.MIDDLE_TRAPEZIUS,
                Muscle.UPPER_TRAPEZIUS,
                Muscle.INFRASPINATUS,
                Muscle.ERECTOR_SPINAE,
            ),
            RegionBody.ShouldersBack to setOf(Muscle.POSTERIOR_DELTOID),
            RegionBody.ArmsBack to setOf(Muscle.TRICEPS_BRACHII),
        ),
        RegionGroup.LowerFront to mapOf(
            RegionBody.LegsFront to setOf(
                Muscle.QUADRICEPS,
                Muscle.RECTUS_FEMORIS,
                Muscle.VASTUS_LATERALIS,
                Muscle.VASTUS_MEDIALIS,
                Muscle.HIP_FLEXORS,
                Muscle.ILIOPSOAS,
            ),
        ),
        RegionGroup.LowerBack to mapOf(
            RegionBody.LegsBack to setOf(
                Muscle.GLUTEUS_MAXIMUS,
                Muscle.HAMSTRINGS,
                Muscle.BICEPS_FEMORIS,
                Muscle.GASTROCNEMIUS,
                Muscle.SOLEUS,
            ),
        ),
    )

    private val bodyByMuscle: Map<Muscle, RegionBody> = hierarchy
        .values
        .flatMap { it.entries }
        .flatMap { (body, muscles) -> muscles.map { muscle -> muscle to body } }
        .toMap()

    private val groupByBody: Map<RegionBody, RegionGroup> = hierarchy
        .flatMap { (group, bodyMap) -> bodyMap.keys.map { body -> body to group } }
        .toMap()

    override fun getBodyForMuscle(muscle: Muscle): RegionBody =
        bodyByMuscle[muscle] ?: error("Unknown muscle in test dictionary: ${muscle.wireKey}")

    override fun getGroupForBody(body: RegionBody): RegionGroup =
        groupByBody[body] ?: error("Unknown body in test dictionary: ${body.wireKey}")

    override fun allBodies(regionGroup: RegionGroup): Set<RegionBody> = hierarchy[regionGroup].orEmpty().keys

    override fun allMuscles(regionGroup: RegionGroup, body: RegionBody): Set<Muscle> =
        hierarchy[regionGroup].orEmpty()[body].orEmpty()
}
