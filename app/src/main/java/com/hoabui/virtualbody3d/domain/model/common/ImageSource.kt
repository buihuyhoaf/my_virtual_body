package com.hoabui.virtualbody3d.domain.model.common

sealed class ImageSource {
    data class Network(val url: String) : ImageSource()
    data class LocalResource(val name: String) : ImageSource()
}
