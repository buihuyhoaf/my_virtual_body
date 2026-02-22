package com.hoabui.virtualbody3d.domain.repository

import com.hoabui.virtualbody3d.domain.model.BodyDashboard

interface BodyDashboardRepository {
    fun getBodyDashboard(): BodyDashboard
}
