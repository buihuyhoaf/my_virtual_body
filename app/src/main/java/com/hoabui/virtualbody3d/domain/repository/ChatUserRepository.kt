package com.hoabui.virtualbody3d.domain.repository

import com.hoabui.virtualbody3d.domain.model.UserInfo

interface UserInfoRepository {
    fun getUserById(userId: String): UserInfo?
}

