package com.hoabui.virtualbody3d.domain.repository

import com.hoabui.virtualbody3d.domain.model.BodyMetrics

interface BodyRepository {
    fun getBodyMetrics(): BodyMetrics
}
