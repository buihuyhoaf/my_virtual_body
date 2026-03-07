package com.hoabui.virtualbody3d.core.utils

object Constants {
    const val PREFS_NAME = "virtual_body_prefs"
    const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"

    /** Base URL for API backend. Change when deploying to production. */
    const val API_BASE_URL = "https://api.example.com/"

    const val BODY_DEV_LOG_TAG = "BodyMorphDebug"
    const val BODY_DEV_MODE = true
    const val FILAMENT_MAX_BONES = 256
    const val BODY_MODEL_ASSET_PATH = "models/fbi__cs2_agent_model_no1.glb"
    const val BELLY_FAT_MORPH_INDEX = 0
    const val BELLY_FAT_MORPH_NAME = "bell_fat"
    const val TOTAL_INITIAL_SETUP_STEPS = 4

    // Body Analysis / Home screen constants
    const val BODY_ANALYSIS_MAX_MEALS_DISPLAYED: Int = 3
    const val BODY_ANALYSIS_PROGRESS_RING_START_ANGLE: Float = -90f
    const val BODY_ANALYSIS_PROGRESS_RING_SWEEP_ANGLE: Float = 360f
    const val BODY_ANALYSIS_MEAL_NAME_MAX_LINES: Int = 2
}
