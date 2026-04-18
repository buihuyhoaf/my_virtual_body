package com.hoabui.virtualbody3d.ui.common_ui.organism.workoutcalendar

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.calendar.WORKOUT_CALENDAR_FALLBACK_DRAWABLE_NAME
import com.hoabui.virtualbody3d.domain.model.calendar.WorkoutCalendarExerciseLineUiModel
import com.hoabui.virtualbody3d.domain.model.common.ImageSource
import com.hoabui.virtualbody3d.domain.repository.ResourceProvider
import com.hoabui.virtualbody3d.ui.common_ui.atom.card.GCard
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.common_ui.image.LocalResourceProvider
import com.hoabui.virtualbody3d.ui.exerciselibrary.model.toCoilModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.model.toExerciseLibraryCardImage
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.tokens.GymToken
import com.hoabui.virtualbody3d.ui.theme.tokens.component.GSurfaceTreatment
import com.hoabui.virtualbody3d.ui.theme.tokens.component.WorkoutCalendarTokens
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged

private enum class SwipeDeleteAnchor { Closed, Open }

@Composable
fun WorkoutCalendarDayExerciseListOrganism(
    selectedDate: LocalDate,
    lines: List<WorkoutCalendarExerciseLineUiModel>,
    modifier: Modifier = Modifier,
    openSwipeRowId: Long? = null,
    pendingSwipeCloseRowId: Long? = null,
    playSwipeHintNudge: Boolean = false,
    onSwipeRowOpened: (Long) -> Unit = {},
    onSwipeRowSettledClosed: (Long) -> Unit = {},
    onConsumePendingSwipeClose: (Long) -> Unit = {},
    onDeleteAffordanceClick: (rowId: Long, exerciseName: String) -> Unit = { _, _ -> },
    onSwipeHintConsumed: () -> Unit = {},
) {
    val token = GymTheme.token
    val cal = token.workoutCalendar
    val locale =
        LocalConfiguration.current.locales.get(0) ?: Locale.getDefault()
    val headerFormat =
        DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale)
    Column(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = cal.exerciseListHeaderToListGap),
        ) {
            WorkoutCalendarSectionLabel(text = selectedDate.format(headerFormat))
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(token.spacing.none),
            verticalArrangement = Arrangement.spacedBy(cal.exerciseItemListGap),
        ) {
            if (lines.isEmpty()) {
                item(key = "empty") {
                    GText(
                        text = stringResource(R.string.workout_calendar_empty_day),
                        style = token.typography.bodyMedium,
                        color = token.colors.textMuted,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            } else {
                itemsIndexed(
                    items = lines,
                    key = { _, line -> line.rowId },
                ) { index, line ->
                    SwipeToDeleteExerciseRow(
                        line = line,
                        cal = cal,
                        token = token,
                        openSwipeRowId = openSwipeRowId,
                        pendingSwipeCloseRowId = pendingSwipeCloseRowId,
                        playSwipeHintNudge = playSwipeHintNudge && index == 0,
                        onSwipeRowOpened = onSwipeRowOpened,
                        onSwipeRowSettledClosed = onSwipeRowSettledClosed,
                        onConsumePendingSwipeClose = onConsumePendingSwipeClose,
                        onDeleteAffordanceClick = onDeleteAffordanceClick,
                        onSwipeHintConsumed = onSwipeHintConsumed,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SwipeToDeleteExerciseRow(
    line: WorkoutCalendarExerciseLineUiModel,
    cal: WorkoutCalendarTokens,
    token: GymToken,
    openSwipeRowId: Long?,
    pendingSwipeCloseRowId: Long?,
    playSwipeHintNudge: Boolean,
    onSwipeRowOpened: (Long) -> Unit,
    onSwipeRowSettledClosed: (Long) -> Unit,
    onConsumePendingSwipeClose: (Long) -> Unit,
    onDeleteAffordanceClick: (rowId: Long, exerciseName: String) -> Unit,
    onSwipeHintConsumed: () -> Unit,
) {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val anchoredState = remember { AnchoredDraggableState(SwipeDeleteAnchor.Closed) }
    val snapSpec = tween<Float>(
        durationMillis = token.motion.duration.standard,
        easing = token.motion.easing.standard,
    )
    val fling = AnchoredDraggableDefaults.flingBehavior(
        state = anchoredState,
        positionalThreshold = { distance ->
            distance * LayoutSwipeFractions.POSITIONAL_THRESHOLD_FRACTION
        },
        animationSpec = snapSpec,
    )

    // Apply anchors during the composition apply phase (before layout). LaunchedEffect runs too late:
    // offset { requireOffset() } crashes on first layout because offset is still NaN.
    DisposableEffect(
        cal.swipeDeleteTrackWidth,
        layoutDirection,
        density.density,
        density.fontScale,
    ) {
        val w = with(density) { cal.swipeDeleteTrackWidth.toPx() }
        val openX =
            if (layoutDirection == LayoutDirection.Rtl) w else -w
        anchoredState.updateAnchors(
            DraggableAnchors {
                SwipeDeleteAnchor.Closed at 0f
                SwipeDeleteAnchor.Open at openX
            },
        )
        onDispose { }
    }

    LaunchedEffect(anchoredState, line.rowId) {
        var skipInitial = true
        snapshotFlow { anchoredState.settledValue }
            .distinctUntilChanged()
            .collect { anchor ->
                if (skipInitial) {
                    skipInitial = false
                    return@collect
                }
                when (anchor) {
                    SwipeDeleteAnchor.Open -> onSwipeRowOpened(line.rowId)
                    SwipeDeleteAnchor.Closed -> onSwipeRowSettledClosed(line.rowId)
                }
            }
    }

    LaunchedEffect(openSwipeRowId, line.rowId, anchoredState) {
        if (openSwipeRowId != null &&
            openSwipeRowId != line.rowId &&
            anchoredState.currentValue == SwipeDeleteAnchor.Open
        ) {
            anchoredState.animateTo(SwipeDeleteAnchor.Closed)
        }
    }

    LaunchedEffect(pendingSwipeCloseRowId, line.rowId, anchoredState) {
        if (pendingSwipeCloseRowId == line.rowId) {
            anchoredState.animateTo(SwipeDeleteAnchor.Closed)
            onConsumePendingSwipeClose(line.rowId)
        }
    }

    LaunchedEffect(playSwipeHintNudge, anchoredState, cal, token) {
        if (!playSwipeHintNudge) return@LaunchedEffect
        delay(LayoutSwipeFractions.NUDGE_START_DELAY_MS)
        val openX = openAnchorOffsetOrNull(anchoredState) ?: return@LaunchedEffect
        val peek = openX * cal.swipeDeleteNudgeFraction
        var applied = 0f
        animate(
            initialValue = 0f,
            targetValue = peek,
            animationSpec = tween(
                durationMillis = token.motion.duration.standard,
                easing = token.motion.easing.emphasized,
            ),
        ) { v, _ ->
            val delta = v - applied
            applied = v
            anchoredState.dispatchRawDelta(delta)
        }
        animate(
            initialValue = applied,
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = token.motion.duration.long,
                easing = token.motion.easing.decelerate,
            ),
        ) { v, _ ->
            val delta = v - applied
            applied = v
            anchoredState.dispatchRawDelta(delta)
        }
        anchoredState.animateTo(SwipeDeleteAnchor.Closed)
        onSwipeHintConsumed()
    }

    val clipShape = RoundedCornerShape(token.radius.sm)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(clipShape),
    ) {
        SwipeDeleteUnderlay(
            cal = cal,
            token = token,
            modifier = Modifier.matchParentSize(),
            onDeleteClick = { onDeleteAffordanceClick(line.rowId, line.title) },
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset {
                    val px = anchoredState.offset
                    val x = if (px.isNaN()) 0f else px
                    IntOffset(x = x.roundToInt(), y = 0)
                }
                .anchoredDraggable(
                    state = anchoredState,
                    orientation = Orientation.Horizontal,
                    enabled = true,
                    interactionSource = remember { MutableInteractionSource() },
                    flingBehavior = fling,
                ),
        ) {
            WorkoutCalendarExerciseRow(
                line = line,
                cal = cal,
                token = token,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun openAnchorOffsetOrNull(state: AnchoredDraggableState<SwipeDeleteAnchor>): Float? {
    val x = state.anchors.positionOf(SwipeDeleteAnchor.Open)
    return x.takeUnless { it.isNaN() }
}

private object LayoutSwipeFractions {
    const val POSITIONAL_THRESHOLD_FRACTION = 0.35f
    const val NUDGE_START_DELAY_MS = 400L
}

@Composable
private fun SwipeDeleteUnderlay(
    cal: WorkoutCalendarTokens,
    token: GymToken,
    modifier: Modifier = Modifier,
    onDeleteClick: () -> Unit,
) {
    val deleteLabel = stringResource(R.string.workout_calendar_swipe_delete)
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(token.colors.error)
            .clickable(role = Role.Button, onClick = onDeleteClick)
            .padding(horizontal = cal.exerciseItemInnerPadding),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Outlined.Delete,
            contentDescription = deleteLabel,
            modifier = Modifier.size(cal.swipeDeleteIconSize),
            tint = token.colors.onError,
        )
        GText(
            text = deleteLabel,
            style = token.typography.labelLarge,
            color = token.colors.onError,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(start = cal.swipeDeleteIconLabelGap),
        )
    }
}

@Composable
private fun WorkoutCalendarExerciseRow(
    line: WorkoutCalendarExerciseLineUiModel,
    cal: WorkoutCalendarTokens,
    token: GymToken,
) {
    val resourceProvider = LocalResourceProvider.current
    val thumbShape = RoundedCornerShape(token.radius.sm)
    val coilModel = remember(line.rowId, line.image) {
        line.image.toExerciseLibraryCardImage().toCoilModel(resourceProvider)
    }
    val fallbackPainter = painterResource(R.drawable.body_unsplash)
    GCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = null,
        shape = RoundedCornerShape(token.radius.sm),
        containerColor = token.colors.surface,
        border = BorderStroke(token.borderWidth.hairline, token.colors.borderSubtle),
        treatment = GSurfaceTreatment.Flat,
        elevation = token.elevation.level0,
        contentModifier = Modifier.padding(cal.exerciseItemInnerPadding),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(cal.exerciseRowThumbnailToTextGap),
        ) {
            AsyncImage(
                model = coilModel,
                contentDescription = line.title,
                modifier = Modifier
                    .size(cal.exerciseRowThumbnailSize)
                    .clip(thumbShape)
                    .background(token.colors.surfaceSubtle),
                contentScale = ContentScale.Crop,
                placeholder = fallbackPainter,
                error = fallbackPainter,
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                if (line.startTimeLabel.isNotBlank() || line.caloriesLabel.isNotBlank()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = token.spacing.xs),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (line.startTimeLabel.isNotBlank()) {
                            GText(
                                text = line.startTimeLabel,
                                style = token.typography.labelSmall,
                                color = token.colors.textSecondary,
                            )
                        }
                        if (line.caloriesLabel.isNotBlank()) {
                            GText(
                                text = line.caloriesLabel,
                                style = token.typography.labelSmall,
                                color = token.colors.primary,
                            )
                        }
                    }
                }
                GText(
                    text = line.title,
                    style = workoutCalendarExerciseNameStyle(token),
                    color = token.colors.textPrimary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = cal.exerciseRowTitleToMetricsGap),
                )
                if (line.setBreakdownLabel.isNotBlank()) {
                    GText(
                        text = line.setBreakdownLabel,
                        style = workoutCalendarSupportingBodyStyle(token),
                        color = token.colors.textSecondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = cal.exerciseRowMetricsToStatusGap),
                    )
                }
                if (line.statusLabel.isNotBlank()) {
                    GText(
                        text = line.statusLabel,
                        style = workoutCalendarSupportingLabelStyle(token),
                        color = token.colors.textMuted,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = cal.exerciseRowMetricsToStatusGap),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DayListPreviewLight() {
    val context = LocalContext.current
    val previewResourceProvider = remember {
        object : ResourceProvider {
            override fun drawableResId(name: String): Int? {
                val id = context.resources.getIdentifier(name, "drawable", context.packageName)
                return id.takeIf { it != 0 }
            }
        }
    }
    CompositionLocalProvider(LocalResourceProvider provides previewResourceProvider) {
        GymTheme(darkTheme = false) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = GymTheme.token.colors.surface,
            ) {
                WorkoutCalendarDayExerciseListOrganism(
                    selectedDate = LocalDate.of(2024, 4, 10),
                    lines = listOf(
                        WorkoutCalendarExerciseLineUiModel(
                            rowId = 1L,
                            title = "Squat",
                            startTimeLabel = "06:30",
                            setBreakdownLabel = "3 Sets • 95 kg x 10",
                            caloriesLabel = "🔥 25 kcal",
                            statusLabel = "Scheduled",
                            image = ImageSource.LocalResource(WORKOUT_CALENDAR_FALLBACK_DRAWABLE_NAME),
                        ),
                        WorkoutCalendarExerciseLineUiModel(
                            rowId = 2L,
                            title = "Romanian deadlift",
                            startTimeLabel = "18:00",
                            setBreakdownLabel = "4 Sets • 75 kg x 8",
                            caloriesLabel = "🔥 18 kcal",
                            statusLabel = "Completed",
                            image = ImageSource.LocalResource(WORKOUT_CALENDAR_FALLBACK_DRAWABLE_NAME),
                        ),
                    ),
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DayListPreviewDark() {
    val context = LocalContext.current
    val previewResourceProvider = remember {
        object : ResourceProvider {
            override fun drawableResId(name: String): Int? {
                val id = context.resources.getIdentifier(name, "drawable", context.packageName)
                return id.takeIf { it != 0 }
            }
        }
    }
    CompositionLocalProvider(LocalResourceProvider provides previewResourceProvider) {
        GymTheme(darkTheme = true) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = GymTheme.token.colors.surface,
            ) {
                WorkoutCalendarDayExerciseListOrganism(
                    selectedDate = LocalDate.of(2024, 4, 10),
                    lines = emptyList(),
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
