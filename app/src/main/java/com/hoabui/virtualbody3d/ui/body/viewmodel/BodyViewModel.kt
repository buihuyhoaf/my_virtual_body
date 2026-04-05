package com.hoabui.virtualbody3d.ui.body.viewmodel

import com.hoabui.virtualbody3d.core.base.UiStateViewModel
import com.hoabui.virtualbody3d.domain.usecase.GetBodyDataUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetCaloriesTodayUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetProgressTimelineUseCase
import com.hoabui.virtualbody3d.ui.body.data.toCalorieGoalUiModel
import com.hoabui.virtualbody3d.ui.body.data.toUiModels
import com.hoabui.virtualbody3d.ui.body.state.BodyScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class BodyViewModel @Inject constructor(
    private val getBodyDataUseCase: GetBodyDataUseCase,
    private val getCaloriesTodayUseCase: GetCaloriesTodayUseCase,
    private val getProgressTimelineUseCase: GetProgressTimelineUseCase,
) : UiStateViewModel<BodyScreenState, BodyEvent>() {

    /**
     * `null` = chưa chọn tay → mặc định hiển thị snapshot mới nhất (index cuối).
     */
    private val progressTimelineSelection = MutableStateFlow<Int?>(null)

    init {
        launchSafely {
            combine(
                getBodyDataUseCase(),
                getCaloriesTodayUseCase(),
                getProgressTimelineUseCase(),
                progressTimelineSelection,
            ) { bodyData, nutritionData, snapshots, selection ->
                val uiSnapshots = snapshots.toUiModels()
                val last = uiSnapshots.lastIndex.coerceAtLeast(0)
                val index = (selection ?: last).coerceIn(0, last)
                BodyScreenState(
                    scanResult = bodyData,
                    nutritionToday = nutritionData.toCalorieGoalUiModel(),
                    progressSnapshots = uiSnapshots,
                    selectedProgressIndex = index,
                )
            }.collect { bodyScreenState ->
                setSuccess(bodyScreenState)
            }
        }
    }

    fun onProgressTimelineIndexSelected(index: Int) {
        progressTimelineSelection.value = index
    }

    override fun onError(throwable: Throwable) {
        setError(throwable.message ?: "Unknown error")
    }
}
