package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.PromoBanner
import com.hoabui.virtualbody3d.domain.repository.PromoBannerRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetPromoBannersUseCase @Inject constructor(
    private val promoBannerRepository: PromoBannerRepository
) {
    operator fun invoke(): Flow<List<PromoBanner>> = flow {
        emit(promoBannerRepository.getPromoBanners())
    }
}
