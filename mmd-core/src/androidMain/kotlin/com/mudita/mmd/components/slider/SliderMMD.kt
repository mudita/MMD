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

package com.mudita.mmd.components.slider

import androidx.annotation.IntRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.DragScope
import androidx.compose.foundation.gestures.DraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.lerp
import com.mudita.mmd.utils.IncreaseHorizontalSemanticsBounds
import kotlinx.coroutines.coroutineScope
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 *
 * SlidersMMD allow users to make selections from a range of values.
 *
 * It uses [SliderDefaultsMMD.Thumb] and [SliderDefaultsMMD.Track] as the thumb and track.
 *
 * SlidersMMD reflect a range of values along a bar, from which users may select a single value. They
 * are ideal for adjusting settings such as volume, brightness, or applying image filters.

 * Use continuous sliders to allow users to make meaningful selections that don’t require a specific
 * value:
 *
 *```kotlin
 * @Preview
 * @Composable
 * fun SliderMMDSample() {
 *     var sliderPosition by remember { mutableStateOf(0f) }
 *     Column(modifier = Modifier.padding(horizontal = 16.dp)) {
 *         Text(text = "%.2f".format(sliderPosition))
 *         SliderMMD(
 *             modifier = Modifier.semantics { contentDescription = "Localized Description" },
 *             value = sliderPosition,
 *             onValueChange = { sliderPosition = it }
 *         )
 *     }
 * }
 * ```
 *
 * You can allow the user to choose only between predefined set of values by specifying the amount
 * of steps between min and max values:
 *
 *```kotlin
 * @Preview
 * @Composable
 * fun StepsSliderMMDSample() {
 *     var sliderPosition by remember { mutableStateOf(0f) }
 *     Column(modifier = Modifier.padding(horizontal = 16.dp)) {
 *         Text(text = sliderPosition.roundToInt().toString())
 *         SliderMMD(
 *             modifier = Modifier.semantics { contentDescription = "Localized Description" },
 *             value = sliderPosition,
 *             onValueChange = { sliderPosition = it },
 *             valueRange = 0f..100f,
 *             onValueChangeFinished = {
 *                 // launch some business logic update with the state you hold
 *                 // viewModel.updateSelectedSliderValue(sliderPosition)
 *             },
 *             // Only allow multiples of 10. Excluding the endpoints of `valueRange`,
 *             // there are 9 steps (10, 20, ..., 90).
 *             steps = 9
 *         )
 *     }
 * }
 *```
 *
 * @param value current value of the slider. If outside of [valueRange] provided, value will be
 *   coerced to this range.
 * @param onValueChange callback in which value should be updated
 * @param modifier the [Modifier] to be applied to this slider
 * @param enabled controls the enabled state of this slider. When `false`, this component will not
 *   respond to user input, and it will appear visually disabled and disabled to accessibility
 *   services.
 * @param valueRange range of values that this slider can take. The passed [value] will be coerced
 *   to this range.
 * @param steps if positive, specifies the amount of discrete allowable values (in addition to the
 *   endpoints of the value range). Step values are evenly distributed across the range. If 0, the
 *   slider will behave continuously and allow any value from the range. Must not be negative.
 * @param onValueChangeFinished called when value change has ended. This should not be used to
 *   update the slider value (use [onValueChange] instead), but rather to know when the user has
 *   completed selecting a new value by ending a drag or a click.
 * @param colors [SliderColorsMMD] that will be used to resolve the colors used for this slider in
 *   different states. See [SliderDefaultsMMD.colors].
 * @param interactionSource the [MutableInteractionSource] representing the stream of [Interaction]s
 *   for this slider. You can create and pass in your own `remember`ed instance to observe
 *   [Interaction]s and customize the appearance / behavior of this slider in different states.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SliderMMD(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    @IntRange(from = 0) steps: Int = 0,
    onValueChangeFinished: (() -> Unit)? = null,
    colors: SliderColorsMMD = SliderDefaultsMMD.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    SliderMMD(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        onValueChangeFinished = onValueChangeFinished,
        colors = colors,
        interactionSource = interactionSource,
        steps = steps,
        thumb = {
            SliderDefaultsMMD.Thumb(
                interactionSource = interactionSource,
                colors = colors,
                enabled = enabled,
            )
        },
        track = { sliderState ->
            SliderDefaultsMMD.Track(colors = colors, enabled = enabled, sliderState = sliderState)
        },
        valueRange = valueRange,
    )
}

/**
 *
 * SlidersMMD allow users to make selections from a range of values.
 *
 * SlidersMMD reflect a range of values along a bar, from which users may select a single value. They
 * are ideal for adjusting settings such as volume, brightness, or applying image filters.
 *
 * Use continuous sliders to allow users to make meaningful selections that don’t require a specific
 * value:
 *
 *```kotlin
 * @Preview
 * @Composable
 * fun SliderMMDSample() {
 *     var sliderPosition by remember { mutableStateOf(0f) }
 *     Column(modifier = Modifier.padding(horizontal = 16.dp)) {
 *         Text(text = "%.2f".format(sliderPosition))
 *         SliderMMD(
 *             modifier = Modifier.semantics { contentDescription = "Localized Description" },
 *             value = sliderPosition,
 *             onValueChange = { sliderPosition = it }
 *         )
 *     }
 * }
 * ```
 *
 * You can allow the user to choose only between predefined set of values by specifying the amount
 * of steps between min and max values:
 *
 *```kotlin
 * @Preview
 * @Composable
 * fun StepsSliderMMDSample() {
 *     var sliderPosition by remember { mutableStateOf(0f) }
 *     Column(modifier = Modifier.padding(horizontal = 16.dp)) {
 *         Text(text = sliderPosition.roundToInt().toString())
 *         SliderMMD(
 *             modifier = Modifier.semantics { contentDescription = "Localized Description" },
 *             value = sliderPosition,
 *             onValueChange = { sliderPosition = it },
 *             valueRange = 0f..100f,
 *             onValueChangeFinished = {
 *                 // launch some business logic update with the state you hold
 *                 // viewModel.updateSelectedSliderValue(sliderPosition)
 *             },
 *             // Only allow multiples of 10. Excluding the endpoints of `valueRange`,
 *             // there are 9 steps (10, 20, ..., 90).
 *             steps = 9
 *         )
 *     }
 * }
 *```
 *
 * SliderMMD using a custom thumb:
 *
 * ```kotlin
 * @OptIn(ExperimentalMaterial3Api::class)
 * @Preview
 * @Composable
 * fun SliderMMDWithCustomThumbSample() {
 *     var sliderPosition by remember { mutableStateOf(0f) }
 *     val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
 *     Column(modifier = Modifier.padding(horizontal = 16.dp)) {
 *         SliderMMD(
 *             modifier = Modifier.semantics { contentDescription = "Localized Description" },
 *             value = sliderPosition,
 *             onValueChange = { sliderPosition = it },
 *             valueRange = 0f..100f,
 *             interactionSource = interactionSource,
 *             onValueChangeFinished = {
 *                 // launch some business logic update with the state you hold
 *                 // viewModel.updateSelectedSliderValue(sliderPosition)
 *             },
 *             thumb = {
 *                 Label(
 *                     label = {
 *                         PlainTooltip(modifier = Modifier.sizeIn(45.dp, 25.dp).wrapContentWidth()) {
 *                             Text("%.2f".format(sliderPosition))
 *                         }
 *                     },
 *                     interactionSource = interactionSource
 *                 ) {
 *                     Icon(
 *                         imageVector = Icons.Filled.Favorite,
 *                         contentDescription = null,
 *                         modifier = Modifier.size(ButtonDefaults.IconSize),
 *                         tint = Color.Red
 *                     )
 *                 }
 *             }
 *         )
 *     }
 * }
 * ```
 *
 * SliderMMD using custom track and thumb:
 *
 * ```kotlin
 * @OptIn(ExperimentalMaterial3Api::class)
 * @Preview
 * @Composable
 * fun SliderMMDWithCustomTrackAndThumb() {
 *     val sliderState = remember {
 *         SliderStateMMD(
 *             valueRange = 0f..100f,
 *             onValueChangeFinished = {
 *                 // launch some business logic update with the state you hold
 *                 // viewModel.updateSelectedSliderValue(sliderPosition)
 *             }
 *         )
 *     }
 *     val interactionSource = remember { MutableInteractionSource() }
 *     val colors = SliderDefaultsMMD.colors(thumbColor = Color.Red, activeTrackColor = Color.Red)
 *     Column(modifier = Modifier.padding(horizontal = 16.dp)) {
 *         Text(text = "%.2f".format(sliderState.value))
 *         SliderMMD(
 *             state = sliderState,
 *             modifier = Modifier.semantics { contentDescription = "Localized Description" },
 *             interactionSource = interactionSource,
 *             thumb = {
 *                 SliderDefaultsMMD.Thumb(interactionSource = interactionSource, colors = colors)
 *             },
 *             track = { SliderDefaultsMMD.Track(colors = colors, sliderState = sliderState) }
 *         )
 *     }
 * }
 * ```
 *
 * @param value current value of the slider. If outside of [valueRange] provided, value will be
 *   coerced to this range.
 * @param onValueChange callback in which value should be updated
 * @param modifier the [Modifier] to be applied to this slider
 * @param enabled controls the enabled state of this slider. When `false`, this component will not
 *   respond to user input, and it will appear visually disabled and disabled to accessibility
 *   services.
 * @param onValueChangeFinished called when value change has ended. This should not be used to
 *   update the slider value (use [onValueChange] instead), but rather to know when the user has
 *   completed selecting a new value by ending a drag or a click.
 * @param colors [SliderColorsMMD] that will be used to resolve the colors used for this slider in
 *   different states. See [SliderDefaultsMMD.colors].
 * @param interactionSource the [MutableInteractionSource] representing the stream of [Interaction]s
 *   for this slider. You can create and pass in your own `remember`ed instance to observe
 *   [Interaction]s and customize the appearance / behavior of this slider in different states.
 * @param steps if positive, specifies the amount of discrete allowable values (in addition to the
 *   endpoints of the value range). Step values are evenly distributed across the range. If 0, the
 *   slider will behave continuously and allow any value from the range. Must not be negative.
 * @param thumb the thumb to be displayed on the slider, it is placed on top of the track. The
 *   lambda receives a [SliderStateMMD] which is used to obtain the current active track.
 * @param track the track to be displayed on the slider, it is placed underneath the thumb. The
 *   lambda receives a [SliderStateMMD] which is used to obtain the current active track.
 * @param valueRange range of values that this slider can take. The passed [value] will be coerced
 *   to this range.
 */
@Composable
@ExperimentalMaterial3Api
fun SliderMMD(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onValueChangeFinished: (() -> Unit)? = null,
    colors: SliderColorsMMD = SliderDefaultsMMD.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    @IntRange(from = 0) steps: Int = 0,
    thumb: @Composable (SliderStateMMD) -> Unit = {
        SliderDefaultsMMD.Thumb(
            interactionSource = interactionSource,
            colors = colors,
            enabled = enabled,
        )
    },
    track: @Composable (SliderStateMMD) -> Unit = { sliderState ->
        SliderDefaultsMMD.Track(colors = colors, enabled = enabled, sliderState = sliderState)
    },
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
) {
    val state =
        remember(steps, valueRange) {
            SliderStateMMD(
                value,
                steps,
                onValueChangeFinished,
                valueRange,
            )
        }

    state.onValueChangeFinished = onValueChangeFinished
    state.onValueChange = onValueChange
    state.value = value

    SliderMMD(
        state = state,
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource,
        thumb = thumb,
        track = track,
    )
}

/**
 *
 * Sliders allow users to make selections from a range of values.
 *
 * Sliders reflect a range of values along a bar, from which users may select a single value. They
 * are ideal for adjusting settings such as volume, brightness, or applying image filters.
 *
 * Use continuous sliders to allow users to make meaningful selections that don’t require a specific
 * value:
 *
 *```kotlin
 * @Preview
 * @Composable
 * fun SliderSample() {
 *     var sliderPosition by remember { mutableStateOf(0f) }
 *     Column(modifier = Modifier.padding(horizontal = 16.dp)) {
 *         Text(text = "%.2f".format(sliderPosition))
 *         SliderMMD(
 *             modifier = Modifier.semantics { contentDescription = "Localized Description" },
 *             value = sliderPosition,
 *             onValueChange = { sliderPosition = it }
 *         )
 *     }
 * }
 * ```
 *
 * You can allow the user to choose only between predefined set of values by specifying the amount
 * of steps between min and max values:
 *
 *```kotlin
 * @Preview
 * @Composable
 * fun StepsSliderSample() {
 *     var sliderPosition by remember { mutableStateOf(0f) }
 *     Column(modifier = Modifier.padding(horizontal = 16.dp)) {
 *         Text(text = sliderPosition.roundToInt().toString())
 *         SliderMMD(
 *             modifier = Modifier.semantics { contentDescription = "Localized Description" },
 *             value = sliderPosition,
 *             onValueChange = { sliderPosition = it },
 *             valueRange = 0f..100f,
 *             onValueChangeFinished = {
 *                 // launch some business logic update with the state you hold
 *                 // viewModel.updateSelectedSliderValue(sliderPosition)
 *             },
 *             // Only allow multiples of 10. Excluding the endpoints of `valueRange`,
 *             // there are 9 steps (10, 20, ..., 90).
 *             steps = 9
 *         )
 *     }
 * }
 *```
 *
 * Slider using a custom thumb:
 *
 * ```kotlin
 * @OptIn(ExperimentalMaterial3Api::class)
 * @Preview
 * @Composable
 * fun SliderWithCustomThumbSample() {
 *     var sliderPosition by remember { mutableStateOf(0f) }
 *     val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
 *     Column(modifier = Modifier.padding(horizontal = 16.dp)) {
 *         SliderMMD(
 *             modifier = Modifier.semantics { contentDescription = "Localized Description" },
 *             value = sliderPosition,
 *             onValueChange = { sliderPosition = it },
 *             valueRange = 0f..100f,
 *             interactionSource = interactionSource,
 *             onValueChangeFinished = {
 *                 // launch some business logic update with the state you hold
 *                 // viewModel.updateSelectedSliderValue(sliderPosition)
 *             },
 *             thumb = {
 *                 Label(
 *                     label = {
 *                         PlainTooltip(modifier = Modifier.sizeIn(45.dp, 25.dp).wrapContentWidth()) {
 *                             Text("%.2f".format(sliderPosition))
 *                         }
 *                     },
 *                     interactionSource = interactionSource
 *                 ) {
 *                     Icon(
 *                         imageVector = Icons.Filled.Favorite,
 *                         contentDescription = null,
 *                         modifier = Modifier.size(ButtonDefaults.IconSize),
 *                         tint = Color.Red
 *                     )
 *                 }
 *             }
 *         )
 *     }
 * }
 * ```
 *
 * Slider using custom track and thumb:
 *
 * ```kotlin
 * @OptIn(ExperimentalMaterial3Api::class)
 * @Preview
 * @Composable
 * fun SliderWithCustomTrackAndThumb() {
 *     val sliderState = remember {
 *         SliderStateMMD(
 *             valueRange = 0f..100f,
 *             onValueChangeFinished = {
 *                 // launch some business logic update with the state you hold
 *                 // viewModel.updateSelectedSliderValue(sliderPosition)
 *             }
 *         )
 *     }
 *     val interactionSource = remember { MutableInteractionSource() }
 *     val colors = SliderDefaults.colors(thumbColor = Color.Red, activeTrackColor = Color.Red)
 *     Column(modifier = Modifier.padding(horizontal = 16.dp)) {
 *         Text(text = "%.2f".format(sliderState.value))
 *         SliderMMD(
 *             state = sliderState,
 *             modifier = Modifier.semantics { contentDescription = "Localized Description" },
 *             interactionSource = interactionSource,
 *             thumb = {
 *                 SliderDefaultsMMD.Thumb(interactionSource = interactionSource, colors = colors)
 *             },
 *             track = { SliderDefaultsMMD.Track(colors = colors, sliderState = sliderState) }
 *         )
 *     }
 * }
 * ```
 *
 * @param state [SliderStateMMD] which contains the slider's current value.
 * @param modifier the [Modifier] to be applied to this slider
 * @param enabled controls the enabled state of this slider. When `false`, this component will not
 *   respond to user input, and it will appear visually disabled and disabled to accessibility
 *   services.
 * @param colors [SliderColorsMMD] that will be used to resolve the colors used for this slider in
 *   different states. See [SliderDefaultsMMD.colors].
 * @param interactionSource the [MutableInteractionSource] representing the stream of [Interaction]s
 *   for this slider. You can create and pass in your own `remember`ed instance to observe
 *   [Interaction]s and customize the appearance / behavior of this slider in different states.
 * @param thumb the thumb to be displayed on the slider, it is placed on top of the track. The
 *   lambda receives a [SliderStateMMD] which is used to obtain the current active track.
 * @param track the track to be displayed on the slider, it is placed underneath the thumb. The
 *   lambda receives a [SliderStateMMD] which is used to obtain the current active track.
 */
@Composable
@ExperimentalMaterial3Api
fun SliderMMD(
    state: SliderStateMMD,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: SliderColorsMMD = SliderDefaultsMMD.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    thumb: @Composable (SliderStateMMD) -> Unit = {
        SliderDefaultsMMD.Thumb(
            interactionSource = interactionSource,
            colors = colors,
            enabled = enabled,
        )
    },
    track: @Composable (SliderStateMMD) -> Unit = { sliderState ->
        SliderDefaultsMMD.Track(colors = colors, enabled = enabled, sliderState = sliderState)
    },
) {
    require(state.steps >= 0) { "steps should be >= 0" }

    SliderMMDImpl(
        state = state,
        modifier = modifier,
        enabled = enabled,
        interactionSource = interactionSource,
        thumb = thumb,
        track = track,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SliderMMDImpl(
    modifier: Modifier,
    state: SliderStateMMD,
    enabled: Boolean,
    interactionSource: MutableInteractionSource,
    thumb: @Composable (SliderStateMMD) -> Unit,
    track: @Composable (SliderStateMMD) -> Unit,
) {
    state.isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val press = Modifier.sliderTapModifier(state, interactionSource, enabled)
    val drag =
        Modifier.draggable(
            orientation = Orientation.Horizontal,
            reverseDirection = state.isRtl,
            enabled = enabled,
            interactionSource = interactionSource,
            onDragStopped = { state.gestureEndAction() },
            startDragImmediately = state.isDragging,
            state = state,
        )

    Layout(
        {
            Box(
                modifier =
                Modifier
                    .layoutId(SliderComponents.THUMB)
                    .wrapContentWidth()
                    .onSizeChanged {
                        state.thumbWidth = it.width.toFloat()
                    },
            ) {
                thumb(state)
            }
            Box(modifier = Modifier.layoutId(SliderComponents.TRACK)) { track(state) }
        },
        modifier =
        modifier
            .minimumInteractiveComponentSize()
            .requiredSizeIn(minWidth = ThumbWidth, minHeight = TrackHeight)
            .sliderSemantics(state, enabled)
            .focusable(enabled, interactionSource)
            .then(press)
            .then(drag),
    ) { measurables, constraints ->
        val thumbPlaceable =
            measurables.fastFirst { it.layoutId == SliderComponents.THUMB }.measure(constraints)

        val trackPlaceable =
            measurables
                .fastFirst { it.layoutId == SliderComponents.TRACK }
                .measure(constraints.offset(horizontal = -thumbPlaceable.width).copy(minHeight = 0))

        val sliderWidth = thumbPlaceable.width + trackPlaceable.width
        val sliderHeight = max(trackPlaceable.height, thumbPlaceable.height)

        state.updateDimensions(trackPlaceable.height.toFloat(), sliderWidth)

        val trackOffsetX = thumbPlaceable.width / 2
        val thumbOffsetX = ((trackPlaceable.width) * state.coercedValueAsFraction).roundToInt()
        val trackOffsetY = (sliderHeight - trackPlaceable.height) / 2
        val thumbOffsetY = (sliderHeight - thumbPlaceable.height) / 2

        layout(sliderWidth, sliderHeight) {
            trackPlaceable.placeRelative(trackOffsetX, trackOffsetY)
            thumbPlaceable.placeRelative(thumbOffsetX, thumbOffsetY)
        }
    }
}

/** Object to hold defaults used by [SliderMMD] */
@Stable
object SliderDefaultsMMD {

    /**
     * Creates a [SliderColorsMMD] that represents the different colors used in parts of the [SliderMMD]
     * in different states.
     */
    @Composable
    fun colors() = defaultSliderColors

    /**
     * Creates a [SliderColorsMMD] that represents the different colors used in parts of the [SliderMMD]
     * in different states.
     *
     * For the name references below the words "active" and "inactive" are used. Active part of the
     * slider is filled with progress, so if slider's progress is 30% out of 100%, left (or right in
     * RTL) 30% of the track will be active, while the rest is inactive.
     *
     * @param thumbColor thumb color when enabled
     * @param thumbBorderColor thumb border color when enabled
     * @param activeTrackColor color of the track in the part that is "active", meaning that the
     *   thumb is ahead of it
     * @param trackBorderColor color of the track border when enabled
     * @param activeTickColor colors to be used to draw tick marks on the active track, if `steps`
     *   is specified
     * @param inactiveTrackColor color of the track in the part that is "inactive", meaning that the
     *   thumb is before it
     * @param inactiveTickColor colors to be used to draw tick marks on the inactive track, if
     *   `steps` are specified on the Slider is specified
     * @param disabledThumbColor thumb colors when disabled]
     * @param disabledTrackBorderColor color of the track border when disabled
     * @param disabledActiveTrackColor color of the track in the "active" part when the Slider is
     *   disabled
     * @param disabledActiveTickColor colors to be used to draw tick marks on the active track when
     *   Slider is disabled and when `steps` are specified on it
     * @param disabledInactiveTrackColor color of the track in the "inactive" part when the Slider
     *   is disabled
     * @param disabledInactiveTickColor colors to be used to draw tick marks on the inactive part of
     *   the track when Slider is disabled and when `steps` are specified on it
     */
    @Composable
    fun colors(
        thumbColor: Color = Color.Unspecified,
        thumbBorderColor: Color = Color.Unspecified,
        activeTrackColor: Color = Color.Unspecified,
        trackBorderColor: Color = Color.Unspecified,
        activeTickColor: Color = Color.Unspecified,
        inactiveTrackColor: Color = Color.Unspecified,
        inactiveTickColor: Color = Color.Unspecified,
        disabledThumbColor: Color = Color.Unspecified,
        disabledThumbBorderColor: Color = Color.Unspecified,
        disabledTrackBorderColor: Color = Color.Unspecified,
        disabledActiveTrackColor: Color = Color.Unspecified,
        disabledActiveTickColor: Color = Color.Unspecified,
        disabledInactiveTrackColor: Color = Color.Unspecified,
        disabledInactiveTickColor: Color = Color.Unspecified,
    ): SliderColorsMMD = defaultSliderColors.copy(
        thumbColor = thumbColor,
        thumbBorderColor = thumbBorderColor,
        activeTrackColor = activeTrackColor,
        trackBorderColor = trackBorderColor,
        activeTickColor = activeTickColor,
        inactiveTrackColor = inactiveTrackColor,
        inactiveTickColor = inactiveTickColor,
        disabledThumbColor = disabledThumbColor,
        disabledThumbBorderColor = disabledThumbBorderColor,
        disabledTrackBorderColor = disabledTrackBorderColor,
        disabledActiveTrackColor = disabledActiveTrackColor,
        disabledActiveTickColor = disabledActiveTickColor,
        disabledInactiveTrackColor = disabledInactiveTrackColor,
        disabledInactiveTickColor = disabledInactiveTickColor,
    )

    private val defaultSliderColors: SliderColorsMMD
        @Composable
        get() = SliderColorsMMD(
            thumbColor = MaterialTheme.colorScheme.primary,
            thumbBorderColor = MaterialTheme.colorScheme.onPrimary,
            activeTrackColor = MaterialTheme.colorScheme.primary,
            trackBorderColor = MaterialTheme.colorScheme.primary,
            activeTickColor = MaterialTheme.colorScheme.secondary,
            inactiveTrackColor = MaterialTheme.colorScheme.secondary,
            inactiveTickColor = MaterialTheme.colorScheme.primary,
            disabledThumbColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
            disabledThumbBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f),
            disabledTrackBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            disabledActiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            disabledActiveTickColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            disabledInactiveTrackColor = MaterialTheme.colorScheme.onPrimary,
            disabledInactiveTickColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        )

    /**
     * The Default thumb for [SliderMMD]
     *
     * @param interactionSource the [MutableInteractionSource] representing the stream of
     *   [Interaction]s for this thumb. You can create and pass in your own `remember`ed instance to
     *   observe
     * @param modifier the [Modifier] to be applied to the thumb.
     * @param colors [SliderColorsMMD] that will be used to resolve the colors used for this thumb in
     *   different states. See [SliderDefaultsMMD.colors].
     * @param enabled controls the enabled state of this slider. When `false`, this component will
     *   not respond to user input, and it will appear visually disabled and disabled to
     *   accessibility services.
     * @param thumbSize the size of the thumb.
     */
    @Composable
    fun Thumb(
        interactionSource: MutableInteractionSource,
        modifier: Modifier = Modifier,
        colors: SliderColorsMMD = colors(),
        enabled: Boolean = true,
        thumbSize: Dp = ThumbSize,
        thumbBorderSize: Dp = ThumbBorderSize,
    ) {
        val interactions = remember { mutableStateListOf<Interaction>() }
        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> interactions.add(interaction)
                    is PressInteraction.Release -> interactions.remove(interaction.press)
                    is PressInteraction.Cancel -> interactions.remove(interaction.press)
                    is DragInteraction.Start -> interactions.add(interaction)
                    is DragInteraction.Stop -> interactions.remove(interaction.start)
                    is DragInteraction.Cancel -> interactions.remove(interaction.start)
                }
            }
        }

        Box(modifier = modifier) {
            Spacer(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(thumbBorderSize)
                    .background(colors.thumbBorderColor(enabled), ThumbShape),
            )
            Spacer(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(thumbSize)
                    .background(colors.thumbColor(enabled), ThumbShape),
            )
        }
    }

    /**
     * The Default track for [SliderMMD]
     *
     * @param sliderState [SliderStateMMD] which is used to obtain the current active track.
     * @param modifier the [Modifier] to be applied to the track.
     * @param enabled controls the enabled state of this slider. When `false`, this component will
     *   not respond to user input, and it will appear visually disabled and disabled to
     *   accessibility services.
     * @param colors [SliderColorsMMD] that will be used to resolve the colors used for this track in
     *   different states. See [SliderDefaultsMMD.colors].
     * @param drawStopIndicator lambda that will be called to draw the stop indicator at the end of
     *   the track.
     * @param drawTick lambda that will be called to draw the ticks if steps are greater than 0.
     * @param thumbTrackGapSize size of the gap between the thumb and the track.
     * @param trackInsideCornerSize size of the corners towards the thumb when a gap is set.
     * @param trackBorderShape shape of the track border.
     * @param trackBorderSize size of the track border.
     */
    @ExperimentalMaterial3Api
    @Composable
    fun Track(
        sliderState: SliderStateMMD,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        colors: SliderColorsMMD = colors(),
        drawStopIndicator: (DrawScope.(Offset) -> Unit)? = {
            if (sliderState.steps > 0) {
                drawStopIndicator(
                    drawScope = this,
                    offset = it,
                    color = colors.trackColor(enabled, active = true),
                    size = TrackStopIndicatorSize,
                )
            }
        },
        drawTick: DrawScope.(Offset, Color) -> Unit = { offset, color ->
            if (sliderState.steps > 0) {
                drawStopIndicator(
                    drawScope = this,
                    offset = offset,
                    color = color,
                    size = TickSize,
                )
            }
        },
        thumbTrackGapSize: Dp = ThumbTrackGapSize,
        trackInsideCornerSize: Dp = TrackInsideCornerSize,
        trackBorderShape: RoundedCornerShape = TrackBorderShape,
        trackBorderSize: Dp = TrackBorderSize,
    ) {
        val inactiveTrackColor = colors.trackColor(enabled, active = false)
        val activeTrackColor = colors.trackColor(enabled, active = true)
        val inactiveTickColor = colors.tickColor(enabled, active = false)
        val activeTickColor = colors.tickColor(enabled, active = true)
        val borderColor = colors.trackBorderColor(enabled)
        Canvas(
            modifier
                .fillMaxWidth()
                .height(TrackHeight)
                .border(width = trackBorderSize, color = borderColor, shape = trackBorderShape)
                .rotate(if (LocalLayoutDirection.current == LayoutDirection.Rtl) 180f else 0f),
        ) {
            drawTrack(
                sliderState.tickFractions,
                0f,
                sliderState.coercedValueAsFraction,
                inactiveTrackColor,
                activeTrackColor,
                inactiveTickColor,
                activeTickColor,
                sliderState.trackHeight.toDp(),
                0.toDp(),
                sliderState.thumbWidth.toDp(),
                thumbTrackGapSize,
                trackInsideCornerSize,
                drawStopIndicator,
                drawTick,
                isRangeSlider = false,
            )
        }
    }

    private fun DrawScope.drawTrack(
        tickFractions: FloatArray,
        activeRangeStart: Float,
        activeRangeEnd: Float,
        inactiveTrackColor: Color,
        activeTrackColor: Color,
        inactiveTickColor: Color,
        activeTickColor: Color,
        height: Dp,
        startThumbWidth: Dp,
        endThumbWidth: Dp,
        thumbTrackGapSize: Dp,
        trackInsideCornerSize: Dp,
        drawStopIndicator: (DrawScope.(Offset) -> Unit)?,
        drawTick: DrawScope.(Offset, Color) -> Unit,
        isRangeSlider: Boolean,
    ) {
        val sliderStart = Offset(0f, center.y)
        val sliderEnd = Offset(size.width, center.y)
        val trackStrokeWidth = height.toPx()

        val sliderValueEnd =
            Offset(sliderStart.x + (sliderEnd.x - sliderStart.x) * activeRangeEnd, center.y)

        val sliderValueStart =
            Offset(sliderStart.x + (sliderEnd.x - sliderStart.x) * activeRangeStart, center.y)

        val cornerSize = trackStrokeWidth / 2
        val insideCornerSize = trackInsideCornerSize.toPx()
        var startGap = 0f
        var endGap = 0f
        if (thumbTrackGapSize > 0.dp) {
            startGap = startThumbWidth.toPx() / 2 + thumbTrackGapSize.toPx()
            endGap = endThumbWidth.toPx() / 2 + thumbTrackGapSize.toPx()
        }

        // inactive track (range slider)
        if (isRangeSlider && sliderValueStart.x > sliderStart.x + startGap + cornerSize) {
            val start = sliderStart.x
            val end = sliderValueStart.x - startGap
            drawTrackPath(
                Offset.Zero,
                Size(end - start, trackStrokeWidth),
                inactiveTrackColor,
                cornerSize,
                insideCornerSize,
            )
            drawStopIndicator?.invoke(this, Offset(start + cornerSize, center.y))
        }
        // inactive track
        if (sliderValueEnd.x < sliderEnd.x - endGap - cornerSize) {
            val start = sliderValueEnd.x + endGap
            val end = sliderEnd.x
            drawTrackPath(
                Offset(start, 0f),
                Size(end - start, trackStrokeWidth),
                inactiveTrackColor,
                insideCornerSize,
                cornerSize,
            )
            drawStopIndicator?.invoke(this, Offset(end - cornerSize, center.y))
        }
        // active track
        val activeTrackStart = if (isRangeSlider) sliderValueStart.x + startGap else 0f
        val activeTrackEnd = sliderValueEnd.x - endGap
        val startCornerRadius = if (isRangeSlider) insideCornerSize else cornerSize
        if (activeTrackEnd - activeTrackStart > startCornerRadius) {
            drawTrackPath(
                Offset(activeTrackStart, 0f),
                Size(activeTrackEnd - activeTrackStart, trackStrokeWidth),
                activeTrackColor,
                startCornerRadius,
                insideCornerSize,
            )
        }

        val start = Offset(sliderStart.x + cornerSize, sliderStart.y)
        val end = Offset(sliderEnd.x - cornerSize, sliderEnd.y)
        val tickStartGap = sliderValueStart.x - startGap..sliderValueStart.x + startGap
        val tickEndGap = sliderValueEnd.x - endGap..sliderValueEnd.x + endGap
        tickFractions.forEachIndexed { index, tick ->
            // skip ticks that fall on the stop indicator
            if (drawStopIndicator != null) {
                if ((isRangeSlider && index == 0) || index == tickFractions.size - 1) {
                    return@forEachIndexed
                }
            }

            val outsideFraction = tick > activeRangeEnd || tick < activeRangeStart
            val center = Offset(androidx.compose.ui.geometry.lerp(start, end, tick).x, center.y)
            // skip ticks that fall on a gap
            if ((isRangeSlider && center.x in tickStartGap) || center.x in tickEndGap) {
                return@forEachIndexed
            }
            drawTick(
                this,
                center, // offset
                if (outsideFraction) inactiveTickColor else activeTickColor, // color
            )
        }
    }

    private fun DrawScope.drawTrackPath(
        offset: Offset,
        size: Size,
        color: Color,
        startCornerRadius: Float,
        endCornerRadius: Float,
    ) {
        val startCorner = CornerRadius(startCornerRadius, startCornerRadius)
        val endCorner = CornerRadius(endCornerRadius, endCornerRadius)
        val track =
            RoundRect(
                rect = Rect(Offset(offset.x, 0f), size = Size(size.width, size.height)),
                topLeft = startCorner,
                topRight = endCorner,
                bottomRight = endCorner,
                bottomLeft = startCorner,
            )
        trackPath.addRoundRect(track)
        drawPath(trackPath, color)
        trackPath.rewind()
    }

    private fun drawStopIndicator(drawScope: DrawScope, offset: Offset, size: Dp, color: Color) {
        with(drawScope) { drawCircle(color = color, center = offset, radius = size.toPx() / 2f) }
    }

    /** The default size for the stop indicator at the end of the track. */
    private val TrackStopIndicatorSize: Dp = 4.dp

    /** The default size for the ticks if steps are greater than 0. */
    private val TickSize: Dp = 4.dp

    private val trackPath = Path()
}

private fun snapValueToTick(
    current: Float,
    tickFractions: FloatArray,
    minPx: Float,
    maxPx: Float,
): Float {
    // target is a closest anchor to the `current`, if exists
    return tickFractions
        .minByOrNull { abs(lerp(minPx, maxPx, it) - current) }
        ?.run { lerp(minPx, maxPx, this) } ?: current
}

private fun stepsToTickFractions(steps: Int): FloatArray {
    return if (steps == 0) floatArrayOf() else FloatArray(steps + 2) { it.toFloat() / (steps + 1) }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun Modifier.sliderSemantics(state: SliderStateMMD, enabled: Boolean): Modifier {
    return semantics {
        if (!enabled) disabled()
        setProgress(
            action = { targetValue ->
                var newValue =
                    targetValue.coerceIn(state.valueRange.start, state.valueRange.endInclusive)
                val originalVal = newValue
                val resolvedValue =
                    if (state.steps > 0) {
                        var distance: Float = newValue
                        for (i in 0..state.steps + 1) {
                            val stepValue =
                                lerp(
                                    state.valueRange.start,
                                    state.valueRange.endInclusive,
                                    i.toFloat() / (state.steps + 1),
                                )
                            if (abs(stepValue - originalVal) <= distance) {
                                distance = abs(stepValue - originalVal)
                                newValue = stepValue
                            }
                        }
                        newValue
                    } else {
                        newValue
                    }

                // This is to keep it consistent with AbsSeekbar.java: return false if no
                // change from current.
                if (resolvedValue == state.value) {
                    false
                } else {
                    if (resolvedValue != state.value) {
                        if (state.onValueChange != null) {
                            state.onValueChange?.invoke(resolvedValue)
                        } else {
                            state.value = resolvedValue
                        }
                    }
                    state.onValueChangeFinished?.invoke()
                    true
                }
            },
        )
    }
        .then(IncreaseHorizontalSemanticsBounds)
        .progressSemantics(
            state.value,
            state.valueRange.start..state.valueRange.endInclusive,
            state.steps,
        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Stable
private fun Modifier.sliderTapModifier(
    state: SliderStateMMD,
    interactionSource: MutableInteractionSource,
    enabled: Boolean,
) =
    if (enabled) {
        pointerInput(state, interactionSource) {
            detectTapGestures(
                onPress = { state.onPress(it) },
                onTap = {
                    state.dispatchRawDelta(0f)
                    state.gestureEndAction()
                },
            )
        }
    } else {
        this
    }

/**
 * Represents the color used by a [SliderMMD] in different states.
 *
 * @param thumbColor thumb color when enabled
 * @param thumbBorderColor thumb border color when enabled
 * @param activeTrackColor color of the track in the part that is "active", meaning that the thumb
 *   is ahead of it
 * @param activeTickColor colors to be used to draw tick marks on the active track, if `steps` is
 *   specified
 * @param inactiveTrackColor color of the track in the part that is "inactive", meaning that the
 *   thumb is before it
 * @param inactiveTickColor colors to be used to draw tick marks on the inactive track, if `steps`
 *   are specified on the Slider is specified
 * @param disabledThumbColor thumb colors when disabled
 * @param disabledThumbBorderColor thumb border colors when disabled
 * @param disabledTrackBorderColor color of the track border when disabled
 * @param disabledActiveTrackColor color of the track in the "active" part when the Slider is
 *   disabled
 * @param disabledActiveTickColor colors to be used to draw tick marks on the active track when
 *   Slider is disabled and when `steps` are specified on it
 * @param disabledInactiveTrackColor color of the track in the "inactive" part when the Slider is
 *   disabled
 * @param disabledInactiveTickColor colors to be used to draw tick marks on the inactive part of the
 *   track when Slider is disabled and when `steps` are specified on it
 * @constructor create an instance with arbitrary colors. See [SliderDefaultsMMD.colors] for the
 *   default implementation that follows Material specifications.
 */
@Immutable
class SliderColorsMMD(
    val thumbColor: Color,
    val thumbBorderColor: Color,
    val activeTrackColor: Color,
    val trackBorderColor: Color,
    val activeTickColor: Color,
    val inactiveTrackColor: Color,
    val inactiveTickColor: Color,
    val disabledThumbColor: Color,
    val disabledThumbBorderColor: Color,
    val disabledTrackBorderColor: Color,
    val disabledActiveTrackColor: Color,
    val disabledActiveTickColor: Color,
    val disabledInactiveTrackColor: Color,
    val disabledInactiveTickColor: Color,
) {

    /**
     * Returns a copy of this SelectableChipColors, optionally overriding some of the values. This
     * uses the Color.Unspecified to mean “use the value from the source”
     */
    fun copy(
        thumbColor: Color = this.thumbColor,
        thumbBorderColor: Color = this.thumbBorderColor,
        activeTrackColor: Color = this.activeTrackColor,
        trackBorderColor: Color = this.trackBorderColor,
        activeTickColor: Color = this.activeTickColor,
        inactiveTrackColor: Color = this.inactiveTrackColor,
        inactiveTickColor: Color = this.inactiveTickColor,
        disabledThumbColor: Color = this.disabledThumbColor,
        disabledThumbBorderColor: Color = this.disabledThumbBorderColor,
        disabledTrackBorderColor: Color = this.disabledTrackBorderColor,
        disabledActiveTrackColor: Color = this.disabledActiveTrackColor,
        disabledActiveTickColor: Color = this.disabledActiveTickColor,
        disabledInactiveTrackColor: Color = this.disabledInactiveTrackColor,
        disabledInactiveTickColor: Color = this.disabledInactiveTickColor,
    ) =
        SliderColorsMMD(
            thumbColor.takeOrElse { this.thumbColor },
            thumbBorderColor.takeOrElse { this.thumbBorderColor },
            activeTrackColor.takeOrElse { this.activeTrackColor },
            trackBorderColor.takeOrElse { this.trackBorderColor },
            activeTickColor.takeOrElse { this.activeTickColor },
            inactiveTrackColor.takeOrElse { this.inactiveTrackColor },
            inactiveTickColor.takeOrElse { this.inactiveTickColor },
            disabledThumbColor.takeOrElse { this.disabledThumbColor },
            disabledThumbBorderColor.takeOrElse { this.disabledThumbBorderColor },
            disabledTrackBorderColor.takeOrElse { this.disabledTrackBorderColor },
            disabledActiveTrackColor.takeOrElse { this.disabledActiveTrackColor },
            disabledActiveTickColor.takeOrElse { this.disabledActiveTickColor },
            disabledInactiveTrackColor.takeOrElse { this.disabledInactiveTrackColor },
            disabledInactiveTickColor.takeOrElse { this.disabledInactiveTickColor },
        )

    @Stable
    internal fun thumbColor(enabled: Boolean): Color =
        if (enabled) thumbColor else disabledThumbColor

    @Stable
    internal fun thumbBorderColor(enabled: Boolean): Color =
        if (enabled) thumbBorderColor else disabledThumbBorderColor

    @Stable
    internal fun trackColor(enabled: Boolean, active: Boolean): Color =
        if (enabled) {
            if (active) activeTrackColor else inactiveTrackColor
        } else {
            if (active) disabledActiveTrackColor else disabledInactiveTrackColor
        }

    @Stable
    internal fun trackBorderColor(enabled: Boolean): Color =
        if (enabled) {
            trackBorderColor
        } else {
            disabledTrackBorderColor
        }

    @Stable
    internal fun tickColor(enabled: Boolean, active: Boolean): Color =
        if (enabled) {
            if (active) activeTickColor else inactiveTickColor
        } else {
            if (active) disabledActiveTickColor else disabledInactiveTickColor
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is SliderColorsMMD) return false

        if (thumbColor != other.thumbColor) return false
        if (thumbBorderColor != other.thumbBorderColor) return false
        if (activeTrackColor != other.activeTrackColor) return false
        if (trackBorderColor != other.trackBorderColor) return false
        if (activeTickColor != other.activeTickColor) return false
        if (inactiveTrackColor != other.inactiveTrackColor) return false
        if (inactiveTickColor != other.inactiveTickColor) return false
        if (disabledThumbColor != other.disabledThumbColor) return false
        if (disabledThumbBorderColor != other.disabledThumbBorderColor) return false
        if (disabledTrackBorderColor != other.disabledTrackBorderColor) return false
        if (disabledActiveTrackColor != other.disabledActiveTrackColor) return false
        if (disabledActiveTickColor != other.disabledActiveTickColor) return false
        if (disabledInactiveTrackColor != other.disabledInactiveTrackColor) return false
        if (disabledInactiveTickColor != other.disabledInactiveTickColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = thumbColor.hashCode()
        result = 31 * result + thumbBorderColor.hashCode()
        result = 31 * result + activeTrackColor.hashCode()
        result = 31 * result + trackBorderColor.hashCode()
        result = 31 * result + activeTickColor.hashCode()
        result = 31 * result + inactiveTrackColor.hashCode()
        result = 31 * result + inactiveTickColor.hashCode()
        result = 31 * result + disabledThumbColor.hashCode()
        result = 31 * result + disabledThumbBorderColor.hashCode()
        result = 31 * result + disabledTrackBorderColor.hashCode()
        result = 31 * result + disabledActiveTrackColor.hashCode()
        result = 31 * result + disabledActiveTickColor.hashCode()
        result = 31 * result + disabledInactiveTrackColor.hashCode()
        result = 31 * result + disabledInactiveTickColor.hashCode()
        return result
    }
}

// Internal to be referred to in tests
private val ThumbShape = CircleShape
private val TrackHeight = 8.dp
private val ThumbWidth = 4.dp
private val ThumbSize = 18.dp
private val ThumbBorderSize = 22.dp
private val ThumbTrackGapSize: Dp = 0.dp
private val TrackInsideCornerSize: Dp = 2.dp
private val TrackBorderSize: Dp = 1.dp
private val TrackBorderShape = RoundedCornerShape(8.dp)

private enum class SliderComponents {
    THUMB,
    TRACK
}

/**
 * Class that holds information about [SliderMMD]'s active track and fractional
 * positions where the discrete ticks should be drawn on the track.
 */
@Suppress("DEPRECATION")
@Deprecated("Not necessary with the introduction of Slider state")
@Stable
class SliderPositionsMMD(
    initialActiveRange: ClosedFloatingPointRange<Float> = 0f..1f,
    initialTickFractions: FloatArray = floatArrayOf(),
) {
    /**
     * [ClosedFloatingPointRange] that indicates the current active range for the start to thumb for
     * a [SliderMMD].
     */
    var activeRange: ClosedFloatingPointRange<Float> by mutableStateOf(initialActiveRange)
        internal set

    /**
     * The discrete points where a tick should be drawn on the track. Each value of tickFractions
     * should be within the range [0f, 1f]. If the track is continuous, then tickFractions will be
     * an empty [FloatArray].
     */
    var tickFractions: FloatArray by mutableStateOf(initialTickFractions)
        internal set

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SliderPositionsMMD) return false

        if (activeRange != other.activeRange) return false
        if (!tickFractions.contentEquals(other.tickFractions)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = activeRange.hashCode()
        result = 31 * result + tickFractions.contentHashCode()
        return result
    }
}

/**
 * Class that holds information about [SliderMMD]'s active range.
 *
 * @param value [Float] that indicates the initial position of the thumb. If outside of [valueRange]
 *   provided, value will be coerced to this range.
 * @param steps if positive, specifies the amount of discrete allowable values (in addition to the
 *   endpoints of the value range). Step values are evenly distributed across the range. If 0, the
 *   slider will behave continuously and allow any value from the range. Must not be negative.
 * @param onValueChangeFinished lambda to be invoked when value change has ended. This callback
 *   shouldn't be used to update the range slider values (use [onValueChange] for that), but rather
 *   to know when the user has completed selecting a new value by ending a drag or a click.
 * @param valueRange range of values that Slider values can take. [value] will be coerced to this
 *   range.
 */
@ExperimentalMaterial3Api
class SliderStateMMD(
    value: Float = 0f,
    @IntRange(from = 0) val steps: Int = 0,
    var onValueChangeFinished: (() -> Unit)? = null,
    val valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
) : DraggableState {

    private var valueState by mutableFloatStateOf(value)

    /**
     * [Float] that indicates the current value that the thumb currently is in respect to the track.
     */
    var value: Float
        set(newVal) {
            val coercedValue = newVal.coerceIn(valueRange.start, valueRange.endInclusive)
            val snappedValue =
                snapValueToTick(
                    coercedValue,
                    tickFractions,
                    valueRange.start,
                    valueRange.endInclusive,
                )
            valueState = snappedValue
        }
        get() = valueState

    override suspend fun drag(
        dragPriority: MutatePriority,
        block: suspend DragScope.() -> Unit,
    ): Unit = coroutineScope {
        isDragging = true
        scrollMutex.mutateWith(dragScope, dragPriority, block)
        isDragging = false
    }

    override fun dispatchRawDelta(delta: Float) {
        val maxPx = max(totalWidth - thumbWidth / 2, 0f)
        val minPx = min(thumbWidth / 2, maxPx)
        rawOffset = (rawOffset + delta + pressOffset)
        pressOffset = 0f
        val offsetInTrack = snapValueToTick(rawOffset, tickFractions, minPx, maxPx)
        val scaledUserValue = scaleToUserValue(minPx, maxPx, offsetInTrack)
        if (scaledUserValue != this.value) {
            if (onValueChange != null) {
                onValueChange?.invoke(scaledUserValue)
            } else {
                this.value = scaledUserValue
            }
        }
    }

    /** callback in which value should be updated */
    internal var onValueChange: ((Float) -> Unit)? = null

    val tickFractions = stepsToTickFractions(steps)
    private var totalWidth by mutableIntStateOf(0)
    var trackHeight by mutableFloatStateOf(0f)
    internal var isRtl = false
    internal var thumbWidth by mutableFloatStateOf(0f)

    internal val coercedValueAsFraction
        get() =
            calcFraction(
                valueRange.start,
                valueRange.endInclusive,
                value.coerceIn(valueRange.start, valueRange.endInclusive),
            )

    internal var isDragging by mutableStateOf(false)
        private set

    internal fun updateDimensions(newTrackHeight: Float, newTotalWidth: Int) {
        trackHeight = newTrackHeight
        totalWidth = newTotalWidth
    }

    internal val gestureEndAction = {
        if (!isDragging) {
            // check isDragging in case the change is still in progress (touch -> drag case)
            onValueChangeFinished?.invoke()
        }
    }

    internal fun onPress(pos: Offset) {
        val to = if (isRtl) totalWidth - pos.x else pos.x
        pressOffset = to - rawOffset
    }

    private var rawOffset by mutableFloatStateOf(scaleToOffset(value))
    private var pressOffset by mutableFloatStateOf(0f)
    private val dragScope: DragScope =
        object : DragScope {
            override fun dragBy(pixels: Float): Unit = dispatchRawDelta(pixels)
        }

    private val scrollMutex = MutatorMutex()

    private fun scaleToUserValue(minPx: Float, maxPx: Float, offset: Float) =
        scale(minPx, maxPx, offset, valueRange.start, valueRange.endInclusive)

    private fun scaleToOffset(userValue: Float) =
        scale(valueRange.start, valueRange.endInclusive, userValue, 0f, 0f)

    private fun scale(a1: Float, b1: Float, x1: Float, a2: Float, b2: Float) =
        lerp(a2, b2, calcFraction(a1, b1, x1))

    private fun calcFraction(a: Float, b: Float, pos: Float) =
        (if (b - a == 0f) 0f else (pos - a) / (b - a)).coerceIn(0f, 1f)
}
