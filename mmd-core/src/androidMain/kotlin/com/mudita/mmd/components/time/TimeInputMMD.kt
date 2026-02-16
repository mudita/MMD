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

@file:OptIn(ExperimentalMaterial3Api::class)

package com.mudita.mmd.components.time

import android.text.format.DateFormat.is24HourFormat
import androidx.annotation.IntRange
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.utf16CodePoint
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.InspectorValueInfo
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.zIndex
import com.mudita.mmd.R
import com.mudita.mmd.internal.bottom
import com.mudita.mmd.internal.date_picker.toLocalString
import com.mudita.mmd.internal.top

/**
 * Time pickers help users select and set a specific time.
 *
 * Shows a time input that allows the user to enter the time via two text fields, one for minutes
 * and one for hours Subscribe to updates through [TimeInputStateMMD]
 *
 *```kotlin
 * @OptIn(ExperimentalMaterial3Api::class)
 * @Composable
 * fun TimeInputMMDSample(modifier: Modifier = Modifier) {
 *     val stateTime = rememberTimeInputMMDState()
 *
 *     Column(
 *         modifier = modifier
 *             .padding(8.dp)
 *             .verticalScroll(rememberScrollState())
 *     ) {
 *         TimeInputMMD(stateTime)
 *     }
 * }
 * ```
 *
 * @param state state for this timepicker, allows to subscribe to changes to [TimeInputStateMMD.hour]
 *   and [TimeInputStateMMD.minute], and set the initial time for this picker.
 * @param modifier the [Modifier] to be applied to this time input
 * @param colors colors [TimeInputColorsMMD] that will be used to resolve the colors used for this
 *   time input in different states. See [TimeInputDefaultsMMD.colors].
 */
@Composable
@ExperimentalMaterial3Api
fun TimeInputMMD(
    state: TimeInputStateMMD,
    modifier: Modifier = Modifier,
    colors: TimeInputColorsMMD = TimeInputDefaultsMMD.colors(),
) {
    TimeInputImpl(modifier, colors, state)
}

/** Contains the default values used by [TimeInputMMD] */
@ExperimentalMaterial3Api
@Stable
object TimeInputDefaultsMMD {

    /** Default colors used by a [TimeInputMMD] in different states */
    @Composable
    fun colors() = defaultTimeInputColors

    /**
     * Default colors used by a [TimeInputMMD] in different states
     *
     * @param periodSelectorBorderColor the color used for the border of the AM/PM toggle.
     * @param periodSelectorSelectedContainerColor the color used for the selected container of the
     *   AM/PM toggle
     * @param periodSelectorUnselectedContainerColor the color used for the unselected container of
     *   the AM/PM toggle
     * @param periodSelectorSelectedContentColor color used for the selected content of the AM/PM
     *   toggle
     * @param periodSelectorUnselectedContentColor color used for the unselected content of the
     *   AM/PM toggle
     * @param timeSelectorSelectedContainerColor color used for the selected container of the
     *   display buttons to switch between hour and minutes
     * @param timeSelectorUnselectedContainerColor color used for the unselected container of the
     *   display buttons to switch between hour and minutes
     * @param timeSelectorSelectedContentColor color used for the selected content of the display
     *   buttons to switch between hour and minutes
     * @param timeSelectorUnselectedContentColor color used for the unselected content of the
     *   display buttons to switch between hour and minutes
     */
    @Composable
    fun colors(
        periodSelectorBorderColor: Color = Color.Unspecified,
        periodSelectorSelectedContainerColor: Color = Color.Unspecified,
        periodSelectorUnselectedContainerColor: Color = Color.Unspecified,
        periodSelectorSelectedContentColor: Color = Color.Unspecified,
        periodSelectorUnselectedContentColor: Color = Color.Unspecified,
        timeSelectorSelectedContainerColor: Color = Color.Unspecified,
        timeSelectorUnselectedContainerColor: Color = Color.Unspecified,
        timeSelectorSelectedContentColor: Color = Color.Unspecified,
        timeSelectorUnselectedContentColor: Color = Color.Unspecified,
    ) =
        defaultTimeInputColors.copy(
            periodSelectorBorderColor = periodSelectorBorderColor,
            periodSelectorSelectedContainerColor = periodSelectorSelectedContainerColor,
            periodSelectorUnselectedContainerColor = periodSelectorUnselectedContainerColor,
            periodSelectorSelectedContentColor = periodSelectorSelectedContentColor,
            periodSelectorUnselectedContentColor = periodSelectorUnselectedContentColor,
            timeSelectorSelectedContainerColor = timeSelectorSelectedContainerColor,
            timeSelectorUnselectedContainerColor = timeSelectorUnselectedContainerColor,
            timeSelectorSelectedContentColor = timeSelectorSelectedContentColor,
            timeSelectorUnselectedContentColor = timeSelectorUnselectedContentColor,
        )

    private val defaultTimeInputColors: TimeInputColorsMMD
        @Composable get() = TimeInputColorsMMD(
            periodSelectorBorderColor = PeriodSelectorOutlineColor,
            periodSelectorSelectedContainerColor = PeriodSelectorSelectedContainerColor,
            periodSelectorUnselectedContainerColor = Color.Transparent,
            periodSelectorSelectedContentColor = PeriodSelectorSelectedLabelTextColor,
            periodSelectorUnselectedContentColor = PeriodSelectorUnselectedLabelTextColor,
            timeSelectorSelectedContainerColor = TimeSelectorSelectedContainerColor,
            timeSelectorUnselectedContainerColor = TimeSelectorUnselectedContainerColor,
            timeSelectorSelectedContentColor = TimeSelectorSelectedLabelTextColor,
            timeSelectorUnselectedContentColor = TimeSelectorUnselectedLabelTextColor,
        )
}

/**
 * Represents the colors used by a [TimeInputMMD] in different states
 *
 * @param periodSelectorBorderColor the color used for the border of the AM/PM toggle.
 * @param periodSelectorSelectedContainerColor the color used for the selected container of the
 *   AM/PM toggle
 * @param periodSelectorUnselectedContainerColor the color used for the unselected container of the
 *   AM/PM toggle
 * @param periodSelectorSelectedContentColor color used for the selected content of the AM/PM toggle
 * @param periodSelectorUnselectedContentColor color used for the unselected content of the AM/PM
 *   toggle
 * @param timeSelectorSelectedContainerColor color used for the selected container of the display
 *   buttons to switch between hour and minutes
 * @param timeSelectorUnselectedContainerColor color used for the unselected container of the
 *   display buttons to switch between hour and minutes
 * @param timeSelectorSelectedContentColor color used for the selected content of the display
 *   buttons to switch between hour and minutes
 * @param timeSelectorUnselectedContentColor color used for the unselected content of the display
 *   buttons to switch between hour and minutes
 * @constructor create an instance with arbitrary colors. See [TimeInputDefaultsMMD.colors] for the
 *   default implementation that follows Material specifications.
 */
@Immutable
@ExperimentalMaterial3Api
class TimeInputColorsMMD(
    val periodSelectorBorderColor: Color,
    val periodSelectorSelectedContainerColor: Color,
    val periodSelectorUnselectedContainerColor: Color,
    val periodSelectorSelectedContentColor: Color,
    val periodSelectorUnselectedContentColor: Color,
    val timeSelectorSelectedContainerColor: Color,
    val timeSelectorUnselectedContainerColor: Color,
    val timeSelectorSelectedContentColor: Color,
    val timeSelectorUnselectedContentColor: Color,
) {
    /**
     * Returns a copy of this TimePickerColors, optionally overriding some of the values. This uses
     * the Color.Unspecified to mean “use the value from the source”
     */
    fun copy(
        periodSelectorBorderColor: Color = this.periodSelectorBorderColor,
        periodSelectorSelectedContainerColor: Color = this.periodSelectorSelectedContainerColor,
        periodSelectorUnselectedContainerColor: Color = this.periodSelectorUnselectedContainerColor,
        periodSelectorSelectedContentColor: Color = this.periodSelectorSelectedContentColor,
        periodSelectorUnselectedContentColor: Color = this.periodSelectorUnselectedContentColor,
        timeSelectorSelectedContainerColor: Color = this.timeSelectorSelectedContainerColor,
        timeSelectorUnselectedContainerColor: Color = this.timeSelectorUnselectedContainerColor,
        timeSelectorSelectedContentColor: Color = this.timeSelectorSelectedContentColor,
        timeSelectorUnselectedContentColor: Color = this.timeSelectorUnselectedContentColor,
    ) =
        TimeInputColorsMMD(
            periodSelectorBorderColor.takeOrElse { this.periodSelectorBorderColor },
            periodSelectorSelectedContainerColor.takeOrElse {
                this.periodSelectorSelectedContainerColor
            },
            periodSelectorUnselectedContainerColor.takeOrElse {
                this.periodSelectorUnselectedContainerColor
            },
            periodSelectorSelectedContentColor.takeOrElse {
                this.periodSelectorSelectedContentColor
            },
            periodSelectorUnselectedContentColor.takeOrElse {
                this.periodSelectorUnselectedContentColor
            },
            timeSelectorSelectedContainerColor.takeOrElse {
                this.timeSelectorSelectedContainerColor
            },
            timeSelectorUnselectedContainerColor.takeOrElse {
                this.timeSelectorUnselectedContainerColor
            },
            timeSelectorSelectedContentColor.takeOrElse { this.timeSelectorSelectedContentColor },
            timeSelectorUnselectedContentColor.takeOrElse {
                this.timeSelectorUnselectedContentColor
            },
        )

    @Stable
    internal fun periodSelectorContainerColor(selected: Boolean) =
        if (selected) {
            periodSelectorSelectedContainerColor
        } else {
            periodSelectorUnselectedContainerColor
        }

    @Stable
    internal fun periodSelectorContentColor(selected: Boolean) =
        if (selected) {
            periodSelectorSelectedContentColor
        } else {
            periodSelectorUnselectedContentColor
        }

    @Stable
    internal fun timeSelectorContainerColor(selected: Boolean) =
        if (selected) {
            timeSelectorSelectedContainerColor
        } else {
            timeSelectorUnselectedContainerColor
        }

    @Stable
    internal fun timeSelectorContentColor(selected: Boolean) =
        if (selected) {
            timeSelectorSelectedContentColor
        } else {
            timeSelectorUnselectedContentColor
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other === null) return false
        if (this::class != other::class) return false

        other as TimeInputColorsMMD

        if (periodSelectorBorderColor != other.periodSelectorBorderColor) return false
        if (periodSelectorSelectedContainerColor != other.periodSelectorSelectedContainerColor)
            return false
        if (periodSelectorUnselectedContainerColor != other.periodSelectorUnselectedContainerColor)
            return false
        if (periodSelectorSelectedContentColor != other.periodSelectorSelectedContentColor)
            return false
        if (periodSelectorUnselectedContentColor != other.periodSelectorUnselectedContentColor)
            return false
        if (timeSelectorSelectedContainerColor != other.timeSelectorSelectedContainerColor)
            return false
        if (timeSelectorUnselectedContainerColor != other.timeSelectorUnselectedContainerColor)
            return false
        if (timeSelectorSelectedContentColor != other.timeSelectorSelectedContentColor) return false
        if (timeSelectorUnselectedContentColor != other.timeSelectorUnselectedContentColor)
            return false

        return true
    }

    override fun hashCode(): Int {
        var result = periodSelectorBorderColor.hashCode()
        result = 31 * result + periodSelectorSelectedContainerColor.hashCode()
        result = 31 * result + periodSelectorUnselectedContainerColor.hashCode()
        result = 31 * result + periodSelectorSelectedContentColor.hashCode()
        result = 31 * result + periodSelectorUnselectedContentColor.hashCode()
        result = 31 * result + timeSelectorSelectedContainerColor.hashCode()
        result = 31 * result + timeSelectorUnselectedContainerColor.hashCode()
        result = 31 * result + timeSelectorSelectedContentColor.hashCode()
        result = 31 * result + timeSelectorUnselectedContentColor.hashCode()
        return result
    }
}

/**
 * Creates a [TimeInputStateMMD] for a time picker that is remembered across compositions and
 * configuration changes.
 *
 * @param initialHour starting hour for this state, will be displayed in the time picker when
 *   launched. Ranges from 0 to 23
 * @param initialMinute starting minute for this state, will be displayed in the time picker when
 *   launched. Ranges from 0 to 59
 * @param is24Hour The format for this time picker. `false` for 12 hour format with an AM/PM toggle
 *   or `true` for 24 hour format without toggle. Defaults to follow system setting.
 */
@Composable
@ExperimentalMaterial3Api
fun rememberTimeInputMMDState(
    initialHour: Int = 0,
    initialMinute: Int = 0,
    is24Hour: Boolean = is24HourFormat,
): TimeInputStateMMD {
    val state: TimeInputStateImpl =
        rememberSaveable(saver = TimeInputStateImpl.Saver()) {
            TimeInputStateImpl(
                initialHour = initialHour,
                initialMinute = initialMinute,
                is24Hour = is24Hour,
            )
        }

    return state
}

/**
 * A state object that can be hoisted to observe the time picker state. It holds the current values
 * and allows for directly setting those values.
 *
 * @see rememberTimeInputMMDState to construct the default implementation.
 */
@ExperimentalMaterial3Api
interface TimeInputStateMMD {

    /** The currently selected minute (0-59). */
    @get:IntRange(from = 0, to = 59)
    @setparam:IntRange(from = 0, to = 59)
    var minute: Int

    /** The currently selected hour (0-23). */
    @get:IntRange(from = 0, to = 23)
    @setparam:IntRange(from = 0, to = 23)
    var hour: Int

    /**
     * Indicates whether the time picker uses 24-hour format (`true`) or 12-hour format with AM/PM
     * (`false`).
     */
    var is24hour: Boolean

    /** Specifies whether the hour or minute component is being actively selected by the user. */
    var selection: TimePickerSelectionMode

    /** Indicates whether the selected time falls within the afternoon period (12 PM - 12 AM). */
    var isAfternoon: Boolean
}

/**
 * Factory function for the default implementation of [TimeInputStateMMD] [rememberTimeInputMMDState]
 * should be used in most cases.
 *
 * @param initialHour starting hour for this state, will be displayed in the time picker when
 *   launched Ranges from 0 to 23
 * @param initialMinute starting minute for this state, will be displayed in the time picker when
 *   launched. Ranges from 0 to 59
 * @param is24Hour The format for this time picker. `false` for 12 hour format with an AM/PM toggle
 *   or `true` for 24 hour format without toggle. Defaults to follow system setting.
 */
@ExperimentalMaterial3Api
fun TimeInputStateMMD(
    initialHour: Int,
    initialMinute: Int,
    is24Hour: Boolean,
): TimeInputStateMMD =
    TimeInputStateImpl(initialHour, initialMinute, is24Hour)

/** The selection mode for the time picker */
@JvmInline
@ExperimentalMaterial3Api
value class TimePickerSelectionMode private constructor(val value: Int) {
    companion object {
        val Hour = TimePickerSelectionMode(0)
        val Minute = TimePickerSelectionMode(1)
    }

    override fun toString(): String =
        when (this) {
            Hour -> "Hour"
            Minute -> "Minute"
            else -> ""
        }
}

private class TimeInputStateImpl(
    initialHour: Int,
    initialMinute: Int,
    is24Hour: Boolean,
) : TimeInputStateMMD {
    init {
        require(initialHour in 0..23) { "initialHour should in [0..23] range" }
        require(initialMinute in 0..59) { "initialMinute should be in [0..59] range" }
    }

    override var is24hour: Boolean = is24Hour

    override var selection by mutableStateOf(TimePickerSelectionMode.Hour)

    override var isAfternoon by mutableStateOf(initialHour >= 12)

    val hourState = mutableIntStateOf(initialHour % 12)

    val minuteState = mutableIntStateOf(initialMinute)

    override var minute: Int
        get() = minuteState.intValue
        set(value) {
            minuteState.intValue = value
        }

    override var hour: Int
        get() = hourState.intValue + if (isAfternoon) 12 else 0
        set(value) {
            isAfternoon = value >= 12
            hourState.intValue = value % 12
        }

    companion object {
        /** The default [Saver] implementation for [TimeInputStateMMD]. */
        fun Saver(): Saver<TimeInputStateImpl, *> =
            Saver(
                save = { listOf(it.hour, it.minute, it.is24hour) },
                restore = { value ->
                    TimeInputStateImpl(
                        initialHour = value[0] as Int,
                        initialMinute = value[1] as Int,
                        is24Hour = value[2] as Boolean,
                    )
                },
            )
    }
}

internal val TimeInputStateMMD.hourForDisplay: Int
    get() = when {
        is24hour -> hour % 24
        hour % 12 == 0 -> 12
        isAfternoon -> hour - 12
        else -> hour
    }

@Composable
private fun TimeInputImpl(
    modifier: Modifier,
    colors: TimeInputColorsMMD,
    state: TimeInputStateMMD,
) {
    var hourValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(text = state.hourForDisplay.toLocalString(minDigits = 2)))
    }
    var minuteValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(text = state.minute.toLocalString(minDigits = 2)))
    }
    Row(
        modifier = modifier.padding(bottom = TimeInputBottomPadding),
        verticalAlignment = Alignment.Top,
    ) {
        val textStyle = TimeFieldLabelTextFont.copy(
            textAlign = TextAlign.Center,
            color = colors.timeSelectorContentColor(true),
        )

        CompositionLocalProvider(
            LocalTextStyle provides textStyle,
            // Always display the time input text field from left to right.
            LocalLayoutDirection provides LayoutDirection.Ltr,
        ) {
            Row {
                TimePickerTextField(
                    modifier = Modifier.onKeyEvent { event ->
                        // Zero == 48, Nine == 57
                        val switchFocus =
                            event.utf16CodePoint in 48..57 && hourValue.selection.start == 2 && hourValue.text.length == 2

                        if (switchFocus) {
                            state.selection = TimePickerSelectionMode.Minute
                        }

                        false
                    },
                    value = hourValue,
                    onValueChange = { newValue ->
                        timeInputOnChange(
                            selection = TimePickerSelectionMode.Hour,
                            state = state,
                            value = newValue,
                            prevValue = hourValue,
                            max = if (state.is24hour) 23 else 12,
                        ) {
                            hourValue = it
                        }
                    },
                    state = state,
                    selection = TimePickerSelectionMode.Hour,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Number,
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { state.selection = TimePickerSelectionMode.Minute },
                    ),
                    colors = colors,
                )
                DisplaySeparator(
                    Modifier.size(DisplaySeparatorWidth, PeriodSelectorContainerHeight),
                )
                TimePickerTextField(
                    modifier = Modifier.onPreviewKeyEvent { event ->
                        // 0 == KEYCODE_DEL
                        val switchFocus =
                            event.utf16CodePoint == 0 && minuteValue.selection.start == 0

                        if (switchFocus) {
                            state.selection = TimePickerSelectionMode.Hour
                        }

                        switchFocus
                    },
                    value = minuteValue,
                    onValueChange = { newValue ->
                        timeInputOnChange(
                            selection = TimePickerSelectionMode.Minute,
                            state = state,
                            value = newValue,
                            prevValue = minuteValue,
                            max = 59,
                        ) {
                            minuteValue = it
                        }
                    },
                    state = state,
                    selection = TimePickerSelectionMode.Minute,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Number,
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { state.selection = TimePickerSelectionMode.Minute },
                    ),
                    colors = colors,
                )
            }
        }

        if (!state.is24hour) {
            Box(Modifier.padding(start = PeriodToggleMargin)) {
                VerticalPeriodToggle(
                    modifier =
                    Modifier.size(PeriodSelectorContainerWidth, PeriodSelectorContainerHeight),
                    state = state,
                    colors = colors,
                )
            }
        }
    }
}

@Composable
private fun VerticalPeriodToggle(
    modifier: Modifier,
    state: TimeInputStateMMD,
    colors: TimeInputColorsMMD,
) {
    val measurePolicy = remember {
        MeasurePolicy { measurables, constraints ->
            val spacer = measurables.fastFirst { it.layoutId == "Spacer" }
            val spacerPlaceable =
                spacer.measure(
                    constraints.copy(
                        minHeight = 0,
                        maxHeight = PeriodSelectorOutlineWidth.roundToPx(),
                    ),
                )

            val items = measurables
                .fastFilter { it.layoutId != "Spacer" }
                .fastMap { item ->
                    item.measure(
                        constraints.copy(minHeight = 0, maxHeight = constraints.maxHeight / 2),
                    )
                }

            layout(constraints.maxWidth, constraints.maxHeight) {
                items[0].place(0, 0)
                items[1].place(0, items[0].height)
                spacerPlaceable.place(0, items[0].height - spacerPlaceable.height / 2)
            }
        }
    }

    val shape = PeriodSelectorContainerShape as CornerBasedShape

    PeriodToggleImpl(
        modifier = modifier,
        state = state,
        colors = colors,
        measurePolicy = measurePolicy,
        startShape = shape.top(),
        endShape = shape.bottom(),
    )
}

@Composable
private fun PeriodToggleImpl(
    modifier: Modifier,
    state: TimeInputStateMMD,
    colors: TimeInputColorsMMD,
    measurePolicy: MeasurePolicy,
    startShape: Shape,
    endShape: Shape,
) {
    val context = LocalContext.current
    val borderStroke = BorderStroke(PeriodSelectorOutlineWidth, colors.periodSelectorBorderColor)
    val shape = PeriodSelectorContainerShape as CornerBasedShape
    val contentDescription = context.getString(R.string.time_picker_period_toggle_description)

    Layout(
        modifier = modifier
            .semantics {
                isTraversalGroup = true
                this.contentDescription = contentDescription
            }
            .selectableGroup()
            .border(border = borderStroke, shape = shape),
        measurePolicy = measurePolicy,
        content = {
            ToggleItem(
                checked = !state.isAfternoon,
                shape = startShape,
                onClick = { state.isAfternoon = false },
                colors = colors,
            ) {
                Text(text = context.getString(R.string.time_picker_am))
            }
            Spacer(
                Modifier
                    .layoutId("Spacer")
                    .zIndex(SeparatorZIndex)
                    .fillMaxSize()
                    .background(color = colors.periodSelectorBorderColor),
            )
            ToggleItem(
                checked = state.isAfternoon,
                shape = endShape,
                onClick = { state.isAfternoon = true },
                colors = colors,
            ) {
                Text(context.getString(R.string.time_picker_pm))
            }
        },
    )
}

@Composable
private fun ToggleItem(
    checked: Boolean,
    shape: Shape,
    onClick: () -> Unit,
    colors: TimeInputColorsMMD,
    content: @Composable RowScope.() -> Unit,
) {
    val contentColor = colors.periodSelectorContentColor(checked)
    val containerColor = colors.periodSelectorContainerColor(checked)

    TextButton(
        modifier = Modifier
            .zIndex(if (checked) 0f else 1f)
            .fillMaxSize()
            .semantics { selected = checked },
        contentPadding = PaddingValues(0.dp),
        shape = shape,
        onClick = onClick,
        content = content,
        colors =
        ButtonDefaults.textButtonColors(
            contentColor = contentColor,
            containerColor = containerColor,
        ),
    )
}

@Composable
private fun DisplaySeparator(modifier: Modifier) {
    val style = LocalTextStyle.current.copy(
        textAlign = TextAlign.Center,
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both,
        ),
    )

    Box(
        modifier = modifier.clearAndSetSemantics {},
        contentAlignment = Alignment.Center,
    ) {
        Text(text = ":", color = TimeFieldSeparatorColor, style = style)
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TimeSelector(
    modifier: Modifier,
    value: Int,
    state: TimeInputStateMMD,
    selection: TimePickerSelectionMode,
    colors: TimeInputColorsMMD,
) {
    val context = LocalContext.current
    val selected = state.selection == selection
    val selectorContentDescription =
        context.getString(
            if (selection == TimePickerSelectionMode.Hour) {
                R.string.time_picker_hour_selection
            } else {
                R.string.time_picker_minute_selection
            },
        )

    val containerColor = colors.timeSelectorContainerColor(selected)
    val contentColor = colors.timeSelectorContentColor(selected)
    Surface(
        modifier = modifier.semantics(mergeDescendants = true) {
            role = Role.RadioButton
            this.contentDescription = selectorContentDescription
        },
        onClick = {
            if (selection != state.selection) {
                state.selection = selection
            }
        },
        selected = selected,
        shape = TimeSelectorContainerShape,
        color = containerColor,
    ) {
        val valueContentDescription = numberContentDescription(
            selection = selection,
            is24Hour = state.is24hour,
            number = value,
        )

        Box(contentAlignment = Alignment.Center) {
            Text(
                modifier = Modifier.semantics { contentDescription = valueContentDescription },
                text = value.toLocalString(minDigits = 2),
                color = contentColor,
            )
        }
    }
}

private fun timeInputOnChange(
    selection: TimePickerSelectionMode,
    state: TimeInputStateMMD,
    value: TextFieldValue,
    prevValue: TextFieldValue,
    max: Int,
    onNewValue: (value: TextFieldValue) -> Unit,
) {
    if (value.text == prevValue.text) {
        // just selection change
        onNewValue(value)
        return
    }

    if (value.text.isEmpty()) {
        if (selection == TimePickerSelectionMode.Hour) {
            state.hour = 0
        } else {
            state.minute = 0
        }
        onNewValue(value.copy(text = ""))
        return
    }

    try {
        val newValue =
            if (value.text.length == 3 && value.selection.start == 1) {
                value.text[0].digitToInt()
            } else {
                value.text.toInt()
            }

        if (newValue <= max) {
            if (selection == TimePickerSelectionMode.Hour) {
                state.hour = newValue
                if (newValue > 1 && !state.is24hour) {
                    state.selection = TimePickerSelectionMode.Minute
                }
            } else {
                state.minute = newValue
            }

            onNewValue(
                if (value.text.length <= 2) {
                    value
                } else {
                    value.copy(text = value.text[0].toString())
                },
            )
        }
    } catch (_: NumberFormatException) {
    } catch (_: IllegalArgumentException) {
        // do nothing no state update
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerTextField(
    modifier: Modifier,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    state: TimeInputStateMMD,
    selection: TimePickerSelectionMode,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    colors: TimeInputColorsMMD,
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val focusRequester = remember { FocusRequester() }
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = colors.timeSelectorContainerColor(true),
        unfocusedContainerColor = colors.timeSelectorContainerColor(true),
        focusedTextColor = colors.timeSelectorContentColor(true),
    )
    val selected = selection == state.selection
    Column(modifier = modifier) {
        if (!selected) {
            TimeSelector(
                modifier = Modifier.size(TimeFieldContainerWidth, TimeFieldContainerHeight),
                value = if (selection == TimePickerSelectionMode.Hour) {
                    state.hourForDisplay
                } else {
                    state.minute
                },
                state = state,
                selection = selection,
                colors = colors,
            )
        }

        val contentDescription = context.getString(
            if (selection == TimePickerSelectionMode.Minute) {
                R.string.time_picker_minute_text_field
            } else {
                R.string.time_picker_hour_text_field
            },
        )

        Box(Modifier.visible(selected)) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .size(TimeFieldContainerWidth, TimeFieldContainerHeight)
                    .semantics { this.contentDescription = contentDescription },
                interactionSource = interactionSource,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                textStyle = LocalTextStyle.current,
                enabled = true,
                singleLine = true,
                cursorBrush = Brush.verticalGradient(
                    0.00f to Color.Transparent,
                    0.10f to Color.Transparent,
                    0.10f to MaterialTheme.colorScheme.primary,
                    0.90f to MaterialTheme.colorScheme.primary,
                    0.90f to Color.Transparent,
                    1.00f to Color.Transparent,
                ),
            ) {
                OutlinedTextFieldDefaults.DecorationBox(
                    value = value.text,
                    visualTransformation = VisualTransformation.None,
                    innerTextField = it,
                    singleLine = true,
                    colors = textFieldColors,
                    enabled = true,
                    interactionSource = interactionSource,
                    contentPadding = PaddingValues(0.dp),
                    container = {
                        OutlinedTextFieldDefaults.Container(
                            enabled = true,
                            isError = false,
                            interactionSource = interactionSource,
                            shape = TimeFieldContainerShape,
                            colors = textFieldColors,
                        )
                    },
                )
            }
        }

        Text(
            modifier = Modifier
                .offset(y = SupportLabelTop)
                .clearAndSetSemantics {},
            text = context.getString(
                if (selection == TimePickerSelectionMode.Hour) {
                    R.string.time_picker_hour
                } else {
                    R.string.time_picker_minute
                },
            ),
            color = TimeFieldSupportingTextColor,
            style = TimeFieldSupportingTextFont,
        )
    }

    LaunchedEffect(state.selection) {
        if (state.selection == selection) {
            focusRequester.requestFocus()
        }
    }
}

@Composable
@ReadOnlyComposable
internal fun numberContentDescription(
    selection: TimePickerSelectionMode,
    is24Hour: Boolean,
    number: Int,
): String {
    val context = LocalContext.current
    val id = if (selection == TimePickerSelectionMode.Minute) {
        R.string.time_picker_minute_suffix
    } else if (is24Hour) {
        R.string.time_picker_hour_24h_suffix
    } else {
        R.string.time_picker_hour_suffix
    }

    return context.getString(id, number)
}

private const val SeparatorZIndex = 2f
private val DisplaySeparatorWidth = 24.dp
private val SupportLabelTop = 7.dp
private val TimeInputBottomPadding = 24.dp
private val PeriodToggleMargin = 12.dp

private val TimeFieldContainerShape = RoundedCornerShape(8.0.dp)
private val TimeSelectorContainerShape = RoundedCornerShape(8.0.dp)
private val PeriodSelectorContainerShape = RoundedCornerShape(8.0.dp)

private val TimeFieldContainerWidth = 96.0.dp
private val TimeFieldContainerHeight = 72.0.dp
private val PeriodSelectorOutlineWidth = 1.0.dp
private val PeriodSelectorContainerHeight = 72.0.dp
private val PeriodSelectorContainerWidth = 52.0.dp

private val TimeFieldSupportingTextFont: TextStyle
    @Composable get() = MaterialTheme.typography.bodySmall

private val TimeFieldSeparatorColor: Color
    @Composable get() = MaterialTheme.colorScheme.onSurface

private val TimeFieldSupportingTextColor: Color
    @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

private val TimeFieldLabelTextFont: TextStyle
    @Composable get() = MaterialTheme.typography.displayMedium

private val PeriodSelectorOutlineColor: Color
    @Composable get() = MaterialTheme.colorScheme.outline

private val PeriodSelectorSelectedContainerColor: Color
    @Composable get() = MaterialTheme.colorScheme.tertiaryContainer

private val TimeSelectorSelectedContainerColor: Color
    @Composable get() = MaterialTheme.colorScheme.primaryContainer

private val TimeSelectorSelectedLabelTextColor: Color
    @Composable get() = MaterialTheme.colorScheme.onPrimaryContainer

private val TimeSelectorUnselectedLabelTextColor: Color
    @Composable get() = MaterialTheme.colorScheme.onSurface

private val PeriodSelectorSelectedLabelTextColor: Color
    @Composable get() = MaterialTheme.colorScheme.onTertiaryContainer

private val TimeSelectorUnselectedContainerColor: Color
    @Composable get() = MaterialTheme.colorScheme.surfaceContainerHighest

private val PeriodSelectorUnselectedLabelTextColor: Color
    @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

/**
 * Measure the composable with 0,0 so that it stays on the screen. Necessary to correctly handle
 * focus
 */
@Stable
private fun Modifier.visible(visible: Boolean) =
    this.then(
        VisibleModifier(
            visible,
            debugInspectorInfo {
                name = "visible"
                properties["visible"] = visible
            },
        ),
    )

private class VisibleModifier(val visible: Boolean, inspectorInfo: InspectorInfo.() -> Unit) :
    LayoutModifier, InspectorValueInfo(inspectorInfo) {

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val placeable = measurable.measure(constraints)

        if (!visible) {
            return layout(0, 0) {}
        }
        return layout(placeable.width, placeable.height) { placeable.place(0, 0) }
    }

    override fun hashCode(): Int = visible.hashCode()

    override fun equals(other: Any?): Boolean {
        val otherModifier = other as? VisibleModifier ?: return false
        return visible == otherModifier.visible
    }
}

internal val is24HourFormat: Boolean
    @Composable @ReadOnlyComposable get() = is24HourFormat(LocalContext.current)
