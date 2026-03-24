package com.hoabui.virtualbody3d.ui.common_ui.atom.text

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.image.GRoundedImage
import com.hoabui.virtualbody3d.ui.theme.GymTheme

/**
 * Atomic text primitive. Material3 [Text] merges [style] with composition-local text style; null [style] uses [GymTheme] body.
 */
@Composable
fun GText(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle? = null,
    color: Color? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    textAlign: TextAlign? = null,
    fontWeight: FontWeight? = null,
) {
    val token = GymTheme.token
    val resolvedStyle = (style ?: token.typography.bodyMedium)
        .let { if (fontWeight != null) it.copy(fontWeight = fontWeight) else it }
    Text(
        text = text,
        modifier = modifier,
        style = resolvedStyle,
        color = color ?: token.colors.textPrimary,
        maxLines = maxLines,
        overflow = overflow,
        softWrap = softWrap,
        textAlign = textAlign
    )
}

/**
 * Unified row label: optional [leadingContent] / [trailingContent] slots; center column is [title] and optional [description] built from [GText].
 * Screen readers merge icon + text by default ([mergeDescendants]); set [mergeDescendants] to false if [trailingContent] must stay separately focusable.
 */
@Composable
fun GLabel(
    modifier: Modifier = Modifier,
    title: String,
    description: String? = null,
    leadingContent: (@Composable RowScope.() -> Unit)? = null,
    trailingContent: (@Composable RowScope.() -> Unit)? = null,
    titleStyle: TextStyle? = null,
    titleColor: Color? = null,
    descriptionStyle: TextStyle? = null,
    descriptionColor: Color? = null,
    titleMaxLines: Int = Int.MAX_VALUE,
    descriptionMaxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis,
    horizontalArrangement: Arrangement.Horizontal? = null,
    mergeDescendants: Boolean = true,
) {
    val token = GymTheme.token
    val arrangement = horizontalArrangement ?: Arrangement.spacedBy(token.spacing.xs)
    val hasDescription = !description.isNullOrEmpty()
    val resolvedTitleStyle = titleStyle
        ?: if (hasDescription) token.typography.titleSmall else token.typography.bodyMedium
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (mergeDescendants) {
                    Modifier.semantics(mergeDescendants = true) {}
                } else {
                    Modifier
                }
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = arrangement,
    ) {
        leadingContent?.invoke(this)
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .fillMaxWidth()
        ) {
            GText(
                text = title,
                style = resolvedTitleStyle,
                color = titleColor ?: token.colors.textPrimary,
                maxLines = titleMaxLines,
                overflow = overflow,
                textAlign = TextAlign.Start,
            )
            if (hasDescription) {
                GText(
                    text = description!!,
                    style = descriptionStyle ?: token.typography.labelSmall,
                    color = descriptionColor ?: token.colors.textSecondary,
                    maxLines = descriptionMaxLines,
                    overflow = overflow,
                    textAlign = TextAlign.Start,
                )
            }
        }
        trailingContent?.invoke(this)
    }
}

/**
 * Title + optional description in a column (delegates to [GLabel] without side slots).
 */
@Composable
fun GTextWithDescription(
    modifier: Modifier = Modifier,
    title: String,
    description: String? = null,
    titleStyle: TextStyle? = null,
    titleColor: Color? = null,
    descriptionStyle: TextStyle? = null,
    descriptionColor: Color? = null,
    titleMaxLines: Int = Int.MAX_VALUE,
    descriptionMaxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Ellipsis,
) {
    GLabel(
        modifier = modifier,
        title = title,
        description = description,
        leadingContent = null,
        trailingContent = null,
        titleStyle = titleStyle,
        titleColor = titleColor,
        descriptionStyle = descriptionStyle,
        descriptionColor = descriptionColor,
        titleMaxLines = titleMaxLines,
        descriptionMaxLines = descriptionMaxLines,
        overflow = overflow,
    )
}

/**
 * Section row with trailing “see more” action (delegates to [GLabel]).
 */
@Composable
fun GTextWithSeeMore(
    modifier: Modifier = Modifier,
    title: String,
    description: String? = null,
    onSeeMoreClick: () -> Unit,
    seeMoreText: String,
    titleStyle: TextStyle? = null,
    titleColor: Color? = null,
    descriptionStyle: TextStyle? = null,
    descriptionColor: Color? = null,
    seeMoreStyle: TextStyle? = null,
    seeMoreColor: Color? = null,
    mergeDescendants: Boolean = false,
) {
    val token = GymTheme.token
    GLabel(
        modifier = modifier,
        title = title,
        description = description,
        leadingContent = null,
        trailingContent = {
            TextButton(onClick = onSeeMoreClick) {
                Text(
                    text = seeMoreText,
                    style = seeMoreStyle ?: token.typography.labelLarge,
                    color = seeMoreColor ?: token.colors.primary
                )
            }
        },
        titleStyle = titleStyle ?: token.typography.titleMedium,
        titleColor = titleColor,
        descriptionStyle = descriptionStyle,
        descriptionColor = descriptionColor,
        titleMaxLines = 1,
        overflow = TextOverflow.Ellipsis,
        horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
        mergeDescendants = mergeDescendants,
    )
}

@Preview(showBackground = true, name = "GText")
@Composable
private fun PreviewGText() {
    GymTheme {
        GText(
            modifier = Modifier.padding(16.dp),
            text = "Body medium on textPrimary"
        )
    }
}

@Preview(showBackground = true, name = "GLabel — title only")
@Composable
private fun PreviewGLabelSingleLine() {
    GymTheme {
        GLabel(
            modifier = Modifier.padding(16.dp),
            title = "Single line label",
        )
    }
}

@Preview(showBackground = true, name = "GLabel — title + description")
@Composable
private fun PreviewGLabelTitleDescription() {
    GymTheme {
        GLabel(
            modifier = Modifier.padding(16.dp),
            title = "Workout name",
            description = "Tap to open details and tracking.",
        )
    }
}

@Preview(showBackground = true, name = "GLabel — leading icon")
@Composable
private fun PreviewGLabelLeadingIcon() {
    GymTheme {
        GLabel(
            modifier = Modifier.padding(16.dp),
            title = "Label with icon",
            leadingContent = {
                GRoundedImage(
                    contentDescription = "Icon",
                    modifier = Modifier.size(28.dp),
                    model = R.drawable.ic_apple,
                    borderWidth = 0.dp,
                )
            },
        )
    }
}

@Preview(showBackground = true, name = "GLabel — see more")
@Composable
private fun PreviewGLabelSeeMore() {
    GymTheme {
        GTextWithSeeMore(
            modifier = Modifier.padding(16.dp),
            title = "Bài tập sắp tới",
            seeMoreText = "Xem thêm",
            onSeeMoreClick = {},
        )
    }
}

@Preview(showBackground = true, name = "GLabel — showcase")
@Composable
private fun PreviewGLabelShowcase() {
    GymTheme {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            GLabel(title = "Title only")
            GLabel(title = "Title", description = "Description under title")
            GLabel(
                title = "With icon",
                leadingContent = {
                    GRoundedImage(
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        model = R.drawable.ic_apple,
                        borderWidth = 0.dp,
                    )
                },
            )
            GTextWithSeeMore(
                title = "Section",
                description = "Optional",
                seeMoreText = "See more",
                onSeeMoreClick = {},
            )
        }
    }
}

@Preview(
    showBackground = true,
    name = "GLabel — dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun PreviewGLabelDark() {
    GymTheme(darkTheme = true) {
        GLabel(
            modifier = Modifier.padding(16.dp),
            title = "Dark mode title",
            description = "Secondary line",
        )
    }
}
