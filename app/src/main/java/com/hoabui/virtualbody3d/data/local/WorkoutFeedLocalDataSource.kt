package com.hoabui.virtualbody3d.data.local

import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.data.model.ExerciseDto
import com.hoabui.virtualbody3d.data.model.WorkoutFeedItemDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local fake data source cho Workout Feed, tương tự các *LocalDataSource khác.
 * Hiện tại dùng dữ liệu mock, sau này có thể thay bằng remote/cache mà không đổi domain.
 */
@Singleton
class WorkoutFeedLocalDataSource @Inject constructor() {

    fun getWorkoutFeed(): Flow<List<WorkoutFeedItemDto>> {
        val today = LocalDate.now()
        val items = listOf(
            WorkoutFeedItemDto(
                label = "Today",
                dateString = today.toString(),
                workoutName = "Chest Workout",
                exercises = listOf(
                    ExerciseDto(
                        id = "bench_press",
                        name = "Bench Press",
                        imageResId = R.drawable.body_unsplash,
                        imageResUrl = null,
                        bodyRegion = "CHEST",
                        difficulty = "MEDIUM",
                        description = "Bench press description",
                        primaryMuscles = listOf("CHEST"),
                        secondaryMuscles = listOf("TRICEPS"),
                        equipment = "BARBELL",
                        safetyNotes = "Use a spotter",
                        lastWeightKg = 80.0,
                        sets = 4,
                        reps = 10
                    ),
                    ExerciseDto(
                        id = "incline_db_press",
                        name = "Incline DB Press",
                        imageResId = R.drawable.body_unsplash,
                        imageResUrl = null,
                        bodyRegion = "CHEST",
                        difficulty = "MEDIUM",
                        description = "Incline dumbbell press description",
                        primaryMuscles = listOf("CHEST"),
                        secondaryMuscles = listOf("SHOULDERS"),
                        equipment = "DUMBBELL",
                        safetyNotes = "Control the weight",
                        lastWeightKg = 30.0,
                        sets = 3,
                        reps = 12
                    ),
                    ExerciseDto(
                        id = "cable_fly",
                        name = "Cable Fly",
                        imageResId = R.drawable.body_unsplash,
                        imageResUrl = null,
                        bodyRegion = "CHEST",
                        difficulty = "EASY",
                        description = "Cable fly description",
                        primaryMuscles = listOf("CHEST"),
                        secondaryMuscles = emptyList(),
                        equipment = "CABLE",
                        safetyNotes = "Keep slight bend in elbows",
                        lastWeightKg = 15.0,
                        sets = 3,
                        reps = 15
                    )
                ),
                feeling = "💪 Strong"
            ),
            WorkoutFeedItemDto(
                label = "Yesterday",
                dateString = today.minusDays(1).toString(),
                workoutName = "Leg Day",
                exercises = listOf(
                    ExerciseDto(
                        id = "squat",
                        name = "Squat",
                        imageResId = R.drawable.body_unsplash,
                        imageResUrl = null,
                        bodyRegion = "LEGS",
                        difficulty = "HARD",
                        description = "Squat description",
                        primaryMuscles = listOf("QUADS"),
                        secondaryMuscles = listOf("GLUTES"),
                        equipment = "BARBELL",
                        safetyNotes = "Keep back straight",
                        lastWeightKg = 100.0,
                        sets = 4,
                        reps = 8
                    ),
                    ExerciseDto(
                        id = "hip_thrust",
                        name = "Hip Thrust",
                        imageResId = R.drawable.body_unsplash,
                        imageResUrl = null,
                        bodyRegion = "GLUTES",
                        difficulty = "MEDIUM",
                        description = "Hip thrust description",
                        primaryMuscles = listOf("GLUTES"),
                        secondaryMuscles = emptyList(),
                        equipment = "BARBELL",
                        safetyNotes = "Support neck and back",
                        lastWeightKg = 80.0,
                        sets = 3,
                        reps = 12
                    ),
                    ExerciseDto(
                        id = "leg_press",
                        name = "Leg Press",
                        imageResId = R.drawable.body_unsplash,
                        imageResUrl = null,
                        bodyRegion = "LEGS",
                        difficulty = "MEDIUM",
                        description = "Leg press description",
                        primaryMuscles = listOf("QUADS"),
                        secondaryMuscles = listOf("GLUTES"),
                        equipment = "MACHINE",
                        safetyNotes = "Do not lock knees",
                        lastWeightKg = 150.0,
                        sets = 3,
                        reps = 15
                    )
                ),
                feeling = "🔥 Great session"
            ),
            WorkoutFeedItemDto(
                label = "May 10",
                dateString = today.minusDays(2).toString(),
                workoutName = "Upper Body",
                exercises = listOf(
                    ExerciseDto(
                        id = "bench_press_upper",
                        name = "Bench Press",
                        imageResId = R.drawable.body_unsplash,
                        imageResUrl = null,
                        bodyRegion = "CHEST",
                        difficulty = "MEDIUM",
                        description = "Bench press description",
                        primaryMuscles = listOf("CHEST"),
                        secondaryMuscles = listOf("TRICEPS"),
                        equipment = "BARBELL",
                        safetyNotes = "Use a spotter",
                        lastWeightKg = 80.0,
                        sets = 4,
                        reps = 10
                    ),
                    ExerciseDto(
                        id = "lat_pulldown",
                        name = "Lat Pulldown",
                        imageResId = R.drawable.body_unsplash,
                        imageResUrl = null,
                        bodyRegion = "BACK",
                        difficulty = "MEDIUM",
                        description = "Lat pulldown description",
                        primaryMuscles = listOf("LATS"),
                        secondaryMuscles = listOf("BICEPS"),
                        equipment = "MACHINE",
                        safetyNotes = "Do not swing body",
                        lastWeightKg = 60.0,
                        sets = 3,
                        reps = 12
                    ),
                    ExerciseDto(
                        id = "shoulder_press",
                        name = "Shoulder Press",
                        imageResId = R.drawable.body_unsplash,
                        imageResUrl = null,
                        bodyRegion = "SHOULDERS",
                        difficulty = "MEDIUM",
                        description = "Shoulder press description",
                        primaryMuscles = listOf("SHOULDERS"),
                        secondaryMuscles = listOf("TRICEPS"),
                        equipment = "DUMBBELL",
                        safetyNotes = "Avoid arching lower back",
                        lastWeightKg = 20.0,
                        sets = 3,
                        reps = 10
                    ),
                    ExerciseDto(
                        id = "cable_fly_upper",
                        name = "Cable Fly",
                        imageResId = R.drawable.body_unsplash,
                        imageResUrl = null,
                        bodyRegion = "CHEST",
                        difficulty = "EASY",
                        description = "Cable fly description",
                        primaryMuscles = listOf("CHEST"),
                        secondaryMuscles = emptyList(),
                        equipment = "CABLE",
                        safetyNotes = "Keep slight bend in elbows",
                        lastWeightKg = 15.0,
                        sets = 3,
                        reps = 15
                    )
                ),
                feeling = "🙂 Normal"
            ),
            WorkoutFeedItemDto(
                label = "May 8",
                dateString = today.minusDays(4).toString(),
                workoutName = "Back & Biceps",
                exercises = listOf(
                    ExerciseDto(
                        id = "deadlift",
                        name = "Deadlift",
                        imageResId = R.drawable.body_unsplash,
                        imageResUrl = null,
                        bodyRegion = "BACK",
                        difficulty = "HARD",
                        description = "Deadlift description",
                        primaryMuscles = listOf("BACK"),
                        secondaryMuscles = listOf("GLUTES", "HAMSTRINGS"),
                        equipment = "BARBELL",
                        safetyNotes = "Maintain neutral spine",
                        lastWeightKg = 120.0,
                        sets = 3,
                        reps = 8
                    ),
                    ExerciseDto(
                        id = "lat_pulldown_back",
                        name = "Lat Pulldown",
                        imageResId = R.drawable.body_unsplash,
                        imageResUrl = null,
                        bodyRegion = "BACK",
                        difficulty = "MEDIUM",
                        description = "Lat pulldown description",
                        primaryMuscles = listOf("LATS"),
                        secondaryMuscles = listOf("BICEPS"),
                        equipment = "MACHINE",
                        safetyNotes = "Do not swing body",
                        lastWeightKg = 60.0,
                        sets = 4,
                        reps = 10
                    ),
                    ExerciseDto(
                        id = "barbell_row",
                        name = "Barbell Row",
                        imageResId = R.drawable.body_unsplash,
                        imageResUrl = null,
                        bodyRegion = "BACK",
                        difficulty = "MEDIUM",
                        description = "Barbell row description",
                        primaryMuscles = listOf("BACK"),
                        secondaryMuscles = listOf("BICEPS"),
                        equipment = "BARBELL",
                        safetyNotes = "Keep back flat",
                        lastWeightKg = 70.0,
                        sets = 3,
                        reps = 12
                    )
                ),
                feeling = "😵 Tired"
            ),
            WorkoutFeedItemDto(
                label = "May 6",
                dateString = today.minusDays(6).toString(),
                workoutName = "Push Day",
                exercises = listOf(
                    ExerciseDto(
                        id = "bench_press_push",
                        name = "Bench Press",
                        imageResId = R.drawable.body_unsplash,
                        imageResUrl = null,
                        bodyRegion = "CHEST",
                        difficulty = "MEDIUM",
                        description = "Bench press description",
                        primaryMuscles = listOf("CHEST"),
                        secondaryMuscles = listOf("TRICEPS"),
                        equipment = "BARBELL",
                        safetyNotes = "Use a spotter",
                        lastWeightKg = 85.0,
                        sets = 4,
                        reps = 8
                    ),
                    ExerciseDto(
                        id = "incline_db_press_push",
                        name = "Incline DB Press",
                        imageResId = R.drawable.body_unsplash,
                        imageResUrl = null,
                        bodyRegion = "CHEST",
                        difficulty = "MEDIUM",
                        description = "Incline dumbbell press description",
                        primaryMuscles = listOf("CHEST"),
                        secondaryMuscles = listOf("SHOULDERS"),
                        equipment = "DUMBBELL",
                        safetyNotes = "Control the weight",
                        lastWeightKg = 32.5,
                        sets = 3,
                        reps = 10
                    ),
                    ExerciseDto(
                        id = "shoulder_press_push",
                        name = "Shoulder Press",
                        imageResId = R.drawable.body_unsplash,
                        imageResUrl = null,
                        bodyRegion = "SHOULDERS",
                        difficulty = "MEDIUM",
                        description = "Shoulder press description",
                        primaryMuscles = listOf("SHOULDERS"),
                        secondaryMuscles = listOf("TRICEPS"),
                        equipment = "DUMBBELL",
                        safetyNotes = "Avoid arching lower back",
                        lastWeightKg = 22.5,
                        sets = 3,
                        reps = 12
                    )
                ),
                feeling = "💪 Strong"
            )
        )
        return flowOf(items)
    }
}

