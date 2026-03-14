package com.hoabui.virtualbody3d.data.local

import com.hoabui.virtualbody3d.data.model.PromoBannerDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PromoBannerLocalDataSource @Inject constructor() {

    fun getPromoBanners(): List<PromoBannerDto> = listOf(
        PromoBannerDto(
            id = "1",
            gradientColorHexList = listOf("#B85450", "#E8B4B0")
        ),
        PromoBannerDto(
            id = "2",
            gradientColorHexList = listOf("#E8B4B0", "#F5E6E4")
        ),
        PromoBannerDto(
            id = "3",
            gradientColorHexList = listOf("#F5E6E4", "#E8B4B0")
        )
    )
}
