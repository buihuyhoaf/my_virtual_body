package com.hoabui.virtualbody3d.data.local

import com.hoabui.virtualbody3d.data.model.WorkoutSessionDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutSessionLocalDataSource @Inject constructor() {

    private val storage = ConcurrentHashMap<String, WorkoutSessionDto>()
    private val _sessions = MutableStateFlow(storage.values.toList())
    val sessions = _sessions.asStateFlow()

    suspend fun save(dto: WorkoutSessionDto) {
        storage[dto.id] = dto
        _sessions.value = storage.values.toList()
    }

    suspend fun getAll(): List<WorkoutSessionDto> = storage.values.toList()
}
