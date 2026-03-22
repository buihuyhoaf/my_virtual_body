package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.exercise.FeedExercise
import com.hoabui.virtualbody3d.domain.repository.ExercisesRepository
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetExercisesByDayUseCase @Inject constructor(
    private val exercisesRepository: ExercisesRepository,
) {

    /**
     * Stream of exercises scheduled for [day]. Defaults to today when [day] is omitted.
     */
    operator fun invoke(day: LocalDate = LocalDate.now()): Flow<List<FeedExercise>> =
        exercisesRepository.getExercisesByDay(day)
}
