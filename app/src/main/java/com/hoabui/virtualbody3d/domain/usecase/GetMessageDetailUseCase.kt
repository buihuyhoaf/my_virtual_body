package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.MessageThread
import com.hoabui.virtualbody3d.domain.repository.MessageRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetMessageDetailUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    operator fun invoke(messageId: String): Flow<MessageThread?> = flow {
        emit(messageRepository.getMessageById(messageId))
    }
}
