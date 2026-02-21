package com.hoabui.virtualbody3d.ui.body.screen

import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.compose.foundation.background
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
import com.hoabui.virtualbody3d.core.utils.Constants.BODY_DEV_LOG_TAG
import com.hoabui.virtualbody3d.core.utils.Constants.BODY_DEV_MODE
import com.hoabui.virtualbody3d.core.utils.Constants.BODY_MODEL_ASSET_PATH
import com.hoabui.virtualbody3d.core.utils.Constants.FILAMENT_MAX_BONES
import com.hoabui.virtualbody3d.ui.body.state.GlbSceneBounds
import com.hoabui.virtualbody3d.ui.theme.GymTheme
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
import org.json.JSONObject
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import kotlin.math.max
import kotlin.math.sqrt
import kotlin.math.tan

@Composable
fun BodyModelPreview(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    var isLoading by remember { mutableStateOf(true) }
    var modelNode by remember { mutableStateOf<ModelNode?>(null) }
    var sceneViewRef by remember { mutableStateOf<SceneView?>(null) }
    var modelYaw by remember { mutableFloatStateOf(180f) }
    var orbitHomePosition by remember { mutableStateOf(Position(x = 0f, y = 0f, z = 4f)) }
    var orbitTargetPosition by remember { mutableStateOf(Position(x = 0f, y = 0f, z = 0f)) }

    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val materialLoader = rememberMaterialLoader(engine)
    val environmentLoader = rememberEnvironmentLoader(engine)
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
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GymTheme.token.colors.background)
    ) {
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
                    cameraManipulator = null,
                    sharedActivity = context as? androidx.activity.ComponentActivity,
                    sharedLifecycle = lifecycle
                ).also { sceneView ->
                    sceneView.setOnTouchListener(object : View.OnTouchListener {
                        private var lastX = 0f

                        override fun onTouch(v: android.view.View?, event: MotionEvent): Boolean {
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

    LaunchedEffect(sceneViewRef) {
        val sceneView = sceneViewRef ?: return@LaunchedEffect
        if (modelNode != null) return@LaunchedEffect

        isLoading = true
        val glbBounds = withContext(Dispatchers.IO) {
            parseGlbSceneBounds(context, BODY_MODEL_ASSET_PATH)
        }

        runCatching {
            val maxBonesInModel = withContext(Dispatchers.IO) {
                readMaxJointCountFromGlb(context, BODY_MODEL_ASSET_PATH)
            }
            if (maxBonesInModel != null && maxBonesInModel > FILAMENT_MAX_BONES) {
                throw IllegalStateException(
                    "Model has $maxBonesInModel bones (> $FILAMENT_MAX_BONES). " +
                        "Please reduce rig complexity or test on a higher-end renderer."
                )
            }
            withContext(Dispatchers.Main.immediate) {
                modelLoader.createModelInstance(BODY_MODEL_ASSET_PATH)
            }
        }.fold(
            onSuccess = { instance ->
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
                val nodePositionY = if (glbBounds != null && glbMinY != null) {
                    (-glbMinY).coerceIn(-1e6f, 1e6f)
                } else {
                    (-runtimeCenterY + halfHeight).coerceIn(-1e6f, 1e6f)
                }
                val modelCenterY = (nodePositionY + frameSizeY * 0.58f).coerceIn(0.01f, 1e6f)
                node.position = Position(x = 0f, y = nodePositionY, z = 0f)

                val diagonal = sqrt(
                    frameSizeX * frameSizeX +
                        frameSizeY * frameSizeY +
                        frameSizeZ * frameSizeZ
                )
                val sphereRadius = diagonal * 0.5f
                val verticalFovDeg = 40f
                val halfFovRad = Math.toRadians((verticalFovDeg / 2f).toDouble()).toFloat()
                val fitDistance = sphereRadius / tan(halfFovRad)
                val fitByBottom = (frameSizeY * 0.58f) / tan(halfFovRad)
                val cameraDistance = max(fitByBottom * 1.0f, fitDistance * 0.94f)
                    .coerceIn(2f, 300f)

                val lookAt = Position(x = 0f, y = modelCenterY, z = 0f)
                orbitTargetPosition = lookAt
                orbitHomePosition = Position(x = 0f, y = modelCenterY, z = -cameraDistance)
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
                            "runtime size=${node.size.x}x${node.size.y}x${node.size.z} cameraZ=-$cameraDistance"
                    )
                }

                modelNode = node
                sceneView.addChildNode(node)
            },
            onFailure = { }
        )
        isLoading = false
    }
}

private fun parseGlbSceneBounds(context: android.content.Context, assetPath: String): GlbSceneBounds? {
    val data = runCatching { context.assets.open(assetPath).use { it.readBytes() } }.getOrNull() ?: return null
    if (data.size < 20) return null
    val header = ByteBuffer.wrap(data, 0, 12).order(ByteOrder.LITTLE_ENDIAN)
    if (header.int != 0x46546C67) return null
    var offset = 12
    while (offset + 8 <= data.size) {
        val chunkHeader = ByteBuffer.wrap(data, offset, 8).order(ByteOrder.LITTLE_ENDIAN)
        val chunkLength = chunkHeader.int
        val chunkType = chunkHeader.int
        offset += 8
        if (chunkLength < 0 || offset + chunkLength > data.size) break
        if (chunkType != 0x4E4F534A) {
            offset += chunkLength
            continue
        }
        val jsonText = String(data, offset, chunkLength, Charset.forName("UTF-8"))
        val root = runCatching { JSONObject(jsonText) }.getOrNull() ?: return null
        val accessors = root.optJSONArray("accessors") ?: break
        val meshes = root.optJSONArray("meshes") ?: break
        var minX = Float.POSITIVE_INFINITY
        var minY = Float.POSITIVE_INFINITY
        var minZ = Float.POSITIVE_INFINITY
        var maxX = Float.NEGATIVE_INFINITY
        var maxY = Float.NEGATIVE_INFINITY
        var maxZ = Float.NEGATIVE_INFINITY
        fun mergeAccessor(accIndex: Int) {
            val acc = accessors.optJSONObject(accIndex) ?: return
            val minArr = acc.optJSONArray("min") ?: return
            val maxArr = acc.optJSONArray("max") ?: return
            if (minArr.length() < 3 || maxArr.length() < 3) return
            val mx = minArr.optDouble(0).toFloat()
            val my = minArr.optDouble(1).toFloat()
            val mz = minArr.optDouble(2).toFloat()
            val bigX = maxArr.optDouble(0).toFloat()
            val bigY = maxArr.optDouble(1).toFloat()
            val bigZ = maxArr.optDouble(2).toFloat()
            if (mx < minX) minX = mx
            if (my < minY) minY = my
            if (mz < minZ) minZ = mz
            if (bigX > maxX) maxX = bigX
            if (bigY > maxY) maxY = bigY
            if (bigZ > maxZ) maxZ = bigZ
        }
        for (i in 0 until meshes.length()) {
            val mesh = meshes.optJSONObject(i) ?: continue
            val primitives = mesh.optJSONArray("primitives") ?: continue
            for (j in 0 until primitives.length()) {
                val prim = primitives.optJSONObject(j) ?: continue
                val attrs = prim.optJSONObject("attributes") ?: continue
                val posIndex = attrs.optInt("POSITION", -1)
                if (posIndex in 0 until accessors.length()) mergeAccessor(posIndex)
            }
        }
        if (minX == Float.POSITIVE_INFINITY) return null
        return GlbSceneBounds(minX, minY, minZ, maxX, maxY, maxZ)
    }
    return null
}

private fun readMaxJointCountFromGlb(context: android.content.Context, assetPath: String): Int? {
    val data = runCatching { context.assets.open(assetPath).use { it.readBytes() } }.getOrNull()
        ?: return null
    if (data.size < 20) return null

    val header = ByteBuffer.wrap(data, 0, 12).order(ByteOrder.LITTLE_ENDIAN)
    val magic = header.int
    if (magic != 0x46546C67) return null

    var offset = 12
    while (offset + 8 <= data.size) {
        val chunkHeader = ByteBuffer.wrap(data, offset, 8).order(ByteOrder.LITTLE_ENDIAN)
        val chunkLength = chunkHeader.int
        val chunkType = chunkHeader.int
        offset += 8

        if (chunkLength < 0 || offset + chunkLength > data.size) return null
        if (chunkType == 0x4E4F534A) {
            val jsonText = String(data, offset, chunkLength, Charset.forName("UTF-8"))
            val root = runCatching { JSONObject(jsonText) }.getOrNull() ?: return null
            val skins = root.optJSONArray("skins") ?: return 0

            var maxJointCount = 0
            for (i in 0 until skins.length()) {
                val skin = skins.optJSONObject(i) ?: continue
                val joints = skin.optJSONArray("joints") ?: continue
                if (joints.length() > maxJointCount) {
                    maxJointCount = joints.length()
                }
            }
            return maxJointCount
        }

        offset += chunkLength
    }
    return null
}
