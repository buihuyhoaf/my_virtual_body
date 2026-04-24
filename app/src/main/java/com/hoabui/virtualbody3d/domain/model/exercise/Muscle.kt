package com.hoabui.virtualbody3d.domain.model.exercise

enum class Muscle(
    val wireKey: String,
) {
    UPPER_PECTORALIS("upper_pectoralis"),
    MIDDLE_PECTORALIS("middle_pectoralis"),
    LOWER_PECTORALIS("lower_pectoralis"),
    PECTORALIS_MAJOR("pectoralis_major"),
    RECTUS_ABDOMINIS("rectus_abdominis"),
    UPPER_ABS("upper_abs"),
    LOWER_ABS("lower_abs"),
    OBLIQUES("obliques"),
    TRANSVERSE_ABDOMINIS("transverse_abdominis"),
    SERRATUS_ANTERIOR("serratus_anterior"),
    ANTERIOR_DELTOID("anterior_deltoid"),
    LATERAL_DELTOID("lateral_deltoid"),
    BICEPS_BRACHII("biceps_brachii"),
    BRACHIALIS("brachialis"),
    BRACHIORADIALIS("brachioradialis"),
    LATISSIMUS_DORSI("latissimus_dorsi"),
    MIDDLE_TRAPEZIUS("middle_trapezius"),
    UPPER_TRAPEZIUS("upper_trapezius"),
    INFRASPINATUS("infraspinatus"),
    ERECTOR_SPINAE("erector_spinae"),
    POSTERIOR_DELTOID("posterior_deltoid"),
    TRICEPS_BRACHII("triceps_brachii"),
    QUADRICEPS("quadriceps"),
    RECTUS_FEMORIS("rectus_femoris"),
    VASTUS_LATERALIS("vastus_lateralis"),
    VASTUS_MEDIALIS("vastus_medialis"),
    HIP_FLEXORS("hip_flexors"),
    ILIOPSOAS("iliopsoas"),
    GLUTEUS_MAXIMUS("gluteus_maximus"),
    HAMSTRINGS("hamstrings"),
    BICEPS_FEMORIS("biceps_femoris"),
    GASTROCNEMIUS("gastrocnemius"),
    SOLEUS("soleus");

    companion object {
        fun fromWireKeyOrNull(raw: String?): Muscle? {
            if (raw == null) return null
            return entries.firstOrNull { it.wireKey == raw }
        }

        fun fromWireKeyStrict(raw: String): Muscle =
            fromWireKeyOrNull(raw)
                ?: error("Unknown muscle key: $raw")
    }
}
