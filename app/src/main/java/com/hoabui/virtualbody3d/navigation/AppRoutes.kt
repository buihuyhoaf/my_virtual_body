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

/** Nested graph: [ExerciseDashboardRoute] + [ExerciseLibraryRoute] + [SessionBookingEditorRoute]. */
@Serializable
data object ExerciseLibraryGraphRoute

@Serializable
data object ExerciseDashboardRoute

@Serializable
data object SessionBookingEditorRoute

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
    /** [ExerciseCategory.name] from dashboard shortcuts; XOR with [initialBodyRegions]. */
    val initialExerciseCategory: String? = null,
    /** [BodyRegion.name] entries for composite dashboard tiles; XOR with [initialExerciseCategory]. */
    val initialBodyRegions: List<String>? = null,
)

@Serializable
data object WorkoutCalendarRoute
