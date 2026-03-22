package com.hoabui.virtualbody3d.data.mapper

import com.hoabui.virtualbody3d.data.model.InitialSetupOptionDto
import com.hoabui.virtualbody3d.data.model.InitialSetupStepDto
import com.hoabui.virtualbody3d.domain.model.onboarding.InitialSetupOption
import com.hoabui.virtualbody3d.domain.model.onboarding.InitialSetupStep

fun InitialSetupOptionDto.toDomain(): InitialSetupOption = InitialSetupOption(
    id = id,
    label = label,
    iconName = iconName
)

fun InitialSetupStepDto.toDomain(): InitialSetupStep = InitialSetupStep(
    question = question,
    subtitle = subtitle,
    options = options.map { it.toDomain() },
    isMultiSelect = isMultiSelect
)
