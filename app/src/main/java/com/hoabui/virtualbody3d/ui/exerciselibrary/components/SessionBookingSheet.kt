package com.hoabui.virtualbody3d.ui.exerciselibrary.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.domain.model.exercise.InstantInterval
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButton
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButtonVariant
import com.hoabui.virtualbody3d.ui.common_ui.atom.icon.GIcon
import com.hoabui.virtualbody3d.ui.common_ui.atom.surface.GSurface
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.common_ui.image.LocalResourceProvider
import com.hoabui.virtualbody3d.ui.exerciselibrary.model.toCoilModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.BookingExerciseSummaryUi
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.SessionBookingPeriodId
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.SessionBookingPeriodUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.SessionBookingUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.TimeSlotCellUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.state.model.TimeSlotSelectionRangeRole
import com.hoabui.virtualbody3d.ui.theme.GymTheme
import com.hoabui.virtualbody3d.ui.theme.icons.ExerciseLibraryPhosphorIcons
import com.hoabui.virtualbody3d.ui.theme.tokens.component.GSurfaceTreatment
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private fun sessionBookingSheetTextStyle(base: TextStyle): TextStyle = base.merge(TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseLibrarySessionBookingSheetHost(
    booking: SessionBookingUiModel?,
    draftCount: Int,
    onDismissRequest: () -> Unit,
    onDateMillisSelected: (Long) -> Unit,
    onLocationSelected: (String) -> Unit,
    onSlotToggled: (LocalTime) -> Unit,
    onClearTimeSelection: () -> Unit,
    onConfirm: () -> Unit,
) {
    if (booking == null) return
    val token = GymTheme.token
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val sheetShape = remember(token.bodyAnalysis.exerciseLibraryBookingSheetTopCornerRadius) {
        RoundedCornerShape(
            topStart = token.bodyAnalysis.exerciseLibraryBookingSheetTopCornerRadius,
            topEnd = token.bodyAnalysis.exerciseLibraryBookingSheetTopCornerRadius,
        )
    }
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = sheetShape,
        containerColor = token.colors.surface,
        dragHandle = null,
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val configuration = LocalConfiguration.current
            val screenHeight = configuration.screenHeightDp.dp
            val sheetMaxHeight = minOf(
                maxHeight,
                screenHeight * token.bodyAnalysis.exerciseLibraryBookingSheetMaxHeightFraction,
            )
            SessionBookingSheetContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = sheetMaxHeight),
                booking = booking,
                draftCount = draftCount,
                onDismissClick = onDismissRequest,
                onDateMillisSelected = onDateMillisSelected,
                onLocationSelected = onLocationSelected,
                onSlotToggled = onSlotToggled,
                onClearTimeSelection = onClearTimeSelection,
                onConfirm = onConfirm,
            )
        }
    }
}

@Composable
private fun SessionBookingSheetContent(
    modifier: Modifier = Modifier,
    booking: SessionBookingUiModel,
    draftCount: Int,
    onDismissClick: () -> Unit,
    onDateMillisSelected: (Long) -> Unit,
    onLocationSelected: (String) -> Unit,
    onSlotToggled: (LocalTime) -> Unit,
    onClearTimeSelection: () -> Unit,
    onConfirm: () -> Unit,
) {
    val systemZone = Clock.systemDefaultZone().zone
    val token = GymTheme.token
    val scrollState = rememberScrollState()
    val slotRowState = rememberLazyListState()
    val sectionIcon = token.bodyAnalysis.exerciseLibraryBookingSectionIconSize
    val chipH = token.bodyAnalysis.exerciseLibraryBookingDateChipHeight
    val chipMinW = token.bodyAnalysis.exerciseLibraryBookingDateChipMinWidth
    val gridCellMinH = token.bodyAnalysis.exerciseLibraryBookingTimeGridCellMinHeight
    val slotChipMinW = token.bodyAnalysis.exerciseLibraryBookingTimeSlotHorizontalMinWidth
    val input = booking.input
    val dayLabelsLocale = Locale.getDefault()
    val shortDayFmt = remember(dayLabelsLocale) {
        DateTimeFormatter.ofPattern("EEE", dayLabelsLocale)
    }
    val selectedLocalDate =
        remember(input.selectedDateMillis, systemZone) {
            Instant.ofEpochMilli(input.selectedDateMillis).atZone(systemZone).toLocalDate()
        }
    val lifecycleOwner = LocalLifecycleOwner.current
    var resumeKey by remember { mutableIntStateOf(0) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                resumeKey++
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    val dayHorizon = remember(input.selectedDateMillis, resumeKey) {
        val today = LocalDate.now()
        (0 until SESSION_BOOKING_DAY_HORIZON).map { today.plusDays(it.toLong()) }
    }
    val selectedLocationName = booking.selectedLocationDisplayName
    var locationMenuExpanded by remember { mutableStateOf(false) }
    val confirmOk = booking.isBookingConfirmEnabled

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(
                start = token.spacing.md,
                end = token.spacing.md,
                top = token.spacing.sm,
                bottom = token.spacing.lg,
            ),
        verticalArrangement = Arrangement.spacedBy(token.spacing.md),
    ) {
        BookingSheetHeaderRow(
            title = stringResource(R.string.exercise_library_booking_title),
            subtitle = stringResource(R.string.exercise_library_cart_selected_count, draftCount),
            onDismiss = onDismissClick,
        )
        if (input.showSlotConflict) {
            GText(
                text = stringResource(R.string.exercise_library_booking_slot_conflict),
                style = sessionBookingSheetTextStyle(token.typography.bodySmall),
                color = token.colors.error,
            )
        }
        BookingSectionLabel(
            icon = ExerciseLibraryPhosphorIcons.detailCategory,
            label = stringResource(R.string.exercise_library_booking_exercises_in_session),
            iconSize = sectionIcon,
        )
        BookingExerciseSnapshotRow(
            exercises = input.bookingExerciseSnapshot,
        )
        BookingSectionLabel(
            icon = ExerciseLibraryPhosphorIcons.bookingCalendar,
            label = stringResource(R.string.exercise_library_cart_pick_date),
            iconSize = sectionIcon,
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(dayHorizon, key = { it.toString() }) { day ->
                BookingDateChipItem(
                    day = day,
                    selectedLocalDate = selectedLocalDate,
                    shortDayFmt = shortDayFmt,
                    chipMinW = chipMinW,
                    chipH = chipH,
                    onDateMillisSelected = onDateMillisSelected,
                )
            }
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.wrapContentWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
                ) {
                    GIcon(
                        imageVector = ExerciseLibraryPhosphorIcons.bookingMapPin,
                        contentDescription = null,
                        modifier = Modifier.size(sectionIcon),
                        tint = token.colors.textSecondary,
                    )
                    GText(
                        text = stringResource(R.string.exercise_library_booking_location),
                        style = sessionBookingSheetTextStyle(token.typography.labelLarge),
                        color = token.colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.width(token.spacing.spacingStep1))
                Spacer(modifier = Modifier.weight(1f))
                GSurface(
                    modifier = Modifier
                        .widthIn(max = token.bodyAnalysis.exerciseLibraryBookingLocationSelectorMaxWidth)
                        .wrapContentWidth(align = Alignment.End)
                        .clip(RoundedCornerShape(token.radius.md))
                        .clickable { locationMenuExpanded = true },
                    color = token.colors.surfaceSubtle,
                    shadowElevation = token.elevation.level0,
                    treatment = GSurfaceTreatment.Flat,
                    border = null,
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = token.spacing.sm,
                            vertical = token.spacing.sm,
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                    ) {
                        GText(
                            text = selectedLocationName,
                            style = sessionBookingSheetTextStyle(token.typography.labelLarge),
                            color = token.colors.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.End,
                        )
                    }
                }
            }
            DropdownMenu(
                expanded = locationMenuExpanded,
                onDismissRequest = { locationMenuExpanded = false },
            ) {
                booking.locations.forEach { loc ->
                    DropdownMenuItem(
                        text = {
                            GText(
                                text = loc.displayName,
                                style = sessionBookingSheetTextStyle(token.typography.bodyLarge),
                                color = token.colors.textPrimary,
                            )
                        },
                        onClick = {
                            onLocationSelected(loc.id)
                            locationMenuExpanded = false
                        },
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.weight(1f)) {
                BookingSectionLabel(
                    icon = ExerciseLibraryPhosphorIcons.bookingClock,
                    label = stringResource(R.string.exercise_library_booking_time),
                    iconSize = sectionIcon,
                )
            }
            GButton(
                text = stringResource(R.string.exercise_library_cart_clear_all),
                onClick = onClearTimeSelection,
                modifier = Modifier.padding(start = token.spacing.xs),
                variant = GButtonVariant.Ghost,
                contentColor = token.colors.error,
                enabled = input.selectedSlotStarts.isNotEmpty(),
            )
        }
        BookingPeriodJumpRow(
            periods = booking.bookingPeriods,
            periodStartIndex = booking.periodStartIndex,
            lazyListState = slotRowState,
        )
        LazyRow(
            state = slotRowState,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
        ) {
            items(
                items = booking.timeSlotCells,
                key = { it.slotStart.toString() },
            ) { cell ->
                TimeSlotGridItem(
                    cell = cell,
                    minHeight = gridCellMinH,
                    minWidth = slotChipMinW,
                    onSlotToggled = onSlotToggled,
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(token.spacing.sm),
        ) {
            GButton(
                text = stringResource(R.string.exercise_library_booking_cancel),
                onClick = onDismissClick,
                modifier = Modifier.weight(1f),
                variant = GButtonVariant.Outlined,
            )
            GButton(
                text = stringResource(R.string.exercise_library_booking_confirm),
                onClick = onConfirm,
                modifier = Modifier.weight(1f),
                enabled = confirmOk,
            )
        }
    }
}

@Composable
private fun BookingExerciseSnapshotRow(
    exercises: ImmutableList<BookingExerciseSummaryUi>,
) {
    val token = GymTheme.token
    val resourceProvider = LocalResourceProvider.current
    val thumb = token.bodyAnalysis.exerciseLibraryBookingStripThumbnailSize
    val gap = token.bodyAnalysis.exerciseLibraryBookingStripImageTextGap
    val itemW = token.bodyAnalysis.exerciseLibraryBookingStripItemWidth
    val shape = RoundedCornerShape(token.bodyAnalysis.upcomingExerciseChipImageCornerRadius)
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(token.spacing.sm),
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(
            items = exercises,
            key = { it.id },
        ) { ex ->
            Row(
                modifier = Modifier.width(itemW),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    model = ex.image.toCoilModel(resourceProvider),
                    contentDescription = ex.title,
                    modifier = Modifier
                        .size(thumb)
                        .clip(shape)
                        .background(token.colors.surfaceSubtle),
                    contentScale = ContentScale.Crop,
                )
                Spacer(modifier = Modifier.width(gap))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(token.spacing.xxs),
                ) {
                    GText(
                        text = ex.title,
                        style = sessionBookingSheetTextStyle(token.typography.labelLarge),
                        color = token.colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (ex.parametersSummary.isNotEmpty()) {
                        GText(
                            text = ex.parametersSummary,
                            style = sessionBookingSheetTextStyle(token.typography.labelSmall),
                            color = token.colors.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

@Composable
/**
 * Period shortcuts scroll the slot [LazyRow]. `LazyListState.animateScrollToItem` uses the framework
 * scroll animation (Compose Foundation does not yet expose a public [AnimationSpec] for this call on
 * our BOM). When an overload with a spec is available, wire `tween(durationMillis = token.motion.duration.long, easing = token.motion.easing.standard)` here.
 */
private fun BookingPeriodJumpRow(
    periods: ImmutableList<SessionBookingPeriodUiModel>,
    periodStartIndex: ImmutableMap<SessionBookingPeriodId, Int>,
    lazyListState: LazyListState,
) {
    val token = GymTheme.token
    val scope = rememberCoroutineScope()
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
        modifier = Modifier.fillMaxWidth(),
    ) {
        items(periods, key = { it.id }) { period ->
            val index = periodStartIndex[period.id] ?: 0
            GButton(
                text = stringResource(period.labelResId),
                onClick = {
                    scope.launch {
                        lazyListState.animateScrollToItem(index)
                    }
                },
                variant = GButtonVariant.Outlined,
            )
        }
    }
}

private const val SESSION_BOOKING_DAY_HORIZON = 14

@Composable
private fun BookingSheetHeaderRow(
    title: String,
    subtitle: String,
    onDismiss: () -> Unit,
) {
    val token = GymTheme.token
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            GText(
                text = title,
                style = sessionBookingSheetTextStyle(token.typography.titleMedium),
                color = token.colors.textPrimary,
            )
            GText(
                text = subtitle,
                style = sessionBookingSheetTextStyle(token.typography.bodySmall),
                color = token.colors.textSecondary,
            )
        }
        Box(
            modifier = Modifier
                .size(token.bodyAnalysis.exerciseLibraryQuickAddIconContainerSize)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true),
                    role = Role.Button,
                    onClick = onDismiss,
                ),
            contentAlignment = Alignment.Center,
        ) {
            GIcon(
                imageVector = ExerciseLibraryPhosphorIcons.bookingSheetClose,
                contentDescription = stringResource(R.string.exercise_library_booking_close_cd),
                modifier = Modifier.size(token.bodyAnalysis.heroSlimChipIconSize),
                tint = token.colors.textSecondary,
            )
        }
    }
}

@Composable
private fun BookingSectionLabel(
    icon: ImageVector,
    label: String,
    iconSize: Dp,
) {
    val token = GymTheme.token
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(token.spacing.xs),
    ) {
        GIcon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(iconSize),
            tint = token.colors.textSecondary,
        )
        GText(
            text = label,
            style = sessionBookingSheetTextStyle(token.typography.labelLarge),
            color = token.colors.textPrimary,
        )
    }
}

@Composable
private fun BookingDateChipItem(
    day: LocalDate,
    selectedLocalDate: LocalDate,
    shortDayFmt: DateTimeFormatter,
    chipMinW: Dp,
    chipH: Dp,
    onDateMillisSelected: (Long) -> Unit,
) {
    val systemZone = Clock.systemDefaultZone().zone
    val millis = remember(day, systemZone) {
        day.atStartOfDay(systemZone).toInstant().toEpochMilli()
    }
    val onClick = remember(day, onDateMillisSelected) {
        { onDateMillisSelected(millis) }
    }
    val selected = day == selectedLocalDate
    BookingDateChip(
        dayOfWeek = shortDayFmt.format(day),
        dayOfMonth = day.dayOfMonth.toString(),
        selected = selected,
        minWidth = chipMinW,
        height = chipH,
        onClick = onClick,
    )
}

@Composable
private fun TimeSlotGridItem(
    cell: TimeSlotCellUiModel,
    minHeight: Dp,
    minWidth: Dp,
    onSlotToggled: (LocalTime) -> Unit,
) {
    val click = remember(cell.slotStart, onSlotToggled) {
        { onSlotToggled(cell.slotStart) }
    }
    TimeSlotHorizontalCell(
        cell = cell,
        minHeight = minHeight,
        minWidth = minWidth,
        onClick = click,
    )
}

@Composable
private fun BookingDateChip(
    dayOfWeek: String,
    dayOfMonth: String,
    selected: Boolean,
    minWidth: Dp,
    height: Dp,
    onClick: () -> Unit,
) {
    val token = GymTheme.token
    val shape = RoundedCornerShape(token.radius.md)
    val bg = if (selected) token.colors.primarySoft else token.colors.surfaceSubtle
    val fg = if (selected) token.colors.onPrimaryContainer else token.colors.textPrimary
    Column(
        modifier = Modifier
            .widthIn(min = minWidth)
            .height(height)
            .clip(shape)
            .background(bg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                role = Role.Button,
                onClick = onClick,
            )
            .padding(
                horizontal = token.spacing.xs,
                vertical = token.spacing.xxs,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        GText(
            text = dayOfWeek,
            style = sessionBookingSheetTextStyle(token.typography.labelSmall),
            color = fg,
        )
        GText(
            text = dayOfMonth,
            style = sessionBookingSheetTextStyle(token.typography.titleSmall),
            color = fg,
        )
    }
}

@Composable
private fun TimeSlotHorizontalCell(
    cell: TimeSlotCellUiModel,
    minHeight: Dp,
    minWidth: Dp,
    onClick: () -> Unit,
) {
    val token = GymTheme.token
    val corner = token.radius.sm
    val flatCorner = token.spacing.none
    val selected = cell.selected
    val shape = when {
        !selected -> RoundedCornerShape(corner)
        cell.rangeRole == TimeSlotSelectionRangeRole.Single -> RoundedCornerShape(corner)
        cell.rangeRole == TimeSlotSelectionRangeRole.RangeStart ->
            RoundedCornerShape(
                topStart = corner,
                bottomStart = corner,
                topEnd = flatCorner,
                bottomEnd = flatCorner,
            )
        cell.rangeRole == TimeSlotSelectionRangeRole.RangeEnd ->
            RoundedCornerShape(
                topEnd = corner,
                bottomEnd = corner,
                topStart = flatCorner,
                bottomStart = flatCorner,
            )
        cell.rangeRole == TimeSlotSelectionRangeRole.RangeMiddle -> RoundedCornerShape(flatCorner)
        else -> RoundedCornerShape(corner)
    }
    val bg = when {
        selected && cell.rangeRole == TimeSlotSelectionRangeRole.RangeMiddle ->
            token.colors.secondaryContainer
        selected -> token.colors.primarySoft
        else -> token.colors.surface
    }
    val timeFg = when {
        selected && cell.rangeRole == TimeSlotSelectionRangeRole.RangeMiddle ->
            token.colors.onSecondaryContainer
        selected -> token.colors.onPrimaryContainer
        else -> token.colors.textPrimary
    }
    val borderColor = when {
        !selected -> token.colors.borderSubtle
        cell.rangeRole == TimeSlotSelectionRangeRole.RangeEnd -> token.colors.secondary
        cell.rangeRole == TimeSlotSelectionRangeRole.RangeMiddle -> token.colors.borderSubtle
        else -> token.colors.primary
    }
    val borderMod =
        if (selected && cell.rangeRole == TimeSlotSelectionRangeRole.RangeMiddle) {
            Modifier
        } else {
            Modifier.border(BorderStroke(token.borderWidth.thin, borderColor), shape)
        }
    Column(
        modifier = Modifier
            .widthIn(min = minWidth)
            .heightIn(min = minHeight)
            .then(borderMod)
            .clip(shape)
            .background(bg)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
                role = Role.Button,
                onClick = onClick,
            )
            .padding(token.spacing.xs),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        GText(
            text = cell.label,
            style = sessionBookingSheetTextStyle(token.typography.labelSmall),
            color = timeFg,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
