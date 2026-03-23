package com.hoabui.virtualbody3d.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Translate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.organism.scaffold.GScaffold
import com.hoabui.virtualbody3d.ui.profile.components.InBodyScoreCard
import com.hoabui.virtualbody3d.ui.profile.components.LogoutButton
import com.hoabui.virtualbody3d.ui.profile.components.ProfileHeader
import com.hoabui.virtualbody3d.ui.profile.components.ProfileSectionTitle
import com.hoabui.virtualbody3d.ui.profile.components.SettingsRow
import com.hoabui.virtualbody3d.ui.profile.viewmodel.ProfileViewModel
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun ProfileScreen(
    onNavigateToBodyAnalysis: () -> Unit,
    onLogout: () -> Unit,
    onEditProfile: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val token = GymTheme.token
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    GScaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = token.spacing.md)
                .padding(bottom = token.spacing.xxl),
        ) {
            Spacer(modifier = Modifier.height(token.spacing.lg))

            ProfileHeader(
                displayName = uiState.displayName.ifEmpty { stringResource(R.string.profile_display_name_placeholder) },
                email = uiState.email.ifEmpty { stringResource(R.string.profile_email_placeholder) },
                onEditProfile = onEditProfile,
            )

            Spacer(modifier = Modifier.height(token.spacing.xl))

            InBodyScoreCard(
                inBodyScore = uiState.inBodyScore,
                scoreLabel = uiState.scoreLabel,
                lastScanDate = uiState.lastScanDate,
                hasScanData = uiState.hasScanData,
                onCardClick = onNavigateToBodyAnalysis,
                onScanNow = onNavigateToBodyAnalysis,
            )

            Spacer(modifier = Modifier.height(token.spacing.xl))

            ProfileSectionTitle(stringResource(R.string.profile_section_account))
            Spacer(modifier = Modifier.height(token.spacing.xs))
            SettingsRow(
                icon = Icons.Default.Person,
                title = stringResource(R.string.profile_personal_info),
                onClick = onEditProfile,
            )
            SettingsRow(
                icon = Icons.Default.Lock,
                title = stringResource(R.string.profile_change_password),
                onClick = {},
            )

            Spacer(modifier = Modifier.height(token.spacing.lg))

            ProfileSectionTitle(stringResource(R.string.profile_section_app_settings))
            Spacer(modifier = Modifier.height(token.spacing.xs))
            SettingsRow(
                icon = Icons.Default.Notifications,
                title = stringResource(R.string.profile_notifications),
                onClick = {},
            )
            SettingsRow(
                icon = Icons.Default.Security,
                title = stringResource(R.string.profile_privacy),
                onClick = {},
            )
            SettingsRow(
                icon = Icons.Default.Translate,
                title = stringResource(R.string.profile_language),
                onClick = {},
            )

            Spacer(modifier = Modifier.height(token.spacing.lg))

            ProfileSectionTitle(stringResource(R.string.profile_section_support))
            Spacer(modifier = Modifier.height(token.spacing.xs))
            SettingsRow(
                icon = Icons.Default.QuestionMark,
                title = stringResource(R.string.profile_help_center),
                onClick = {},
            )
            SettingsRow(
                icon = Icons.Default.Info,
                title = stringResource(R.string.profile_about_app),
                onClick = {},
            )

            Spacer(modifier = Modifier.height(token.spacing.xxl))

            LogoutButton(onClick = onLogout)
        }
    }
}

