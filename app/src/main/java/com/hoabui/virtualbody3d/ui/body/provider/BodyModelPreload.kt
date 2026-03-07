package com.hoabui.virtualbody3d.ui.body.provider

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.hoabui.virtualbody3d.core.utils.Constants.BODY_MODEL_ASSET_PATH
import com.hoabui.virtualbody3d.core.utils.Constants.FILAMENT_MAX_BONES
import com.hoabui.virtualbody3d.ui.body.GlbMetadataCache
import com.hoabui.virtualbody3d.ui.body.parseGlbMetadata
import io.github.sceneview.SceneView
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Invisible composable that creates a shared Filament engine and preloads the body GLB.
 * Place once at app root (e.g. MainActivity) so [BodyModelPreview] on Home can use [BodyModelProvider] and skip loading.
 */
@Composable
fun BodyModelPreload(
    modifier: Modifier = Modifier
) {
    val provider = LocalBodyModelProvider.current
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val materialLoader = rememberMaterialLoader(engine)
    val environmentLoader = rememberEnvironmentLoader(engine)

    // Minimal SceneView so engine has a GL context when attached (1dp so it doesn't affect layout)
    Box(modifier = modifier.size(1.dp)) {
        AndroidView(
            factory = { ctx ->
                SceneView(
                    context = ctx,
                    sharedEngine = engine,
                    sharedModelLoader = modelLoader,
                    sharedMaterialLoader = materialLoader,
                    sharedEnvironmentLoader = environmentLoader,
                    sharedActivity = context as? androidx.activity.ComponentActivity,
                    sharedLifecycle = lifecycle
                )
            },
            update = { it.lifecycle = lifecycle }
        )
    }

    LaunchedEffect(engine, modelLoader, materialLoader, environmentLoader) {
        if (provider.isReady()) return@LaunchedEffect
        val metadata = withContext(Dispatchers.IO) {
            GlbMetadataCache.getOrPut(context, BODY_MODEL_ASSET_PATH, ::parseGlbMetadata)
        }
        val maxBones = metadata.maxJointCount
        if (maxBones != null && maxBones > FILAMENT_MAX_BONES) return@LaunchedEffect
        val instance = withContext(Dispatchers.IO) {
            @Suppress("UNCHECKED_CAST")
            runCatching {
                modelLoader.loadModelInstance(BODY_MODEL_ASSET_PATH)
            }.getOrNull()
        } ?: return@LaunchedEffect
        withContext(Dispatchers.Main.immediate) {
            if (!provider.isReady()) {
                provider.setShared(
                    engine = engine,
                    modelLoader = modelLoader,
                    materialLoader = materialLoader,
                    environmentLoader = environmentLoader,
                    instance = instance as com.google.android.filament.gltfio.FilamentInstance
                )
            }
        }
    }
}
