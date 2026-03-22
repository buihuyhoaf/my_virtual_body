package com.hoabui.virtualbody3d.ui.messages.viewmodel

import com.hoabui.virtualbody3d.core.base.UiStateViewModel
import com.hoabui.virtualbody3d.domain.model.chat.UserInfo
import com.hoabui.virtualbody3d.domain.usecase.GetMessageThreadsUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetUserInfoUseCase
import com.hoabui.virtualbody3d.ui.messages.state.MessagesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

sealed interface MessagesEvent

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val getMessageThreadsUseCase: GetMessageThreadsUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase
) : UiStateViewModel<MessagesUiState, MessagesEvent>() {

    init {
        launchSafely {
            setLoading()
            getMessageThreadsUseCase().collectLatest { threads ->
                val usersById: Map<String, UserInfo> = threads
                    .map { it.senderId }
                    .distinct()
                    .mapNotNull { id ->
                        getUserInfoUseCase(id)?.let { user -> id to user }
                    }
                    .toMap()

                setSuccess(
                    MessagesUiState(
                        threads = threads,
                        usersById = usersById
                    )
                )
            }
        }
    }
}
