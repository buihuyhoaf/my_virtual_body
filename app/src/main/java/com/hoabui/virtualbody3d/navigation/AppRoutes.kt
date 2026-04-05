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
data object MessagesRoute

@Serializable
data class MessageDetailRoute(val messageId: String)

@Serializable
data object CenfitCoachRoute

@Serializable
data object ProfileRoute

@Serializable
data object BodyDetailAnalystRoute

@Serializable
data class BodyRegionDetailRoute(val region: String)

@Serializable
data object ExerciseLibraryRoute
