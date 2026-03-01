package com.hoabui.virtualbody3d.data.repository

import com.hoabui.virtualbody3d.data.remote.ApiService
import com.hoabui.virtualbody3d.data.remote.LoginRequestDto
import com.hoabui.virtualbody3d.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AuthRepository {

    override suspend fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            throw Exception("Invalid credentials")
        }
        val body = LoginRequestDto(email = email, password = password)
        runCatching { apiService.login(body) }
        delay(200)
        // Tạm thời: luôn success sau 200ms (backend có thể chưa có endpoint thật)
    }
}
