package com.hoabui.virtualbody3d.ui.exerciselibrary.wiring

data class ExerciseCatalogActions(
    val onQueryChange: (String) -> Unit,
    val onCardTap: (String) -> Unit,
)
