package com.hoabui.virtualbody3d.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Base Retrofit API service contract.
 * Add shared or sample endpoints here; feature-specific APIs can live in separate interfaces.
 */
interface ApiService {

    /**
     * Sample endpoint for health/connectivity check. Replace or remove when real backend is available.
     */
    @GET("health")
    suspend fun health(): Response<Unit>

    /**
     * Gửi thông tin đăng nhập lên backend.
     */
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequestDto): Response<Unit>
}
