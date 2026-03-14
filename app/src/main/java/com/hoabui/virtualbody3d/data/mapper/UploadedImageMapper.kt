package com.hoabui.virtualbody3d.data.mapper

import com.hoabui.virtualbody3d.data.model.UploadedImageDto
import com.hoabui.virtualbody3d.domain.model.UploadedImage

fun UploadedImageDto.toDomain(): UploadedImage = UploadedImage(
    imageId = imageId,
    imageUrl = imageUrl
)
