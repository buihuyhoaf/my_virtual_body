package com.hoabui.virtualbody3d.ui.body.state

data class BodyScreenState(
    val uiState: BodyUiState = BodyUiState(),
    val selectedTab: BodyTab = BodyTab.Body,
    val title: String = "My Body"
)
