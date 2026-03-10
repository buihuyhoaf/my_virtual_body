package com.hoabui.virtualbody3d.data.repository

import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.UserInfo
import com.hoabui.virtualbody3d.domain.repository.UserInfoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserInfoRepositoryImpl @Inject constructor() : UserInfoRepository {

    private val users: Map<String, UserInfo> = listOf(
        UserInfo(
            id = "coach_alex",
            displayName = "Coach Alex",
            avatarResId = R.drawable.body_unsplash
        ),
        UserInfo(
            id = "workout_bot",
            displayName = "Workout Bot",
            avatarResId = R.drawable.body_unsplash
        ),
        UserInfo(
            id = "system",
            displayName = "System",
            avatarResId = R.drawable.body_unsplash
        )
    ).associateBy { it.id }

    override fun getUserById(userId: String): UserInfo? = users[userId]
}

