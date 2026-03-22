package com.hoabui.virtualbody3d.domain.repository

import com.hoabui.virtualbody3d.domain.model.content.Supplement
import kotlinx.coroutines.flow.Flow

interface SupplementRepository {
    fun getSupplements(): Flow<List<Supplement>>
}
