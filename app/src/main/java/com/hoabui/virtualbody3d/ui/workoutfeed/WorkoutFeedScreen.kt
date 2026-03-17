package com.hoabui.virtualbody3d.ui.workoutfeed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

// ---------------------------------------------------------------------------
// Fake data models (UI only, no domain/DB)
// ---------------------------------------------------------------------------

private data class WorkoutDayUi(
    val label: String,
    val date: LocalDate,
    val workoutName: String,
    val exercises: List<ExerciseUi>,
    val feeling: String
)

private data class ExerciseUi(
    val name: String,
    val sets: Int,
    val reps: Int
)

private fun fakeWorkoutFeedItems(): List<WorkoutDayUi> {
    val today = LocalDate.now()
    return listOf(
        WorkoutDayUi(
            label = "Today",
            date = today,
            workoutName = "Chest Workout",
            exercises = listOf(
                ExerciseUi("Bench Press", 4, 10),
                ExerciseUi("Incline DB Press", 3, 12),
                ExerciseUi("Cable Fly", 3, 15)
            ),
            feeling = "💪 Strong"
        ),
        WorkoutDayUi(
            label = "Yesterday",
            date = today.minusDays(1),
            workoutName = "Leg Day",
            exercises = listOf(
                ExerciseUi("Squat", 4, 8),
                ExerciseUi("Hip Thrust", 3, 12),
                ExerciseUi("Leg Press", 3, 15)
            ),
            feeling = "🔥 Great session"
        ),
        WorkoutDayUi(
            label = "May 10",
            date = today.minusDays(2),
            workoutName = "Upper Body",
            exercises = listOf(
                ExerciseUi("Bench Press", 4, 10),
                ExerciseUi("Lat Pulldown", 3, 12),
                ExerciseUi("Shoulder Press", 3, 10),
                ExerciseUi("Cable Fly", 3, 15)
            ),
            feeling = "🙂 Normal"
        ),
        WorkoutDayUi(
            label = "May 8",
            date = today.minusDays(4),
            workoutName = "Back & Biceps",
            exercises = listOf(
                ExerciseUi("Deadlift", 3, 8),
                ExerciseUi("Lat Pulldown", 4, 10),
                ExerciseUi("Barbell Row", 3, 12)
            ),
            feeling = "😵 Tired"
        ),
        WorkoutDayUi(
            label = "May 6",
            date = today.minusDays(6),
            workoutName = "Push Day",
            exercises = listOf(
                ExerciseUi("Bench Press", 4, 8),
                ExerciseUi("Incline DB Press", 3, 10),
                ExerciseUi("Shoulder Press", 3, 12)
            ),
            feeling = "💪 Strong"
        )
    )
}

private fun formatHeaderDate(date: LocalDate): String {
    val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    val monthDay = date.format(DateTimeFormatter.ofPattern("MMM d", Locale.getDefault()))
    return "$dayOfWeek, $monthDay"
}

// ---------------------------------------------------------------------------
// Screen
// ---------------------------------------------------------------------------

@Composable
fun WorkoutFeedScreen(
    modifier: Modifier = Modifier
) {
    val token = GymTheme.token
    val feedItems = remember { fakeWorkoutFeedItems() }
    val todayItem = feedItems.firstOrNull { it.label == "Today" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = token.spacing.md)
    ) {
        TopDateHeader(todayWorkout = todayItem)
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(token.spacing.md),
            contentPadding = PaddingValues(
                top = token.spacing.md,
                bottom = token.spacing.xl
            )
        ) {
            items(
                items = feedItems,
                key = { it.date.toString() + it.workoutName }
            ) { day ->
                WorkoutDayCard(day = day)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// TopDateHeader
// ---------------------------------------------------------------------------

@Composable
private fun TopDateHeader(
    todayWorkout: WorkoutDayUi?
) {
    val token = GymTheme.token
    val today = LocalDate.now()
    val dateStr = formatHeaderDate(today)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = token.spacing.md, bottom = token.spacing.xs)
    ) {
        Text(
            text = "Today • $dateStr",
            style = token.typography.titleLarge,
            color = token.colors.textPrimary
        )
        Text(
            text = when {
                todayWorkout != null -> "${todayWorkout.workoutName} • ${todayWorkout.exercises.size} exercises"
                else -> "No workout today"
            },
            style = token.typography.bodyMedium,
            color = token.colors.textSecondary,
            modifier = Modifier.padding(top = token.spacing.xxs)
        )
        HorizontalDivider(
            modifier = Modifier.padding(top = token.spacing.md),
            color = token.colors.borderSubtle,
            thickness = 1.dp
        )
    }
}

// ---------------------------------------------------------------------------
// WorkoutDayCard
// ---------------------------------------------------------------------------

@Composable
private fun WorkoutDayCard(
    day: WorkoutDayUi
) {
    val token = GymTheme.token
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(token.card.cornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = token.colors.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = token.card.elevation),
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(token.card.padding)
            ) {
                WorkoutDayHeader(
                    label = day.label,
                    date = day.date
                )
                Column(
                    modifier = Modifier.padding(top = token.spacing.md),
                    verticalArrangement = Arrangement.spacedBy(token.spacing.xs)
                ) {
                    day.exercises.forEach { exercise ->
                        ExerciseRow(
                            name = exercise.name,
                            sets = exercise.sets,
                            reps = exercise.reps
                        )
                    }
                }
                FeelingSection(feeling = day.feeling)
            }
        }
    )
}

@Composable
private fun WorkoutDayHeader(
    label: String,
    date: LocalDate
) {
    val token = GymTheme.token
    val dateStr = when (label) {
        "Today", "Yesterday" -> label
        else -> formatHeaderDate(date)
    }
    Text(
        text = "Workout • $dateStr",
        style = token.typography.titleSmall,
        color = token.colors.textSecondary
    )
}

// ---------------------------------------------------------------------------
// ExerciseRow
// ---------------------------------------------------------------------------

@Composable
private fun ExerciseRow(
    name: String,
    sets: Int,
    reps: Int
) {
    val token = GymTheme.token
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            style = token.typography.bodyMedium,
            color = token.colors.textPrimary
        )
        Text(
            text = "$sets × $reps",
            style = token.typography.bodyMedium,
            color = token.colors.textSecondary
        )
    }
}

// ---------------------------------------------------------------------------
// FeelingSection
// ---------------------------------------------------------------------------

@Composable
private fun FeelingSection(
    feeling: String
) {
    val token = GymTheme.token
    Column(
        modifier = Modifier.padding(top = token.spacing.lg)
    ) {
        Text(
            text = "Feeling",
            style = token.typography.labelMedium,
            color = token.colors.textSecondary
        )
        Text(
            text = feeling,
            style = token.typography.bodyMedium,
            color = token.colors.textPrimary,
            modifier = Modifier.padding(top = token.spacing.xxs)
        )
    }
}
