package com.hoabui.virtualbody3d.ui.createbaseline.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.ui.createbaseline.viewmodel.ReviewMetric
import com.hoabui.virtualbody3d.ui.createbaseline.viewmodel.ReviewState
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.R

@Composable
fun CreateBaselineReviewBottomSheet(
    reviewState: ReviewState,
    onUpdateField: (ReviewMetric, String) -> Unit,
    onConfirm: () -> Unit,
    onRetake: () -> Unit,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    val colors = token.colors
    val spacing = token.spacing
    val typography = token.typography
    val radius = token.radius
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    var focusedMetric by remember { mutableStateOf<ReviewMetric?>(null) }
    var editBuffer by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.xl)
            .verticalScroll(scrollState)
    ) {
        // Header
        Text(
            text = stringResource(R.string.review_baseline_title),
            style = typography.titleLarge,
            color = colors.textPrimary
        )
        Spacer(modifier = Modifier.height(spacing.xs))
        Text(
            text = stringResource(R.string.review_baseline_subtitle),
            style = typography.bodyMedium,
            color = colors.textSecondary
        )
        Spacer(modifier = Modifier.height(spacing.lg))

        // Section 1 – Summary card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(radius.lg),
            color = colors.surfaceElevated,
            shadowElevation = 4.dp,
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(spacing.lg),
                verticalArrangement = Arrangement.spacedBy(spacing.md)
            ) {
                SummaryRow(
                    label = stringResource(R.string.review_weight_kg),
                    value = reviewState.editableData.weight
                )
                SummaryRow(
                    label = stringResource(R.string.review_skeletal_muscle_kg),
                    value = reviewState.editableData.muscleMass
                )
                SummaryRow(
                    label = stringResource(R.string.review_body_fat_percent),
                    value = reviewState.editableData.bodyFatPercent
                )
            }
        }
        Spacer(modifier = Modifier.height(spacing.lg))

        // Section 2 – Editable metrics list
        EditableMetricRow(
            label = stringResource(R.string.review_bmi),
            value = reviewState.editableData.bmi,
            originalValue = reviewState.originalData.bmi,
            isEditing = focusedMetric == ReviewMetric.BMI,
            editBuffer = editBuffer,
            onStartEdit = {
                focusedMetric = ReviewMetric.BMI
                editBuffer = reviewState.editableData.bmi
            },
            onBufferChange = { editBuffer = it },
            onCommit = {
                onUpdateField(ReviewMetric.BMI, editBuffer.trim())
                focusedMetric = null
                focusManager.clearFocus()
            },
            onDismissEdit = { focusedMetric = null; focusManager.clearFocus() }
        )
        EditableMetricRow(
            label = stringResource(R.string.review_body_fat_mass),
            value = reviewState.editableData.bodyFatMass,
            originalValue = reviewState.originalData.bodyFatMass,
            isEditing = focusedMetric == ReviewMetric.BODY_FAT_MASS,
            editBuffer = editBuffer,
            onStartEdit = {
                focusedMetric = ReviewMetric.BODY_FAT_MASS
                editBuffer = reviewState.editableData.bodyFatMass
            },
            onBufferChange = { editBuffer = it },
            onCommit = {
                onUpdateField(ReviewMetric.BODY_FAT_MASS, editBuffer.trim())
                focusedMetric = null
                focusManager.clearFocus()
            },
            onDismissEdit = { focusedMetric = null; focusManager.clearFocus() }
        )
        EditableMetricRow(
            label = stringResource(R.string.review_fat_free_mass),
            value = reviewState.editableData.fatFreeMass,
            originalValue = reviewState.originalData.fatFreeMass,
            isEditing = focusedMetric == ReviewMetric.FAT_FREE_MASS,
            editBuffer = editBuffer,
            onStartEdit = {
                focusedMetric = ReviewMetric.FAT_FREE_MASS
                editBuffer = reviewState.editableData.fatFreeMass
            },
            onBufferChange = { editBuffer = it },
            onCommit = {
                onUpdateField(ReviewMetric.FAT_FREE_MASS, editBuffer.trim())
                focusedMetric = null
                focusManager.clearFocus()
            },
            onDismissEdit = { focusedMetric = null; focusManager.clearFocus() }
        )
        EditableMetricRow(
            label = stringResource(R.string.review_bmr),
            value = reviewState.editableData.bmr,
            originalValue = reviewState.originalData.bmr,
            isEditing = focusedMetric == ReviewMetric.BMR,
            editBuffer = editBuffer,
            onStartEdit = {
                focusedMetric = ReviewMetric.BMR
                editBuffer = reviewState.editableData.bmr
            },
            onBufferChange = { editBuffer = it },
            onCommit = {
                onUpdateField(ReviewMetric.BMR, editBuffer.trim())
                focusedMetric = null
                focusManager.clearFocus()
            },
            onDismissEdit = { focusedMetric = null; focusManager.clearFocus() }
        )
        EditableMetricRow(
            label = stringResource(R.string.review_visceral_fat_level),
            value = reviewState.editableData.visceralFatLevel,
            originalValue = reviewState.originalData.visceralFatLevel,
            isEditing = focusedMetric == ReviewMetric.VISCERAL_FAT_LEVEL,
            editBuffer = editBuffer,
            onStartEdit = {
                focusedMetric = ReviewMetric.VISCERAL_FAT_LEVEL
                editBuffer = reviewState.editableData.visceralFatLevel
            },
            onBufferChange = { editBuffer = it },
            onCommit = {
                onUpdateField(ReviewMetric.VISCERAL_FAT_LEVEL, editBuffer.trim())
                focusedMetric = null
                focusManager.clearFocus()
            },
            onDismissEdit = { focusedMetric = null; focusManager.clearFocus() }
        )
        Spacer(modifier = Modifier.height(spacing.lg))

        // Section 3 – Info text
        Text(
            text = stringResource(R.string.review_baseline_info_caption),
            style = typography.bodySmall,
            color = colors.textSecondary
        )
        Spacer(modifier = Modifier.height(spacing.xl))

        // Bottom action area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = spacing.xl),
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = onRetake,
                modifier = Modifier.padding(0.dp)
            ) {
                Text(
                    text = stringResource(R.string.review_retake),
                    style = typography.titleMedium,
                    color = colors.textSecondary
                )
            }
            Button(
                onClick = onConfirm,
                enabled = reviewState.isValid && !reviewState.isLoading,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.primary,
                    disabledContainerColor = colors.surfaceSubtle
                ),
                shape = RoundedCornerShape(radius.lg),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = token.elevation.level0)
            ) {
                if (reviewState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = colors.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = stringResource(R.string.review_confirm_baseline),
                        style = typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    val typography = GymTheme.token.typography
    val colors = GymTheme.token.colors
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = typography.titleMedium,
            color = colors.textSecondary
        )
        Text(
            text = value,
            style = typography.titleLarge,
            color = colors.textPrimary
        )
    }
}

@Composable
private fun EditableMetricRow(
    label: String,
    value: String,
    originalValue: String,
    isEditing: Boolean,
    editBuffer: String,
    onStartEdit: () -> Unit,
    onBufferChange: (String) -> Unit,
    onCommit: () -> Unit,
    onDismissEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    val colors = token.colors
    val typography = token.typography
    val spacing = token.spacing
    val radius = token.radius
    val isModified = value != originalValue

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = spacing.xs)
            .clickable(enabled = !isEditing) { onStartEdit() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                style = typography.bodyLarge,
                color = colors.textPrimary
            )
            if (isModified) {
                Spacer(modifier = Modifier.size(spacing.xs))
                Surface(
                    shape = RoundedCornerShape(radius.sm),
                    color = colors.primary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = stringResource(R.string.review_edited),
                        style = typography.labelSmall,
                        color = colors.primary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
        if (isEditing) {
            OutlinedTextField(
                value = editBuffer,
                onValueChange = onBufferChange,
                modifier = Modifier
                    .widthIn(min = 80.dp)
                    .onFocusChanged { if (!it.hasFocus) onCommit() },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.primary,
                    unfocusedBorderColor = colors.surfaceBorder,
                    focusedTextColor = colors.textPrimary,
                    unfocusedTextColor = colors.textPrimary,
                    cursorColor = colors.primary,
                    focusedLabelColor = colors.primary
                ),
                shape = RoundedCornerShape(radius.md)
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.xs)
            ) {
                Text(
                    text = value.ifEmpty { "—" },
                    style = typography.bodyLarge,
                    color = colors.textPrimary
                )
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = colors.textSecondary
                )
            }
        }
    }
}
