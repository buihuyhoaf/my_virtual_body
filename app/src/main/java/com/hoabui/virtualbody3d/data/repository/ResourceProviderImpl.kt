package com.hoabui.virtualbody3d.data.repository

import android.content.Context
import com.hoabui.virtualbody3d.domain.repository.ResourceProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourceProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ResourceProvider {
    override fun drawableResId(name: String): Int? {
        val id = context.resources.getIdentifier(name, "drawable", context.packageName)
        return id.takeIf { it != 0 }
    }
}
