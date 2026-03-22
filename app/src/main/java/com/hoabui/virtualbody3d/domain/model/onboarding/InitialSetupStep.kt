package com.hoabui.virtualbody3d.domain.model.onboarding

/**
 * Một bước trong flow Initial Setup.
 * @param question Câu hỏi hiển thị (title).
 * @param subtitle Mô tả ngắn (vd. step 4); null nếu không có.
 * @param options Danh sách lựa chọn; rỗng cho step không có chọn (vd. step 4).
 * @param isMultiSelect true nếu user có thể chọn nhiều option (step 2, 3).
 */
data class InitialSetupStep(
    val question: String,
    val subtitle: String? = null,
    val options: List<InitialSetupOption>,
    val isMultiSelect: Boolean = false
)
