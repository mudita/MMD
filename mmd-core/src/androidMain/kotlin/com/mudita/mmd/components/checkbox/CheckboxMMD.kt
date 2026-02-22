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

package com.mudita.mmd.components.checkbox

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.triStateToggleable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlin.math.floor
import kotlin.math.max

/**
 * Checkboxes allow users to select one or more items from a set. Checkboxes can turn an option on
 * or off.
 *
 * Simple Checkbox sample:
 *
 *```kotlin
 *@Composable
 * fun CheckboxSample() {
 *     val checkedState = remember { mutableStateOf(true) }
 *     CheckboxMMD(checked = checkedState.value, onCheckedChange = { checkedState.value = it })
 * }
 *```
 *
 * Combined Checkbox with Text sample:
 *
 *```kotlin
 * @Composable
 * fun CheckboxCustom() {
 *     val (checkedState, onStateChange) = remember { mutableStateOf(true) }
 *     Row(
 *         Modifier.fillMaxWidth()
 *             .height(56.dp)
 *             .toggleable(
 *                 value = checkedState,
 *                 onValueChange = { onStateChange(!checkedState) },
 *                 role = Role.Checkbox
 *             )
 *             .padding(horizontal = 16.dp),
 *         verticalAlignment = Alignment.CenterVertically
 *     ) {
 *         CheckboxMMD(
 *             checked = checkedState,
 *             onCheckedChange = null // null recommended for accessibility with screenreaders
 *         )
 *         Text(
 *             text = "Option selection",
 *             style = MaterialTheme.typography.bodyLarge,
 *             modifier = Modifier.padding(start = 16.dp)
 *         )
 *     }
 * }
 * ```
 *
 * @param checked whether this checkbox is checked or unchecked
 * @param onCheckedChange called when this checkbox is clicked. If `null`, then this checkbox will
 *   not be interactable, unless something else handles its input events and updates its state.
 * @param modifier the [Modifier] to be applied to this checkbox
 * @param enabled controls the enabled state of this checkbox. When `false`, this component will not
 *   respond to user input, and it will appear visually disabled and disabled to accessibility
 *   services.
 * @param colors [CheckboxColorsMMD] that will be used to resolve the colors used for this checkbox in
 *   different states. See [CheckboxDefaultsMMD.colors].
 * @param interactionSource an optional hoisted [MutableInteractionSource] for observing and
 *   emitting [Interaction]s for this checkbox. You can use this to change the checkbox's appearance
 *   or preview the checkbox in different states. Note that if `null` is provided, interactions will
 *   still happen internally.
 * @see [TriStateCheckboxMMD] if you require support for an indeterminate state.
 */
@Composable
fun CheckboxMMD(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CheckboxColorsMMD = CheckboxDefaultsMMD.colors(),
    interactionSource: MutableInteractionSource? = null
) {
    TriStateCheckboxMMD(
        state = ToggleableState(checked),
        onClick = { onCheckedChange?.invoke(!checked) },
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        interactionSource = interactionSource,
    )
}

/**
 * Checkboxes can have a parent-child relationship with other checkboxes. When the parent checkbox
 * is checked, all child checkboxes are checked. If a parent checkbox is unchecked, all child
 * checkboxes are unchecked. If some, but not all, child checkboxes are checked, the parent checkbox
 * becomes an indeterminate checkbox.
 *
 *```kotlin
 * @Composable
 * fun TriStateCheckbox() {
 *     Column {
 *         // define dependent checkboxes states
 *         val (state, onStateChange) = remember { mutableStateOf(false) }
 *         val (state2, onStateChange2) = remember { mutableStateOf(true) }
 *
 *         // TriStateCheckbox state reflects state of dependent checkboxes
 *         val parentState =
 *             remember(state, state2) {
 *                 if (state && state2) ToggleableState.On
 *                 else if (!state && !state2) ToggleableState.Off else ToggleableState.Indeterminate
 *             }
 *         // click on TriStateCheckbox can set state for dependent checkboxes
 *         val onParentClick = {
 *             val s = parentState != ToggleableState.On
 *             onStateChange(s)
 *             onStateChange2(s)
 *         }
 *
 *         // The sample below composes just basic checkboxes which are not fully accessible on their
 *         // own. See the CheckboxWithTextSample as a way to ensure your checkboxes are fully
 *         // accessible.
 *         Row(
 *             verticalAlignment = Alignment.CenterVertically,
 *             modifier =
 *             Modifier.triStateToggleable(
 *                 state = parentState,
 *                 onClick = onParentClick,
 *                 role = Role.Checkbox
 *             )
 *         ) {
 *             TriStateCheckboxMMD(
 *                 state = parentState,
 *                 onClick = null,
 *             )
 *             Text("Received Emails")
 *         }
 *         Spacer(Modifier.size(25.dp))
 *         Column(Modifier.padding(24.dp, 0.dp, 0.dp, 0.dp)) {
 *             Row(
 *                 verticalAlignment = Alignment.CenterVertically,
 *                 modifier =
 *                 Modifier.toggleable(
 *                     value = state,
 *                     onValueChange = onStateChange,
 *                     role = Role.Checkbox
 *                 )
 *             ) {
 *                 CheckboxMMD(state, null)
 *                 Text("Daily")
 *             }
 *             Spacer(Modifier.size(25.dp))
 *             Row(
 *                 verticalAlignment = Alignment.CenterVertically,
 *                 modifier =
 *                 Modifier.toggleable(
 *                     value = state2,
 *                     onValueChange = onStateChange2,
 *                     role = Role.Checkbox
 *                 )
 *             ) {
 *                 CheckboxMMD(state2, null)
 *                 Text("Weekly")
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * @param state whether this checkbox is checked, unchecked, or in an indeterminate state
 * @param onClick called when this checkbox is clicked. If `null`, then this checkbox will not be
 *   interactable, unless something else handles its input events and updates its [state].
 * @param modifier the [Modifier] to be applied to this checkbox
 * @param enabled controls the enabled state of this checkbox. When `false`, this component will not
 *   respond to user input, and it will appear visually disabled and disabled to accessibility
 *   services.
 * @param colors [CheckboxColorsMMD] that will be used to resolve the colors used for this checkbox in
 *   different states. See [CheckboxDefaultsMMD.colors].
 * @param interactionSource an optional hoisted [MutableInteractionSource] for observing and
 *   emitting [Interaction]s for this checkbox. You can use this to change the checkbox's appearance
 *   or preview the checkbox in different states. Note that if `null` is provided, interactions will
 *   still happen internally.
 * @see [CheckboxMMD] if you want a simple component that represents Boolean state
 */
@Composable
fun TriStateCheckboxMMD(
    state: ToggleableState,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: CheckboxColorsMMD = CheckboxDefaultsMMD.colors(),
    interactionSource: MutableInteractionSource? = null
) {
    val toggleableModifier =
        if (onClick != null) {
            Modifier.triStateToggleable(
                state = state,
                onClick = onClick,
                enabled = enabled,
                role = Role.Checkbox,
                interactionSource = interactionSource,
                indication = null
            )
        } else {
            Modifier
        }
    CheckboxImpl(
        enabled = enabled,
        value = state,
        modifier =
        modifier
            .then(
                if (onClick != null) {
                    Modifier.minimumInteractiveComponentSize()
                } else {
                    Modifier
                }
            )
            .then(toggleableModifier)
            .padding(CheckboxDefaultPadding),
        colors = colors
    )
}

/** Defaults used in [CheckboxMMD] and [TriStateCheckboxMMD]. */
object CheckboxDefaultsMMD {
    /**
     * Creates a [CheckboxColorsMMD] that will animate between the provided colors according to the
     * Material specification.
     */
    @Composable
    fun colors() = defaultCheckboxColors

    /**
     * Creates a [CheckboxColorsMMD] that will animate between the provided colors according to the
     * Material specification.
     *
     * @param checkedColor the color that will be used for the border and box when checked
     * @param uncheckedColor color that will be used for the border when unchecked. By default, the
     *   inner box is transparent when unchecked.
     * @param checkmarkColor color that will be used for the checkmark when checked
     * @param disabledCheckedColor color that will be used for the box and border when disabled and
     *   checked
     * @param disabledUncheckedColor color that will be used for the border when disabled and
     *   unchecked. By default, the inner box is transparent when unchecked.
     * @param disabledIndeterminateColor color that will be used for the box and border in a
     *   [TriStateCheckboxMMD] when disabled AND in an [ToggleableState.Indeterminate] state
     */
    @Composable
    fun colors(
        checkedColor: Color = Color.Unspecified,
        uncheckedColor: Color = Color.Unspecified,
        checkmarkColor: Color = Color.Unspecified,
        disabledCheckedColor: Color = Color.Unspecified,
        disabledUncheckedColor: Color = Color.Unspecified,
        disabledIndeterminateColor: Color = Color.Unspecified
    ): CheckboxColorsMMD = defaultCheckboxColors.copy(
        checkedCheckmarkColor = checkmarkColor,
        uncheckedCheckmarkColor = Color.Transparent,
        checkedBoxColor = checkedColor,
        uncheckedBoxColor = Color.Transparent,
        disabledCheckedBoxColor = disabledCheckedColor,
        disabledUncheckedBoxColor = Color.Transparent,
        disabledIndeterminateBoxColor = disabledIndeterminateColor,
        checkedBorderColor = checkedColor,
        uncheckedBorderColor = uncheckedColor,
        disabledBorderColor = disabledCheckedColor,
        disabledUncheckedBorderColor = disabledUncheckedColor,
        disabledIndeterminateBorderColor = disabledIndeterminateColor
    )

    private val defaultCheckboxColors: CheckboxColorsMMD
        @Composable get() = CheckboxColorsMMD(
            checkedCheckmarkColor = SelectedIconColor,
            uncheckedCheckmarkColor = Color.Transparent,
            checkedBoxColor = SelectedContainerColor,
            uncheckedBoxColor = Color.Transparent,
            disabledCheckedBoxColor = SelectedDisabledContainerColor
                .copy(alpha = SelectedDisabledContainerOpacity),
            disabledUncheckedBoxColor = Color.Transparent,
            disabledIndeterminateBoxColor = SelectedDisabledContainerColor
                .copy(alpha = SelectedDisabledContainerOpacity),
            checkedBorderColor = SelectedContainerColor,
            uncheckedBorderColor = UnselectedOutlineColor,
            disabledBorderColor = SelectedDisabledContainerColor
                .copy(alpha = SelectedDisabledContainerOpacity),
            disabledUncheckedBorderColor = UnselectedDisabledOutlineColor
                .copy(alpha = UnselectedDisabledContainerOpacity),
            disabledIndeterminateBorderColor = SelectedDisabledContainerColor
                .copy(alpha = SelectedDisabledContainerOpacity)
        )
}

@Composable
private fun CheckboxImpl(
    enabled: Boolean,
    value: ToggleableState,
    modifier: Modifier,
    colors: CheckboxColorsMMD
) {
    val checkDrawFraction = when (value) {
        ToggleableState.On -> 1f
        ToggleableState.Off -> 0f
        ToggleableState.Indeterminate -> 1f
    }

    val checkCenterGravitationShiftFraction = when (value) {
        ToggleableState.On -> 0f
        ToggleableState.Off -> 0f
        ToggleableState.Indeterminate -> 1f
    }

    val checkCache = remember { CheckDrawingCache() }
    val checkColor = colors.checkmarkColor(value)
    val boxColor = colors.boxColor(enabled, value)
    val borderColor = colors.borderColor(enabled, value)

    Canvas(
        modifier
            .wrapContentSize(Alignment.Center)
            .requiredSize(CheckboxSize)
    ) {
        val strokeWidthPx = floor(StrokeWidth.toPx())
        drawBox(
            boxColor = boxColor.value,
            borderColor = borderColor.value,
            radius = RadiusSize.toPx(),
            strokeWidth = strokeWidthPx
        )
        drawCheck(
            checkColor = checkColor.value,
            checkFraction = checkDrawFraction,
            crossCenterGravitation = checkCenterGravitationShiftFraction,
            strokeWidthPx = strokeWidthPx,
            drawingCache = checkCache
        )
    }
}

private fun DrawScope.drawBox(
    boxColor: Color,
    borderColor: Color,
    radius: Float,
    strokeWidth: Float
) {
    val halfStrokeWidth = strokeWidth / 2.0f
    val stroke = Stroke(strokeWidth)
    val checkboxSize = size.width
    if (boxColor == borderColor) {
        drawRoundRect(
            boxColor,
            size = Size(checkboxSize, checkboxSize),
            cornerRadius = CornerRadius(radius),
            style = Fill
        )
    } else {
        drawRoundRect(
            boxColor,
            topLeft = Offset(strokeWidth, strokeWidth),
            size = Size(checkboxSize - strokeWidth * 2, checkboxSize - strokeWidth * 2),
            cornerRadius = CornerRadius(max(0f, radius - strokeWidth)),
            style = Fill
        )
        drawRoundRect(
            borderColor,
            topLeft = Offset(halfStrokeWidth, halfStrokeWidth),
            size = Size(checkboxSize - strokeWidth, checkboxSize - strokeWidth),
            cornerRadius = CornerRadius(radius - halfStrokeWidth),
            style = stroke
        )
    }
}

private fun DrawScope.drawCheck(
    checkColor: Color,
    checkFraction: Float,
    crossCenterGravitation: Float,
    strokeWidthPx: Float,
    drawingCache: CheckDrawingCache
) {
    val stroke = Stroke(width = strokeWidthPx, cap = StrokeCap.Square)
    val width = size.width
    val checkCrossX = 0.4f
    val checkCrossY = 0.7f
    val leftX = 0.2f
    val leftY = 0.5f
    val rightX = 0.8f
    val rightY = 0.3f

    val gravitatedCrossX = lerp(checkCrossX, 0.5f, crossCenterGravitation)
    val gravitatedCrossY = lerp(checkCrossY, 0.5f, crossCenterGravitation)
    // gravitate only Y for end to achieve center line
    val gravitatedLeftY = lerp(leftY, 0.5f, crossCenterGravitation)
    val gravitatedRightY = lerp(rightY, 0.5f, crossCenterGravitation)

    with(drawingCache) {
        checkPath.reset()
        checkPath.moveTo(width * leftX, width * gravitatedLeftY)
        checkPath.lineTo(width * gravitatedCrossX, width * gravitatedCrossY)
        checkPath.lineTo(width * rightX, width * gravitatedRightY)
        pathMeasure.setPath(checkPath, false)
        pathToDraw.reset()
        pathMeasure.getSegment(0f, pathMeasure.length * checkFraction, pathToDraw, true)
    }
    drawPath(drawingCache.pathToDraw, checkColor, style = stroke)
}

@Immutable
private class CheckDrawingCache(
    val checkPath: Path = Path(),
    val pathMeasure: PathMeasure = PathMeasure(),
    val pathToDraw: Path = Path()
)

/**
 * Represents the colors used by the three different sections (checkmark, box, and border) of a
 * [CheckboxMMD] or [TriStateCheckboxMMD] in different states.
 *
 * @param checkedCheckmarkColor color that will be used for the checkmark when checked
 * @param uncheckedCheckmarkColor color that will be used for the checkmark when unchecked
 * @param checkedBoxColor the color that will be used for the box when checked
 * @param uncheckedBoxColor color that will be used for the box when unchecked
 * @param disabledCheckedBoxColor color that will be used for the box when disabled and checked
 * @param disabledUncheckedBoxColor color that will be used for the box when disabled and unchecked
 * @param disabledIndeterminateBoxColor color that will be used for the box and border in a
 *   [TriStateCheckboxMMD] when disabled AND in an [ToggleableState.Indeterminate] state.
 * @param checkedBorderColor color that will be used for the border when checked
 * @param uncheckedBorderColor color that will be used for the border when unchecked
 * @param disabledBorderColor color that will be used for the border when disabled and checked
 * @param disabledUncheckedBorderColor color that will be used for the border when disabled and
 *   unchecked
 * @param disabledIndeterminateBorderColor color that will be used for the border when disabled and
 *   in an [ToggleableState.Indeterminate] state.
 * @constructor create an instance with arbitrary colors, see [CheckboxDefaultsMMD.colors] for the
 *   default implementation that follows Material specifications.
 */
@Immutable
class CheckboxColorsMMD(
    val checkedCheckmarkColor: Color,
    val uncheckedCheckmarkColor: Color,
    val checkedBoxColor: Color,
    val uncheckedBoxColor: Color,
    val disabledCheckedBoxColor: Color,
    val disabledUncheckedBoxColor: Color,
    val disabledIndeterminateBoxColor: Color,
    val checkedBorderColor: Color,
    val uncheckedBorderColor: Color,
    val disabledBorderColor: Color,
    val disabledUncheckedBorderColor: Color,
    val disabledIndeterminateBorderColor: Color
) {
    /**
     * Returns a copy of this CheckboxColors, optionally overriding some of the values. This uses
     * the Color.Unspecified to mean “use the value from the source”
     */
    fun copy(
        checkedCheckmarkColor: Color = this.checkedCheckmarkColor,
        uncheckedCheckmarkColor: Color = this.uncheckedCheckmarkColor,
        checkedBoxColor: Color = this.checkedBoxColor,
        uncheckedBoxColor: Color = this.uncheckedBoxColor,
        disabledCheckedBoxColor: Color = this.disabledCheckedBoxColor,
        disabledUncheckedBoxColor: Color = this.disabledUncheckedBoxColor,
        disabledIndeterminateBoxColor: Color = this.disabledIndeterminateBoxColor,
        checkedBorderColor: Color = this.checkedBorderColor,
        uncheckedBorderColor: Color = this.uncheckedBorderColor,
        disabledBorderColor: Color = this.disabledBorderColor,
        disabledUncheckedBorderColor: Color = this.disabledUncheckedBorderColor,
        disabledIndeterminateBorderColor: Color = this.disabledIndeterminateBorderColor
    ) =
        CheckboxColorsMMD(
            checkedCheckmarkColor.takeOrElse { this.checkedCheckmarkColor },
            uncheckedCheckmarkColor.takeOrElse { this.uncheckedCheckmarkColor },
            checkedBoxColor.takeOrElse { this.checkedBoxColor },
            uncheckedBoxColor.takeOrElse { this.uncheckedBoxColor },
            disabledCheckedBoxColor.takeOrElse { this.disabledCheckedBoxColor },
            disabledUncheckedBoxColor.takeOrElse { this.disabledUncheckedBoxColor },
            disabledIndeterminateBoxColor.takeOrElse { this.disabledIndeterminateBoxColor },
            checkedBorderColor.takeOrElse { this.checkedBorderColor },
            uncheckedBorderColor.takeOrElse { this.uncheckedBorderColor },
            disabledBorderColor.takeOrElse { this.disabledBorderColor },
            disabledUncheckedBorderColor.takeOrElse { this.disabledUncheckedBorderColor },
            disabledIndeterminateBorderColor.takeOrElse { this.disabledIndeterminateBorderColor },
        )

    /**
     * Represents the color used for the checkmark inside the checkbox, depending on [state].
     *
     * @param state the [ToggleableState] of the checkbox
     */
    @Composable
    internal fun checkmarkColor(state: ToggleableState): State<Color> {
        return rememberUpdatedState(
            if (state == ToggleableState.Off) {
                uncheckedCheckmarkColor
            } else {
                checkedCheckmarkColor
            }
        )
    }

    /**
     * Represents the color used for the box (background) of the checkbox, depending on [enabled]
     * and [state].
     *
     * @param enabled whether the checkbox is enabled or not
     * @param state the [ToggleableState] of the checkbox
     */
    @Composable
    internal fun boxColor(enabled: Boolean, state: ToggleableState): State<Color> {
        val target =
            if (enabled) {
                when (state) {
                    ToggleableState.On,
                    ToggleableState.Indeterminate -> checkedBoxColor

                    ToggleableState.Off -> uncheckedBoxColor
                }
            } else {
                when (state) {
                    ToggleableState.On -> disabledCheckedBoxColor
                    ToggleableState.Indeterminate -> disabledIndeterminateBoxColor
                    ToggleableState.Off -> disabledUncheckedBoxColor
                }
            }

        return rememberUpdatedState(target)
    }

    /**
     * Represents the color used for the border of the checkbox, depending on [enabled] and [state].
     *
     * @param enabled whether the checkbox is enabled or not
     * @param state the [ToggleableState] of the checkbox
     */
    @Composable
    internal fun borderColor(enabled: Boolean, state: ToggleableState): State<Color> {
        val target =
            if (enabled) {
                when (state) {
                    ToggleableState.On,
                    ToggleableState.Indeterminate -> checkedBorderColor

                    ToggleableState.Off -> uncheckedBorderColor
                }
            } else {
                when (state) {
                    ToggleableState.Indeterminate -> disabledIndeterminateBorderColor
                    ToggleableState.On -> disabledBorderColor
                    ToggleableState.Off -> disabledUncheckedBorderColor
                }
            }

        return rememberUpdatedState(target)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is CheckboxColorsMMD) return false

        if (checkedCheckmarkColor != other.checkedCheckmarkColor) return false
        if (uncheckedCheckmarkColor != other.uncheckedCheckmarkColor) return false
        if (checkedBoxColor != other.checkedBoxColor) return false
        if (uncheckedBoxColor != other.uncheckedBoxColor) return false
        if (disabledCheckedBoxColor != other.disabledCheckedBoxColor) return false
        if (disabledUncheckedBoxColor != other.disabledUncheckedBoxColor) return false
        if (disabledIndeterminateBoxColor != other.disabledIndeterminateBoxColor) return false
        if (checkedBorderColor != other.checkedBorderColor) return false
        if (uncheckedBorderColor != other.uncheckedBorderColor) return false
        if (disabledBorderColor != other.disabledBorderColor) return false
        if (disabledUncheckedBorderColor != other.disabledUncheckedBorderColor) return false
        if (disabledIndeterminateBorderColor != other.disabledIndeterminateBorderColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = checkedCheckmarkColor.hashCode()
        result = 31 * result + uncheckedCheckmarkColor.hashCode()
        result = 31 * result + checkedBoxColor.hashCode()
        result = 31 * result + uncheckedBoxColor.hashCode()
        result = 31 * result + disabledCheckedBoxColor.hashCode()
        result = 31 * result + disabledUncheckedBoxColor.hashCode()
        result = 31 * result + disabledIndeterminateBoxColor.hashCode()
        result = 31 * result + checkedBorderColor.hashCode()
        result = 31 * result + uncheckedBorderColor.hashCode()
        result = 31 * result + disabledBorderColor.hashCode()
        result = 31 * result + disabledUncheckedBorderColor.hashCode()
        result = 31 * result + disabledIndeterminateBorderColor.hashCode()
        return result
    }
}

private val CheckboxDefaultPadding = 2.dp
private val CheckboxSize = 23.dp
private val StrokeWidth = 2.dp
private val RadiusSize = 3.dp

const val SelectedDisabledContainerOpacity = 0.25f
const val UnselectedDisabledContainerOpacity = 0.25f

private val SelectedIconColor: Color
    @Composable get() = MaterialTheme.colorScheme.onPrimary

private val SelectedContainerColor: Color
    @Composable get() = MaterialTheme.colorScheme.primary

private val SelectedDisabledContainerColor: Color
    @Composable get() = MaterialTheme.colorScheme.onSurface

private val UnselectedOutlineColor: Color
    @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

private val UnselectedDisabledOutlineColor: Color
    @Composable get() = MaterialTheme.colorScheme.onSurface
