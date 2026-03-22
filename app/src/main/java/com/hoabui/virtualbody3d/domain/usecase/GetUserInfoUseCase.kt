package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.chat.UserInfo
import com.hoabui.virtualbody3d.domain.repository.UserInfoRepository
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val userInfoRepository: UserInfoRepository
) {
    operator fun invoke(userId: String): UserInfo? = userInfoRepository.getUserById(userId)
}

