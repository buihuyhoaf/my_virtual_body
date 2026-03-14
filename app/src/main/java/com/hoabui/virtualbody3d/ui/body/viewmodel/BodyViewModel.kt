package com.hoabui.virtualbody3d.ui.body.viewmodel

import com.hoabui.virtualbody3d.core.base.UiStateViewModel
import com.hoabui.virtualbody3d.domain.usecase.GetBodyDataUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetCaloriesTodayUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetPromoBannersUseCase
import com.hoabui.virtualbody3d.ui.body.data.toNutritionSummaryUiState
import com.hoabui.virtualbody3d.ui.body.state.BodyScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class BodyViewModel @Inject constructor(
    private val getBodyDataUseCase: GetBodyDataUseCase,
    private val getCaloriesTodayUseCase: GetCaloriesTodayUseCase,
    private val getPromoBannersUseCase: GetPromoBannersUseCase
) : UiStateViewModel<BodyScreenState, BodyEvent>() {

    init {
        launchSafely {
            combine(
                getBodyDataUseCase(),
                getCaloriesTodayUseCase(),
                getPromoBannersUseCase()
            ) { bodyData, nutritionData, promoBanners ->
                BodyScreenState(
                    scanResult = bodyData,
                    nutritionToday = nutritionData.toNutritionSummaryUiState(),
                    promoBanners = promoBanners
                )
            }.collect { bodyScreenState ->
                setSuccess(bodyScreenState)
            }
        }
    }

    override fun onError(throwable: Throwable) {
        setError(throwable.message ?: "Unknown error")
    }
}
