package com.hoabui.virtualbody3d.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hoabui.virtualbody3d.R
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = token.spacing.md)
            .padding(bottom = token.spacing.xxl)
    ) {
        Spacer(modifier = Modifier.height(token.spacing.lg))

        ProfileHeader(
            displayName = uiState.displayName.ifEmpty { stringResource(R.string.profile_display_name_placeholder) },
            email = uiState.email.ifEmpty { stringResource(R.string.profile_email_placeholder) },
            onEditProfile = onEditProfile,
            token = token
        )

        Spacer(modifier = Modifier.height(token.spacing.xl))

        InBodyScoreCard(
            inBodyScore = uiState.inBodyScore,
            scoreLabel = uiState.scoreLabel,
            lastScanDate = uiState.lastScanDate,
            hasScanData = uiState.hasScanData,
            onCardClick = onNavigateToBodyAnalysis,
            onScanNow = onNavigateToBodyAnalysis,
            token = token
        )

        Spacer(modifier = Modifier.height(token.spacing.xl))

        ProfileSectionTitle(stringResource(R.string.profile_section_account), token)
        Spacer(modifier = Modifier.height(token.spacing.xs))
        SettingsRow(
            icon = Icons.Default.Person,
            title = stringResource(R.string.profile_personal_info),
            onClick = onEditProfile,
            token = token
        )
        SettingsRow(
            icon = Icons.Default.Lock,
            title = stringResource(R.string.profile_change_password),
            onClick = { },
            token = token
        )

        Spacer(modifier = Modifier.height(token.spacing.lg))

        ProfileSectionTitle(stringResource(R.string.profile_section_app_settings), token)
        Spacer(modifier = Modifier.height(token.spacing.xs))
        SettingsRow(
            icon = Icons.Default.Notifications,
            title = stringResource(R.string.profile_notifications),
            onClick = { },
            token = token
        )
        SettingsRow(
            icon = Icons.Default.Security,
            title = stringResource(R.string.profile_privacy),
            onClick = { },
            token = token
        )
        SettingsRow(
            icon = Icons.Default.Translate,
            title = stringResource(R.string.profile_language),
            onClick = { },
            token = token
        )

        Spacer(modifier = Modifier.height(token.spacing.lg))

        ProfileSectionTitle(stringResource(R.string.profile_section_support), token)
        Spacer(modifier = Modifier.height(token.spacing.xs))
        SettingsRow(
            icon = Icons.Default.QuestionMark,
            title = stringResource(R.string.profile_help_center),
            onClick = { },
            token = token
        )
        SettingsRow(
            icon = Icons.Default.Info,
            title = stringResource(R.string.profile_about_app),
            onClick = { },
            token = token
        )

        Spacer(modifier = Modifier.height(token.spacing.xxl))

        LogoutButton(onClick = onLogout, token = token)
    }
}

@Composable
private fun ProfileHeader(
    displayName: String,
    email: String,
    onEditProfile: () -> Unit,
    token: com.hoabui.virtualbody3d.ui.theme.tokens.GymToken
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(token.colors.surfaceSubtle),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = token.colors.textSecondary
            )
        }
        Spacer(modifier = Modifier.height(token.spacing.md))
        Text(
            text = displayName,
            style = token.typography.headlineMedium,
            color = token.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(token.spacing.xxs))
        Text(
            text = email,
            style = token.typography.bodyMedium,
            color = token.colors.textSecondary
        )
        Spacer(modifier = Modifier.height(token.spacing.md))
        TextButton(onClick = onEditProfile) {
            Text(
                text = stringResource(R.string.profile_edit_profile),
                style = token.typography.labelLarge,
                color = token.colors.primary
            )
        }
    }
}

@Composable
private fun InBodyScoreCard(
    inBodyScore: Int?,
    scoreLabel: String,
    lastScanDate: String,
    hasScanData: Boolean,
    onCardClick: () -> Unit,
    onScanNow: () -> Unit,
    token: com.hoabui.virtualbody3d.ui.theme.tokens.GymToken
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        shape = MaterialTheme.shapes.medium,
        color = token.colors.surface,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = token.colors.borderSubtle
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(token.card.padding)
        ) {
            Text(
                text = stringResource(R.string.profile_inbody_score),
                style = token.typography.titleMedium,
                color = token.colors.textSecondary
            )
            if (hasScanData && inBodyScore != null) {
                Spacer(modifier = Modifier.height(token.spacing.xs))
                Text(
                    text = inBodyScore.toString(),
                    style = token.typography.displaySmall,
                    color = token.colors.textPrimary
                )
                if (scoreLabel.isNotEmpty()) {
                    Text(
                        text = scoreLabel,
                        style = token.typography.bodyMedium,
                        color = token.colors.textSecondary
                    )
                }
                val dateText = if (lastScanDate.isEmpty()) stringResource(R.string.profile_last_scan_unknown) else stringResource(R.string.profile_last_scan, lastScanDate)
                Text(
                    text = dateText,
                    style = token.typography.bodySmall,
                    color = token.colors.textMuted
                )
                Spacer(modifier = Modifier.height(token.spacing.xs))
                Text(
                    text = stringResource(R.string.profile_view_body_analysis),
                    style = token.typography.labelLarge,
                    color = token.colors.primary
                )
            } else {
                Spacer(modifier = Modifier.height(token.spacing.md))
                Text(
                    text = stringResource(R.string.profile_no_body_scan_yet),
                    style = token.typography.titleMedium,
                    color = token.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(token.spacing.xxs))
                Text(
                    text = stringResource(R.string.profile_no_scan_subtitle),
                    style = token.typography.bodyMedium,
                    color = token.colors.textSecondary
                )
                Spacer(modifier = Modifier.height(token.spacing.md))
                TextButton(onClick = onScanNow) {
                    Text(
                        text = stringResource(R.string.profile_scan_now),
                        style = token.typography.labelLarge,
                        color = token.colors.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileSectionTitle(
    title: String,
    token: com.hoabui.virtualbody3d.ui.theme.tokens.GymToken
) {
    Text(
        text = title,
        style = token.typography.labelLarge,
        color = token.colors.textSecondary
    )
}

@Composable
private fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
    token: com.hoabui.virtualbody3d.ui.theme.tokens.GymToken
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = token.colors.surface,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = token.spacing.md, vertical = token.spacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(token.spacing.md)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = token.colors.textSecondary
                )
                Text(
                    text = title,
                    style = token.typography.bodyLarge,
                    color = token.colors.textPrimary
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = token.colors.textSecondary
            )
        }
    }
}

@Composable
private fun LogoutButton(
    onClick: () -> Unit,
    token: com.hoabui.virtualbody3d.ui.theme.tokens.GymToken
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        TextButton(onClick = onClick) {
            Text(
                text = stringResource(R.string.profile_logout),
                style = token.typography.labelLarge,
                color = token.colors.error
            )
        }
    }
}
