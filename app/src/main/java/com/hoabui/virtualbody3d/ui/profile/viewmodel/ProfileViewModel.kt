package com.hoabui.virtualbody3d.ui.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hoabui.virtualbody3d.domain.usecase.GetBodyDataUseCase
import com.hoabui.virtualbody3d.ui.body.state.toUiState
import com.hoabui.virtualbody3d.ui.profile.state.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getBodyDataUseCase: GetBodyDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getBodyDataUseCase()
                .catch { }
                .collect { scanResult ->
                    val bodyUiState = scanResult.toUiState()
                    val score = bodyUiState.bmiScalePosition
                        ?.let { (it * 100f).toInt().coerceIn(0, 100) }
                        ?: 0
                    val label = when {
                        score >= 80 -> "Excellent"
                        score >= 60 -> "Good"
                        score >= 40 -> "Fair"
                        else -> "Needs attention"
                    }
                    _uiState.value = _uiState.value.copy(
                        inBodyScore = score,
                        scoreLabel = label,
                        lastScanDate = "",
                        hasScanData = true
                    )
                }
        }
    }

    fun updateUserPlaceholder(displayName: String, email: String) {
        _uiState.value = _uiState.value.copy(
            displayName = displayName,
            email = email
        )
    }
}
