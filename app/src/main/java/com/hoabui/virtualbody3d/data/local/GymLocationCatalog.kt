package com.hoabui.virtualbody3d.data.local

import com.hoabui.virtualbody3d.domain.model.exercise.DEFAULT_SESSION_LOCATION_ID
import com.hoabui.virtualbody3d.domain.model.exercise.GymLocation
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GymLocationCatalog @Inject constructor() {

    /** Static facilities for Phase A; replace with API-driven data later. */
    val locations: List<GymLocation> = listOf(
        GymLocation(
            id = DEFAULT_SESSION_LOCATION_ID,
            displayName = "My gym",
        ),
        GymLocation(
            id = "downtown",
            displayName = "Downtown",
        ),
    )
}
