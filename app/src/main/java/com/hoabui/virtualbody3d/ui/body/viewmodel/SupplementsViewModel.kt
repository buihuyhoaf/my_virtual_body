package com.hoabui.virtualbody3d.ui.body.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hoabui.virtualbody3d.domain.usecase.GetSupplementsUseCase
import com.hoabui.virtualbody3d.ui.body.data.SupplementUiItem
import com.hoabui.virtualbody3d.ui.body.data.toSupplementUiItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SupplementsViewModel @Inject constructor(
    private val getSupplementsUseCase: GetSupplementsUseCase
) : ViewModel() {

    private val _supplements = MutableStateFlow<List<SupplementUiItem>>(emptyList())
    val supplements: StateFlow<List<SupplementUiItem>> = _supplements.asStateFlow()

    init {
        getSupplementsUseCase()
            .onEach { list -> _supplements.value = list.map { it.toSupplementUiItem() } }
            .catch { _ -> _supplements.value = emptyList() }
            .launchIn(viewModelScope)
    }

}
