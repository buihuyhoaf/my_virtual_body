package com.hoabui.virtualbody3d.ui.common_ui.image

import android.net.Uri
import androidx.compose.runtime.staticCompositionLocalOf
import com.hoabui.virtualbody3d.domain.model.common.ImageSource
import com.hoabui.virtualbody3d.domain.repository.ResourceProvider

val LocalResourceProvider = staticCompositionLocalOf<ResourceProvider> {
    error("ResourceProvider is not provided")
}

fun ImageSource.toImageModel(resourceProvider: ResourceProvider): Any? = when (this) {
    is ImageSource.Network -> url
    is ImageSource.LocalResource -> resourceProvider.drawableResId(name)
    is ImageSource.ContentUri -> Uri.parse(uriString)
}
