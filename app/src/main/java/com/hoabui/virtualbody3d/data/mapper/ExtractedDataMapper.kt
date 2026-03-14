package com.hoabui.virtualbody3d.data.mapper

import com.hoabui.virtualbody3d.data.model.ExtractedDataDto
import com.hoabui.virtualbody3d.domain.model.ExtractedData

fun ExtractedDataDto.toDomain(): ExtractedData = ExtractedData(
    weight = weight,
    bodyFatPercent = bodyFatPercent,
    muscleMass = muscleMass,
    bmi = bmi,
    bodyFatMass = bodyFatMass,
    fatFreeMass = fatFreeMass,
    bmr = bmr,
    visceralFatLevel = visceralFatLevel,
    rawLines = rawLines
)
