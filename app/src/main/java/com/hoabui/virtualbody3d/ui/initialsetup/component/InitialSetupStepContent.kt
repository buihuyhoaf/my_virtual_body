package com.hoabui.virtualbody3d.ui.initialsetup.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DirectionsRun
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.AccessibilityNew
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.InitialSetupOption
import com.hoabui.virtualbody3d.domain.model.InitialSetupStep
import com.hoabui.virtualbody3d.ui.theme.tokens.RadiusTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.SpacingTokens
import com.hoabui.virtualbody3d.ui.theme.tokens.semantic.SemanticColorTokens
import kotlinx.coroutines.delay

private const val QUESTION_ANIM_DURATION_MS = 350
private const val OPTIONS_DELAY_MS = 400
private const val OPTIONS_ANIM_DURATION_MS = 400

@Composable
fun InitialSetupStep1Content(
    step: InitialSetupStep?,
    colors: SemanticColorTokens,
    spacing: SpacingTokens,
    typography: Typography,
    radius: RadiusTokens,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (step == null) return
    var optionsVisible by remember(step) { mutableStateOf(false) }
    LaunchedEffect(step) {
        optionsVisible = false
        delay(OPTIONS_DELAY_MS.toLong())
        optionsVisible = true
    }
    Column(modifier = modifier) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(QUESTION_ANIM_DURATION_MS)) +
                slideInVertically(animationSpec = tween(QUESTION_ANIM_DURATION_MS)) { it / 4 },
            exit = fadeOut(animationSpec = tween(QUESTION_ANIM_DURATION_MS)) +
                slideOutVertically(animationSpec = tween(QUESTION_ANIM_DURATION_MS)) { -it / 4 }
        ) {
            Text(
                text = step.question,
                style = typography.headlineLarge,
                color = colors.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = spacing.xl)
            )
        }
        AnimatedVisibility(
            visible = optionsVisible,
            enter = fadeIn(animationSpec = tween(OPTIONS_ANIM_DURATION_MS)) +
                slideInVertically(
                    animationSpec = tween(OPTIONS_ANIM_DURATION_MS),
                    initialOffsetY = { it / 4 }
                ),
            exit = fadeOut(animationSpec = tween(OPTIONS_ANIM_DURATION_MS))
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                step.options.forEachIndexed { index, option ->
                    val isSelected = selectedIndex == index
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(radius.xl))
                            .background(
                                if (isSelected) colors.initialSetupOptionSelectedBg
                                else colors.surface
                            )
                            .border(
                                width = if (isSelected) 0.dp else 1.dp,
                                color = colors.borderSubtle,
                                shape = RoundedCornerShape(radius.xl)
                            )
                            .clickable { onOptionSelected(index) }
                            .padding(spacing.lg),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = option.label,
                            style = typography.titleMedium,
                            color = if (isSelected) colors.primary else colors.textPrimary
                        )
                        Icon(
                            imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            tint = if (isSelected) colors.primary else colors.initialSetupOptionUnselectedIcon,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InitialSetupStep2Content(
    step: InitialSetupStep?,
    colors: SemanticColorTokens,
    spacing: SpacingTokens,
    typography: Typography,
    radius: RadiusTokens,
    selectedIndices: Set<Int>,
    onToggleOption: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (step == null) return
    var optionsVisible by remember(step) { mutableStateOf(false) }
    LaunchedEffect(step) {
        optionsVisible = false
        kotlinx.coroutines.delay(OPTIONS_DELAY_MS.toLong())
        optionsVisible = true
    }
    Column(modifier = modifier) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(QUESTION_ANIM_DURATION_MS)) +
                slideInVertically(animationSpec = tween(QUESTION_ANIM_DURATION_MS)) { it / 4 },
            exit = fadeOut(animationSpec = tween(QUESTION_ANIM_DURATION_MS)) +
                slideOutVertically(animationSpec = tween(QUESTION_ANIM_DURATION_MS)) { -it / 4 }
        ) {
            Text(
                text = step.question,
                style = typography.headlineLarge,
                color = colors.textPrimary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = spacing.xl)
            )
        }
        AnimatedVisibility(
            visible = optionsVisible,
            enter = fadeIn(animationSpec = tween(OPTIONS_ANIM_DURATION_MS)) +
                slideInVertically(
                    animationSpec = tween(OPTIONS_ANIM_DURATION_MS),
                    initialOffsetY = { it / 4 }
                ),
            exit = fadeOut(animationSpec = tween(OPTIONS_ANIM_DURATION_MS))
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
                verticalArrangement = Arrangement.spacedBy(spacing.md),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(step.options) { index, option ->
                    val isSelected = selectedIndices.contains(index)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(radius.xl))
                            .background(
                                if (isSelected) colors.primary else colors.surface
                            )
                            .border(
                                width = if (isSelected) 0.dp else 1.dp,
                                color = colors.initialSetupOptionBorder,
                                shape = RoundedCornerShape(radius.xl)
                            )
                            .clickable { onToggleOption(index) }
                            .padding(vertical = spacing.xl, horizontal = spacing.md),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = option.label,
                            style = typography.titleMedium,
                            color = if (isSelected) colors.onPrimary else colors.textPrimary
                        )
                    }
                }
            }
        }
    }
}

private fun iconNameToImageVector(iconName: String?): ImageVector? = when (iconName) {
    "accessibility_new" -> Icons.Outlined.AccessibilityNew
    "fitness_center" -> Icons.Outlined.FitnessCenter
    "straighten" -> Icons.Outlined.Straighten
    "directions_run" -> Icons.AutoMirrored.Outlined.DirectionsRun
    "self_improvement" -> Icons.Outlined.SelfImprovement
    else -> null
}

@Composable
fun InitialSetupStep3Content(
    step: InitialSetupStep?,
    colors: SemanticColorTokens,
    spacing: SpacingTokens,
    typography: Typography,
    radius: RadiusTokens,
    selectedIndices: Set<Int>,
    onToggleOption: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (step == null) return
    val options = step.options
    val gridOptions = options.take(4)
    val fullBodyOption = options.getOrNull(4)
    val fullBodyIndex = 4
    var optionsVisible by remember(step) { mutableStateOf(false) }
    LaunchedEffect(step) {
        optionsVisible = false
        kotlinx.coroutines.delay(OPTIONS_DELAY_MS.toLong())
        optionsVisible = true
    }

    Column(modifier = modifier) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(QUESTION_ANIM_DURATION_MS)) +
                slideInVertically(animationSpec = tween(QUESTION_ANIM_DURATION_MS)) { it / 4 },
            exit = fadeOut(animationSpec = tween(QUESTION_ANIM_DURATION_MS)) +
                slideOutVertically(animationSpec = tween(QUESTION_ANIM_DURATION_MS)) { -it / 4 }
        ) {
            Text(
                text = step.question,
                style = typography.headlineLarge,
                color = colors.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = spacing.lg)
            )
        }
        AnimatedVisibility(
            visible = optionsVisible,
            enter = fadeIn(animationSpec = tween(OPTIONS_ANIM_DURATION_MS)) +
                slideInVertically(
                    animationSpec = tween(OPTIONS_ANIM_DURATION_MS),
                    initialOffsetY = { it / 4 }
                ),
            exit = fadeOut(animationSpec = tween(OPTIONS_ANIM_DURATION_MS))
        ) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing.md)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
                verticalArrangement = Arrangement.spacedBy(spacing.md),
                userScrollEnabled = false,
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(gridOptions) { index, option ->
                    InitialSetupStep3Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        option = option,
                        index = index,
                        isSelected = selectedIndices.contains(index),
                        colors = colors,
                        spacing = spacing,
                        typography = typography,
                        radius = radius,
                        onToggleOption = onToggleOption
                    )
                }
            }
            fullBodyOption?.let { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(radius.lg))
                        .background(
                            if (selectedIndices.contains(fullBodyIndex)) colors.primary else colors.surface
                        )
                        .border(
                            width = if (selectedIndices.contains(fullBodyIndex)) 0.dp else 1.dp,
                            color = colors.initialSetupOptionBorder,
                            shape = RoundedCornerShape(radius.lg)
                        )
                        .clickable { onToggleOption(fullBodyIndex) }
                        .padding(vertical = spacing.md, horizontal = spacing.lg),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    iconNameToImageVector(option.iconName)?.let { icon ->
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (selectedIndices.contains(fullBodyIndex)) colors.onPrimary else colors.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.size(spacing.xs))
                    Text(
                        text = option.label,
                        style = typography.bodyMedium,
                        color = if (selectedIndices.contains(fullBodyIndex)) colors.onPrimary else colors.textPrimary
                    )
                }
            }
        }
        }
    }
}

@Composable
private fun InitialSetupStep3Card(
    modifier: Modifier,
    option: InitialSetupOption,
    index: Int,
    isSelected: Boolean,
    colors: SemanticColorTokens,
    spacing: SpacingTokens,
    typography: Typography,
    radius: RadiusTokens,
    onToggleOption: (Int) -> Unit
) {
    val icon = iconNameToImageVector(option.iconName)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(radius.lg))
            .background(if (isSelected) colors.primary else colors.surface)
            .border(
                width = if (isSelected) 0.dp else 1.dp,
                color = colors.initialSetupOptionBorder,
                shape = RoundedCornerShape(radius.lg)
            )
            .clickable { onToggleOption(index) }
            .padding(spacing.md)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = if (isSelected) colors.onPrimary else colors.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(spacing.xxs))
            Text(
                text = option.label,
                style = typography.bodyMedium,
                color = if (isSelected) colors.onPrimary else colors.textPrimary
            )
        }
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = colors.onPrimary,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(16.dp)
            )
        }
    }
}

@Composable
fun InitialSetupStep4Content(
    step: InitialSetupStep?,
    colors: SemanticColorTokens,
    spacing: SpacingTokens,
    typography: Typography,
    radius: RadiusTokens,
    modifier: Modifier = Modifier
) {
    if (step == null) return
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = step.question,
            style = typography.headlineLarge,
            color = colors.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = spacing.xl)
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(spacing.lg))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(radius.xl))
                    .background(colors.initialSetupStep4IconBg)
                    .border(1.dp, colors.initialSetupStep4IconBorder, RoundedCornerShape(radius.xl))
            ) {
                Image(
                    painter = painterResource(R.drawable.body_unsplash),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(spacing.lg))
            step.subtitle?.let { subtitle ->
                Text(
                    text = subtitle,
                    style = typography.bodyLarge,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = spacing.lg)
                )
                Spacer(modifier = Modifier.height(spacing.xl))
            }
        }
    }
}
