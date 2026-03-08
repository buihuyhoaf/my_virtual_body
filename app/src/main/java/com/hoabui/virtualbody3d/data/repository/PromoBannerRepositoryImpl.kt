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
            title = "Track your progress",
            subtitle = "See how your body changes over time",
            gradientColorHexList = listOf("#B85450", "#E8B4B0")
        ),
        PromoBanner(
            id = "2",
            title = "Nutrition tips",
            subtitle = "Fuel your workouts with the right meals",
            gradientColorHexList = listOf("#E8B4B0", "#F5E6E4")
        ),
        PromoBanner(
            id = "3",
            title = "Body analysis",
            subtitle = "Tap a region for detailed insights",
            gradientColorHexList = listOf("#F5E6E4", "#E8B4B0")
        )
    )
}
