package com.hoabui.virtualbody3d.data.local

import com.hoabui.virtualbody3d.data.model.ProgressSnapshotDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgressTimelineLocalDataSource @Inject constructor() {

    fun getSnapshots(): List<ProgressSnapshotDto> = listOf(
        ProgressSnapshotDto(
            dateIso = "2025-03-01",
            imageUrl = null,
            weightKg = 75.0f,
            bodyFatPercent = 20.0f,
            muscleMassKg = 32.4f
        ),
        ProgressSnapshotDto(
            dateIso = "2025-03-05",
            imageUrl = null,
            weightKg = 74.2f,
            bodyFatPercent = 19.5f,
            muscleMassKg = 32.7f
        ),
        ProgressSnapshotDto(
            dateIso = "2025-03-10",
            imageUrl = null,
            weightKg = 73.5f,
            bodyFatPercent = 19.0f,
            muscleMassKg = 33.0f
        ),
        ProgressSnapshotDto(
            dateIso = "2025-03-15",
            imageUrl = null,
            weightKg = 72.8f,
            bodyFatPercent = 18.6f,
            muscleMassKg = 33.2f
        ),
        ProgressSnapshotDto(
            dateIso = "2025-03-20",
            imageUrl = null,
            weightKg = 72.0f,
            bodyFatPercent = 18.2f,
            muscleMassKg = 33.6f
        ),
    )
}
