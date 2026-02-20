package com.example.myvirtualbody.ui.body

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.ZoomIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
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
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.Charset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

private val BodyPrimaryColor = Color(0xFF2B7CEE)
private val BodyEmeraldColor = Color(0xFF10B981)
private val BodyAmberColor = Color(0xFFF59E0B)
private val BodyRoseColor = Color(0xFFF43F5E)
private val BodySceneBackgroundColor = Color(0xFF1A2332)
private const val BODY_DEV_LOG_TAG = "BodyMorphDebug"
private const val BODY_DEV_MODE = true
private const val FILAMENT_MAX_BONES = 256
private const val BODY_MODEL_ASSET_PATH = "models/male_asian4.glb"
private const val BELLY_FAT_MORPH_INDEX = 0
private const val BELLY_FAT_MORPH_NAME = "bell_fat"

private data class BodyDevTuning(
    val bellyFat: Float = 0f
)

/**
 * Production-ready Body Analysis Screen following clean architecture principles.
 * Stateless composable - all state is passed as parameters.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyAnalysisScreen(
    uiState: BodyUiState,
    selectedTab: BodyTab,
    title: String,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onTabSelected: (BodyTab) -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var devBellyFat by remember { mutableFloatStateOf(0f) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            BodyTopBar(
                title = title,
                onBackClick = onBackClick,
                onSettingsClick = onSettingsClick
            )
        },
        bottomBar = {
            BodyBottomBar(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected
            )
        }
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            BodyPreviewSection(
                isDevMode = BODY_DEV_MODE,
                devTuning = BodyDevTuning(
                    bellyFat = devBellyFat
                ),
                modifier = Modifier.weight(0.65f)
            )
            AnalysisPanel(
                uiState = uiState,
                onEditClick = onEditClick,
                isDevMode = BODY_DEV_MODE,
                devBellyFat = devBellyFat,
                onDevBellyFatChanged = { devBellyFat = it },
                modifier = Modifier
                    .weight(0.35f)
                    .fillMaxHeight()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BodyTopBar(
    title: String,
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = BodyDimens.topBarPaddingHorizontal,
                    vertical = BodyDimens.topBarPaddingVertical
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun BodyPreviewSection(
    isDevMode: Boolean,
    devTuning: BodyDevTuning,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.radialGradient(
                    center = Offset(0.5f, 0.5f),
                    radius = 1.2f,
                    colors = listOf(
                        BodyPrimaryColor.copy(alpha = 0.06f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
    ) {
        BodyModelPreview(
            devTuning = if (isDevMode) devTuning else null,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = BodyDimens.spacingXLarge)
                .widthIn(max = 192.dp)
                .height(BodyDimens.spacingSmall)
                .background(
                    Color.Black.copy(alpha = 0.05f),
                    RoundedCornerShape(BodyDimens.cornerSmall)
                )
        )
        ViewControls(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = BodyDimens.spacingLarge)
        )
    }
}

@Composable
private fun ViewControls(
    onRotateClick: () -> Unit = {},
    onZoomClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(BodyDimens.spacingSmall)
    ) {
        IconButton(
            onClick = onRotateClick,
            modifier = Modifier
                .size(BodyDimens.floatingButtonSize)
                .background(
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    RoundedCornerShape(BodyDimens.cornerXLarge)
                )
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant,
                    RoundedCornerShape(BodyDimens.cornerXLarge)
                )
        ) {
            Icon(
                imageVector = Icons.Outlined.Refresh,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
        IconButton(
            onClick = onZoomClick,
            modifier = Modifier
                .size(BodyDimens.floatingButtonSize)
                .background(
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    RoundedCornerShape(BodyDimens.cornerXLarge)
                )
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant,
                    RoundedCornerShape(BodyDimens.cornerXLarge)
                )
        ) {
            Icon(
                imageVector = Icons.Outlined.ZoomIn,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * 3D body model preview using SceneView.
 * Integrated SceneView for 3D GLB model rendering with touch controls.
 */
@Composable
private fun BodyModelPreview(
    modifier: Modifier = Modifier,
    devTuning: BodyDevTuning? = null,
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var modelNode by remember { mutableStateOf<ModelNode?>(null) }
    var sceneViewRef by remember { mutableStateOf<SceneView?>(null) }
    var orbitHomePosition by remember { mutableStateOf(Position(x = 0f, y = 0f, z = 4f)) }
    var orbitTargetPosition by remember { mutableStateOf(Position(x = 0f, y = 0f, z = 0f)) }

    // Shared Filament/SceneView components (proper lighting, camera, rotation/pinch)
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
    val cameraManipulator = rememberCameraManipulator(
        orbitHomePosition = orbitHomePosition,
        targetPosition = orbitTargetPosition
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BodySceneBackgroundColor)
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
                    cameraManipulator = cameraManipulator,
                    sharedActivity = context as? androidx.activity.ComponentActivity,
                    sharedLifecycle = lifecycle
                ).also { sceneViewRef = it }
            },
            update = { sceneView ->
                sceneView.lifecycle = lifecycle
                sceneView.cameraManipulator = cameraManipulator
                modelNode?.let { node ->
                    if (node !in sceneView.childNodes) {
                        sceneView.addChildNode(node)
                    }
                }
            }
        )

        error?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

    // Load GLB from assets
    LaunchedEffect(sceneViewRef) {
        val sceneView = sceneViewRef ?: return@LaunchedEffect
        if (modelNode != null) return@LaunchedEffect

        isLoading = true
        error = null

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
            // Filament/gltfio typically require engine thread (main) for loading.
            withContext(Dispatchers.Main.immediate) {
                modelLoader.createModelInstance(BODY_MODEL_ASSET_PATH)
            }
        }.fold(
            onSuccess = { instance ->
                error = null
                val node = ModelNode(
                    modelInstance = instance,
                    autoAnimate = false,
                    scaleToUnits = 1.0f,
                    centerOrigin = Position(x = 0f, y = 0f, z = 0f)
                )

                // Auto-fit camera: stable 3/4 view across models with different pivots/sizes.
                val width = node.extents.x.coerceAtLeast(0.1f)
                val height = node.extents.y.coerceAtLeast(0.1f)
                val depth = node.extents.z.coerceAtLeast(0.1f)
                val cameraDistance = maxOf(
                    height * 1.9f,
                    width * 2.2f,
                    depth * 2.8f,
                    2.0f
                )
                val targetY = (height * 0.08f).coerceIn(-0.2f, height * 0.4f)
                val eyeX = (width * 0.08f).coerceAtLeast(0.02f)
                val eyeY = targetY + height * 0.10f

                orbitTargetPosition = Position(x = 0f, y = targetY, z = 0f)
                orbitHomePosition = Position(x = eyeX, y = eyeY, z = cameraDistance)
                cameraNode.position = orbitHomePosition

                modelNode = node
                sceneView.addChildNode(node)
            },
            onFailure = { e ->
                error = "Failed to load model: ${e.message}"
            }
        )
        isLoading = false
    }

    // Dev morph tuning for male_asian4.glb via morph "bell_fat" (index 0).
    LaunchedEffect(modelNode, devTuning) {
        val node = modelNode ?: return@LaunchedEffect
        val tuning = devTuning ?: BodyDevTuning()

        // male_asian4.glb -> index 0 is "bell_fat".
        val morphWeights = floatArrayOf(tuning.bellyFat.coerceIn(0f, 1f))
        runCatching {
            node.setMorphWeights(morphWeights, BELLY_FAT_MORPH_INDEX)
        }.onSuccess {
            Log.d(
                BODY_DEV_LOG_TAG,
                "setMorphWeights success index [$BELLY_FAT_MORPH_INDEX]=$BELLY_FAT_MORPH_NAME " +
                        morphWeights.joinToString(prefix = "[", postfix = "]")
            )
        }.onFailure { throwable ->
            Log.w(
                BODY_DEV_LOG_TAG,
                "setMorphWeights failed for index [$BELLY_FAT_MORPH_INDEX]=$BELLY_FAT_MORPH_NAME: ${throwable.message}",
                throwable
            )
        }
    }
}

@Composable
private fun AnalysisPanel(
    uiState: BodyUiState,
    onEditClick: () -> Unit,
    isDevMode: Boolean,
    devBellyFat: Float,
    onDevBellyFatChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
            .verticalScroll(scrollState)
            .padding(BodyDimens.panelPadding)
    ) {
        if (isDevMode) {
            DevControlsCard(
                bellyFat = devBellyFat,
                onBellyFatChanged = onDevBellyFatChanged
            )
            Spacer(modifier = Modifier.height(BodyDimens.spacingMedium))
        }

        Text(
            text = "ANALYSIS",
            style = MaterialTheme.typography.labelSmall,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Quick Stats",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(BodyDimens.spacingMedium))
        MetricCard(
            label = "Height",
            value = uiState.height,
            unit = uiState.heightUnit
        )
        Spacer(modifier = Modifier.height(BodyDimens.spacingMedium))
        MetricCard(
            label = "Weight",
            value = uiState.weight,
            unit = uiState.weightUnit,
            progress = uiState.weightProgress,
            progressColor = BodyPrimaryColor
        )
        Spacer(modifier = Modifier.height(BodyDimens.spacingMedium))
        MetricCard(
            label = "Body Fat %",
            value = uiState.bodyFat,
            unit = "%",
            progress = uiState.bodyFatProgress,
            progressColor = BodyEmeraldColor
        )
        Spacer(modifier = Modifier.height(BodyDimens.spacingMedium))
        MetricCard(
            label = "Muscle Mass",
            value = uiState.muscleMass,
            unit = uiState.muscleMassUnit,
            progress = uiState.muscleMassProgress,
            progressColor = BodyPrimaryColor
        )
        Spacer(modifier = Modifier.height(BodyDimens.spacingMedium))
        BmiCard(
            value = uiState.bmi,
            status = uiState.bmiStatus,
            scalePosition = uiState.bmiScalePosition
        )
        Spacer(modifier = Modifier.height(BodyDimens.spacingLarge))
        EditProfileButton(
            onClick = onEditClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun DevControlsCard(
    bellyFat: Float,
    onBellyFatChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                RoundedCornerShape(BodyDimens.cornerMedium)
            )
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                RoundedCornerShape(BodyDimens.cornerMedium)
            )
            .padding(BodyDimens.cardPadding)
    ) {
        Text(
            text = "DEV MODE",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(BodyDimens.spacingSmall))
        DevSliderRow(
            label = "Belly Fat (bell_fat, index 0)",
            value = bellyFat,
            onValueChanged = onBellyFatChanged
        )
    }
}

@Composable
private fun DevSliderRow(
    label: String,
    value: Float,
    onValueChanged: (Float) -> Unit
) {
    Text(
        text = "$label ${(value * 100f).toInt()}%",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
    Slider(
        value = value,
        onValueChange = onValueChanged,
        valueRange = 0f..1f
    )
}

@Composable
private fun MetricCard(
    label: String,
    value: String,
    unit: String,
    progress: Float? = null,
    progressColor: Color = BodyPrimaryColor,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                RoundedCornerShape(BodyDimens.cornerMedium)
            )
            .padding(BodyDimens.cardPadding)
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(BodyDimens.spacingTiny))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value.ifEmpty { "--" },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (label.equals(
                        "Height",
                        ignoreCase = true
                    )
                ) BodyPrimaryColor else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(BodyDimens.spacingTiny))
            Text(
                text = unit,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        progress?.let { p ->
            Spacer(modifier = Modifier.height(BodyDimens.spacingSmall))
            LinearProgressIndicator(
                progress = { p.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(BodyDimens.progressBarHeight),
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}

@Composable
private fun BmiCard(
    value: String,
    status: String?,
    scalePosition: Float?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                RoundedCornerShape(BodyDimens.cornerMedium)
            )
            .border(
                1.dp,
                BodyPrimaryColor.copy(alpha = 0.2f),
                RoundedCornerShape(BodyDimens.cornerMedium)
            )
            .padding(BodyDimens.cardPadding)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "BMI".uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            status?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = BodyEmeraldColor,
                    modifier = Modifier
                        .background(
                            BodyEmeraldColor.copy(alpha = 0.15f),
                            RoundedCornerShape(BodyDimens.cornerXLarge)
                        )
                        .padding(
                            horizontal = BodyDimens.spacingSmall,
                            vertical = BodyDimens.spacingTiny
                        )
                )
            }
        }
        Spacer(modifier = Modifier.height(BodyDimens.spacingTiny))
        Text(
            text = value.ifEmpty { "--" },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        scalePosition?.let { pos ->
            Spacer(modifier = Modifier.height(BodyDimens.spacingSmall))
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(BodyDimens.progressBarHeight)
            ) {
                val widthPx = maxWidth
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(BodyDimens.progressBarHeight)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(BodyAmberColor.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(BodyEmeraldColor)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(BodyRoseColor.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = widthPx * pos.coerceIn(0f, 1f) - 2.dp)
                        .size(4.dp, 8.dp)
                        .background(
                            MaterialTheme.colorScheme.onSurface,
                            RoundedCornerShape(2.dp)
                        )
                )
            }
        }
    }
}

@Composable
private fun EditProfileButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BodyPrimaryColor.copy(alpha = 0.15f),
            contentColor = BodyPrimaryColor
        ),
        shape = RoundedCornerShape(BodyDimens.cornerLarge)
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(BodyDimens.spacingSmall))
        Text(
            text = "Edit Profile",
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun BodyBottomBar(
    selectedTab: BodyTab,
    onTabSelected: (BodyTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(BodyDimens.bottomBarHeight)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = BodyDimens.panelPadding),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BodyTab.values().forEach { tab ->
                val selected = selectedTab == tab
                Column(
                    modifier = Modifier
                        .clickable { onTabSelected(tab) }
                        .padding(BodyDimens.spacingTiny)
                        .then(
                            if (selected) Modifier
                                .background(
                                    BodyPrimaryColor.copy(alpha = 0.15f),
                                    RoundedCornerShape(BodyDimens.cornerXLarge)
                                )
                                .padding(
                                    horizontal = BodyDimens.spacingSmall,
                                    vertical = BodyDimens.spacingTiny
                                )
                            else Modifier
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        tint = if (selected) BodyPrimaryColor else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(BodyDimens.spacingTiny))
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = if (selected) BodyPrimaryColor else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

enum class BodyTab(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Body(label = "Body", icon = Icons.Default.Person),
    Nutrition(label = "Nutrition", icon = Icons.Default.Restaurant),
    Workout(label = "Workout", icon = Icons.Default.FitnessCenter),
    Progress(label = "Progress", icon = Icons.Outlined.TrendingUp)
}

private fun readMaxJointCountFromGlb(context: android.content.Context, assetPath: String): Int? {
    val data = runCatching { context.assets.open(assetPath).use { it.readBytes() } }.getOrNull()
        ?: return null
    if (data.size < 20) return null

    val header = ByteBuffer.wrap(data, 0, 12).order(ByteOrder.LITTLE_ENDIAN)
    val magic = header.int
    if (magic != 0x46546C67) return null // "glTF"

    var offset = 12
    while (offset + 8 <= data.size) {
        val chunkHeader = ByteBuffer.wrap(data, offset, 8).order(ByteOrder.LITTLE_ENDIAN)
        val chunkLength = chunkHeader.int
        val chunkType = chunkHeader.int
        offset += 8

        if (chunkLength < 0 || offset + chunkLength > data.size) return null
        if (chunkType == 0x4E4F534A) { // "JSON"
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
