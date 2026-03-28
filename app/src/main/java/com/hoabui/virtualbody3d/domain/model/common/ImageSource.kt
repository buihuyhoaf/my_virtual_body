package com.hoabui.virtualbody3d.domain.model.common

sealed class ImageSource {
    data class Network(val url: String) : ImageSource()
    data class LocalResource(val name: String) : ImageSource()
    /** Content [android.net.Uri] as string (e.g. `file://` or `content://`); resolved in UI layer. */
    data class ContentUri(val uriString: String) : ImageSource()
}
