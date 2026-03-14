package com.hoabui.virtualbody3d.data.local

import com.hoabui.virtualbody3d.data.model.UserInfoDto
import com.hoabui.virtualbody3d.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserInfoLocalDataSource @Inject constructor() {

    private val users: Map<String, UserInfoDto> = listOf(
        UserInfoDto(
            id = "coach_alex",
            displayName = "Coach Alex",
            avatarResId = R.drawable.body_unsplash
        ),
        UserInfoDto(
            id = "workout_bot",
            displayName = "Workout Bot",
            avatarResId = R.drawable.body_unsplash
        ),
        UserInfoDto(
            id = "system",
            displayName = "System",
            avatarResId = R.drawable.body_unsplash
        )
    ).associateBy { it.id }

    fun getUserById(userId: String): UserInfoDto? = users[userId]
}
