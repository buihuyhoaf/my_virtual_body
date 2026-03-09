package com.hoabui.virtualbody3d.data.repository

import com.hoabui.virtualbody3d.domain.model.PromoBanner
import com.hoabui.virtualbody3d.domain.repository.PromoBannerRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PromoBannerRepositoryImpl @Inject constructor() : PromoBannerRepository {

    override fun getPromoBanners(): List<PromoBanner> = listOf(
        PromoBanner(
            id = "1",
            gradientColorHexList = listOf("#B85450", "#E8B4B0")
        ),
        PromoBanner(
            id = "2",
            gradientColorHexList = listOf("#E8B4B0", "#F5E6E4")
        ),
        PromoBanner(
            id = "3",
            gradientColorHexList = listOf("#F5E6E4", "#E8B4B0")
        )
    )
}
