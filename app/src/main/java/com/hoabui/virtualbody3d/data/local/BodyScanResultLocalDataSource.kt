package com.hoabui.virtualbody3d.data.local

import com.google.gson.Gson
import com.hoabui.virtualbody3d.data.local.db.BodyScanResultDao
import com.hoabui.virtualbody3d.data.model.BodyScanResultDto
import com.hoabui.virtualbody3d.di.IoDispatcher
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

@Singleton
class BodyScanResultLocalDataSource @Inject constructor(
    private val bodyScanResultDao: BodyScanResultDao,
    private val gson: Gson,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
) {
    fun observeBodyScanResult(): Flow<BodyScanResultDto> =
        bodyScanResultDao.observeActive()
            .map { row ->
                requireNotNull(row) { "body_scan_results row missing (migration seed failed?)" }
                gson.fromJson(row.payloadJson, BodyScanResultDto::class.java)
            }
            .flowOn(ioDispatcher)
}
