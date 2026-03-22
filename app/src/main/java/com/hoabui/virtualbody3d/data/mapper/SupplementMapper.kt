package com.hoabui.virtualbody3d.data.mapper

import com.hoabui.virtualbody3d.data.model.SupplementDto
import com.hoabui.virtualbody3d.domain.model.content.Supplement

fun SupplementDto.toDomain(): Supplement = Supplement(
    id = id,
    name = name,
    nutrient = nutrient,
    imageResId = imageResId
)
