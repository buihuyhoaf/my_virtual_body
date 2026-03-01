package com.hoabui.virtualbody3d.ui.createbaseline.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.ExtractedData
import com.hoabui.virtualbody3d.ui.theme.GymTheme

@Composable
fun CreateBaselineReviewDialog(
    data: ExtractedData,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val token = GymTheme.token
    val colors = token.colors
    val spacing = token.spacing
    val typography = token.typography
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.review_extracted_title),
                style = typography.titleLarge,
                color = colors.textPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                Text("Weight: ${data.weight}", style = typography.bodyLarge, color = colors.textPrimary)
                Spacer(modifier = Modifier.height(spacing.xs))
                Text("Body fat: ${data.bodyFatPercent}", style = typography.bodyLarge, color = colors.textPrimary)
                Spacer(modifier = Modifier.height(spacing.xs))
                Text("Muscle mass: ${data.muscleMass}", style = typography.bodyLarge, color = colors.textPrimary)
                Spacer(modifier = Modifier.height(spacing.xs))
                Text("BMI: ${data.bmi}", style = typography.bodyLarge, color = colors.textPrimary)
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                shape = RoundedCornerShape(token.radius.lg),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = token.elevation.level0)
            ) {
                Text(
                    text = stringResource(R.string.review_confirm_baseline),
                    style = typography.titleMedium
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary),
                border = BorderStroke(token.createBaseline.borderWidth, colors.primary),
                shape = RoundedCornerShape(token.radius.lg)
            ) {
                Text(
                    text = stringResource(R.string.review_back),
                    style = typography.titleMedium
                )
            }
        },
        shape = RoundedCornerShape(token.radius.lg)
    )
}
