package com.hoabui.virtualbody3d.data.local

import com.hoabui.virtualbody3d.data.model.WorkoutScheduleDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutScheduleLocalDataSource @Inject constructor() {

    private val storage = ConcurrentHashMap<String, WorkoutScheduleDto>()
    private val _schedules = MutableStateFlow(storage.values.toList())
    val schedules = _schedules.asStateFlow()

    suspend fun save(dto: WorkoutScheduleDto) {
        storage[dto.id] = dto
        _schedules.value = storage.values.toList()
    }

    suspend fun getAll(): List<WorkoutScheduleDto> = storage.values.toList()
}
