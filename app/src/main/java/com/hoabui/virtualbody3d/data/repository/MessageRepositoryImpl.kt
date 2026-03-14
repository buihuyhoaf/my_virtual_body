package com.hoabui.virtualbody3d.data.repository

import com.hoabui.virtualbody3d.data.local.MessageLocalDataSource
import com.hoabui.virtualbody3d.data.mapper.toDomain
import com.hoabui.virtualbody3d.domain.model.MessageThread
import com.hoabui.virtualbody3d.domain.repository.MessageRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepositoryImpl @Inject constructor(
    private val localDataSource: MessageLocalDataSource
) : MessageRepository {

    override fun getMessageThreads(): List<MessageThread> =
        localDataSource.getMessageThreads().map { it.toDomain() }

    override fun getMessageById(messageId: String): MessageThread? =
        localDataSource.getMessageById(messageId)?.toDomain()
}
