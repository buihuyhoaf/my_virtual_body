package com.hoabui.virtualbody3d.domain.repository

import com.hoabui.virtualbody3d.domain.model.onboarding.InitialSetupStep

interface InitialSetupRepository {
    suspend fun getSteps(): List<InitialSetupStep>

    /**
     * Gửi thông tin initial setup (reflection intent, focus, focus area) lên backend.
     * @throws Exception khi request thất bại (network, 4xx/5xx).
     */
    suspend fun submitInitialSetup(
        reflectionIntentId: String,
        focusIds: List<String>,
        focusAreaIds: List<String>
    )
}
