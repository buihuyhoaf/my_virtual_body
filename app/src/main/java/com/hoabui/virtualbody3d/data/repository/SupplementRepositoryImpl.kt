package com.hoabui.virtualbody3d.data.repository

import com.hoabui.virtualbody3d.data.local.SupplementLocalDataSource
import com.hoabui.virtualbody3d.data.mapper.toDomain
import com.hoabui.virtualbody3d.domain.model.Supplement
import com.hoabui.virtualbody3d.domain.repository.SupplementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupplementRepositoryImpl @Inject constructor(
    private val localDataSource: SupplementLocalDataSource
) : SupplementRepository {

    override fun getSupplements(): Flow<List<Supplement>> =
        localDataSource.getSupplements().map { list -> list.map { it.toDomain() } }
}
