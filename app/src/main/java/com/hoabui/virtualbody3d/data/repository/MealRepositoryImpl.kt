package com.hoabui.virtualbody3d.data.repository

import com.hoabui.virtualbody3d.data.local.MealLocalDataSource
import com.hoabui.virtualbody3d.data.mapper.toDomain
import com.hoabui.virtualbody3d.domain.model.MealAnalysis
import com.hoabui.virtualbody3d.domain.repository.MealRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MealRepositoryImpl @Inject constructor(
    private val localDataSource: MealLocalDataSource
) : MealRepository {

    override suspend fun analyzeMeal(imageUrl: String): MealAnalysis =
        localDataSource.analyzeMeal(imageUrl).toDomain()

    override suspend fun getDaysWithMeals(): List<LocalDate> =
        localDataSource.getDaysWithMeals()

    override suspend fun getMealsByDay(day: LocalDate): List<MealAnalysis> =
        localDataSource.getMealsByDay(day).map { it.toDomain() }
}
