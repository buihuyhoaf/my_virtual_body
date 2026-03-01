package com.hoabui.virtualbody3d.data.remote

import com.google.gson.annotations.SerializedName

/**
 * Request body gửi lên backend khi user hoàn thành Initial Setup.
 * @param reflectionIntentId Id lựa chọn step 1 (Why are you here?)
 * @param focusIds Danh sách id lựa chọn step 2 (What's your focus?)
 * @param focusAreaIds Danh sách id lựa chọn step 3 (What would you like to focus on?)
 */
data class InitialSetupRequestDto(
    @SerializedName("reflection_intent_id") val reflectionIntentId: String,
    @SerializedName("focus_ids") val focusIds: List<String>,
    @SerializedName("focus_area_ids") val focusAreaIds: List<String>
)
