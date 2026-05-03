package com.hoabui.virtualbody3d.ui.common_ui.organism.exerciselibrary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.hoabui.virtualbody3d.R
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButton
import com.hoabui.virtualbody3d.ui.common_ui.atom.button.GButtonVariant
import com.hoabui.virtualbody3d.ui.common_ui.atom.icon.GIcon
import com.hoabui.virtualbody3d.ui.common_ui.atom.surface.GSurface
import com.hoabui.virtualbody3d.ui.common_ui.atom.text.GText
import com.hoabui.virtualbody3d.ui.common_ui.image.LocalResourceProvider
import com.hoabui.virtualbody3d.ui.exerciselibrary.components.isSessionBookingSlotEnabled
import com.hoabui.virtualbody3d.ui.exerciselibrary.catalog.toCoilModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.sessionbooking.SessionBookingPeriodId
import com.hoabui.virtualbody3d.ui.exerciselibrary.sessionbooking.SessionBookingPeriodUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.sessionbooking.SessionBookingUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.sessionbooking.TimeSlotCellUiModel
import com.hoabui.virtualbody3d.ui.exerciselibrary.sessionbooking.TimeSlotSelectionRangeRole
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
import java.time.temporal.ChronoUnit
import java.util.Locale

private fun sessionBookingEditorTextStyle(base: TextStyle): TextStyle =
    base.merge(TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)))

/**
 * Day/time/location booking grid (extracted from the former bottom sheet). Cancel/Confirm live on the screen.
 */
@Composable
fun SessionBookingEditorOrganism(
    booking: SessionBookingUiModel,
    showSlotConflict: Boolean,
    onDateMillisSelected: (Long) -> Unit,
    onLocationSelected: (String) -> Unit,
    onSlotToggled: (LocalTime) -> Unit,
    onClearTimeSelection: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val systemZone = Clock.systemDefaultZone().zone
    val token = GymTheme.token
    val slotRowState = rememberLazyListState()
    val sectionIcon = token.bodyAnalysis.exerciseLibraryBookingSectionIconSize
    val chipH = token.bodyAnalysis.exerciseLibraryBookingDateChipHeight
    val chipMinW = token.bodyAnalysis.exerciseLibraryBookingDateChipMinWidth
    val gridCellMinH = token.bodyAnalysis.exerciseLibraryBookingTimeGridCellMinHeight
    val slotChipMinW = token.bodyAnalysis.exerciseLibraryBookingTimeSlotHorizontalMinWidth
    val dayLabelsLocale = Locale.getDefault()
    val shortDayFmt = remember(dayLabelsLocale) {
        DateTimeFormatter.ofPattern("EEE", dayLabelsLocale)
    }
    val selectedLocalDate =
        remember(booking.selectedDateMillis, systemZone) {
            Instant.ofEpochMilli(booking.selectedDateMillis).atZone(systemZone).toLocalDate()
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
    val dayHorizon = remember(booking.selectedDateMillis, resumeKey, systemZone) {
        val today = LocalDate.now(systemZone)
        (0 until SESSION_BOOKING_DAY_HORIZON).map { today.plusDays(it.toLong()) }
    }
    val (bookingToday, bookingNowMinute) = remember(selectedLocalDate, resumeKey, systemZone) {
        LocalDate.now(systemZone) to LocalTime.now(systemZone).truncatedTo(ChronoUnit.MINUTES)
    }
    val selectedLocationName = booking.selectedLocationDisplayName
    var locationMenuExpanded by remember { mutableStateOf(false) }

    // No verticalScroll here: [SessionBookingEditorScreen] provides the page scroll. Nested scroll would get
    // unbounded max height and crash.
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = token.spacing.md,
                end = token.spacing.md,
                top = token.spacing.sm,
                bottom = token.spacing.lg,
            ),
        verticalArrangement = Arrangement.spacedBy(token.spacing.md),
    ) {
        if (showSlotConflict) {
            GText(
                text = stringResource(R.string.exercise_library_booking_slot_conflict),
                style = sessionBookingEditorTextStyle(token.typography.bodySmall),
                color = token.colors.error,
            )
        }
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
                        style = sessionBookingEditorTextStyle(token.typography.labelLarge),
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
                            style = sessionBookingEditorTextStyle(token.typography.labelLarge),
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
                                style = sessionBookingEditorTextStyle(token.typography.bodyLarge),
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
                enabled = booking.selectedSlotStarts.isNotEmpty(),
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
                val slotEnabled = isSessionBookingSlotEnabled(
                    selectedDay = selectedLocalDate,
                    today = bookingToday,
                    slotStart = cell.slotStart,
                    nowMinute = bookingNowMinute,
                )
                TimeSlotGridItem(
                    cell = cell,
                    minHeight = gridCellMinH,
                    minWidth = slotChipMinW,
                    enabled = slotEnabled,
                    onSlotToggled = onSlotToggled,
                )
            }
        }
    }
}

@Composable
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
            style = sessionBookingEditorTextStyle(token.typography.labelLarge),
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
    enabled: Boolean,
    onSlotToggled: (LocalTime) -> Unit,
) {
    val click = remember(cell.slotStart, onSlotToggled) {
        { onSlotToggled(cell.slotStart) }
    }
    TimeSlotHorizontalCell(
        cell = cell,
        minHeight = minHeight,
        minWidth = minWidth,
        enabled = enabled,
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
            style = sessionBookingEditorTextStyle(token.typography.labelSmall),
            color = fg,
        )
        GText(
            text = dayOfMonth,
            style = sessionBookingEditorTextStyle(token.typography.titleSmall),
            color = fg,
        )
    }
}

@Composable
private fun TimeSlotHorizontalCell(
    cell: TimeSlotCellUiModel,
    minHeight: Dp,
    minWidth: Dp,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val token = GymTheme.token
    val corner = token.radius.sm
    val flatCorner = token.spacing.none
    val selected = cell.selected
    if (!enabled) {
        val shape = RoundedCornerShape(corner)
        Column(
            modifier = Modifier
                .widthIn(min = minWidth)
                .heightIn(min = minHeight)
                .border(BorderStroke(token.borderWidth.thin, token.colors.borderSubtle), shape)
                .clip(shape)
                .background(token.colors.surfaceSubtle)
                .clickable(
                    enabled = false,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    role = Role.Button,
                    onClick = onClick,
                )
                .padding(token.spacing.xs),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            GText(
                text = cell.label,
                style = sessionBookingEditorTextStyle(token.typography.labelSmall),
                color = token.colors.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        return
    }
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
                enabled = true,
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
            style = sessionBookingEditorTextStyle(token.typography.labelSmall),
            color = timeFg,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
