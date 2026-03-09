package com.hoabui.virtualbody3d.ui.messages.viewmodel

import com.hoabui.virtualbody3d.core.base.BaseViewModel
import com.hoabui.virtualbody3d.domain.usecase.GetMessageThreadsUseCase
import com.hoabui.virtualbody3d.ui.messages.state.MessagesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest

sealed interface MessagesEvent

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val getMessageThreadsUseCase: GetMessageThreadsUseCase
) : BaseViewModel<MessagesUiState, MessagesEvent>() {

    override fun initialState(): MessagesUiState = MessagesUiState(isLoading = true)

    init {
        launchSafely {
            getMessageThreadsUseCase().collectLatest { threads ->
                updateState {
                    copy(
                        isLoading = false,
                        threads = threads
                    )
                }
            }
        }
    }
}

