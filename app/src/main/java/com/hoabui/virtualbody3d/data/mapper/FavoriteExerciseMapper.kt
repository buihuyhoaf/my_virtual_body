package com.hoabui.virtualbody3d.data.mapper

import com.hoabui.virtualbody3d.data.model.FavoriteExerciseDto
import com.hoabui.virtualbody3d.domain.model.FavoriteExercise

fun FavoriteExerciseDto.toDomain(): FavoriteExercise = FavoriteExercise(
    id = id,
    name = name,
    exerciseVolume = exerciseVolume,
    imageResId = imageResId
)
