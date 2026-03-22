package com.hoabui.virtualbody3d.data.repository

import com.hoabui.virtualbody3d.data.local.PromoBannerLocalDataSource
import com.hoabui.virtualbody3d.data.mapper.toDomain
import com.hoabui.virtualbody3d.domain.model.content.PromoBanner
import com.hoabui.virtualbody3d.domain.repository.PromoBannerRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PromoBannerRepositoryImpl @Inject constructor(
    private val localDataSource: PromoBannerLocalDataSource
) : PromoBannerRepository {

    override fun getPromoBanners(): List<PromoBanner> =
        localDataSource.getPromoBanners().map { it.toDomain() }
}
