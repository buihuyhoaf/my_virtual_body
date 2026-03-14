package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.Supplement
import com.hoabui.virtualbody3d.domain.repository.SupplementRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSupplementsUseCase @Inject constructor(
    private val supplementRepository: SupplementRepository
) {
    operator fun invoke(): Flow<List<Supplement>> =
        supplementRepository.getSupplements()
}
