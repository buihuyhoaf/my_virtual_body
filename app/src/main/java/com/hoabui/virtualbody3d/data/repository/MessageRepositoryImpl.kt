package com.hoabui.virtualbody3d.data.repository

import com.hoabui.virtualbody3d.domain.model.MessageThread
import com.hoabui.virtualbody3d.domain.repository.MessageRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepositoryImpl @Inject constructor() : MessageRepository {

    override fun getMessageThreads(): List<MessageThread> = listOf(
        MessageThread(
            id = "welcome",
            senderName = "Coach Alex",
            lastMessage = "Here’s a quick overview of how to get the most from your body insights.",
            content = "Here’s a quick overview of how to get the most from your body insights.\n\n" +
                "• Capture your baseline to create a starting point.\n" +
                "• Explore your 3D body model to understand posture and proportions.\n" +
                "• Visit the Calendar tab to see how your activities and meals add up over time.\n\n" +
                "We’re excited to support your journey.",
            timestamp = "Today • 09:24",
            isRead = false
        ),
        MessageThread(
            id = "coaching_tip",
            senderName = "Workout Bot",
            lastMessage = "Small, consistent changes will compound much faster than big, irregular efforts.",
            content = "Small, consistent changes will compound much faster than big, irregular efforts.\n\n" +
                "Pick one habit you can repeat daily with low friction and stick with it for the next two weeks.\n" +
                "You can always adjust the plan later, but consistency is what unlocks insight.",
            timestamp = "Yesterday • 20:10",
            isRead = true
        ),
        MessageThread(
            id = "scan_reminder",
            senderName = "System",
            lastMessage = "It’s been a while since your last 3D scan. A new scan helps you see subtle changes.",
            content = "It’s been a while since your last 3D scan.\n\n" +
                "A new scan helps you:\n" +
                "• Visualize posture changes\n" +
                "• Notice muscle balance shifts\n" +
                "• Keep your progress grounded in reality, not just feeling.\n\n" +
                "When you’re ready, open the Body Analysis tab to begin.",
            timestamp = "Mon • 14:05",
            isRead = false
        )
    )
}

