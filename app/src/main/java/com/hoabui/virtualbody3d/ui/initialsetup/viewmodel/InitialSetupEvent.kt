package com.hoabui.virtualbody3d.ui.initialsetup.viewmodel

sealed interface InitialSetupEvent {
    data object Complete : InitialSetupEvent
}
