package com.example.myvirtualbody.viewer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import io.github.sceneview.SceneView
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraManipulator
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironment
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberMainLightNode
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberRenderer
import io.github.sceneview.rememberScene
import io.github.sceneview.rememberView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val MODEL_ASSET = "models/female_gym_outfit_sports_bra_and_leggins.glb"

/**
 * 3D model viewer screen using SceneView (io.github.sceneview) via AndroidView.
 * Loads a GLB from assets, supports touch rotation and pinch-to-zoom,
 * and exposes [ModelViewerState] for future morph target control (e.g. body fat).
 */
@Composable
fun ModelViewerScreen(
    modifier: Modifier = Modifier,
    onStateUpdated: (ModelViewerState) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var modelNode by remember { mutableStateOf<ModelNode?>(null) }
    var sceneViewRef by remember { mutableStateOf<SceneView?>(null) }

    // Shared Filament/SceneView components (proper lighting, camera, rotation/pinch)
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val materialLoader = rememberMaterialLoader(engine)
    val environmentLoader = rememberEnvironmentLoader(engine)
    val scene = rememberScene(engine)
    val view = rememberView(engine)
    val renderer = rememberRenderer(engine)
    val cameraNode = rememberCameraNode(engine) {
        position = Position(z = 4f)
    }
    val mainLightNode = rememberMainLightNode(engine) {
        intensity = 100_000f
    }
    val environment = rememberEnvironment(environmentLoader) {
        SceneView.createEnvironment(environmentLoader, isOpaque = true)
    }
    val cameraManipulator = rememberCameraManipulator(
        orbitHomePosition = Position(z = 4f),
        targetPosition = Position(x = 0f, y = 0f, z = 0f)
    )

    // Notify parent of state changes (for morph targets later)
    LaunchedEffect(modelNode, isLoading, error) {
        onStateUpdated(
            ModelViewerState(
                modelNode = modelNode,
                isLoading = isLoading,
                error = error
            )
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                SceneView(
                    context = ctx,
                    sharedEngine = engine,
                    sharedModelLoader = modelLoader,
                    sharedMaterialLoader = materialLoader,
                    sharedEnvironmentLoader = environmentLoader,
                    sharedScene = scene,
                    sharedView = view,
                    sharedRenderer = renderer,
                    sharedCameraNode = cameraNode,
                    sharedMainLightNode = mainLightNode,
                    sharedEnvironment = environment,
                    cameraManipulator = cameraManipulator,
                    sharedActivity = context as? androidx.activity.ComponentActivity,
                    sharedLifecycle = lifecycle
                ).also { sceneViewRef = it }
            },
            update = { sceneView ->
                sceneView.lifecycle = lifecycle
                modelNode?.let { node ->
                    if (node !in sceneView.childNodes) {
                        sceneView.addChildNode(node)
                    }
                }
            }
        )

        when {
            error != null -> Text(
                text = error!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(MaterialTheme.colorScheme.surface)
            )
            isLoading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

    // Load GLB from assets (sync createModelInstance on Default dispatcher to avoid blocking UI)
    LaunchedEffect(sceneViewRef) {
        val sceneView = sceneViewRef ?: return@LaunchedEffect
        if (modelNode != null) return@LaunchedEffect

        isLoading = true
        error = null

        runCatching {
            // Filament/gltfio typically require engine thread (main) for loading
            withContext(Dispatchers.Main.immediate) {
                modelLoader.createModelInstance(MODEL_ASSET)
            }
        }.fold(
            onSuccess = { instance ->
                val node = ModelNode(
                    modelInstance = instance,
                    autoAnimate = false,
                    scaleToUnits = 1.2f,
                    centerOrigin = Position(x = 0f, y = 0f, z = 0f)
                )
                modelNode = node
                sceneView.addChildNode(node)
            },
            onFailure = { e ->
                error = "Failed to load model: ${e.message}"
            }
        )
        isLoading = false
    }
}
