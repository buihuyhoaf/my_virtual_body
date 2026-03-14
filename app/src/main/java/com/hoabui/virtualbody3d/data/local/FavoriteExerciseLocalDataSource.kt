package com.hoabui.virtualbody3d.data.local

import com.hoabui.virtualbody3d.data.model.FavoriteExerciseDto
import com.hoabui.virtualbody3d.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteExerciseLocalDataSource @Inject constructor() {

    fun getFavoriteExercises(): Flow<List<FavoriteExerciseDto>> = flowOf(
        listOf(
            FavoriteExerciseDto(id = "1", name = "Bench Press", exerciseVolume = "80 - 12reps x 3set", imageResId = R.drawable.body_unsplash),
            FavoriteExerciseDto(id = "2", name = "Squat", exerciseVolume = "80 - 12reps x 3set", imageResId = R.drawable.body_unsplash),
            FavoriteExerciseDto(id = "3", name = "Deadlift", exerciseVolume = "80 - 12reps x 3set", imageResId = R.drawable.body_unsplash)
        )
    )
}
