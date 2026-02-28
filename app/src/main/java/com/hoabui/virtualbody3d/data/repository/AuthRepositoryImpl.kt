package com.hoabui.virtualbody3d.data.repository

import com.hoabui.virtualbody3d.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor() : AuthRepository {

    override suspend fun login(email: String, password: String) {
        delay(1000)
        if (email.isBlank() || password.isBlank()) {
            throw Exception("Invalid credentials")
        }
    }
}
