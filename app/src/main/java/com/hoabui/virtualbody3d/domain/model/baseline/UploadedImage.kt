package com.hoabui.virtualbody3d.domain.model.baseline

/**
 * Result of uploading an image to the backend.
 * Used to pass the image reference to the analysis step.
 */
data class UploadedImage(
    val imageId: String,
    val imageUrl: String
)
