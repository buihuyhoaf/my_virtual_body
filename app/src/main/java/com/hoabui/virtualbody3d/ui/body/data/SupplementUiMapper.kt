package com.hoabui.virtualbody3d.ui.body.data

import com.hoabui.virtualbody3d.domain.model.Supplement

fun Supplement.toSupplementUiItem(): SupplementUiItem = SupplementUiItem(
    name = name,
    nutrient = nutrient,
    imageResId = imageResId
)
