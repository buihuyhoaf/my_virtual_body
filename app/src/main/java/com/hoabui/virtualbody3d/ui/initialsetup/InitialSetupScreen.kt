package com.hoabui.virtualbody3d.ui.initialsetup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hoabui.virtualbody3d.ui.initialsetup.component.InitialSetupFooter
import com.hoabui.virtualbody3d.ui.initialsetup.component.InitialSetupHeader
import com.hoabui.virtualbody3d.ui.initialsetup.component.InitialSetupStep1Content
import com.hoabui.virtualbody3d.ui.initialsetup.component.InitialSetupStep2Content
import com.hoabui.virtualbody3d.ui.initialsetup.component.InitialSetupStep3Content
import com.hoabui.virtualbody3d.ui.initialsetup.component.InitialSetupStep4Content
import com.hoabui.virtualbody3d.ui.initialsetup.viewmodel.InitialSetupEvent
import com.hoabui.virtualbody3d.ui.initialsetup.viewmodel.InitialSetupViewModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun InitialSetupScreen(
    modifier: Modifier = Modifier,
    viewModel: InitialSetupViewModel = hiltViewModel(),
    onComplete: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val token = GymTheme.token
    val colors = token.colors
    val spacing = token.spacing
    val typography = token.typography
    val radius = token.radius
    val onboardingTokens = token.onboarding
    val elevation = token.elevation

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is InitialSetupEvent.Complete -> onComplete()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.surface)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(horizontal = spacing.xl)
    ) {
        if (state.totalSteps > 0) {
            InitialSetupHeader(
                currentStep = state.currentStep,
                totalSteps = state.totalSteps,
                colors = colors,
                spacing = spacing,
                typography = typography,
                onboardingTokens = onboardingTokens
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = colors.primary)
                    }
                }
                else -> when (state.currentStep) {
                    0 -> InitialSetupStep1Content(
                        modifier = Modifier.fillMaxSize(),
                        step = state.currentStepData,
                        colors = colors,
                        spacing = spacing,
                        typography = typography,
                        radius = radius,
                        selectedIndex = state.selectedStep0Index,
                        onOptionSelected = viewModel::onStep0OptionSelected
                    )
                    1 -> InitialSetupStep2Content(
                        modifier = Modifier.fillMaxSize(),
                        step = state.currentStepData,
                        colors = colors,
                        spacing = spacing,
                        typography = typography,
                        radius = radius,
                        selectedIndices = state.selectedStep1Indices,
                        onToggleOption = viewModel::onStep1ToggleOption
                    )
                    2 -> InitialSetupStep3Content(
                        modifier = Modifier.fillMaxSize(),
                        step = state.currentStepData,
                        colors = colors,
                        spacing = spacing,
                        typography = typography,
                        radius = radius,
                        selectedIndices = state.selectedStep2Indices,
                        onToggleOption = viewModel::onStep2ToggleOption
                    )
                    3 -> InitialSetupStep4Content(
                        modifier = Modifier.fillMaxSize(),
                        step = state.currentStepData,
                        colors = colors,
                        spacing = spacing,
                        typography = typography,
                        radius = radius
                    )
                    else -> {}
                }
            }
        }

        if (state.totalSteps > 0) {
            InitialSetupFooter(
                currentStep = state.currentStep,
                totalSteps = state.totalSteps,
                colors = colors,
                spacing = spacing,
                typography = typography,
                radius = radius,
                onboardingTokens = onboardingTokens,
                elevation = elevation,
                isNextEnabled = state.isNextEnabled,
                onSkip = viewModel::onSkip,
                onPrimaryClick = viewModel::onPrimaryClick
            )
        }
    }
}
