package com.hoabui.virtualbody3d.ui.theme.font

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.hoabui.virtualbody3d.R

/**
 * Inter variable font family with explicit weight mapping.
 * Uses res/font/inter_variable.ttf (variable font).
 */
val InterFontFamily = FontFamily(
    Font(R.font.inter_variable, weight = FontWeight.Normal),
    Font(R.font.inter_variable, weight = FontWeight.Medium),
    Font(R.font.inter_variable, weight = FontWeight.SemiBold),
    Font(R.font.inter_variable, weight = FontWeight.Bold)
)
