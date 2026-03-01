package com.hoabui.virtualbody3d.data.remote

import com.google.gson.annotations.SerializedName

/**
 * Request body gửi lên backend khi user đăng nhập.
 */
data class LoginRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)
