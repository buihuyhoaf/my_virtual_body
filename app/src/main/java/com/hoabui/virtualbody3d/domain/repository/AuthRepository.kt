package com.hoabui.virtualbody3d.domain.repository

interface AuthRepository {
    suspend fun login(email: String, password: String)
}
