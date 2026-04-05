package com.hoabui.virtualbody3d.ui.body.state

import com.hoabui.virtualbody3d.domain.model.body.BodyScanResult
import com.hoabui.virtualbody3d.ui.body.data.CalorieGoalUiModel
import com.hoabui.virtualbody3d.ui.body.data.ProgressSnapshotUiModel

data class BodyScreenState(
    val scanResult: BodyScanResult,
    val nutritionToday: CalorieGoalUiModel,
    val progressSnapshots: List<ProgressSnapshotUiModel> = emptyList(),
    /** Index đang chọn trên [ProgressTimelineRow]; đồng bộ với ViewModel. */
    val selectedProgressIndex: Int = 0,
)
