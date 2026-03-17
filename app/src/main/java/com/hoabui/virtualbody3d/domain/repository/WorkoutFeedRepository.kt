package com.hoabui.virtualbody3d.domain.repository

import com.hoabui.virtualbody3d.domain.model.WorkoutFeedItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository abstraction for workout feed data.
 * Returns domain models; data layer is responsible for DTO mapping.
 */
interface WorkoutFeedRepository {

    /**
     * Stream of workout feed items (e.g. recent workout days with exercises).
     * Ready to swap to real API/cache later.
     */
    fun getWorkoutFeed(): Flow<List<WorkoutFeedItem>>
}
