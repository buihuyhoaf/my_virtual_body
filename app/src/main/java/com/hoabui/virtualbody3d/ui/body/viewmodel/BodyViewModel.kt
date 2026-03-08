package com.hoabui.virtualbody3d.ui.body.viewmodel

import com.hoabui.virtualbody3d.core.base.BaseViewModel
import com.hoabui.virtualbody3d.domain.usecase.GetBodyDataUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetCaloriesTodayUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetPromoBannersUseCase
import com.hoabui.virtualbody3d.ui.body.state.BodyRegion
import com.hoabui.virtualbody3d.ui.body.state.BodyScreenState
import com.hoabui.virtualbody3d.ui.body.state.toCaloriesTodayUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.combine

@HiltViewModel
class BodyViewModel @Inject constructor(
    private val getBodyDataUseCase: GetBodyDataUseCase,
    private val getCaloriesTodayUseCase: GetCaloriesTodayUseCase,
    private val getPromoBannersUseCase: GetPromoBannersUseCase
) : BaseViewModel<BodyScreenState, BodyEvent>(BodyScreenState()) {

    init {
        launchSafely {
            combine(
                getBodyDataUseCase(),
                getCaloriesTodayUseCase(),
                getPromoBannersUseCase()
            ) { bodyData, caloriesTodayData, promoBanners ->
                BodyScreenState(
                    scanResult = bodyData,
                    caloriesToday = caloriesTodayData.toCaloriesTodayUiState(),
                    selectedRegion = null,
                    promoBanners = promoBanners
                )
            }.collect { emitted ->
                updateState { emitted.copy(selectedRegion = this.selectedRegion) }
            }
        }
    }

    fun onRegionSelected(region: BodyRegion) {
        updateState { copy(selectedRegion = region) }
    }
}
