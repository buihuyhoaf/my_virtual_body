package com.hoabui.virtualbody3d.domain.repository

import com.hoabui.virtualbody3d.domain.model.content.PromoBanner

interface PromoBannerRepository {
    fun getPromoBanners(): List<PromoBanner>
}
