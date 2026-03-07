package com.hoabui.virtualbody3d.ui.camera.viewmodel

/**
 * One-off events for the camera/baseline capture flow (e.g. navigation).
 */
sealed class CameraCaptureEvent {
    data object NavigateHome : CameraCaptureEvent()
}
