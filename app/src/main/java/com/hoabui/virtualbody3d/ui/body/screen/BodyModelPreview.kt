package com.hoabui.virtualbody3d.ui.body.screen

import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.filament.ColorGrading
import com.hoabui.virtualbody3d.core.utils.Constants.BODY_DEV_LOG_TAG
import com.hoabui.virtualbody3d.core.utils.Constants.BODY_DEV_MODE
import com.hoabui.virtualbody3d.core.utils.Constants.BODY_MODEL_ASSET_PATH
import com.hoabui.virtualbody3d.core.utils.Constants.FILAMENT_MAX_BONES
import com.hoabui.virtualbody3d.ui.body.GlbMetadataCache
import com.hoabui.virtualbody3d.ui.body.parseGlbMetadata
import com.hoabui.virtualbody3d.ui.body.provider.LocalBodyModelProvider
import io.github.sceneview.SceneView
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.ModelNode
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
import kotlin.math.max
import kotlin.math.sqrt
import kotlin.math.tan

@Composable
fun BodyModelPreview(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val provider = LocalBodyModelProvider.current

    var isLoading by remember { mutableStateOf(true) }
    var modelNode by remember { mutableStateOf<ModelNode?>(null) }
    var sceneViewRef by remember { mutableStateOf<SceneView?>(null) }
    var modelYaw by remember { mutableFloatStateOf(180f) }
    var orbitHomePosition by remember { mutableStateOf(Position(x = 4f, y = 0f, z = 0f)) }
    var orbitTargetPosition by remember { mutableStateOf(Position(x = 0f, y = 0f, z = 0f)) }

    val localEngine = rememberEngine()
    val localModelLoader = rememberModelLoader(localEngine)
    val localMaterialLoader = rememberMaterialLoader(localEngine)
    val localEnvironmentLoader = rememberEnvironmentLoader(localEngine)

    val useShared = provider.isReady()
    val engine = (if (useShared) provider.getEngine() else null) ?: localEngine
    val modelLoader = (if (useShared) provider.getModelLoader() else null) ?: localModelLoader
    val materialLoader = (if (useShared) provider.getMaterialLoader() else null) ?: localMaterialLoader
    val environmentLoader = (if (useShared) provider.getEnvironmentLoader() else null) ?: localEnvironmentLoader

    val scene = rememberScene(engine)
    val view = rememberView(engine)
    val renderer = rememberRenderer(engine)
    val cameraNode = rememberCameraNode(engine) {
        position = orbitHomePosition
    }
    val mainLightNode = rememberMainLightNode(engine) {
        intensity = 100_000f
    }
    val environment = rememberEnvironment(environmentLoader) {
        SceneView.createEnvironment(environmentLoader, isOpaque = false)
    }
    val colorGrading = remember(engine) {
        ColorGrading.Builder()
            .toneMapping(ColorGrading.ToneMapping.LINEAR)
            .exposure(1.2f)
            .build(engine)
    }
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        AndroidView(
            modifier = modifier,
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
                    cameraManipulator = null,
                    sharedActivity = context as? androidx.activity.ComponentActivity,
                    sharedLifecycle = lifecycle
                ).also { sceneView ->
                    view.setColorGrading(colorGrading)
                    sceneView.setOnTouchListener(object : View.OnTouchListener {
                        private var lastX = 0f

                        override fun onTouch(v: View?, event: MotionEvent): Boolean {
                            when (event.actionMasked) {
                                MotionEvent.ACTION_DOWN -> {
                                    lastX = event.x
                                    return true
                                }

                                MotionEvent.ACTION_MOVE -> {
                                    val node = modelNode ?: return true
                                    val dx = event.x - lastX
                                    lastX = event.x

                                    modelYaw = (modelYaw + dx * 0.35f) % 360f
                                    node.rotation = Rotation(0f, modelYaw, 0f)
                                    return true
                                }

                                MotionEvent.ACTION_UP -> {
                                    v?.performClick()
                                    return true
                                }

                                else -> return true
                            }
                        }
                    })
                    sceneViewRef = sceneView
                }
            },
            update = { sceneView ->
                sceneView.lifecycle = lifecycle
                sceneView.cameraManipulator = null
                modelNode?.let { node ->
                    if (node !in sceneView.childNodes) {
                        sceneView.addChildNode(node)
                    }
                }
            }
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

    LaunchedEffect(sceneViewRef, useShared) {
        val sceneView = sceneViewRef ?: return@LaunchedEffect
        if (modelNode != null) return@LaunchedEffect

        isLoading = true
        val metadata = withContext(Dispatchers.IO) {
            GlbMetadataCache.getOrPut(context, BODY_MODEL_ASSET_PATH, ::parseGlbMetadata)
        }
        val glbBounds = metadata.bounds

        val instance = if (useShared) {
            provider.getPreloadedInstance()
        } else {
            runCatching {
                val maxBonesInModel = metadata.maxJointCount
                if (maxBonesInModel != null && maxBonesInModel > FILAMENT_MAX_BONES) {
                    throw IllegalStateException(
                        "Model has $maxBonesInModel bones (> $FILAMENT_MAX_BONES). " +
                            "Please reduce rig complexity or test on a higher-end renderer."
                    )
                }
                withContext(Dispatchers.IO) {
                    modelLoader.loadModelInstance(BODY_MODEL_ASSET_PATH)
                }
            }.getOrNull()
        }

        withContext(Dispatchers.Main.immediate) {
            if (instance != null) {
                val node = ModelNode(
                    modelInstance = instance,
                    autoAnimate = false,
                    scaleToUnits = null,
                    centerOrigin = null
                )
                val frameSizeX = (glbBounds?.width ?: node.size.x).coerceIn(0.01f, 1e6f)
                val frameSizeY = (glbBounds?.height ?: node.size.y).coerceIn(0.01f, 1e6f)
                val frameSizeZ = (glbBounds?.depth ?: node.size.z).coerceIn(0.01f, 1e6f)
                val halfHeight = (node.extents.y * 0.5f).coerceIn(0.01f, 1e6f)
                val runtimeCenterY = node.center.y

                val glbMinY = glbBounds?.let { it.centerY - (it.height * 0.5f) }

                // Đặt chân model lên mặt phẳng Y = 0.
                val nodePositionY = if (glbBounds != null && glbMinY != null) {
                    (-glbMinY).coerceIn(-1e6f, 1e6f)
                } else {
                    (-runtimeCenterY + halfHeight).coerceIn(-1e6f, 1e6f)
                }

                // Căn model: chân ở Y = 0, giữ X/Z tại 0 (origin nằm trên trục thẳng đứng giữa scene).
                node.position = Position(x = 0f, y = nodePositionY, z = 0f)

                // Sau khi chân nằm ở Y = 0, tâm hình học nằm ở giữa chiều cao model.
                val modelCenterY = (nodePositionY + frameSizeY * 0.5f).coerceIn(0.0f, 1e6f)

                val diagonal = sqrt(
                    frameSizeX * frameSizeX +
                        frameSizeY * frameSizeY +
                        frameSizeZ * frameSizeZ
                )
                val sphereRadius = diagonal * 0.5f
                val verticalFovDeg = 40f
                val halfFovRad = Math.toRadians((verticalFovDeg / 2f).toDouble()).toFloat()
                val fitDistance = sphereRadius / tan(halfFovRad)
                // Không dùng bottomFactor nữa để tránh hiện tượng model nổi lên khỏi đáy.
                // Dùng đúng khoảng cách “fit theo hình cầu” để bao trọn model.
                val cameraDistance = fitDistance
                    .coerceIn(2f, 300f)

                val lookAt = Position(x = 0f, y = modelCenterY, z = 0f)
                orbitTargetPosition = lookAt
                // Force a 90-degree horizontal view: place camera on X axis, look at model center.
                orbitHomePosition = Position(x = cameraDistance, y = modelCenterY, z = 0f)
                cameraNode.position = orbitHomePosition
                cameraNode.lookAt(orbitTargetPosition)

                Log.d(
                    BODY_DEV_LOG_TAG,
                    "Camera framing v5: source=${if (glbBounds != null) "glbBounds" else "runtime"} " +
                        "frameSize=($frameSizeX, $frameSizeY, $frameSizeZ) " +
                        "node.size=(${node.size.x}, ${node.size.y}, ${node.size.z}) " +
                        "node.extents=(${node.extents.x}, ${node.extents.y}, ${node.extents.z}) " +
                        "runtimeCenterY=$runtimeCenterY glbCenterY=${glbBounds?.centerY} glbMinY=$glbMinY " +
                        "modelCenterY=$modelCenterY sphereRadius=$sphereRadius fitDistance=$fitDistance cameraDistance=$cameraDistance " +
                        "cameraNode.position=(${cameraNode.position.x}, ${cameraNode.position.y}, ${cameraNode.position.z}) lookAt=(${lookAt.x}, ${lookAt.y}, ${lookAt.z})"
                )

                node.rotation = Rotation(0f, 180f, 0f)

                if (BODY_DEV_MODE && glbBounds != null) {
                    Log.d(
                        BODY_DEV_LOG_TAG,
                        "GLB bounds: w=${glbBounds.width} h=${glbBounds.height} d=${glbBounds.depth} centerY=${glbBounds.centerY}; " +
                            "runtime size=${node.size.x}x${node.size.y}x${node.size.z} cameraX=$cameraDistance"
                    )
                }

                modelNode = node
                sceneView.addChildNode(node)
            }
            isLoading = false
        }
    }
}
