package com.hoabui.virtualbody3d.domain.usecase

import com.hoabui.virtualbody3d.domain.model.UploadedImage
import com.hoabui.virtualbody3d.domain.repository.BaselineRepository
import java.io.File
import javax.inject.Inject

/**
 * Uploads a prepared image file to the backend.
 */
class UploadImageUseCase @Inject constructor(
    private val baselineRepository: BaselineRepository
) {
    /**
     * Uploads [file] to the backend.
     * @return [UploadedImage] with imageId and imageUrl for analysis
     * @throws Exception on upload failure
     */
    suspend operator fun invoke(file: File): UploadedImage =
        baselineRepository.uploadImage(file)
}
