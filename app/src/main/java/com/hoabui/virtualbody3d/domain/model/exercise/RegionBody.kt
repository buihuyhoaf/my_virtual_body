package com.hoabui.virtualbody3d.domain.model.exercise

enum class RegionBody(
    val wireKey: String,
) {
    Chest("chest"),
    Belly("belly"),
    ShouldersFront("shoulders_front"),
    ArmsFront("arms_front"),
    Back("back"),
    ShouldersBack("shoulders_back"),
    ArmsBack("arms_back"),
    LegsFront("legs_front"),
    LegsBack("legs_back");

    companion object {
        fun fromWireKeyOrNull(raw: String?): RegionBody? {
            if (raw == null) return null
            return entries.firstOrNull { it.wireKey == raw }
        }

        fun fromWireKeyStrict(raw: String): RegionBody =
            fromWireKeyOrNull(raw)
                ?: error("Unknown region body key: $raw")
    }
}
