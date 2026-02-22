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

package com.mudita.mmd.components.radio_button

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

/**
 *
 * Radio buttons(no animations) allow users to select one option from a set.
 *
 *```kotlin
 * @Composable
 * fun RadioButtonSample() {
 *     // We have two radio buttons and only one can be selected
 *     var state by remember { mutableStateOf(true) }
 *     // Note that Modifier.selectableGroup() is essential to ensure correct accessibility behavior.
 *     // We also set a content description for this sample, but note that a RadioButton would usually
 *     // be part of a higher level component, such as a raw with text, and that component would need
 *     // to provide an appropriate content description. See RadioGroupSample.
 *     Row(Modifier.selectableGroup()) {
 *         RadioButtonMMD(
 *             selected = state,
 *             onClick = { state = true },
 *             modifier = Modifier.semantics { contentDescription = "Localized Description" }
 *         )
 *         RadioButtonMMD(
 *             selected = !state,
 *             onClick = { state = false },
 *             modifier = Modifier.semantics { contentDescription = "Localized Description" }
 *         )
 *     }
 * }
 * ```
 *
 * [RadioButtonMMD]s can be combined together with [Text] in the desired layout (e.g. [Column] or
 * [Row]) to achieve radio group-like behaviour, where the entire layout is selectable:
 *
 *```kotlin
 * @Composable
 * fun RadioButtonCustom() {
 *     val radioOptions = listOf("Calls", "Missed", "Friends")
 *     val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
 *     // Note that Modifier.selectableGroup() is essential to ensure correct accessibility behavior
 *     Row(
 *         Modifier
 *             .selectableGroup()
 *             .fillMaxWidth()
 *     ) {
 *         radioOptions.forEach { text ->
 *             Row(
 *                 Modifier
 *                     .height(56.dp)
 *                     .selectable(
 *                         selected = (text == selectedOption),
 *                         onClick = { onOptionSelected(text) },
 *                         role = Role.RadioButton
 *                     )
 *                     .padding(horizontal = 16.dp),
 *                 verticalAlignment = Alignment.CenterVertically
 *             ) {
 *                 RadioButtonMMD(
 *                     selected = (text == selectedOption),
 *                     onClick = null
 *                 )
 *                 Text(
 *                     text = text,
 *                     style = MaterialTheme.typography.bodyLarge,
 *                     modifier = Modifier.padding(start = 16.dp)
 *                 )
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * @param selected whether this radio button is selected or not
 * @param onClick called when this radio button is clicked. If `null`, then this radio button will
 *   not be interactable, unless something else handles its input events and updates its state.
 * @param modifier the [Modifier] to be applied to this radio button
 * @param enabled controls the enabled state of this radio button. When `false`, this component will
 *   not respond to user input, and it will appear visually disabled and disabled to accessibility
 *   services.
 * @param colors [RadioButtonColorsMMD] that will be used to resolve the color used for this radio
 *   button in different states. See [RadioButtonDefaultsMMD.colors].
 * @param interactionSource an optional hoisted [MutableInteractionSource] for observing and
 *   emitting [Interaction]s for this radio button. You can use this to change the radio button's
 *   appearance or preview the radio button in different states. Note that if `null` is provided,
 *   interactions will still happen internally.
 */
@Composable
fun RadioButtonMMD(
    selected: Boolean,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: RadioButtonColorsMMD = RadioButtonDefaultsMMD.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    val radioColor = colors.radioColor(enabled, selected)
    val selectableModifier =
        if (onClick != null) {
            Modifier.selectable(
                selected = selected,
                onClick = onClick,
                enabled = enabled,
                role = Role.RadioButton,
                interactionSource = interactionSource,
                indication = null,
            )
        } else {
            Modifier
        }
    Canvas(
        modifier
            .then(
                if (onClick != null) {
                    Modifier.minimumInteractiveComponentSize()
                } else {
                    Modifier
                },
            )
            .then(selectableModifier)
            .wrapContentSize(Alignment.Center)
            .padding(RadioButtonPadding)
            .requiredSize(IconSize),
    ) {
        // Draw the radio button
        val strokeWidth = RadioStrokeWidth.toPx()
        drawCircle(
            radioColor.value,
            radius = (IconSize / 2).toPx() - strokeWidth / 2,
            style = Stroke(strokeWidth),
        )
        if (selected) {
            drawCircle(
                radioColor.value,
                (RadioButtonDotSize / 2).toPx() - strokeWidth / 2,
                style = Fill,
            )
        }
    }
}

/** Defaults used in [RadioButtonMMD]. */
object RadioButtonDefaultsMMD {

    /**
     * Creates a [RadioButtonColorsMMD] that will animate between the provided colors according to the
     * Material specification.
     */
    @Composable
    fun colors() = defaultRadioButtonColors

    /**
     * Creates a [RadioButtonColorsMMD] that will animate between the provided colors according to the
     * Material specification.
     *
     * @param selectedColor the color to use for the RadioButton when selected and enabled.
     * @param unselectedColor the color to use for the RadioButton when unselected and enabled.
     * @param disabledSelectedColor the color to use for the RadioButton when disabled and selected.
     * @param disabledUnselectedColor the color to use for the RadioButton when disabled and not
     *   selected.
     * @return the resulting [RadioButtonColorsMMD] used for the RadioButton
     */
    @Composable
    fun colors(
        selectedColor: Color = Color.Unspecified,
        unselectedColor: Color = Color.Unspecified,
        disabledSelectedColor: Color = Color.Unspecified,
        disabledUnselectedColor: Color = Color.Unspecified,
    ): RadioButtonColorsMMD =
        defaultRadioButtonColors.copy(
            selectedColor,
            unselectedColor,
            disabledSelectedColor,
            disabledUnselectedColor,
        )

    private val defaultRadioButtonColors: RadioButtonColorsMMD
        @Composable get() = RadioButtonColorsMMD(
            selectedColor = SelectedIconColor,
            unselectedColor = UnselectedIconColor,
            disabledSelectedColor = DisabledSelectedIconColor.copy(alpha = DisabledSelectedIconOpacity),
            disabledUnselectedColor = DisabledUnselectedIconColor.copy(alpha = DisabledUnselectedIconOpacity),
        )
}

/**
 * Represents the color used by a [RadioButtonMMD] in different states.
 *
 * @param selectedColor the color to use for the RadioButton when selected and enabled.
 * @param unselectedColor the color to use for the RadioButton when unselected and enabled.
 * @param disabledSelectedColor the color to use for the RadioButton when disabled and selected.
 * @param disabledUnselectedColor the color to use for the RadioButton when disabled and not
 *   selected.
 * @constructor create an instance with arbitrary colors. See [RadioButtonDefaultsMMD.colors] for the
 *   default implementation that follows Material specifications.
 */
@Immutable
class RadioButtonColorsMMD(
    val selectedColor: Color,
    val unselectedColor: Color,
    val disabledSelectedColor: Color,
    val disabledUnselectedColor: Color,
) {
    /**
     * Returns a copy of this SelectableChipColors, optionally overriding some of the values. This
     * uses the Color.Unspecified to mean “use the value from the source”
     */
    fun copy(
        selectedColor: Color = this.selectedColor,
        unselectedColor: Color = this.unselectedColor,
        disabledSelectedColor: Color = this.disabledSelectedColor,
        disabledUnselectedColor: Color = this.disabledUnselectedColor,
    ) =
        RadioButtonColorsMMD(
            selectedColor.takeOrElse { this.selectedColor },
            unselectedColor.takeOrElse { this.unselectedColor },
            disabledSelectedColor.takeOrElse { this.disabledSelectedColor },
            disabledUnselectedColor.takeOrElse { this.disabledUnselectedColor },
        )

    /**
     * Represents the main color used to draw the outer and inner circles, depending on whether the
     * [RadioButtonMMD] is [enabled] / [selected].
     *
     * @param enabled whether the [RadioButtonMMD] is enabled
     * @param selected whether the [RadioButtonMMD] is selected
     */
    @Composable
    internal fun radioColor(enabled: Boolean, selected: Boolean): State<Color> {
        val target =
            when {
                enabled && selected -> selectedColor
                enabled && !selected -> unselectedColor
                !enabled && selected -> disabledSelectedColor
                else -> disabledUnselectedColor
            }

        return rememberUpdatedState(target)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is RadioButtonColorsMMD) return false

        if (selectedColor != other.selectedColor) return false
        if (unselectedColor != other.unselectedColor) return false
        if (disabledSelectedColor != other.disabledSelectedColor) return false
        if (disabledUnselectedColor != other.disabledUnselectedColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = selectedColor.hashCode()
        result = 31 * result + unselectedColor.hashCode()
        result = 31 * result + disabledSelectedColor.hashCode()
        result = 31 * result + disabledUnselectedColor.hashCode()
        return result
    }
}

private const val DisabledSelectedIconOpacity = 0.25f
private const val DisabledUnselectedIconOpacity = 0.25f

private val RadioButtonPadding = 2.dp
private val RadioButtonDotSize = 12.dp
private val RadioStrokeWidth = 2.dp
private val IconSize = 20.0.dp
private val SelectedIconColor: Color
    @Composable get() = MaterialTheme.colorScheme.primary

private val UnselectedIconColor: Color
    @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

private val DisabledSelectedIconColor: Color
    @Composable get() = MaterialTheme.colorScheme.onSurface

private val DisabledUnselectedIconColor: Color
    @Composable get() = MaterialTheme.colorScheme.onSurface
