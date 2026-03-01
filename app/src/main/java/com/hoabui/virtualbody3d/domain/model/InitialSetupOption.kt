package com.hoabui.virtualbody3d.domain.model

/**
 * Một lựa chọn trong step Initial Setup.
 * @param id Định danh (để lưu/ghi nhận lựa chọn).
 * @param label Nội dung hiển thị.
 * @param iconName Tên icon (vd. "accessibility_new") dùng cho step có icon; null nếu không dùng.
 */
data class InitialSetupOption(
    val id: String,
    val label: String,
    val iconName: String? = null
)
