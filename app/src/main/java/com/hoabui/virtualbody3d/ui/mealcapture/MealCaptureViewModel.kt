package com.hoabui.virtualbody3d.ui.mealcapture

import android.net.Uri
import com.hoabui.virtualbody3d.core.base.UiStateViewModel
import com.hoabui.virtualbody3d.domain.usecase.AnalyzeMealImageUseCase
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
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel responsible for running the MEAL analysis pipeline and maintaining
 * a list of meal pages to be displayed in the vertical pager.
 *
 * It is intentionally independent from [com.hoabui.virtualbody3d.ui.camera.viewmodel.CameraCaptureViewModel]
 * and talks directly to domain use cases.
 */
@HiltViewModel
class MealCaptureViewModel @Inject constructor(
    private val prepareImageUseCase: PrepareImageUseCase,
    private val uploadImageUseCase: UploadImageUseCase,
    private val analyzeMealImageUseCase: AnalyzeMealImageUseCase
) : UiStateViewModel<Unit, Unit>() {

    private val _mealPages = MutableStateFlow<List<MealPageUiModel>>(emptyList())
    val mealPages: StateFlow<List<MealPageUiModel>> = _mealPages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * Entry point from UI when the user confirms using a photo for meal analysis.
     * Runs: PreProcessing → Uploading → Analyzing(MEAL) and appends a new page on success.
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
                val page = mealAnalysis.toMealPageUiModel(
                    imageUri = imageUri
                )

                _mealPages.update { current ->
                    listOf(page) + current
                }
            } finally {
                _isLoading.value = false
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

