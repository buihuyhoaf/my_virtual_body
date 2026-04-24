package com.hoabui.virtualbody3d.domain.model.exercise

enum class RegionGroup(
    val wireKey: String,
) {
    UpperFront("upper_front"),
    UpperBack("upper_back"),
    LowerFront("lower_front"),
    LowerBack("lower_back");

    companion object {
        fun fromWireKeyOrNull(raw: String?): RegionGroup? {
            if (raw == null) return null
            return entries.firstOrNull { it.wireKey == raw }
        }

        fun fromWireKeyStrict(raw: String): RegionGroup =
            fromWireKeyOrNull(raw)
                ?: error("Unknown region group key: $raw")
    }
}
