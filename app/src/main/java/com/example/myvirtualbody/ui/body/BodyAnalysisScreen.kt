package com.example.myvirtualbody.ui.body

import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.myvirtualbody.Constants.BODY_DEV_LOG_TAG
import com.example.myvirtualbody.Constants.BODY_DEV_MODE
import com.example.myvirtualbody.Constants.BODY_MODEL_ASSET_PATH
import com.example.myvirtualbody.Constants.FILAMENT_MAX_BONES
import com.example.myvirtualbody.R
import com.example.myvirtualbody.ui.theme.*
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
    modifier: Modifier = Modifier
) {
    val bodyScore = ((uiState.bmiScalePosition ?: 0.76f) * 100f).toInt().coerceIn(0, 100)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            BodyTopBar(
                title = title,
                onBackClick = onBackClick,
                onSettingsClick = onSettingsClick,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            )
        },
        bottomBar = {
            BodyBottomBar(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            BodyPreviewSection(
                uiState = uiState,
                bodyScore = bodyScore,
                modifier = Modifier
                    .weight(0.6f)
            )
            AnalysisPanel(
                uiState = uiState,
                bodyScore = bodyScore,
                modifier = Modifier
                    .weight(0.4f)
                    .offset(y = (-10).dp)
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
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color.White.copy(alpha = 0.8f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f))
        ) {
            IconButton(onClick = onBackClick, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = BodyPrimary,
            shadowElevation = 10.dp
        ) {
            IconButton(onClick = onSettingsClick, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.Default.PersonSearch,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun BodyPreviewSection(
    uiState: BodyUiState,
    bodyScore: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.radialGradient(
                    center = Offset(0.5f, 0.5f),
                    radius = 1.2f,
                    colors = listOf(BodyPrimaryTint, MaterialTheme.colorScheme.surface)
                )
            )
    ) {
        BodyModelPreview(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.24f)
                        )
                    )
                )
        )
        BodyScoreChip(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 28.dp, start = 16.dp),
            score = bodyScore
        )
        FloatingMetricChip(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 84.dp, start = 16.dp),
            icon = Icons.Default.Straighten,
            value = formatMeasurement(uiState.height, uiState.heightUnit)
        )
        FloatingMetricChip(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 140.dp, start = 16.dp),
            icon = Icons.Default.MonitorWeight,
            value = formatMeasurement(uiState.weight, uiState.weightUnit)
        )
        FloatingMetricChip(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 28.dp, end = 16.dp),
            icon = Icons.Default.Opacity,
            value = formatPercent(uiState.bodyFat)
        )
        FloatingMetricChip(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 84.dp, end = 16.dp),
            icon = Icons.Default.FitnessCenter,
            value = formatMeasurement(uiState.muscleMass, uiState.muscleMassUnit)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = BodyDimens.spacingXLarge)
                .widthIn(max = 192.dp)
                .height(BodyDimens.spacingSmall)
                .background(
                    BodyPreviewTrack,
                    RoundedCornerShape(BodyDimens.cornerSmall)
                )
        )
    }
}

private fun formatMeasurement(value: String, unit: String): String {
    val trimmedValue = value.trim()
    val trimmedUnit = unit.trim()
    return listOf(trimmedValue, trimmedUnit).filter { it.isNotEmpty() }.joinToString(" ")
}

private fun formatPercent(value: String): String {
    val trimmed = value.trim()
    if (trimmed.isBlank()) return ""
    return if (trimmed.endsWith("%")) trimmed else "$trimmed%"
}

/**
 * 3D body model preview using SceneView.
 * Integrated SceneView for 3D GLB model rendering with touch controls.
 */
@Composable
private fun BodyModelPreview(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    var isLoading by remember { mutableStateOf(true) }
    var modelNode by remember { mutableStateOf<ModelNode?>(null) }
    var sceneViewRef by remember { mutableStateOf<SceneView?>(null) }
    // Initial yaw 180° so model's front (glTF -Z) faces the camera for frontal view.
    var modelYaw by remember { mutableFloatStateOf(180f) }
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
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BodySceneBackground)
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

                                    // Accumulate yaw so model can rotate full 360 degrees.
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

    // Load GLB from assets
    LaunchedEffect(sceneViewRef) {
        val sceneView = sceneViewRef ?: return@LaunchedEffect
        if (modelNode != null) return@LaunchedEffect

        isLoading = true

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
                val node = ModelNode(
                    modelInstance = instance,
                    autoAnimate = false,
                    scaleToUnits = 1.0f,
                    centerOrigin = null
                )
                // Model extents = full size; center Y = half height so feet (bottom of bbox) sit at y = 0.
                val modelCenterY = (node.extents.y / 2f).coerceIn(0.01f, 1e6f)
                node.position = Position(x = 0f, y = modelCenterY, z = 0f)

                // Frame entire model: calculate distance so full bounding box fits in frustum (frontal view).
                val sizeX = node.size.x.coerceIn(0.01f, 1e6f)
                val sizeY = node.size.y.coerceIn(0.01f, 1e6f)
                val sizeZ = node.size.z.coerceIn(0.01f, 1e6f)
                
                // Use vertical FOV (typically 45°) to calculate distance needed to fit model height
                val verticalFovDeg = 45f
                val halfFovRad = Math.toRadians((verticalFovDeg / 2f).toDouble()).toFloat()
                
                // Calculate distance needed to fit height (vertical dimension)
                val fitDistanceByHeight = (sizeY * 0.5f) / tan(halfFovRad)
                
                // Calculate distance needed to fit width (horizontal dimension)
                // Assuming aspect ratio ~1:1, horizontal FOV ≈ vertical FOV
                val fitDistanceByWidth = (sizeX * 0.5f) / tan(halfFovRad)
                
                // Use the larger distance to ensure both width and height fit
                val fitDistance = max(fitDistanceByHeight, fitDistanceByWidth)
                
                // Add padding (1.5x) to ensure full model visible including extremities
                val cameraDistance = (fitDistance * 1.5f).coerceIn(2f, 300f)

                // Frontal view: camera on -Z looking at +Z; model rotated 180° so front faces camera.
                val lookAt = Position(x = 0f, y = modelCenterY, z = 0f)
                orbitTargetPosition = lookAt
                orbitHomePosition = Position(x = 0f, y = modelCenterY, z = -cameraDistance)
                cameraNode.position = orbitHomePosition
                cameraNode.lookAt(orbitTargetPosition)

                // Apply initial frontal yaw so the model faces the camera on first load.
                node.rotation = Rotation(0f, 180f, 0f)

                if (BODY_DEV_MODE) {
                    val bounds = withContext(Dispatchers.IO) { parseGlbSceneBounds(context, BODY_MODEL_ASSET_PATH) }
                    if (bounds != null) {
                        Log.d(
                            BODY_DEV_LOG_TAG,
                            "GLB bounds: w=${bounds.width} h=${bounds.height} d=${bounds.depth} centerY=${bounds.centerY}; " +
                                "runtime size=${node.size.x}x${node.size.y}x${node.size.z} cameraZ=-$cameraDistance"
                        )
                    }
                }

                modelNode = node
                sceneView.addChildNode(node)
            },
            onFailure = { }
        )
        isLoading = false
    }
}

@Composable
private fun LegacyAnalysisPanel(
    uiState: BodyUiState,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, TopBarBorder)
            .verticalScroll(scrollState)
            .padding(BodyDimens.panelPadding)
    ) {
        Text(
            text = stringResource(R.string.body_analysis),
            style = MaterialTheme.typography.labelSmall,
            letterSpacing = 1.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = stringResource(R.string.body_quick_stats),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(BodyDimens.spacingMedium))
        MetricCard(
            label = stringResource(R.string.body_height),
            value = uiState.height,
            unit = uiState.heightUnit,
            highlightValue = true
        )
        Spacer(modifier = Modifier.height(BodyDimens.spacingMedium))
        MetricCard(
            label = stringResource(R.string.body_weight),
            value = uiState.weight,
            unit = uiState.weightUnit,
            highlightValue = false,
            progress = uiState.weightProgress,
            progressColor = BodyPrimary
        )
        Spacer(modifier = Modifier.height(BodyDimens.spacingMedium))
        MetricCard(
            label = stringResource(R.string.body_body_fat),
            value = uiState.bodyFat,
            unit = stringResource(R.string.body_unit_percent),
            highlightValue = false,
            progress = uiState.bodyFatProgress,
            progressColor = BodyEmerald
        )
        Spacer(modifier = Modifier.height(BodyDimens.spacingMedium))
        MetricCard(
            label = stringResource(R.string.body_muscle_mass),
            value = uiState.muscleMass,
            unit = uiState.muscleMassUnit,
            highlightValue = false,
            progress = uiState.muscleMassProgress,
            progressColor = BodyPrimary
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
private fun MetricCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    unit: String,
    highlightValue: Boolean = false,
    progress: Float? = null,
    progressColor: Color = BodyPrimary,
) {
    Column(
        modifier = modifier
            .background(
                CardBackground,
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
                text = value.ifEmpty { stringResource(R.string.body_placeholder) },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (highlightValue) BodyPrimary else MaterialTheme.colorScheme.onSurface
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
                CardBackground,
                RoundedCornerShape(BodyDimens.cornerMedium)
            )
            .border(
                1.dp,
                BodyPrimaryBorder,
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
                text = stringResource(R.string.body_bmi).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            status?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = BodyEmerald,
                    modifier = Modifier
                        .background(
                            BodyEmeraldLight,
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
            text = value.ifEmpty { stringResource(R.string.body_placeholder) },
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
                            .background(BodyAmberLight, RoundedCornerShape(2.dp))
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(BodyEmerald)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(BodyRoseLight, RoundedCornerShape(2.dp))
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
            containerColor = BodyPrimaryLight,
            contentColor = BodyPrimary
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
            text = stringResource(R.string.body_edit_profile),
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
            .border(1.dp, TopBarBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = BodyDimens.panelPadding),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BodyTab.entries.forEach { tab ->
                val selected = selectedTab == tab
                Column(
                    modifier = Modifier
                        .clickable { onTabSelected(tab) }
                        .padding(BodyDimens.spacingTiny)
                        .then(
                            if (selected) Modifier
                                .background(
                                    BodyPrimaryLight,
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
                        contentDescription = stringResource(tab.labelResId),
                        tint = if (selected) BodyPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(BodyDimens.spacingTiny))
                    Text(
                        text = stringResource(tab.labelResId),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        color = if (selected) BodyPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

enum class BodyTab(
    @param:androidx.annotation.StringRes val labelResId: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Body(labelResId = R.string.tab_body, icon = Icons.Default.Person),
    Nutrition(labelResId = R.string.tab_nutrition, icon = Icons.Default.Restaurant),
    Workout(labelResId = R.string.tab_workout, icon = Icons.Default.FitnessCenter),
    Progress(labelResId = R.string.tab_progress, icon = Icons.Outlined.TrendingUp)
}

/**
 * Axis-aligned bounds read from glTF accessors (POSITION min/max).
 * Used to analyze GLB for camera framing; runtime [ModelNode.size] is the source of truth.
 */
private data class GlbSceneBounds(
    val minX: Float,
    val minY: Float,
    val minZ: Float,
    val maxX: Float,
    val maxY: Float,
    val maxZ: Float
) {
    val width: Float get() = maxX - minX
    val height: Float get() = maxY - minY
    val depth: Float get() = maxZ - minZ
    val centerY: Float get() = (minY + maxY) / 2f
}

/**
 * Parses the GLB JSON chunk and returns scene AABB from POSITION accessors (min/max).
 * Returns null if file is invalid or no POSITION accessors with min/max are found.
 */
private fun parseGlbSceneBounds(context: android.content.Context, assetPath: String): GlbSceneBounds? {
    val data = runCatching { context.assets.open(assetPath).use { it.readBytes() } }.getOrNull() ?: return null
    if (data.size < 20) return null
    val header = ByteBuffer.wrap(data, 0, 12).order(ByteOrder.LITTLE_ENDIAN)
    if (header.int != 0x46546C67) return null // "glTF"
    var offset = 12
    while (offset + 8 <= data.size) {
        val chunkHeader = ByteBuffer.wrap(data, offset, 8).order(ByteOrder.LITTLE_ENDIAN)
        val chunkLength = chunkHeader.int
        val chunkType = chunkHeader.int
        offset += 8
        if (chunkLength < 0 || offset + chunkLength > data.size) break
        if (chunkType != 0x4E4F534A) { offset += chunkLength; continue } // "JSON"
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
            val Mx = maxArr.optDouble(0).toFloat()
            val My = maxArr.optDouble(1).toFloat()
            val Mz = maxArr.optDouble(2).toFloat()
            if (mx < minX) minX = mx
            if (my < minY) minY = my
            if (mz < minZ) minZ = mz
            if (Mx > maxX) maxX = Mx
            if (My > maxY) maxY = My
            if (Mz > maxZ) maxZ = Mz
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
