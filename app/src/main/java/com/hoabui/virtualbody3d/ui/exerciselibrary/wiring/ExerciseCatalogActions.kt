package com.hoabui.virtualbody3d.ui.exerciselibrary.wiring

data class ExerciseCatalogActions(
    val onQueryChange: (String) -> Unit,
    val onExerciseClick: (String) -> Unit,
    val onLibraryListToggle: (String) -> Unit,
    val onDetailAddToCart: (String) -> Unit,
    val onClearExerciseDetail: () -> Unit,
)
