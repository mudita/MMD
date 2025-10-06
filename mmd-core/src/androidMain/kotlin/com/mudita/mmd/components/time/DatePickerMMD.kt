/*
 *
 * Copyright 2025 The Android Open Source Project
 *
 * Based on Material 3. Modified by Mudita Sp. z o.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.mudita.mmd.components.time

import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.snapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.ScrollAxisRange
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.horizontalScrollAxisRange
import androidx.compose.ui.semantics.isContainer
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.semantics.verticalScrollAxisRange
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.mudita.mmd.R
import com.mudita.mmd.internal.ProvideContentColorTextStyleMMD
import com.mudita.mmd.internal.date_picker.CalendarModelMMD
import com.mudita.mmd.internal.date_picker.CalendarMonthMMD
import com.mudita.mmd.internal.date_picker.DateInputContent
import com.mudita.mmd.internal.date_picker.DateSkeleton
import com.mudita.mmd.internal.date_picker.DaysInWeek
import com.mudita.mmd.internal.date_picker.MillisecondsIn24Hours
import com.mudita.mmd.internal.date_picker.createCalendarModel
import com.mudita.mmd.internal.date_picker.defaultLocale
import com.mudita.mmd.internal.date_picker.formatDateWithSkeleton
import com.mudita.mmd.internal.date_picker.toLocalString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.max

/**
 * By default, a date picker lets you pick a date via a calendar UI. However, it also allows
 * switching into a date input mode for a manual entry of dates using the numbers on a keyboard.
 *
 * A simple DatePicker looks like:
 *
 *```kotlin
 * @OptIn(ExperimentalMaterial3Api::class)
 * @Preview
 * @Composable
 * fun DatePickerSample() {
 *     Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
 *         // Pre-select a date for January 4, 2020
 *         val datePickerState = rememberDatePickerMMDState(initialSelectedDateMillis = 1578096000000)
 *         DatePickerMMD(state = datePickerState, modifier = Modifier.padding(16.dp))
 *
 *         Text(
 *             "Selected date timestamp: ${datePickerState.selectedDateMillis ?: "no selection"}",
 *             modifier = Modifier.align(Alignment.CenterHorizontally)
 *         )
 *     }
 * }
 * ```
 *
 * A DatePicker with an initial UI of a date input mode looks like:
 *
 *```kotlin
 * @OptIn(ExperimentalMaterial3Api::class)
 * @Preview
 * @Composable
 * fun DateInputSample() {
 *     Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
 *         val state = rememberDatePickerMMDState(initialDisplayMode = DisplayModeMMD.Input)
 *         DatePickerMMD(state = state, modifier = Modifier.padding(16.dp))
 *
 *         Text(
 *             "Entered date timestamp: ${state.selectedDateMillis ?: "no input"}",
 *             modifier = Modifier.align(Alignment.CenterHorizontally)
 *         )
 *     }
 * }
 *```
 *
 * A DatePicker with a provided [SelectableDatesMMD] that blocks certain days from being selected looks
 * like:
 *
 *```kotlin
 * @Suppress("ClassVerificationFailure")
 * @OptIn(ExperimentalMaterial3Api::class)
 * @Preview
 * @Composable
 * fun DatePickerWithDateSelectableDatesSample() {
 *     val datePickerState =
 *         rememberDatePickerMMDState(
 *             selectableDates =
 *             object : SelectableDatesMMD {
 *                 // Blocks Sunday and Saturday from being selected.
 *                 override fun isSelectableDate(utcTimeMillis: Long): Boolean {
 *                     return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
 *                         val dayOfWeek =
 *                             Instant.ofEpochMilli(utcTimeMillis)
 *                                 .atZone(ZoneId.of("UTC"))
 *                                 .toLocalDate()
 *                                 .dayOfWeek
 *                         dayOfWeek != DayOfWeek.SUNDAY && dayOfWeek != DayOfWeek.SATURDAY
 *                     } else {
 *                         val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
 *                         calendar.timeInMillis = utcTimeMillis
 *                         calendar[Calendar.DAY_OF_WEEK] != Calendar.SUNDAY &&
 *                                 calendar[Calendar.DAY_OF_WEEK] != Calendar.SATURDAY
 *                     }
 *                 }
 *
 *                 // Allow selecting dates from year 2023 forward.
 *                 override fun isSelectableYear(year: Int): Boolean {
 *                     return year > 2022
 *                 }
 *             }
 *         )
 *
 *     Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
 *         DatePickerMMD(state = datePickerState)
 *         Text(
 *             "Selected date timestamp: ${datePickerState.selectedDateMillis ?: "no selection"}",
 *             modifier = Modifier.align(Alignment.CenterHorizontally)
 *         )
 *     }
 * }
 * ```
 *
 * @param state state of the date picker. See [rememberDatePickerMMDState].
 * @param modifier the [Modifier] to be applied to this date picker
 * @param dateFormatter a [DatePickerFormatterMMD] that provides formatting skeletons for dates display
 * @param title the title to be displayed in the date picker
 * @param headline the headline to be displayed in the date picker
 * @param showModeToggle indicates if this DatePicker should show a mode toggle action that
 *   transforms it into a date input
 * @param colors [DatePickerColorsMMD] that will be used to resolve the colors used for this date
 *   picker in different states. See [DatePickerDefaultsMMD.colors].
 */
@ExperimentalMaterial3Api
@Composable
fun DatePickerMMD(
    state: DatePickerStateMMD,
    modifier: Modifier = Modifier,
    dateFormatter: DatePickerFormatterMMD = remember { DatePickerDefaultsMMD.dateFormatter() },
    title: (@Composable () -> Unit)? = {
        DatePickerDefaultsMMD.DatePickerTitle(
            displayMode = state.displayMode,
            modifier = Modifier.padding(DatePickerTitlePadding),
        )
    },
    headline: (@Composable () -> Unit)? = {
        DatePickerDefaultsMMD.DatePickerHeadline(
            selectedDateMillis = state.selectedDateMillis,
            displayMode = state.displayMode,
            dateFormatter = dateFormatter,
            modifier = Modifier.padding(DatePickerHeadlinePadding),
        )
    },
    showModeToggle: Boolean = true,
    colors: DatePickerColorsMMD = DatePickerDefaultsMMD.colors(),
) {
    val defaultLocale = defaultLocale()
    val calendarModel = remember(defaultLocale) { createCalendarModel(defaultLocale) }
    DateEntryContainer(
        modifier = modifier,
        title = title,
        headline = headline,
        modeToggleButton =
        if (showModeToggle) {
            {
                DisplayModeToggleButton(
                    modifier = Modifier.padding(DatePickerModeTogglePadding),
                    displayMode = state.displayMode,
                    onDisplayModeChange = { displayMode -> state.displayMode = displayMode },
                )
            }
        } else {
            null
        },
        headlineTextStyle = HeaderHeadlineFont,
        headerMinHeight = HeaderContainerHeight,
        colors = colors,
    ) {
        SwitchableDateEntryContent(
            selectedDateMillis = state.selectedDateMillis,
            displayedMonthMillis = state.displayedMonthMillis,
            displayMode = state.displayMode,
            onDateSelectionChange = { dateInMillis -> state.selectedDateMillis = dateInMillis },
            onDisplayedMonthChange = { monthInMillis ->
                state.displayedMonthMillis = monthInMillis
            },
            calendarModel = calendarModel,
            yearRange = state.yearRange,
            dateFormatter = dateFormatter,
            selectableDates = state.selectableDates,
            colors = colors,
        )
    }
}

/**
 * A state object that can be hoisted to observe the date picker state. See
 * [rememberDatePickerMMDState].
 */
@ExperimentalMaterial3Api
@Stable
interface DatePickerStateMMD {

    /**
     * A timestamp that represents the selected date _start_ of the day in _UTC_ milliseconds from
     * the epoch.
     *
     * @throws IllegalArgumentException in case the value is set with a timestamp that does not fall
     *   within the [yearRange].
     */
    @get:Suppress("AutoBoxing")
    var selectedDateMillis: Long?

    /**
     * A timestamp that represents the currently displayed month _start_ date in _UTC_ milliseconds
     * from the epoch.
     *
     * @throws IllegalArgumentException in case the value is set with a timestamp that does not fall
     *   within the [yearRange].
     */
    var displayedMonthMillis: Long

    /** A [DisplayModeMMD] that represents the current UI mode (i.e. picker or input). */
    var displayMode: DisplayModeMMD

    /** An [IntRange] that holds the year range that the date picker will be limited to. */
    val yearRange: IntRange

    /**
     * A [SelectableDatesMMD] that is consulted to check if a date is allowed.
     *
     * In case a date is not allowed to be selected, it will appear disabled in the UI.
     */
    val selectableDates: SelectableDatesMMD
}

/** An interface that controls the selectable dates and years in the date pickers UI. */
@ExperimentalMaterial3Api
@Stable
interface SelectableDatesMMD {

    /**
     * Returns true if the date item representing the [utcTimeMillis] should be enabled for
     * selection in the UI.
     */
    fun isSelectableDate(utcTimeMillis: Long) = true

    /**
     * Returns true if a given [year] should be enabled for selection in the UI. When a year is
     * defined as non selectable, all the dates in that year will also be non selectable.
     */
    fun isSelectableYear(year: Int) = true
}

/** A date formatter interface used by [DatePickerMMD]. */
@ExperimentalMaterial3Api
interface DatePickerFormatterMMD {

    /**
     * Format a given [monthMillis] to a string representation of the month and the year (i.e.
     * January 2023).
     *
     * @param monthMillis timestamp in _UTC_ milliseconds from the epoch that represents the month
     * @param locale a [CalendarLocale] to use when formatting the month and year
     * @see defaultLocale
     */
    fun formatMonthYear(@Suppress("AutoBoxing") monthMillis: Long?, locale: CalendarLocale): String?

    /**
     * Format a given [dateMillis] to a string representation of the date (i.e. Mar 27, 2021).
     *
     * @param dateMillis timestamp in _UTC_ milliseconds from the epoch that represents the date
     * @param locale a [CalendarLocale] to use when formatting the date
     * @param forContentDescription indicates that the requested formatting is for content
     *   description. In these cases, the output may include a more descriptive wording that will be
     *   passed to a screen readers.
     * @see defaultLocale
     */
    fun formatDate(
        @Suppress("AutoBoxing") dateMillis: Long?,
        locale: CalendarLocale,
        forContentDescription: Boolean = false,
    ): String?
}

/** Represents the different modes that a date picker can be at. */
@Immutable
@JvmInline
@ExperimentalMaterial3Api
value class DisplayModeMMD internal constructor(internal val value: Int) {

    companion object {
        /** Date picker mode */
        val Picker = DisplayModeMMD(0)

        /** Date text input mode */
        val Input = DisplayModeMMD(1)
    }

    override fun toString() =
        when (this) {
            Picker -> "Picker"
            Input -> "Input"
            else -> "Unknown"
        }
}

/**
 * Creates a [DatePickerStateMMD] for a [DatePickerMMD] that is remembered across compositions.
 *
 * To create a date picker state outside composition, see the `DatePickerState` function.
 *
 * @param initialSelectedDateMillis timestamp in _UTC_ milliseconds from the epoch that represents
 *   an initial selection of a date. Provide a `null` to indicate no selection.
 * @param initialDisplayedMonthMillis timestamp in _UTC_ milliseconds from the epoch that represents
 *   an initial selection of a month to be displayed to the user. By default, in case an
 *   `initialSelectedDateMillis` is provided, the initial displayed month would be the month of the
 *   selected date. Otherwise, in case `null` is provided, the displayed month would be the current
 *   one.
 * @param yearRange an [IntRange] that holds the year range that the date picker will be limited to
 * @param initialDisplayMode an initial [DisplayModeMMD] that this state will hold
 * @param selectableDates a [SelectableDatesMMD] that is consulted to check if a date is allowed. In
 *   case a date is not allowed to be selected, it will appear disabled in the UI.
 */
@Composable
@ExperimentalMaterial3Api
fun rememberDatePickerMMDState(
    @Suppress("AutoBoxing") initialSelectedDateMillis: Long? = null,
    @Suppress("AutoBoxing") initialDisplayedMonthMillis: Long? = initialSelectedDateMillis,
    yearRange: IntRange = DatePickerDefaultsMMD.YearRange,
    initialDisplayMode: DisplayModeMMD = DisplayModeMMD.Picker,
    selectableDates: SelectableDatesMMD = DatePickerDefaultsMMD.AllDates,
): DatePickerStateMMD {
    val locale = defaultLocale()
    return rememberSaveable(saver = DatePickerStateMMDImpl.Saver(selectableDates, locale)) {
        DatePickerStateMMDImpl(
            initialSelectedDateMillis = initialSelectedDateMillis,
            initialDisplayedMonthMillis = initialDisplayedMonthMillis,
            yearRange = yearRange,
            initialDisplayMode = initialDisplayMode,
            selectableDates = selectableDates,
            locale = locale,
        )
    }
        .apply {
            // Update the state's selectable dates if they were changed.
            this.selectableDates = selectableDates
        }
}

/**
 * Creates a [DatePickerStateMMD].
 *
 * Note that in most cases, you are advised to use the [rememberDatePickerMMDState] when in a
 * composition.
 *
 * @param locale a [CalendarLocale] to be used when formatting dates, determining the input format,
 *   and more
 * @param initialSelectedDateMillis timestamp in _UTC_ milliseconds from the epoch that represents
 *   an initial selection of a date. Provide a `null` to indicate no selection. Note that the
 *   state's [DatePickerStateMMD.selectedDateMillis] will provide a timestamp that represents the
 *   _start_ of the day, which may be different than the provided initialSelectedDateMillis.
 * @param initialDisplayedMonthMillis timestamp in _UTC_ milliseconds from the epoch that represents
 *   an initial selection of a month to be displayed to the user. In case `null` is provided, the
 *   displayed month would be the current one.
 * @param yearRange an [IntRange] that holds the year range that the date picker will be limited to
 * @param initialDisplayMode an initial [DisplayModeMMD] that this state will hold
 * @param selectableDates a [SelectableDatesMMD] that is consulted to check if a date is allowed. In
 *   case a date is not allowed to be selected, it will appear disabled in the UI.
 * @throws [IllegalArgumentException] if the initial selected date or displayed month represent a
 *   year that is out of the year range.
 * @see rememberDatePickerMMDState
 */
@ExperimentalMaterial3Api
fun DatePickerState(
    locale: CalendarLocale,
    @Suppress("AutoBoxing") initialSelectedDateMillis: Long? = null,
    @Suppress("AutoBoxing") initialDisplayedMonthMillis: Long? = initialSelectedDateMillis,
    yearRange: IntRange = DatePickerDefaultsMMD.YearRange,
    initialDisplayMode: DisplayModeMMD = DisplayModeMMD.Picker,
    selectableDates: SelectableDatesMMD = DatePickerDefaultsMMD.AllDates,
): DatePickerStateMMD =
    DatePickerStateMMDImpl(
        initialSelectedDateMillis = initialSelectedDateMillis,
        initialDisplayedMonthMillis = initialDisplayedMonthMillis,
        yearRange = yearRange,
        initialDisplayMode = initialDisplayMode,
        selectableDates = selectableDates,
        locale = locale,
    )

/** Contains default values used by the [DatePickerMMD]. */
@ExperimentalMaterial3Api
@Stable
object DatePickerDefaultsMMD {

    /** Colors used by the [DatePickerMMD]. */
    @Composable
    fun colors() = defaultDatePickerColors

    /**
     * @param containerColor the color used for the date picker's background
     * @param titleContentColor the color used for the date picker's title
     * @param headlineContentColor the color used for the date picker's headline
     * @param weekdayContentColor the color used for the weekday letters
     * @param subheadContentColor the color used for the month and year subhead labels that appear
     *   when months are displayed at a `DateRangePicker`.
     * @param navigationContentColor the content color used for the year selection menu button and
     *   the months arrow navigation when displayed at a `DatePicker`.
     * @param yearContentColor the color used for a year item content
     * @param disabledYearContentColor the color used for a disabled year item content
     * @param currentYearContentColor the color used for the current year content when selecting a
     *   year
     * @param selectedYearContentColor the color used for a selected year item content
     * @param disabledSelectedYearContentColor the color used for a disabled selected year item
     *   content
     * @param selectedYearContainerColor the color used for a selected year item container
     * @param disabledSelectedYearContainerColor the color used for a disabled selected year item
     *   container
     * @param dayContentColor the color used for days content
     * @param disabledDayContentColor the color used for disabled days content
     * @param selectedDayContentColor the color used for selected days content
     * @param disabledSelectedDayContentColor the color used for disabled selected days content
     * @param selectedDayContainerColor the color used for a selected day container
     * @param disabledSelectedDayContainerColor the color used for a disabled selected day container
     * @param todayContentColor the color used for the day that marks the current date
     * @param todayDateBorderColor the color used for the border of the day that marks the current
     *   date
     * @param dayInSelectionRangeContentColor the content color used for days that are within a date
     *   range selection
     * @param dayInSelectionRangeContainerColor the container color used for days that are within a
     *   date range selection
     * @param dividerColor the color used for the dividers used at the date pickers
     * @param dateTextFieldColors the [TextFieldColors] defaults for the date text field when in
     *   [DisplayModeMMD.Input]. See [OutlinedTextFieldDefaults.colors].
     */
    @Composable
    fun colors(
        containerColor: Color = Color.Unspecified,
        titleContentColor: Color = Color.Unspecified,
        headlineContentColor: Color = Color.Unspecified,
        weekdayContentColor: Color = Color.Unspecified,
        subheadContentColor: Color = Color.Unspecified,
        navigationContentColor: Color = Color.Unspecified,
        yearContentColor: Color = Color.Unspecified,
        disabledYearContentColor: Color = Color.Unspecified,
        currentYearContentColor: Color = Color.Unspecified,
        selectedYearContentColor: Color = Color.Unspecified,
        disabledSelectedYearContentColor: Color = Color.Unspecified,
        selectedYearContainerColor: Color = Color.Unspecified,
        disabledSelectedYearContainerColor: Color = Color.Unspecified,
        dayContentColor: Color = Color.Unspecified,
        disabledDayContentColor: Color = Color.Unspecified,
        selectedDayContentColor: Color = Color.Unspecified,
        disabledSelectedDayContentColor: Color = Color.Unspecified,
        selectedDayContainerColor: Color = Color.Unspecified,
        disabledSelectedDayContainerColor: Color = Color.Unspecified,
        todayContentColor: Color = Color.Unspecified,
        todayDateBorderColor: Color = Color.Unspecified,
        dayInSelectionRangeContentColor: Color = Color.Unspecified,
        dayInSelectionRangeContainerColor: Color = Color.Unspecified,
        dividerColor: Color = Color.Unspecified,
        dateTextFieldColors: TextFieldColors? = OutlinedTextFieldDefaults.colors(),
    ): DatePickerColorsMMD =
        defaultDatePickerColors.copy(
            containerColor = containerColor,
            titleContentColor = titleContentColor,
            headlineContentColor = headlineContentColor,
            weekdayContentColor = weekdayContentColor,
            subheadContentColor = subheadContentColor,
            navigationContentColor = navigationContentColor,
            yearContentColor = yearContentColor,
            disabledYearContentColor = disabledYearContentColor,
            currentYearContentColor = currentYearContentColor,
            selectedYearContentColor = selectedYearContentColor,
            disabledSelectedYearContentColor = disabledSelectedYearContentColor,
            selectedYearContainerColor = selectedYearContainerColor,
            disabledSelectedYearContainerColor = disabledSelectedYearContainerColor,
            dayContentColor = dayContentColor,
            disabledDayContentColor = disabledDayContentColor,
            selectedDayContentColor = selectedDayContentColor,
            disabledSelectedDayContentColor = disabledSelectedDayContentColor,
            selectedDayContainerColor = selectedDayContainerColor,
            disabledSelectedDayContainerColor = disabledSelectedDayContainerColor,
            todayContentColor = todayContentColor,
            todayDateBorderColor = todayDateBorderColor,
            dayInSelectionRangeContentColor = dayInSelectionRangeContentColor,
            dayInSelectionRangeContainerColor = dayInSelectionRangeContainerColor,
            dividerColor = dividerColor,
            dateTextFieldColors = dateTextFieldColors,
        )

    private val defaultDatePickerColors: DatePickerColorsMMD
        @Composable
        get() = DatePickerColorsMMD(
            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            headlineContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            weekdayContentColor = MaterialTheme.colorScheme.onSurface,
            subheadContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            navigationContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            yearContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledYearContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                .copy(alpha = .25f),
            currentYearContentColor = MaterialTheme.colorScheme.primary,
            selectedYearContentColor = MaterialTheme.colorScheme.onPrimary,
            disabledSelectedYearContentColor = MaterialTheme.colorScheme.onPrimary
                .copy(alpha = .25f),
            selectedYearContainerColor = MaterialTheme.colorScheme.primary,
            disabledSelectedYearContainerColor = MaterialTheme.colorScheme.primary
                .copy(alpha = .25f),
            dayContentColor = MaterialTheme.colorScheme.onSurface,
            disabledDayContentColor = MaterialTheme.colorScheme.onSurface
                .copy(alpha = .25f),
            selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,
            disabledSelectedDayContentColor = MaterialTheme.colorScheme.onPrimary
                .copy(alpha = .25f),
            selectedDayContainerColor = MaterialTheme.colorScheme.primary,
            disabledSelectedDayContainerColor = MaterialTheme.colorScheme.primary
                .copy(alpha = .25f),
            todayContentColor = MaterialTheme.colorScheme.primary,
            todayDateBorderColor = MaterialTheme.colorScheme.primary,
            dayInSelectionRangeContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            dayInSelectionRangeContainerColor = MaterialTheme.colorScheme.onSecondaryContainer,
            dividerColor = MaterialTheme.colorScheme.primary,
            dateTextFieldColors = OutlinedTextFieldDefaults.colors(),
        )

    /**
     * Returns a [DatePickerFormatterMMD].
     *
     * The date formatter will apply the best possible localized form of the given skeleton and
     * Locale. A skeleton is similar to, and uses the same format characters as, a Unicode <a
     * href="http://www.unicode.org/reports/tr35/#Date_Format_Patterns">UTS #35</a> pattern.
     *
     * One difference is that order is irrelevant. For example, "MMMMd" will return "MMMM d" in the
     * `en_US` locale, but "d. MMMM" in the `de_CH` locale.
     *
     * @param yearSelectionSkeleton a date format skeleton used to format the date picker's year
     *   selection menu button (e.g. "March 2021").
     * @param selectedDateSkeleton a date format skeleton used to format a selected date (e.g. "Mar
     *   27, 2021")
     * @param selectedDateDescriptionSkeleton a date format skeleton used to format a selected date
     *   to be used as content description for screen readers (e.g. "Saturday, March 27, 2021")
     */
    @OptIn(ExperimentalMaterial3Api::class)
    fun dateFormatter(
        yearSelectionSkeleton: DateSkeleton = YearMonthSkeleton,
        selectedDateSkeleton: DateSkeleton = YearAbbrMonthDaySkeleton,
        selectedDateDescriptionSkeleton: DateSkeleton = YearMonthWeekdayDaySkeleton,
    ): DatePickerFormatterMMD =
        DatePickerFormatterMMDImpl(
            yearSelectionSkeleton = yearSelectionSkeleton,
            selectedDateSkeleton = selectedDateSkeleton,
            selectedDateDescriptionSkeleton = selectedDateDescriptionSkeleton,
        )

    /**
     * A default date picker title composable.
     *
     * @param displayMode the current [DisplayModeMMD]
     * @param modifier a [Modifier] to be applied for the title
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DatePickerTitle(displayMode: DisplayModeMMD, modifier: Modifier = Modifier) {
        val context = LocalContext.current

        when (displayMode) {
            DisplayModeMMD.Picker ->
                Text(
                    text = context.getString(R.string.date_picker_select_date),
                    modifier = modifier,
                )

            DisplayModeMMD.Input ->
                Text(
                    text = context.getString(R.string.date_picker_select_date),
                    modifier = modifier,
                )
        }
    }

    /**
     * A default date picker headline composable that displays a default headline text when there is
     * no date selection, and an actual date string when there is.
     *
     * @param selectedDateMillis a timestamp that represents the selected date _start_ of the day in
     *   _UTC_ milliseconds from the epoch
     * @param displayMode the current [DisplayModeMMD]
     * @param dateFormatter a [DatePickerFormatterMMD]
     * @param modifier a [Modifier] to be applied for the headline
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun DatePickerHeadline(
        @Suppress("AutoBoxing") selectedDateMillis: Long?,
        displayMode: DisplayModeMMD,
        dateFormatter: DatePickerFormatterMMD,
        modifier: Modifier = Modifier,
    ) {
        val context = LocalContext.current
        val defaultLocale = defaultLocale()
        val formattedDate =
            dateFormatter.formatDate(dateMillis = selectedDateMillis, locale = defaultLocale)
        val verboseDateDescription =
            dateFormatter.formatDate(
                dateMillis = selectedDateMillis,
                locale = defaultLocale,
                forContentDescription = true,
            )
                ?: when (displayMode) {
                    DisplayModeMMD.Picker -> context.getString(R.string.date_picker_none)
                    DisplayModeMMD.Input -> context.getString(R.string.date_picker_none)
                    else -> ""
                }

        val headlineText =
            formattedDate
                ?: when (displayMode) {
                    DisplayModeMMD.Picker -> context.getString(R.string.date_picker_selected_date_mode)
                    DisplayModeMMD.Input -> context.getString(R.string.date_picker_entered_date_mode)
                    else -> ""
                }

        val headlineDescription =
            when (displayMode) {
                DisplayModeMMD.Picker -> context.getString(R.string.date_picker_current_selection)
                DisplayModeMMD.Input -> context.getString(R.string.date_picker_entered_date)
                else -> ""
            }.format(verboseDateDescription)

        Text(
            text = headlineText,
            modifier =
            modifier.semantics {
                liveRegion = LiveRegionMode.Polite
                contentDescription = headlineDescription
            },
            maxLines = 1,
        )
    }

    /**
     * THIS SHOULD BE REWRITTEN WHEN HORIZONTAL SCROLL IS ON!
     *
     * Creates and remembers a [FlingBehavior] that will represent natural fling curve with snap to
     * the most visible month in the months list.
     *
     * @param lazyListState a [LazyListState]
     * @param decayAnimationSpec the decay to use
     */
    @Composable
    internal fun rememberSnapFlingBehavior(
        lazyListState: LazyListState,
        decayAnimationSpec: DecayAnimationSpec<Float> = exponentialDecay(),
    ): FlingBehavior {
        return remember(decayAnimationSpec, lazyListState) {
            val original = SnapLayoutInfoProvider(lazyListState)
            val snapLayoutInfoProvider =
                object : SnapLayoutInfoProvider by original {
                    override fun calculateApproachOffset(
                        velocity: Float,
                        decayOffset: Float,
                    ): Float = 0.0f
                }

            snapFlingBehavior(
                snapLayoutInfoProvider = snapLayoutInfoProvider,
                decayAnimationSpec = decayAnimationSpec,
                snapAnimationSpec = spring(stiffness = Spring.StiffnessMediumLow),
            )
        }
    }

    /** The range of years for the date picker dialogs. */
    val YearRange: IntRange = IntRange(1900, 2100)

    /** A default [SelectableDatesMMD] that allows all dates to be selected. */
    @OptIn(ExperimentalMaterial3Api::class)
    val AllDates: SelectableDatesMMD = object : SelectableDatesMMD {}

    /**
     * A date format skeleton used to format the date picker's year selection menu button (e.g.
     * "March 2021")
     */
    val YearMonthSkeleton: DateSkeleton = DateSkeleton.YearMonthSkeleton

    /** A date format skeleton used to format a selected date (e.g. "Mar 27, 2021") */
    val YearAbbrMonthDaySkeleton: DateSkeleton = DateSkeleton.YearAbbrMonthDaySkeleton

    /**
     * A date format skeleton used to format a selected date to be used as content description for
     * screen readers (e.g. "Saturday, March 27, 2021")
     */
    val YearMonthWeekdayDaySkeleton: DateSkeleton = DateSkeleton.YearMonthWeekdayDaySkeleton
}

/**
 * Represents the colors used by the date picker.
 *
 * @param containerColor the color used for the date picker's background
 * @param titleContentColor the color used for the date picker's title
 * @param headlineContentColor the color used for the date picker's headline
 * @param weekdayContentColor the color used for the weekday letters
 * @param subheadContentColor the color used for the month and year subhead labels that appear when
 *   months are displayed at a `DateRangePicker`.
 * @param navigationContentColor the content color used for the year selection menu button and the
 *   months arrow navigation when displayed at a `DatePicker`.
 * @param yearContentColor the color used for a year item content
 * @param disabledYearContentColor the color used for a disabled year item content
 * @param currentYearContentColor the color used for the current year content when selecting a year
 * @param selectedYearContentColor the color used for a selected year item content
 * @param disabledSelectedYearContentColor the color used for a disabled selected year item content
 * @param selectedYearContainerColor the color used for a selected year item container
 * @param disabledSelectedYearContainerColor the color used for a disabled selected year item
 *   container
 * @param dayContentColor the color used for days content
 * @param disabledDayContentColor the color used for disabled days content
 * @param selectedDayContentColor the color used for selected days content
 * @param disabledSelectedDayContentColor the color used for disabled selected days content
 * @param selectedDayContainerColor the color used for a selected day container
 * @param disabledSelectedDayContainerColor the color used for a disabled selected day container
 * @param todayContentColor the color used for the day that marks the current date
 * @param todayDateBorderColor the color used for the border of the day that marks the current date
 * @param dayInSelectionRangeContentColor the content color used for days that are within a date
 *   range selection
 * @param dayInSelectionRangeContainerColor the container color used for days that are within a date
 *   range selection
 * @param dividerColor the color used for the dividers used at the date pickers
 * @param dateTextFieldColors the [TextFieldColors] defaults for the date text field when in
 *   [DisplayModeMMD.Input]. See [OutlinedTextFieldDefaults.colors].
 * @constructor create an instance with arbitrary colors, see [DatePickerDefaultsMMD.colors] for the
 *   default implementation that follows Material specifications.
 */
@ExperimentalMaterial3Api
@Immutable
class DatePickerColorsMMD(
    val containerColor: Color,
    val titleContentColor: Color,
    val headlineContentColor: Color,
    val weekdayContentColor: Color,
    val subheadContentColor: Color,
    val navigationContentColor: Color,
    val yearContentColor: Color,
    val disabledYearContentColor: Color,
    val currentYearContentColor: Color,
    val selectedYearContentColor: Color,
    val disabledSelectedYearContentColor: Color,
    val selectedYearContainerColor: Color,
    val disabledSelectedYearContainerColor: Color,
    val dayContentColor: Color,
    val disabledDayContentColor: Color,
    val selectedDayContentColor: Color,
    val disabledSelectedDayContentColor: Color,
    val selectedDayContainerColor: Color,
    val disabledSelectedDayContainerColor: Color,
    val todayContentColor: Color,
    val todayDateBorderColor: Color,
    val dayInSelectionRangeContainerColor: Color,
    val dayInSelectionRangeContentColor: Color,
    val dividerColor: Color,
    val dateTextFieldColors: TextFieldColors,
) {
    /**
     * Returns a copy of this DatePickerColors, optionally overriding some of the values. This uses
     * the Color.Unspecified to mean “use the value from the source” // For `dateTextFieldColors`
     * use null to mean "use the value from source"
     */
    fun copy(
        containerColor: Color = this.containerColor,
        titleContentColor: Color = this.titleContentColor,
        headlineContentColor: Color = this.headlineContentColor,
        weekdayContentColor: Color = this.weekdayContentColor,
        subheadContentColor: Color = this.subheadContentColor,
        navigationContentColor: Color = this.navigationContentColor,
        yearContentColor: Color = this.yearContentColor,
        disabledYearContentColor: Color = this.disabledYearContentColor,
        currentYearContentColor: Color = this.currentYearContentColor,
        selectedYearContentColor: Color = this.selectedYearContentColor,
        disabledSelectedYearContentColor: Color = this.disabledSelectedYearContentColor,
        selectedYearContainerColor: Color = this.selectedYearContainerColor,
        disabledSelectedYearContainerColor: Color = this.disabledSelectedYearContainerColor,
        dayContentColor: Color = this.dayContentColor,
        disabledDayContentColor: Color = this.disabledDayContentColor,
        selectedDayContentColor: Color = this.selectedDayContentColor,
        disabledSelectedDayContentColor: Color = this.disabledSelectedDayContentColor,
        selectedDayContainerColor: Color = this.selectedDayContainerColor,
        disabledSelectedDayContainerColor: Color = this.disabledSelectedDayContainerColor,
        todayContentColor: Color = this.todayContentColor,
        todayDateBorderColor: Color = this.todayDateBorderColor,
        dayInSelectionRangeContainerColor: Color = this.dayInSelectionRangeContainerColor,
        dayInSelectionRangeContentColor: Color = this.dayInSelectionRangeContentColor,
        dividerColor: Color = this.dividerColor,
        dateTextFieldColors: TextFieldColors? = this.dateTextFieldColors,
    ) = DatePickerColorsMMD(
        containerColor.takeOrElse { this.containerColor },
        titleContentColor.takeOrElse { this.titleContentColor },
        headlineContentColor.takeOrElse { this.headlineContentColor },
        weekdayContentColor.takeOrElse { this.weekdayContentColor },
        subheadContentColor.takeOrElse { this.subheadContentColor },
        navigationContentColor.takeOrElse { this.navigationContentColor },
        yearContentColor.takeOrElse { this.yearContentColor },
        disabledYearContentColor.takeOrElse { this.disabledYearContentColor },
        currentYearContentColor.takeOrElse { this.currentYearContentColor },
        selectedYearContentColor.takeOrElse { this.selectedYearContentColor },
        disabledSelectedYearContentColor.takeOrElse { this.disabledSelectedYearContentColor },
        selectedYearContainerColor.takeOrElse { this.selectedYearContainerColor },
        disabledSelectedYearContainerColor.takeOrElse {
            this.disabledSelectedYearContainerColor
        },
        dayContentColor.takeOrElse { this.dayContentColor },
        disabledDayContentColor.takeOrElse { this.disabledDayContentColor },
        selectedDayContentColor.takeOrElse { this.selectedDayContentColor },
        disabledSelectedDayContentColor.takeOrElse { this.disabledSelectedDayContentColor },
        selectedDayContainerColor.takeOrElse { this.selectedDayContainerColor },
        disabledSelectedDayContainerColor.takeOrElse { this.disabledSelectedDayContainerColor },
        todayContentColor.takeOrElse { this.todayContentColor },
        todayDateBorderColor.takeOrElse { this.todayDateBorderColor },
        dayInSelectionRangeContainerColor.takeOrElse { this.dayInSelectionRangeContainerColor },
        dayInSelectionRangeContentColor.takeOrElse { this.dayInSelectionRangeContentColor },
        dividerColor.takeOrElse { this.dividerColor },
        dateTextFieldColors.takeOrElse { this.dateTextFieldColors },
    )

    internal fun TextFieldColors?.takeOrElse(block: () -> TextFieldColors): TextFieldColors =
        this ?: block()

    /**
     * Represents the content color for a calendar day.
     *
     * @param isToday indicates that the color is for a date that represents today
     * @param selected indicates that the color is for a selected day
     * @param inRange indicates that the day is part of a selection range of days
     * @param enabled indicates that the day is enabled for selection
     */
    @Composable
    internal fun dayContentColor(
        isToday: Boolean,
        selected: Boolean,
        inRange: Boolean,
        enabled: Boolean,
    ): State<Color> {
        val target =
            when {
                selected && enabled -> selectedDayContentColor
                selected && !enabled -> disabledSelectedDayContentColor
                inRange && enabled -> dayInSelectionRangeContentColor
                inRange && !enabled -> disabledDayContentColor
                isToday -> todayContentColor
                enabled -> dayContentColor
                else -> disabledDayContentColor
            }

        return rememberUpdatedState(target)
    }

    /**
     * Represents the container color for a calendar day.
     *
     * @param selected indicates that the color is for a selected day
     * @param enabled indicates that the day is enabled for selection
     */
    @Composable
    internal fun dayContainerColor(
        selected: Boolean,
        enabled: Boolean,
    ): State<Color> {
        val target =
            if (selected) {
                if (enabled) selectedDayContainerColor else disabledSelectedDayContainerColor
            } else {
                Color.Transparent
            }
        return rememberUpdatedState(target)
    }

    /**
     * Represents the content color for a calendar year.
     *
     * @param currentYear indicates that the color is for a year that represents the current year
     * @param selected indicates that the color is for a selected year
     * @param enabled indicates that the year is enabled for selection
     */
    @Composable
    internal fun yearContentColor(
        currentYear: Boolean,
        selected: Boolean,
        enabled: Boolean,
    ): State<Color> {
        val target =
            when {
                selected && enabled -> selectedYearContentColor
                selected && !enabled -> disabledSelectedYearContentColor
                currentYear -> currentYearContentColor
                enabled -> yearContentColor
                else -> disabledYearContentColor
            }

        return rememberUpdatedState(target)
    }

    /**
     * Represents the container color for a calendar year.
     *
     * @param selected indicates that the color is for a selected day
     * @param enabled indicates that the year is enabled for selection
     */
    @Composable
    internal fun yearContainerColor(selected: Boolean, enabled: Boolean): State<Color> {
        val target =
            if (selected) {
                if (enabled) selectedYearContainerColor else disabledSelectedYearContainerColor
            } else {
                Color.Transparent
            }
        return rememberUpdatedState(target)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is DatePickerColorsMMD) return false
        if (containerColor != other.containerColor) return false
        if (titleContentColor != other.titleContentColor) return false
        if (headlineContentColor != other.headlineContentColor) return false
        if (weekdayContentColor != other.weekdayContentColor) return false
        if (subheadContentColor != other.subheadContentColor) return false
        if (yearContentColor != other.yearContentColor) return false
        if (disabledYearContentColor != other.disabledYearContentColor) return false
        if (currentYearContentColor != other.currentYearContentColor) return false
        if (selectedYearContentColor != other.selectedYearContentColor) return false
        if (disabledSelectedYearContentColor != other.disabledSelectedYearContentColor) return false
        if (selectedYearContainerColor != other.selectedYearContainerColor) return false
        if (disabledSelectedYearContainerColor != other.disabledSelectedYearContainerColor)
            return false
        if (dayContentColor != other.dayContentColor) return false
        if (disabledDayContentColor != other.disabledDayContentColor) return false
        if (selectedDayContentColor != other.selectedDayContentColor) return false
        if (disabledSelectedDayContentColor != other.disabledSelectedDayContentColor) return false
        if (selectedDayContainerColor != other.selectedDayContainerColor) return false
        if (disabledSelectedDayContainerColor != other.disabledSelectedDayContainerColor) {
            return false
        }
        if (todayContentColor != other.todayContentColor) return false
        if (todayDateBorderColor != other.todayDateBorderColor) return false
        if (dayInSelectionRangeContainerColor != other.dayInSelectionRangeContainerColor) {
            return false
        }
        if (dayInSelectionRangeContentColor != other.dayInSelectionRangeContentColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = containerColor.hashCode()
        result = 31 * result + titleContentColor.hashCode()
        result = 31 * result + headlineContentColor.hashCode()
        result = 31 * result + weekdayContentColor.hashCode()
        result = 31 * result + subheadContentColor.hashCode()
        result = 31 * result + yearContentColor.hashCode()
        result = 31 * result + disabledYearContentColor.hashCode()
        result = 31 * result + currentYearContentColor.hashCode()
        result = 31 * result + selectedYearContentColor.hashCode()
        result = 31 * result + disabledSelectedYearContentColor.hashCode()
        result = 31 * result + selectedYearContainerColor.hashCode()
        result = 31 * result + disabledSelectedYearContainerColor.hashCode()
        result = 31 * result + dayContentColor.hashCode()
        result = 31 * result + disabledDayContentColor.hashCode()
        result = 31 * result + selectedDayContentColor.hashCode()
        result = 31 * result + disabledSelectedDayContentColor.hashCode()
        result = 31 * result + selectedDayContainerColor.hashCode()
        result = 31 * result + disabledSelectedDayContainerColor.hashCode()
        result = 31 * result + todayContentColor.hashCode()
        result = 31 * result + todayDateBorderColor.hashCode()
        result = 31 * result + dayInSelectionRangeContainerColor.hashCode()
        result = 31 * result + dayInSelectionRangeContentColor.hashCode()
        return result
    }
}

/**
 * An abstract for the date pickers states.
 *
 * This base class common state properties and provides a base implementation that is extended by
 * the different state classes.
 *
 * @param initialDisplayedMonthMillis timestamp in _UTC_ milliseconds from the epoch that represents
 *   an initial selection of a month to be displayed to the user. In case `null` is provided, the
 *   displayed month would be the current one.
 * @param yearRange an [IntRange] that holds the year range that the date picker will be limited to
 * @param selectableDates a [SelectableDatesMMD] that is consulted to check if a date is allowed. In
 *   case a date is not allowed to be selected, it will appear disabled in the UI.
 * @throws [IllegalArgumentException] if the initial selected date or displayed month represent a
 *   year that is out of the year range.
 * @see rememberDatePickerMMDState
 */
@OptIn(ExperimentalMaterial3Api::class)
@Stable
internal abstract class BaseDatePickerStateImpl(
    @Suppress("AutoBoxing") initialDisplayedMonthMillis: Long?,
    val yearRange: IntRange,
    selectableDates: SelectableDatesMMD,
    locale: CalendarLocale,
) {

    val calendarModel = createCalendarModel(locale)

    var selectableDates by mutableStateOf(selectableDates)

    private var _displayedMonth =
        mutableStateOf(
            if (initialDisplayedMonthMillis != null) {
                val month = calendarModel.getMonth(initialDisplayedMonthMillis)
                require(yearRange.contains(month.year)) {
                    "The initial display month's year (${month.year}) is out of the years range of " + "$yearRange."
                }
                month
            } else {
                // Set the displayed month to the current one.
                calendarModel.getMonth(calendarModel.today)
            },
        )

    var displayedMonthMillis: Long
        get() = _displayedMonth.value.startUtcTimeMillis
        set(monthMillis) {
            val month = calendarModel.getMonth(monthMillis)
            require(yearRange.contains(month.year)) {
                "The display month's year (${month.year}) is out of the years range of $yearRange."
            }
            _displayedMonth.value = month
        }
}

/**
 * A default implementation of the [DatePickerStateMMD]. See [rememberDatePickerMMDState].
 *
 * @param initialSelectedDateMillis timestamp in _UTC_ milliseconds from the epoch that represents
 *   an initial selection of a date. Provide a `null` to indicate no selection. Note that the
 *   state's [selectedDateMillis] will provide a timestamp that represents the _start_ of the day,
 *   which may be different than the provided initialSelectedDateMillis.
 * @param initialDisplayedMonthMillis timestamp in _UTC_ milliseconds from the epoch that represents
 *   an initial selection of a month to be displayed to the user. In case `null` is provided, the
 *   displayed month would be the current one.
 * @param yearRange an [IntRange] that holds the year range that the date picker will be limited to
 * @param initialDisplayMode an initial [DisplayModeMMD] that this state will hold
 * @param selectableDates a [SelectableDatesMMD] that is consulted to check if a date is allowed. In
 *   case a date is not allowed to be selected, it will appear disabled in the UI
 * @param locale a [CalendarLocale] to be used when formatting dates, determining the input format,
 *   and more
 * @throws [IllegalArgumentException] if the initial selected date or displayed month represent a
 *   year that is out of the year range.
 * @see rememberDatePickerMMDState
 */
@OptIn(ExperimentalMaterial3Api::class)
@Stable
private class DatePickerStateMMDImpl(
    @Suppress("AutoBoxing") initialSelectedDateMillis: Long?,
    @Suppress("AutoBoxing") initialDisplayedMonthMillis: Long?,
    yearRange: IntRange,
    initialDisplayMode: DisplayModeMMD,
    selectableDates: SelectableDatesMMD,
    locale: CalendarLocale,
) :
    BaseDatePickerStateImpl(initialDisplayedMonthMillis, yearRange, selectableDates, locale),
    DatePickerStateMMD {

    /** A mutable state of [CalendarDateMMD] that represents a selected date. */
    private var _selectedDate =
        mutableStateOf(
            if (initialSelectedDateMillis != null) {
                val date = calendarModel.getCanonicalDate(initialSelectedDateMillis)
                require(yearRange.contains(date.year)) {
                    "The provided initial date's year (${date.year}) is out of the years range of $yearRange."
                }
                date
            } else {
                null
            },
        )

    override var selectedDateMillis: Long?
        @Suppress("AutoBoxing") get() = _selectedDate.value?.utcTimeMillis
        set(@Suppress("AutoBoxing") dateMillis) {
            if (dateMillis != null) {
                val date = calendarModel.getCanonicalDate(dateMillis)
                // Validate that the give date is within the valid years range.
                require(yearRange.contains(date.year)) {
                    "The provided date's year (${date.year}) is out of the years range of $yearRange."
                }
                _selectedDate.value = date
            } else {
                _selectedDate.value = null
            }
        }

    /**
     * A mutable state of [DisplayModeMMD] that represents the current display mode of the UI (i.e.
     * picker or input).
     */
    private var _displayMode = mutableStateOf(initialDisplayMode)

    override var displayMode
        get() = _displayMode.value
        set(displayMode) {
            selectedDateMillis?.let {
                displayedMonthMillis = calendarModel.getMonth(it).startUtcTimeMillis
            }
            _displayMode.value = displayMode
        }

    companion object {
        /**
         * The default [Saver] implementation for [DatePickerStateMMDImpl].
         *
         * @param selectableDates a [SelectableDatesMMD] instance that is consulted to check if a date
         *   is allowed
         */
        fun Saver(
            selectableDates: SelectableDatesMMD,
            locale: CalendarLocale,
        ): Saver<DatePickerStateMMDImpl, Any> =
            listSaver(
                save = {
                    listOf(
                        it.selectedDateMillis,
                        it.displayedMonthMillis,
                        it.yearRange.first,
                        it.yearRange.last,
                        it.displayMode.value,
                    )
                },
                restore = { value ->
                    DatePickerStateMMDImpl(
                        initialSelectedDateMillis = value[0] as Long?,
                        initialDisplayedMonthMillis = value[1] as Long?,
                        yearRange = IntRange(value[2] as Int, value[3] as Int),
                        initialDisplayMode = DisplayModeMMD(value[4] as Int),
                        selectableDates = selectableDates,
                        locale = locale,
                    )
                },
            )
    }
}

/**
 * A date formatter used by [DatePickerMMD].
 *
 * The date formatter will apply the best possible localized form of the given skeleton and Locale.
 * A skeleton is similar to, and uses the same format characters as, a Unicode <a
 * href="http://www.unicode.org/reports/tr35/#Date_Format_Patterns">UTS #35</a> pattern.
 *
 * One difference is that order is irrelevant. For example, "MMMMd" will return "MMMM d" in the
 * `en_US` locale, but "d. MMMM" in the `de_CH` locale.
 *
 * @param yearSelectionSkeleton a date format skeleton used to format the date picker's year
 *   selection menu button (e.g. "March 2021").
 * @param selectedDateSkeleton a date format skeleton used to format a selected date (e.g. "Mar 27,
 *   2021")
 * @param selectedDateDescriptionSkeleton a date format skeleton used to format a selected date to
 *   be used as content description for screen readers (e.g. "Saturday, March 27, 2021")
 */
@OptIn(ExperimentalMaterial3Api::class)
@Immutable
private class DatePickerFormatterMMDImpl(
    val yearSelectionSkeleton: DateSkeleton,
    val selectedDateSkeleton: DateSkeleton,
    val selectedDateDescriptionSkeleton: DateSkeleton,
) : DatePickerFormatterMMD {

    override fun formatMonthYear(monthMillis: Long?, locale: CalendarLocale): String? {
        if (monthMillis == null) return null
        return formatDateWithSkeleton(monthMillis, yearSelectionSkeleton, locale)
    }

    override fun formatDate(
        dateMillis: Long?,
        locale: CalendarLocale,
        forContentDescription: Boolean,
    ): String? {
        if (dateMillis == null) return null
        return formatDateWithSkeleton(
            dateMillis,
            if (forContentDescription) {
                selectedDateDescriptionSkeleton
            } else {
                selectedDateSkeleton
            },
            locale,
        )
    }

    override fun equals(other: Any?): Boolean {
        if (other !is DatePickerFormatterMMDImpl) return false

        if (yearSelectionSkeleton != other.yearSelectionSkeleton) return false
        if (selectedDateSkeleton != other.selectedDateSkeleton) return false
        if (selectedDateDescriptionSkeleton != other.selectedDateDescriptionSkeleton) return false

        return true
    }

    override fun hashCode(): Int {
        var result = yearSelectionSkeleton.hashCode()
        result = 31 * result + selectedDateSkeleton.hashCode()
        result = 31 * result + selectedDateDescriptionSkeleton.hashCode()
        return result
    }
}

/**
 * A base container for the date picker and the date input. This container composes the top common
 * area of the UI, and accepts [content] for the actual calendar picker or text field input.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DateEntryContainer(
    modifier: Modifier,
    title: (@Composable () -> Unit)?,
    headline: (@Composable () -> Unit)?,
    modeToggleButton: (@Composable () -> Unit)?,
    colors: DatePickerColorsMMD,
    headlineTextStyle: TextStyle,
    headerMinHeight: Dp,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .sizeIn(minWidth = ContainerWidth)
            .semantics {
                @Suppress("DEPRECATION")
                isContainer = true
            }
            .background(colors.containerColor),
    ) {
        DatePickerHeader(
            modifier = Modifier,
            title = title,
            titleContentColor = colors.titleContentColor,
            headlineContentColor = colors.headlineContentColor,
            minHeight = headerMinHeight,
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                val horizontalArrangement =
                    when {
                        headline != null && modeToggleButton != null -> Arrangement.SpaceBetween
                        headline != null -> Arrangement.Start
                        else -> Arrangement.End
                    }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = horizontalArrangement,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (headline != null) {
                        ProvideTextStyle(value = headlineTextStyle) {
                            Box(modifier = Modifier.weight(1f)) { headline() }
                        }
                    }
                    modeToggleButton?.invoke()
                }
                // Display a divider only when there is a title, headline, or a mode toggle.
                if (title != null || headline != null || modeToggleButton != null) {
                    HorizontalDivider(color = colors.dividerColor)
                }
            }
        }
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DisplayModeToggleButton(
    modifier: Modifier,
    displayMode: DisplayModeMMD,
    onDisplayModeChange: (DisplayModeMMD) -> Unit,
) {
    if (displayMode == DisplayModeMMD.Picker) {
        IconButton(
            onClick = { onDisplayModeChange(DisplayModeMMD.Input) },
            modifier = modifier,
        ) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = null,
            )
        }
    } else {
        IconButton(
            onClick = { onDisplayModeChange(DisplayModeMMD.Picker) },
            modifier = modifier,
        ) {
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = null,
            )
        }
    }
}

/**
 * Date entry content that displays a [DatePickerContent] or a [DateInputContent] according to the
 * state's display mode.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwitchableDateEntryContent(
    selectedDateMillis: Long?,
    displayedMonthMillis: Long,
    displayMode: DisplayModeMMD,
    onDateSelectionChange: (dateInMillis: Long?) -> Unit,
    onDisplayedMonthChange: (monthInMillis: Long) -> Unit,
    calendarModel: CalendarModelMMD,
    yearRange: IntRange,
    dateFormatter: DatePickerFormatterMMD,
    selectableDates: SelectableDatesMMD,
    colors: DatePickerColorsMMD,
) {
    when (displayMode) {
        DisplayModeMMD.Picker -> DatePickerContent(
            selectedDateMillis = selectedDateMillis,
            displayedMonthMillis = displayedMonthMillis,
            onDateSelectionChange = onDateSelectionChange,
            onDisplayedMonthChange = onDisplayedMonthChange,
            calendarModel = calendarModel,
            yearRange = yearRange,
            dateFormatter = dateFormatter,
            selectableDates = selectableDates,
            colors = colors,
        )

        DisplayModeMMD.Input -> DateInputContent(
            selectedDateMillis = selectedDateMillis,
            onDateSelectionChange = onDateSelectionChange,
            calendarModel = calendarModel,
            yearRange = yearRange,
            dateFormatter = dateFormatter,
            selectableDates = selectableDates,
            colors = colors,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerContent(
    selectedDateMillis: Long?,
    displayedMonthMillis: Long,
    onDateSelectionChange: (dateInMillis: Long) -> Unit,
    onDisplayedMonthChange: (monthInMillis: Long) -> Unit,
    calendarModel: CalendarModelMMD,
    yearRange: IntRange,
    dateFormatter: DatePickerFormatterMMD,
    selectableDates: SelectableDatesMMD,
    colors: DatePickerColorsMMD,
) {
    val displayedMonth = calendarModel.getMonth(displayedMonthMillis)
    val monthIndex = displayedMonth.indexIn(yearRange).coerceAtLeast(0)
    val monthsListState = rememberLazyListState(initialFirstVisibleItemIndex = monthIndex)

    // Scroll to the resolved displayedMonth, if needed.
    LaunchedEffect(monthIndex) {
        // The DatePicker has other actions that can trigger a scroll and update the
        // displayedMonthMillis as they do so, hence we check here for isScrollInProgress and only
        // scroll to the monthIndex when there is none in progress.
        if (
            !monthsListState.isScrollInProgress &&
            monthsListState.firstVisibleItemIndex != monthIndex
        ) {
            monthsListState.scrollToItem(monthIndex)
        }
    }

    val coroutineScope = rememberCoroutineScope()
    var yearPickerVisible by rememberSaveable { mutableStateOf(false) }
    val defaultLocale = defaultLocale()
    Column {
        MonthsNavigation(
            modifier = Modifier.padding(horizontal = DatePickerHorizontalPadding),
            nextAvailable = monthsListState.canScrollForward,
            previousAvailable = monthsListState.canScrollBackward,
            yearPickerVisible = yearPickerVisible,
            yearPickerText = dateFormatter.formatMonthYear(
                monthMillis = displayedMonthMillis,
                locale = defaultLocale,
            ) ?: "-",
            onNextClicked = {
                coroutineScope.launch {
                    try {
                        monthsListState.scrollToItem(
                            monthsListState.firstVisibleItemIndex + 1,
                        )
                    } catch (_: IllegalArgumentException) {
                        // Ignore. This may happen if the user clicked the "next" arrow fast while
                        // the list was still navigating to the next item.
                    }
                }
            },
            onPreviousClicked = {
                coroutineScope.launch {
                    try {
                        monthsListState.scrollToItem(
                            monthsListState.firstVisibleItemIndex - 1,
                        )
                    } catch (_: IllegalArgumentException) {
                        // Ignore. This may happen if the user clicked the "previous" arrow fast
                        // while  the list was still navigating to the previous item.
                    }
                }
            },
            onYearPickerButtonClicked = { yearPickerVisible = !yearPickerVisible },
            colors = colors,
        )

        Box {
            Column(modifier = Modifier.padding(horizontal = DatePickerHorizontalPadding)) {
                WeekDays(colors, calendarModel)
                HorizontalMonthsList(
                    lazyListState = monthsListState,
                    selectedDateMillis = selectedDateMillis,
                    onDateSelectionChange = onDateSelectionChange,
                    onDisplayedMonthChange = onDisplayedMonthChange,
                    calendarModel = calendarModel,
                    yearRange = yearRange,
                    dateFormatter = dateFormatter,
                    selectableDates = selectableDates,
                    colors = colors,
                )
            }
            if (yearPickerVisible) {
                val yearsPaneTitle = "Year picker visible"
                Column(modifier = Modifier.semantics { paneTitle = yearsPaneTitle }) {
                    YearPicker(
                        modifier =
                        Modifier
                            .requiredHeight(
                                RecommendedSizeForAccessibility * (MaxCalendarRows + 1) - DividerDefaults.Thickness,
                            )
                            .padding(horizontal = DatePickerHorizontalPadding),
                        displayedMonthMillis = displayedMonthMillis,
                        onYearSelected = { year ->
                            yearPickerVisible = !yearPickerVisible
                            coroutineScope.launch {
                                monthsListState.scrollToItem(
                                    (year - yearRange.first) * 12 + displayedMonth.month - 1,
                                )
                            }
                        },
                        selectableDates = selectableDates,
                        calendarModel = calendarModel,
                        yearRange = yearRange,
                        colors = colors,
                    )
                    HorizontalDivider(color = colors.dividerColor)
                }
            }
        }
    }
}

@Composable
internal fun DatePickerHeader(
    modifier: Modifier,
    title: (@Composable () -> Unit)?,
    titleContentColor: Color,
    headlineContentColor: Color,
    minHeight: Dp,
    content: @Composable () -> Unit,
) {
    // Apply a defaultMinSize only when the title is not null.
    val heightModifier =
        if (title != null) {
            Modifier.defaultMinSize(minHeight = minHeight)
        } else {
            Modifier
        }
    Column(
        modifier
            .fillMaxWidth()
            .then(heightModifier),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        if (title != null) {
            val textStyle = HeaderSupportingTextFont
            ProvideContentColorTextStyleMMD(
                contentColor = titleContentColor,
                textStyle = textStyle,
            ) {
                Box(contentAlignment = Alignment.BottomStart) { title() }
            }
        }
        CompositionLocalProvider(LocalContentColor provides headlineContentColor, content = content)
    }
}

/** Composes a horizontal pageable list of months. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HorizontalMonthsList(
    lazyListState: LazyListState,
    selectedDateMillis: Long?,
    onDateSelectionChange: (dateInMillis: Long) -> Unit,
    onDisplayedMonthChange: (monthInMillis: Long) -> Unit,
    calendarModel: CalendarModelMMD,
    yearRange: IntRange,
    dateFormatter: DatePickerFormatterMMD,
    selectableDates: SelectableDatesMMD,
    colors: DatePickerColorsMMD,
) {
    val today = calendarModel.today
    val firstMonth =
        remember(yearRange) {
            calendarModel.getMonth(
                year = yearRange.first,
                month = 1, // January
            )
        }
    ProvideTextStyle(DateLabelTextFont) {
        // FIXME: Use LazyRowMMD when available.
        LazyRow(
            // Apply this to prevent the screen reader from scrolling to the next or previous month,
            // and instead, traverse outside the Month composable when swiping from a focused first
            // or last day of the month.
            modifier = Modifier.semantics {
                horizontalScrollAxisRange = ScrollAxisRange(value = { 0f }, maxValue = { 0f })
            },
            state = lazyListState,
            userScrollEnabled = false,
            flingBehavior = DatePickerDefaultsMMD.rememberSnapFlingBehavior(lazyListState),
        ) {
            items(numberOfMonthsInRange(yearRange)) {
                val month = calendarModel.plusMonths(from = firstMonth, addedMonthsCount = it)
                Box(modifier = Modifier.fillParentMaxWidth()) {
                    Month(
                        month = month,
                        onDateSelectionChange = onDateSelectionChange,
                        todayMillis = today.utcTimeMillis,
                        startDateMillis = selectedDateMillis,
                        endDateMillis = null,
                        dateFormatter = dateFormatter,
                        selectableDates = selectableDates,
                        colors = colors,
                    )
                }
            }
        }
    }

    LaunchedEffect(lazyListState) {
        updateDisplayedMonth(
            lazyListState = lazyListState,
            onDisplayedMonthChange = onDisplayedMonthChange,
            calendarModel = calendarModel,
            yearRange = yearRange,
        )
    }
}

internal suspend fun updateDisplayedMonth(
    lazyListState: LazyListState,
    onDisplayedMonthChange: (monthInMillis: Long) -> Unit,
    calendarModel: CalendarModelMMD,
    yearRange: IntRange,
) {
    snapshotFlow { lazyListState.firstVisibleItemIndex }
        .collect {
            val yearOffset = lazyListState.firstVisibleItemIndex / 12
            val month = lazyListState.firstVisibleItemIndex % 12 + 1
            onDisplayedMonthChange(
                calendarModel
                    .getMonth(year = yearRange.first + yearOffset, month = month)
                    .startUtcTimeMillis,
            )
        }
}

/** Composes the weekdays letters. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WeekDays(
    colors: DatePickerColorsMMD,
    calendarModel: CalendarModelMMD,
) {
    val firstDayOfWeek = calendarModel.firstDayOfWeek
    val weekdays = calendarModel.weekdayNames
    val dayNames = arrayListOf<Pair<String, String>>()
    // Start with firstDayOfWeek - 1 as the days are 1-based.
    for (i in firstDayOfWeek - 1 until weekdays.size) {
        dayNames.add(weekdays[i])
    }
    for (i in 0 until firstDayOfWeek - 1) {
        dayNames.add(weekdays[i])
    }
    val textStyle = WeekdaysLabelTextFont

    Row(
        modifier = Modifier
            .defaultMinSize(minHeight = RecommendedSizeForAccessibility)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        dayNames.fastForEach {
            Box(
                modifier = Modifier
                    .clearAndSetSemantics { contentDescription = it.first }
                    .size(
                        width = RecommendedSizeForAccessibility,
                        height = RecommendedSizeForAccessibility,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = it.second,
                    modifier = Modifier.wrapContentSize(),
                    color = colors.weekdayContentColor,
                    style = textStyle,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

/** A composable that renders a calendar month and displays a date selection. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun Month(
    month: CalendarMonthMMD,
    onDateSelectionChange: (dateInMillis: Long) -> Unit,
    todayMillis: Long,
    startDateMillis: Long?,
    endDateMillis: Long?,
    dateFormatter: DatePickerFormatterMMD,
    selectableDates: SelectableDatesMMD,
    colors: DatePickerColorsMMD,
) {
    val defaultLocale = defaultLocale()
    var cellIndex = 0
    Column(
        modifier = Modifier
            .requiredHeight(RecommendedSizeForAccessibility * MaxCalendarRows),
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        for (weekIndex in 0 until MaxCalendarRows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                for (dayIndex in 0 until DaysInWeek) {
                    if (
                        cellIndex < month.daysFromStartOfWeekToFirstOfMonth ||
                        cellIndex >=
                        (month.daysFromStartOfWeekToFirstOfMonth + month.numberOfDays)
                    ) {
                        // Empty cell
                        Spacer(
                            modifier = Modifier.requiredSize(
                                width = RecommendedSizeForAccessibility,
                                height = RecommendedSizeForAccessibility,
                            ),
                        )
                    } else {
                        val dayNumber = cellIndex - month.daysFromStartOfWeekToFirstOfMonth
                        val dateInMillis =
                            month.startUtcTimeMillis + (dayNumber * MillisecondsIn24Hours)
                        val isToday = dateInMillis == todayMillis
                        val startDateSelected = dateInMillis == startDateMillis
                        val endDateSelected = dateInMillis == endDateMillis
                        val dayContentDescription = dayContentDescription(isToday = isToday)
                        val formattedDateDescription =
                            dateFormatter.formatDate(
                                dateInMillis,
                                defaultLocale,
                                forContentDescription = true,
                            ) ?: ""
                        Day(
                            modifier = Modifier,
                            selected = startDateSelected || endDateSelected,
                            onClick = { onDateSelectionChange(dateInMillis) },
                            enabled = remember(dateInMillis, selectableDates) {
                                // Disabled a day in case its year is not selectable, or the
                                // date itself is specifically not allowed by the state's
                                // SelectableDates.
                                with(selectableDates) {
                                    isSelectableYear(month.year) && isSelectableDate(dateInMillis)
                                }
                            },
                            today = isToday,
                            description = if (dayContentDescription != null) {
                                "$dayContentDescription, $formattedDateDescription"
                            } else {
                                formattedDateDescription
                            },
                            colors = colors,
                        ) {
                            Text(
                                text = (dayNumber + 1).toLocalString(),
                                // The semantics are set at the Day level.
                                modifier = Modifier.clearAndSetSemantics {},
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                    cellIndex++
                }
            }
        }
    }
}

/** Returns the number of months within the given year range. */
internal fun numberOfMonthsInRange(yearRange: IntRange) =
    (yearRange.last - yearRange.first + 1) * 12

@Composable
private fun dayContentDescription(
    isToday: Boolean,
): String? {
    val context = LocalContext.current
    val descriptionBuilder = StringBuilder()
    if (isToday) {
        if (descriptionBuilder.isNotEmpty()) descriptionBuilder.append(", ")
        descriptionBuilder.append(context.getString(R.string.date_picker_today))
    }
    return if (descriptionBuilder.isEmpty()) null else descriptionBuilder.toString()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Day(
    modifier: Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
    today: Boolean,
    description: String,
    colors: DatePickerColorsMMD,
    content: @Composable () -> Unit,
) {
    Surface(
        selected = selected,
        onClick = onClick,
        modifier = modifier
            // Apply and merge semantics here. This will ensure that when scrolling the list the
            // entire Day surface is treated as one unit and holds the date semantics even when
            // it's
            // not completely visible atm.
            .semantics(mergeDescendants = true) {
                text = AnnotatedString(description)
                role = Role.Button
            },
        enabled = enabled,
        shape = DayShape,
        color = colors
            .dayContainerColor(selected = selected, enabled = enabled)
            .value,
        contentColor = colors
            .dayContentColor(
                isToday = today,
                selected = selected,
                inRange = false,
                enabled = enabled,
            )
            .value,
        border = if (today && !selected) {
            BorderStroke(
                DateTodayContainerOutlineWidth,
                colors.todayDateBorderColor,
            )
        } else {
            null
        },
    ) {
        Box(
            modifier =
            Modifier.requiredSize(DateStateLayerWidth, DateStateLayerHeight),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun YearPicker(
    modifier: Modifier,
    displayedMonthMillis: Long,
    onYearSelected: (year: Int) -> Unit,
    selectableDates: SelectableDatesMMD,
    calendarModel: CalendarModelMMD,
    yearRange: IntRange,
    colors: DatePickerColorsMMD,
) {
    ProvideTextStyle(value = SelectionYearLabelTextFont) {
        val currentYear = calendarModel.getMonth(calendarModel.today).year
        val displayedYear = calendarModel.getMonth(displayedMonthMillis).year
        val lazyGridState =
            rememberLazyGridState(
                // Set the initial index to a few years before the current year to allow quicker
                // selection of previous years.
                initialFirstVisibleItemIndex = max(0, displayedYear - yearRange.first - YearsInRow),
            )
        // Match the years container color to any elevated surface color that is composed under it.
        val containerColor = colors.containerColor
        val coroutineScope = rememberCoroutineScope()
        val scrollToEarlierYearsLabel = "Scroll to earlier years" // FIXME(No translation)
        val scrollToLaterYearsLabel = "Scroll to later years" // FIXME(No translation)
        // FIXME: Use LazyVerticalGridMMD when available.
        LazyVerticalGrid(
            columns = GridCells.Fixed(YearsInRow),
            modifier = modifier
                .background(containerColor)
                // Apply this to have the screen reader traverse outside the visible list of
                // years
                // and not scroll them by default.
                .semantics {
                    verticalScrollAxisRange = ScrollAxisRange(value = { 0f }, maxValue = { 0f })
                },
            state = lazyGridState,
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalArrangement = Arrangement.spacedBy(YearsVerticalPadding),
        ) {
            items(yearRange.count()) {
                val selectedYear = it + yearRange.first
                val localizedYear = selectedYear.toLocalString()
                Year(
                    modifier = Modifier
                        .requiredSize(
                            width = SelectionYearContainerWidth,
                            height = SelectionYearContainerHeight,
                        )
                        .semantics {
                            // Apply a11y custom actions to the first and last items in the
                            // years
                            // grid. The actions will suggest to scroll to earlier or later
                            // years in
                            // the grid.
                            customActions =
                                if (
                                    lazyGridState.firstVisibleItemIndex == it ||
                                    lazyGridState.layoutInfo.visibleItemsInfo
                                        .lastOrNull()
                                        ?.index == it
                                ) {
                                    customScrollActions(
                                        state = lazyGridState,
                                        coroutineScope = coroutineScope,
                                        scrollUpLabel = scrollToEarlierYearsLabel,
                                        scrollDownLabel = scrollToLaterYearsLabel,
                                    )
                                } else {
                                    emptyList()
                                }
                        },
                    selected = selectedYear == displayedYear,
                    currentYear = selectedYear == currentYear,
                    onClick = { onYearSelected(selectedYear) },
                    enabled = selectableDates.isSelectableYear(selectedYear),
                    colors = colors,
                ) {
                    Text(
                        text = localizedYear,
                        // The semantics are set at the Year level.
                        modifier = Modifier.clearAndSetSemantics {},
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Year(
    modifier: Modifier,
    selected: Boolean,
    currentYear: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
    colors: DatePickerColorsMMD,
    content: @Composable () -> Unit,
) {
    val border =
        remember(currentYear, selected) {
            if (currentYear && !selected) {
                // Use the day's spec to draw a border around the current year.
                BorderStroke(DateTodayContainerOutlineWidth, colors.todayDateBorderColor)
            } else {
                null
            }
        }
    Surface(
        selected = selected,
        onClick = onClick,
        // Apply and merge semantics here. This will ensure that when scrolling the list the entire
        // Year surface is treated as one unit and holds the date semantics even when it's not
        // completely visible atm.
        modifier = modifier.semantics(mergeDescendants = true) {
            role = Role.Button
        },
        enabled = enabled,
        shape = YearShape,
        color = colors.yearContainerColor(selected = selected, enabled = enabled).value,
        contentColor = colors
            .yearContentColor(currentYear = currentYear, selected = selected, enabled = enabled)
            .value,
        border = border,
    ) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { content() }
    }
}

/**
 * A composable that shows a year menu button and a couple of buttons that enable navigation between
 * displayed months.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MonthsNavigation(
    modifier: Modifier,
    nextAvailable: Boolean,
    previousAvailable: Boolean,
    yearPickerVisible: Boolean,
    yearPickerText: String,
    onNextClicked: () -> Unit,
    onPreviousClicked: () -> Unit,
    onYearPickerButtonClicked: () -> Unit,
    colors: DatePickerColorsMMD,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .requiredHeight(MonthYearHeight),
        horizontalArrangement =
        if (yearPickerVisible) {
            Arrangement.Start
        } else {
            Arrangement.SpaceBetween
        },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(LocalContentColor provides colors.navigationContentColor) {
            // A menu button for selecting a year.
            YearPickerMenuButton(
                onClick = onYearPickerButtonClicked,
                expanded = yearPickerVisible,
            ) {
                Text(
                    text = yearPickerText,
                    modifier = Modifier.semantics {
                        // Make the screen reader read out updates to the menu button text as
                        // the
                        // user navigates the arrows or scrolls to change the displayed month.
                        liveRegion = LiveRegionMode.Polite
                        contentDescription = yearPickerText
                    },
                )
            }
            // Show arrows for traversing months (only visible when the year selection is off)
            if (!yearPickerVisible) {
                Row {
                    IconButton(onClick = onPreviousClicked, enabled = previousAvailable) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = null,
                        )
                    }
                    IconButton(onClick = onNextClicked, enabled = nextAvailable) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun YearPickerMenuButton(
    onClick: () -> Unit,
    expanded: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        colors = ButtonDefaults.textButtonColors(contentColor = LocalContentColor.current),
        elevation = null,
        border = null,
    ) {
        content()
        Spacer(Modifier.size(ButtonDefaults.IconSpacing))
        Icon(
            Icons.Filled.ArrowDropDown,
            contentDescription = null,
            Modifier.rotate(if (expanded) 180f else 0f),
        )
    }
}

private fun customScrollActions(
    state: LazyGridState,
    coroutineScope: CoroutineScope,
    scrollUpLabel: String,
    scrollDownLabel: String,
): List<CustomAccessibilityAction> {
    val scrollUpAction = {
        if (!state.canScrollBackward) {
            false
        } else {
            coroutineScope.launch { state.scrollToItem(state.firstVisibleItemIndex - YearsInRow) }
            true
        }
    }
    val scrollDownAction = {
        if (!state.canScrollForward) {
            false
        } else {
            coroutineScope.launch { state.scrollToItem(state.firstVisibleItemIndex + YearsInRow) }
            true
        }
    }
    return listOf(
        CustomAccessibilityAction(label = scrollUpLabel, action = scrollUpAction),
        CustomAccessibilityAction(label = scrollDownLabel, action = scrollDownAction),
    )
}

internal val RecommendedSizeForAccessibility = 48.dp
internal val MonthYearHeight = 56.dp
internal val DatePickerHorizontalPadding = 12.dp
internal val DatePickerModeTogglePadding = PaddingValues(end = 12.dp, bottom = 12.dp)

private val DatePickerTitlePadding = PaddingValues(start = 24.dp, end = 12.dp, top = 16.dp)
private val DatePickerHeadlinePadding = PaddingValues(start = 24.dp, end = 12.dp, bottom = 12.dp)

private val YearsVerticalPadding = 16.dp

private const val MaxCalendarRows = 6
private const val YearsInRow: Int = 3

private val DateTodayContainerOutlineWidth = 1.dp

// Year
private val YearShape = RoundedCornerShape(50)

// YearPicker
private val SelectionYearContainerHeight = 36.0.dp
private val SelectionYearContainerWidth = 72.0.dp
private val SelectionYearLabelTextFont
    @Composable get() = MaterialTheme.typography.bodyLarge

// Day
private val DayShape = RoundedCornerShape(50)
private val DateStateLayerWidth = 40.0.dp
private val DateStateLayerHeight = 40.0.dp

// Weekdays
private val WeekdaysLabelTextFont
    @Composable get() = MaterialTheme.typography.bodyLarge

// HorizontalMonthsList
private val DateLabelTextFont
    @Composable get() = MaterialTheme.typography.bodyLarge

// DatePickerHeader
private val HeaderSupportingTextFont
    @Composable get() = MaterialTheme.typography.labelLarge

// DateEntryContainer
private val ContainerWidth = 350.0.dp

// DatePicker
private val HeaderHeadlineFont
    @Composable get() = MaterialTheme.typography.headlineLarge
private val HeaderContainerHeight = 120.0.dp
