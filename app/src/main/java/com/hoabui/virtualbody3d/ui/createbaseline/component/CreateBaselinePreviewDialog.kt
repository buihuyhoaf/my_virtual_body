package com.hoabui.virtualbody3d.ui.createbaseline.component

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke

@Composable
fun CreateBaselinePreviewDialog(
    file: File,
    onCancel: () -> Unit,
    onUpload: () -> Unit,
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    val colors = token.colors
    val spacing = token.spacing
    val radius = token.radius
    var bitmap by remember(file) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(file) {
        bitmap = withContext(Dispatchers.IO) {
            runCatching {
                BitmapFactory.decodeFile(file.absolutePath)
            }.getOrNull()
        }
    }

    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth(0.9f)
                .widthIn(max = 448.dp)
                .padding(horizontal = spacing.xl)
        ) {
            Surface(
                shape = RoundedCornerShape(radius.lg),
                color = colors.surface,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(spacing.md)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(spacing.xxs)
                    ) {
                        Text(
                            text = stringResource(R.string.confirm_image_title),
                            style = token.typography.titleLarge,
                            color = colors.textPrimary
                        )
                        Text(
                            text = stringResource(R.string.confirm_image_subtitle),
                            style = token.typography.bodySmall,
                            color = colors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(3f / 4f)
                            .clip(RoundedCornerShape(radius.md))
                            .background(colors.surfaceSubtle)
                            .border(
                                width = 1.dp,
                                color = colors.borderSubtle,
                                shape = RoundedCornerShape(radius.md)
                            )
                    ) {
                        bitmap?.asImageBitmap()?.let { imageBitmap ->
                            Image(
                                bitmap = imageBitmap,
                                contentDescription = stringResource(R.string.confirm_image_title),
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(radius.md)),
                                contentScale = ContentScale.Crop
                            )
                        } ?: run {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = colors.primary)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = spacing.xs),
                        horizontalArrangement = Arrangement.spacedBy(spacing.md)
                    ) {
                        OutlinedButton(
                            onClick = onCancel,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.primary),
                            border = BorderStroke(token.createBaseline.borderWidth, colors.primary),
                            shape = RoundedCornerShape(radius.lg)
                        ) {
                            Text(
                                text = stringResource(R.string.confirm_image_cancel),
                                style = token.typography.titleMedium
                            )
                        }
                        Button(
                            onClick = onUpload,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                            shape = RoundedCornerShape(radius.lg)
                        ) {
                            Text(
                                text = stringResource(R.string.confirm_image_upload),
                                style = token.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
