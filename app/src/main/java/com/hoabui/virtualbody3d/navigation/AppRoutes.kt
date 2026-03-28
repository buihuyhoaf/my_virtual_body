package com.hoabui.virtualbody3d.navigation

import kotlinx.serialization.Serializable

@Serializable
data object OnboardingRoute

@Serializable
data object LoginRoute

@Serializable
data object InitialSetupRoute

@Serializable
data object CreateBaselineRoute

@Serializable
data object HomeRoute

@Serializable
data object AddRoute

@Serializable
data object MealCaptureRoute

@Serializable
data object MessagesRoute

@Serializable
data class MessageDetailRoute(val messageId: String)

@Serializable
data object CenfitCoachRoute

@Serializable
data object ProfileRoute

@Serializable
data object BodyScanResultRoute

@Serializable
data object BodyDetailAnalystRoute

@Serializable
data class BodyRegionDetailRoute(val region: String)

@Serializable
data object ExerciseLibraryRoute

@Serializable
data class ExerciseDetailRoute(val exerciseId: String)

@Serializable
data class AddWorkoutRoute(val exerciseId: String)
