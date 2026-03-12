package com.hoabui.virtualbody3d.ui.mealcapture

import android.net.Uri
import com.hoabui.virtualbody3d.core.base.UiStateViewModel
import com.hoabui.virtualbody3d.domain.usecase.AnalyzeMealImageUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetMealDaysUseCase
import com.hoabui.virtualbody3d.domain.usecase.GetMealsByDayUseCase
import com.hoabui.virtualbody3d.domain.usecase.PrepareImageUseCase
import com.hoabui.virtualbody3d.domain.usecase.UploadImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel for the meals screen: load meals by day on enter, refetch today after capture,
 * and load next day when user scrolls to the end of the list.
 */
@HiltViewModel
class MealsViewModel @Inject constructor(
    private val prepareImageUseCase: PrepareImageUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val analyzeMealImageUseCase: AnalyzeMealImageUseCase,
    private val getMealDaysUseCase: GetMealDaysUseCase,
    private val getMealsByDayUseCase: GetMealsByDayUseCase
) : UiStateViewModel<Unit, Unit>() {

    private val _daysWithMeals = MutableStateFlow<List<LocalDate>>(emptyList())
    val daysWithMeals: StateFlow<List<LocalDate>> = _daysWithMeals.asStateFlow()

    private val _mealPages = MutableStateFlow<List<MealPageUiModel>>(emptyList())
    val mealPages: StateFlow<List<MealPageUiModel>> = _mealPages.asStateFlow()

    /** Meals for today; updated when today is loaded. Used by CaloriesTodayPanel on Body screen. */
    private val _mealsForToday = MutableStateFlow<List<MealPageUiModel>>(emptyList())
    val mealsForToday: StateFlow<List<MealPageUiModel>> = _mealsForToday.asStateFlow()

    /** Index in [daysWithMeals] for the last loaded day (used to load next day when user scrolls to end). */
    private val _currentDayIndex = MutableStateFlow(0)
    val currentDayIndex: StateFlow<Int> = _currentDayIndex.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isLoadingNextDay = MutableStateFlow(false)
    val isLoadingNextDay: StateFlow<Boolean> = _isLoadingNextDay.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadOnEnter()
    }

    /**
     * Load days with meals and then meals for today (first day to show).
     */
    private fun loadOnEnter() {
        launchSafely {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val days = runCatching {
                    withContext(Dispatchers.IO) { getMealDaysUseCase() }
                }.getOrElse { e ->
                    _errorMessage.value = e.message ?: "Failed to load meal days"
                    return@launchSafely
                }
                val today = LocalDate.now()
                // If no days from API (e.g. no meals yet), treat today as the only segment
                _daysWithMeals.value = if (days.isEmpty()) listOf(today) else days

                val resolvedDays = _daysWithMeals.value
                val idx = resolvedDays.indexOf(today).coerceAtLeast(0)
                _currentDayIndex.value = idx

                loadMealsForDayInternal(today, replace = true)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Entry point when the user confirms using a photo for meal analysis.
     * Runs pipeline, adds new meal to list (so page 1 scroll works), then refetches today from API.
     */
    fun onMealImageConfirmed(file: File) {
        launchSafely {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val prepared = runCatching {
                    withContext(Dispatchers.IO) { prepareImageUseCase(file) }
                }.getOrElse { e ->
                    _errorMessage.value = e.message ?: "Failed to process meal image"
                    return@launchSafely
                }

                val uploaded = runCatching {
                    withContext(Dispatchers.IO) { uploadImageUseCase(prepared) }
                }.getOrElse { e ->
                    _errorMessage.value = e.message ?: "Failed to upload meal image"
                    return@launchSafely
                }

                val mealAnalysis = runCatching {
                    withContext(Dispatchers.IO) {
                        analyzeMealImageUseCase(uploaded.imageUrl)
                    }
                }.getOrElse { e ->
                    _errorMessage.value = e.message ?: "Failed to analyze meal image"
                    return@launchSafely
                }

                val imageUri = Uri.fromFile(prepared)
                val page = mealAnalysis.toMealPageUiModel(imageUri = imageUri)

                _mealPages.update { current ->
                    listOf(page) + current
                }

                // Refetch today from API so list is in sync with server
                refreshTodayMeals()

                // Ensure today is in days list (for edge case: first meal of the day)
                refreshDaysWithMeals()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun refreshTodayMeals() {
        launchSafely {
            val today = LocalDate.now()
            loadMealsForDayInternal(today, replace = true)
        }
    }

    private fun refreshDaysWithMeals() {
        launchSafely {
            val days = runCatching {
                withContext(Dispatchers.IO) { getMealDaysUseCase() }
            }.getOrNull() ?: return@launchSafely
            val today = LocalDate.now()
            _daysWithMeals.value = days
            val idx = days.indexOf(today).coerceAtLeast(0)
            _currentDayIndex.value = idx
        }
    }

    /**
     * Load meals for [day] and either replace the list (first load / refetch today) or append (load next day).
     */
    private suspend fun loadMealsForDayInternal(day: LocalDate, replace: Boolean) {
        val list = runCatching {
            withContext(Dispatchers.IO) { getMealsByDayUseCase(day) }
        }.getOrElse { return }

        val pages = list.map { it.toMealPageUiModelFromApi() }
        if (replace) {
            _mealPages.value = pages
            if (day == LocalDate.now()) {
                _mealsForToday.value = pages
            }
        } else {
            _mealPages.update { current -> current + pages }
        }
    }

    /**
     * Call when user scrolls near the end of the meal list. Loads the next day's meals if available.
     */
    fun loadNextDayIfNeeded() {
        launchSafely {
            val days = _daysWithMeals.value
            val idx = _currentDayIndex.value
            if (idx + 1 >= days.size) return@launchSafely
            if (_isLoadingNextDay.value) return@launchSafely

            _isLoadingNextDay.value = true
            try {
                val nextDay = days[idx + 1]
                loadMealsForDayInternal(nextDay, replace = false)
                _currentDayIndex.value = idx + 1
            } finally {
                _isLoadingNextDay.value = false
            }
        }
    }

    fun removeMeal(id: String) {
        _mealPages.update { current -> current.filterNot { it.id == id } }
    }

    fun clearAllMeals() {
        _mealPages.value = emptyList()
    }
}
