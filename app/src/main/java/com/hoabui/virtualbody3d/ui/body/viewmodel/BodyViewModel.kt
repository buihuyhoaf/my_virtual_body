package com.hoabui.virtualbody3d.ui.body.viewmodel

import androidx.lifecycle.ViewModel
import com.hoabui.virtualbody3d.domain.usecase.GetBodyMetricsUseCase
import com.hoabui.virtualbody3d.ui.body.state.BodyScreenState
import com.hoabui.virtualbody3d.ui.body.state.BodyTab
import com.hoabui.virtualbody3d.ui.body.state.toUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class BodyViewModel @Inject constructor(
    getBodyMetricsUseCase: GetBodyMetricsUseCase
) : ViewModel() {

    private val _screenState = MutableStateFlow(
        BodyScreenState(uiState = getBodyMetricsUseCase().toUiState())
    )
    val screenState: StateFlow<BodyScreenState> = _screenState.asStateFlow()

    fun onTabSelected(tab: BodyTab) {
        _screenState.update { it.copy(selectedTab = tab) }
    }
}
