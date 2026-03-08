package com.hoabui.virtualbody3d.domain.repository

import com.hoabui.virtualbody3d.domain.model.PromoBanner

interface PromoBannerRepository {
    fun getPromoBanners(): List<PromoBanner>
}
