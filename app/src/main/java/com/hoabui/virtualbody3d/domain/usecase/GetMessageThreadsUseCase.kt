package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.chat.MessageThread
import com.hoabui.virtualbody3d.domain.repository.MessageRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetMessageThreadsUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    operator fun invoke(): Flow<List<MessageThread>> = flow {
        emit(messageRepository.getMessageThreads())
    }
}

