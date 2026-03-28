package com.hoabui.virtualbody3d.domain.repository

interface ResourceProvider {
    fun drawableResId(name: String): Int?
}
