package com.hoabui.virtualbody3d.ui.addworkout

sealed interface AddWorkoutEvent {
    data object Saved : AddWorkoutEvent
    data object Cancel : AddWorkoutEvent
}
