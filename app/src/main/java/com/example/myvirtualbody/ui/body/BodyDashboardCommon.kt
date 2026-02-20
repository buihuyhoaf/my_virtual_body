package com.example.myvirtualbody.ui.body

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myvirtualbody.ui.theme.BodyPrimary
import com.example.myvirtualbody.ui.theme.BodyPrimaryLight
import com.example.myvirtualbody.ui.theme.GlassChipBackgroundEnd
import com.example.myvirtualbody.ui.theme.GlassChipBackgroundStart
import com.example.myvirtualbody.ui.theme.GlassChipBorder
import com.example.myvirtualbody.ui.theme.GlassChipIconBackground
import com.example.myvirtualbody.ui.theme.ScoreRingTrack

private val DashboardChipShape = RoundedCornerShape(BodyDimens.cornerXLarge)
private val DashboardProgressShape = RoundedCornerShape(BodyDimens.cornerSmall)

@Composable
fun FloatingMetricChip(
    icon: ImageVector,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.widthIn(min = 92.dp),
        shape = DashboardChipShape,
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassChipBorder),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        listOf(GlassChipBackgroundStart, GlassChipBackgroundEnd)
                    ),
                    shape = DashboardChipShape
                )
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(
                        color = GlassChipIconBackground,
                        shape = DashboardChipShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = BodyPrimary,
                    modifier = Modifier.size(13.dp)
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun BodyScoreChip(
    score: Int,
    modifier: Modifier = Modifier
) {
    val clamped = score.coerceIn(0, 100)
    Surface(
        modifier = modifier.widthIn(min = 92.dp),
        shape = DashboardChipShape,
        color = Color.Transparent,
        border = androidx.compose.foundation.BorderStroke(1.dp, GlassChipBorder),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        listOf(GlassChipBackgroundStart, GlassChipBackgroundEnd)
                    ),
                    shape = DashboardChipShape
                )
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { clamped / 100f },
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 4.dp,
                    color = BodyPrimary,
                    trackColor = ScoreRingTrack,
                    strokeCap = StrokeCap.Round
                )
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(BodyPrimaryLight, DashboardProgressShape)
                )
            }
            Text(
                text = clamped.toString(),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.ExtraBold,
                color = BodyPrimary
            )
        }
    }
}
