package com.hoabui.virtualbody3d.data.model

data class InitialSetupStepDto(
    val question: String,
    val subtitle: String? = null,
    val options: List<InitialSetupOptionDto>,
    val isMultiSelect: Boolean = false
)

data class InitialSetupOptionDto(
    val id: String,
    val label: String,
    val iconName: String? = null
)
