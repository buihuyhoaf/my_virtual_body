package com.hoabui.virtualbody3d.navigation

import kotlinx.serialization.Serializable

@Serializable
data object LoginRoute

@Serializable
data object HomeRoute

@Serializable
data object AddRoute

@Serializable
data object MealCaptureRoute

@Serializable
data object CenfitCoachRoute

@Serializable
data object ProfileRoute

@Serializable
data object BodyDetailAnalystRoute

@Serializable
data class BodyRegionDetailRoute(val region: String)

@Serializable
data class ExerciseLibraryRoute(
    /** When set, library opens with the selection bar expanded in edit mode for this Room row. */
    val scheduleRowIdToEdit: Long? = null,
)

@Serializable
data object WorkoutCalendarRoute
