package com.hoabui.virtualbody3d.ui.mealcapture

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun MealResultPage(
    meal: MealPageUiModel,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    val colors = token.colors
    val spacing = token.spacing

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(spacing.md)
    ) {
        var bitmap by remember(meal.imageUri) { mutableStateOf<Bitmap?>(null) }

        LaunchedEffect(meal.imageUri) {
            val path = meal.imageUri.path
            bitmap = if (path != null) {
                withContext(Dispatchers.IO) {
                    runCatching { BitmapFactory.decodeFile(path) }.getOrNull()
                }
            } else {
                null
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(token.radius.lg)),
                contentAlignment = Alignment.Center
            ) {
                val imageBitmap = bitmap?.asImageBitmap()
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = meal.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    CircularProgressIndicator(color = colors.primary)
                }
            }

            Spacer(modifier = Modifier.height(spacing.lg))

            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = colors.surface,
                    contentColor = colors.textPrimary
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = token.elevation.level1),
                shape = RoundedCornerShape(token.radius.lg)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing.lg)
                ) {
                    Text(
                        text = meal.title,
                        style = token.typography.titleLarge,
                        color = colors.textPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (meal.caloriesText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(spacing.xs))
                        Text(
                            text = meal.caloriesText,
                            style = token.typography.titleMedium,
                            color = colors.textSecondary
                        )
                    }

                    if (meal.macroSummaryText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(spacing.md))
                        Text(
                            text = meal.macroSummaryText,
                            style = token.typography.bodyMedium,
                            color = colors.textPrimary
                        )
                    } else if (meal.rawLines.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(spacing.md))
                        Text(
                            text = meal.rawLines.joinToString(separator = "\n"),
                            style = token.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                }
            }
        }
    }
}

