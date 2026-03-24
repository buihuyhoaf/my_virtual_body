package com.hoabui.virtualbody3d.ui.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButton
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButtonVariant
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
internal fun ProfileHeader(
    displayName: String,
    email: String,
    onEditProfile: () -> Unit,
) {
    val token = GymTheme.token
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(token.colors.surfaceSubtle),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = token.colors.textSecondary,
            )
        }
        Spacer(modifier = Modifier.height(token.spacing.md))
        Text(
            text = displayName,
            style = token.typography.headlineMedium,
            color = token.colors.textPrimary,
        )
        Spacer(modifier = Modifier.height(token.spacing.xxs))
        Text(
            text = email,
            style = token.typography.bodyMedium,
            color = token.colors.textSecondary,
        )
        Spacer(modifier = Modifier.height(token.spacing.md))
        GButton(
            text = stringResource(R.string.profile_edit_profile),
            onClick = onEditProfile,
            variant = GButtonVariant.Ghost,
        )
    }
}

@Composable
internal fun InBodyScoreCard(
    inBodyScore: Int?,
    scoreLabel: String,
    lastScanDate: String,
    hasScanData: Boolean,
    onCardClick: () -> Unit,
    onScanNow: () -> Unit,
) {
    val token = GymTheme.token
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick),
        shape = RoundedCornerShape(token.radius.md),
        color = token.colors.surface,
        border = BorderStroke(width = 1.dp, color = token.colors.borderSubtle),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(token.card.padding),
        ) {
            Text(
                text = stringResource(R.string.profile_inbody_score),
                style = token.typography.titleMedium,
                color = token.colors.textSecondary,
            )
            if (hasScanData && inBodyScore != null) {
                Spacer(modifier = Modifier.height(token.spacing.xs))
                Text(
                    text = inBodyScore.toString(),
                    style = token.typography.displaySmall,
                    color = token.colors.textPrimary,
                )
                if (scoreLabel.isNotEmpty()) {
                    Text(
                        text = scoreLabel,
                        style = token.typography.bodyMedium,
                        color = token.colors.textSecondary,
                    )
                }
                val dateText = if (lastScanDate.isEmpty()) {
                    stringResource(R.string.profile_last_scan_unknown)
                } else {
                    stringResource(R.string.profile_last_scan, lastScanDate)
                }
                Text(
                    text = dateText,
                    style = token.typography.bodySmall,
                    color = token.colors.textMuted,
                )
                Spacer(modifier = Modifier.height(token.spacing.xs))
                Text(
                    text = stringResource(R.string.profile_view_body_analysis),
                    style = token.typography.labelLarge,
                    color = token.colors.primary,
                )
            } else {
                Spacer(modifier = Modifier.height(token.spacing.md))
                Text(
                    text = stringResource(R.string.profile_no_body_scan_yet),
                    style = token.typography.titleMedium,
                    color = token.colors.textPrimary,
                )
                Spacer(modifier = Modifier.height(token.spacing.xxs))
                Text(
                    text = stringResource(R.string.profile_no_scan_subtitle),
                    style = token.typography.bodyMedium,
                    color = token.colors.textSecondary,
                )
                Spacer(modifier = Modifier.height(token.spacing.md))
                GButton(
                    text = stringResource(R.string.profile_scan_now),
                    onClick = onScanNow,
                    variant = GButtonVariant.Ghost,
                )
            }
        }
    }
}

@Composable
internal fun LogoutButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        GButton(
            text = stringResource(R.string.profile_logout),
            onClick = onClick,
            variant = GButtonVariant.Ghost,
        )
    }
}
