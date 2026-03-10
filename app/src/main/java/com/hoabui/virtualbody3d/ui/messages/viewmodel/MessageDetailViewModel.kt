package com.hoabui.virtualbody3d.ui.messages.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.hoabui.virtualbody3d.core.base.UiStateViewModel
import com.hoabui.virtualbody3d.domain.model.MessageThread
import com.hoabui.virtualbody3d.domain.usecase.GetMessageDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.collectLatest

sealed interface MessageDetailEvent

@HiltViewModel
class MessageDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMessageDetailUseCase: GetMessageDetailUseCase
) : UiStateViewModel<MessageThread?, MessageDetailEvent>() {

    init {
        val messageId = savedStateHandle.get<String>("messageId") ?: ""
        launchSafely {
            setLoading()
            getMessageDetailUseCase(messageId).collectLatest { message ->
                setSuccess(message)
            }
        }
    }
}
