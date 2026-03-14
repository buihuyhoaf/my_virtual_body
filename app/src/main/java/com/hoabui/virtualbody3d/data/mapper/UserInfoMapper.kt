package com.hoabui.virtualbody3d.data.mapper

import com.hoabui.virtualbody3d.data.model.UserInfoDto
import com.hoabui.virtualbody3d.domain.model.UserInfo

fun UserInfoDto.toDomain(): UserInfo = UserInfo(
    id = id,
    displayName = displayName,
    avatarResId = avatarResId
)
