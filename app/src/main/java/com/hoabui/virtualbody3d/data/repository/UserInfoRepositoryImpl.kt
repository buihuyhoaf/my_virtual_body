package com.hoabui.virtualbody3d.data.repository

import com.hoabui.virtualbody3d.data.local.UserInfoLocalDataSource
import com.hoabui.virtualbody3d.data.mapper.toDomain
import com.hoabui.virtualbody3d.domain.model.UserInfo
import com.hoabui.virtualbody3d.domain.repository.UserInfoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserInfoRepositoryImpl @Inject constructor(
    private val localDataSource: UserInfoLocalDataSource
) : UserInfoRepository {

    override fun getUserById(userId: String): UserInfo? =
        localDataSource.getUserById(userId)?.toDomain()
}
