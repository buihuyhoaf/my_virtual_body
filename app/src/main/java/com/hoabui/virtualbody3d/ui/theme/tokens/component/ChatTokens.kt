package com.hoabui.virtualbody3d.ui.theme.tokens.component

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import com.hoabui.virtualbody3d.ui.theme.tokens.primitive.PrimitiveSpacingTokens

/**
 * Component tokens for message detail / chat UI: avatars, input bar, bubbles.
 * Sizes derived from [PrimitiveSpacingTokens] for consistency.
 */
@Immutable
data class ChatTokens(
    val avatarSizeHeader: Dp,
    val avatarSizeBubble: Dp,
    val sendButtonSize: Dp,
    val inputMinHeight: Dp,
    val inputMaxHeight: Dp,
    val bubbleMaxWidth: Dp
)

fun gymChatTokens(spacing: PrimitiveSpacingTokens): ChatTokens = ChatTokens(
    avatarSizeHeader = spacing.xl,
    avatarSizeBubble = spacing.iconMedium,
    sendButtonSize = spacing.xl + spacing.xxs,
    inputMinHeight = spacing.xxl,
    inputMaxHeight = spacing.xxl * 2 + spacing.lg,
    bubbleMaxWidth = spacing.xxxl * 5
)
