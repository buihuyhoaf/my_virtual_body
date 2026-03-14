package com.hoabui.virtualbody3d.data.local

import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.data.model.SupplementDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupplementLocalDataSource @Inject constructor() {

    fun getSupplements(): Flow<List<SupplementDto>> = flowOf(
        listOf(
            SupplementDto(
                id = "1",
                name = "Vitamin D3",
                nutrient = "Vitamin D",
                imageResId = R.drawable.body_unsplash
            ),
            SupplementDto(
                id = "2",
                name = "Magnesium Glycinate",
                nutrient = "Magnesium",
                imageResId = R.drawable.body_unsplash
            ),
            SupplementDto(
                id = "3",
                name = "Omega-3",
                nutrient = "Fish Oil",
                imageResId = R.drawable.body_unsplash
            )
        )
    )
}
