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

package com.mudita.mmd.components.chips

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.DragInteraction
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.fastFirstOrNull
import com.mudita.mmd.components.text_field.heightOrZero
import com.mudita.mmd.components.text_field.widthOrZero

/**
 * AssistChipMMD
 *
 * Chips help people enter information, make selections, filter content, or trigger actions. Chips
 * can show multiple interactive elements together in the same area, such as a list of selectable
 * movie times, or a series of email contacts.
 *
 * Assist chips represent smart or automated actions that can span multiple apps, such as opening a
 * calendar event from the home screen. Assist chips function as though the user asked an assistant
 * to complete the action. They should appear dynamically and contextually in a UI.
 *
 * This assist chip is applied with a flat style.
 *
 * Example of a flat AssistChip:
 *
 * ```kotlin
 * AssistChipMMD(
 *     onClick = { Log.d("Assist chip", "hello world") },
 *     label = { TextMMD("Assist chip", fontWeight = FontWeight.Bold) },
 *     leadingIcon = {
 *         Icon(
 *             Icons.Filled.Settings,
 *             contentDescription = "Localized description",
 *             Modifier.size(AssistChipDefaultsMMD.IconSize)
 *         )
 *     }
 * )
 * ```
 *
 * @param onClick called when this chip is clicked
 * @param label text label for this chip
 * @param modifier the [Modifier] to be applied to this chip
 * @param enabled controls the enabled state of this chip. When `false`, this component will not
 *   respond to user input, and it will appear visually disabled and disabled to accessibility
 *   services.
 * @param leadingIcon optional icon at the start of the chip, preceding the [label] text
 * @param trailingIcon optional icon at the end of the chip
 * @param shape defines the shape of this chip's container, border (when [border] is not null), and
 *   shadow (when using [elevation])
 * @param colors [ChipColorsMMD] that will be used to resolve the colors used for this chip in
 *   different states. See [AssistChipDefaultsMMD.assistChipColors].
 * @param elevation [ChipElevationMMD] used to resolve the elevation for this chip in different states.
 *   This controls the size of the shadow below the chip. Additionally, when the container color is
 *   [ColorScheme.surface], this controls the amount of primary color applied as an overlay. See
 *   [AssistChipDefaultsMMD.assistChipElevation].
 * @param border the border to draw around the container of this chip. Pass `null` for no border.
 *   See [AssistChipDefaultsMMD.assistChipBorder].
 * @param interactionSource an optional hoisted [MutableInteractionSource] for observing and
 *   emitting [Interaction]s for this chip. You can use this to change the chip's appearance or
 *   preview the chip in different states. Note that if `null` is provided, interactions will still
 *   happen internally.
 */
@Composable
fun AssistChipMMD(
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = AssistChipDefaultsMMD.shape,
    colors: ChipColorsMMD = AssistChipDefaultsMMD.assistChipColors(),
    elevation: ChipElevationMMD? = AssistChipDefaultsMMD.assistChipElevation(),
    border: BorderStroke? = AssistChipDefaultsMMD.assistChipBorder(enabled),
    interactionSource: MutableInteractionSource? = null,
) =
    Chip(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        label = label,
        labelTextStyle = MaterialTheme.typography.labelLarge,
        labelColor = colors.labelColor(enabled),
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        minHeight = AssistChipDefaultsMMD.Height,
        paddingValues = AssistChipPadding,
        interactionSource = interactionSource,
    )

/**
 * FilterChip MMD
 *
 * Chips help people enter information, make selections, filter content, or trigger actions. Chips
 * can show multiple interactive elements together in the same area, such as a list of selectable
 * movie times, or a series of email contacts.
 *
 * Filter chips use tags or descriptive words to filter content. They can be a good alternative to
 * toggle buttons or checkboxes.
 *
 * This filter chip is applied with a flat style.
 *
 * Tapping on a filter chip toggles its selection state. A selection state [leadingIcon] can be
 * provided (e.g. a checkmark) to be appended at the starting edge of the chip's label.
 *
 * Example of a flat FilterChip with a trailing icon:
 *
 * ```kotlin
 * @Composable
 * fun FilterChipExample(isEnabled: Boolean = true) {
 *     var selected by remember { mutableStateOf(false) }
 *
 *     FilterChipMMD(
 *         onClick = { selected = !selected },
 *         label = {
 *             TextMMD("Filter chip", fontWeight = FontWeight.Bold)
 *         },
 *         selected = selected,
 *         leadingIcon = if (selected) {
 *             {
 *                 Icon(
 *                     imageVector = Icons.Filled.Done,
 *                     contentDescription = "Done icon",
 *                     modifier = Modifier.size(FilterChipDefaultsMMD.IconSize)
 *                 )
 *             }
 *         } else {
 *             null
 *         },
 *         enabled = isEnabled
 *     )
 * }
 * ```
 *
 * @param selected whether this chip is selected or not
 * @param onClick called when this chip is clicked
 * @param label text label for this chip
 * @param modifier the [Modifier] to be applied to this chip
 * @param enabled controls the enabled state of this chip. When `false`, this component will not
 *   respond to user input, and it will appear visually disabled and disabled to accessibility
 *   services.
 * @param leadingIcon optional icon at the start of the chip, preceding the [label] text. When
 *   [selected] is true, this icon may visually indicate that the chip is selected (for example, via
 *   a checkmark icon).
 * @param trailingIcon optional icon at the end of the chip
 * @param shape defines the shape of this chip's container, border (when [border] is not null), and
 *   shadow (when using [elevation])
 * @param colors [SelectableChipColorsMMD] that will be used to resolve the colors used for this chip
 *   in different states. See [FilterChipDefaultsMMD.filterChipColors].
 * @param elevation [SelectableChipElevationMMD] used to resolve the elevation for this chip in
 *   different states. This controls the size of the shadow below the chip. Additionally, when the
 *   container color is [ColorScheme.surface], this controls the amount of primary color applied as
 *   an overlay. See [FilterChipDefaultsMMD.filterChipElevation].
 * @param border the border to draw around the container of this chip. Pass `null` for no border.
 *   See [FilterChipDefaultsMMD.filterChipBorder].
 * @param interactionSource an optional hoisted [MutableInteractionSource] for observing and
 *   emitting [Interaction]s for this chip. You can use this to change the chip's appearance or
 *   preview the chip in different states. Note that if `null` is provided, interactions will still
 *   happen internally.
 */
@Composable
fun FilterChipMMD(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = FilterChipDefaultsMMD.shape,
    colors: SelectableChipColorsMMD = FilterChipDefaultsMMD.filterChipColors(),
    elevation: SelectableChipElevationMMD? = FilterChipDefaultsMMD.filterChipElevation(),
    border: BorderStroke? = FilterChipDefaultsMMD.filterChipBorder(enabled, selected),
    interactionSource: MutableInteractionSource? = null,
) =
    SelectableChip(
        selected = selected,
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        label = label,
        labelTextStyle = MaterialTheme.typography.labelLarge,
        leadingIcon = leadingIcon,
        avatar = null,
        trailingIcon = trailingIcon,
        elevation = elevation,
        colors = colors,
        minHeight = FilterChipDefaultsMMD.Height,
        paddingValues = FilterChipPadding,
        shape = shape,
        border = border,
        interactionSource = interactionSource,
    )

/**
 * InputChip MMD
 *
 * Chips help people enter information, make selections, filter content, or trigger actions. Chips
 * can show multiple interactive elements together in the same area, such as a list of selectable
 * movie times, or a series of email contacts.
 *
 * Input chips represent discrete pieces of information entered by a user.
 *
 * An Input Chip can have a leading icon or an avatar at its start. In case both are provided, the
 * avatar will take precedence and will be displayed.
 *
 * Example of an InputChip with an avatar and a trailing icon:
 *
 * ```kotlin
 * @Composable
 * fun InputChipExample(
 *     text: String,
 *     isEnabled: Boolean = true,
 *     onDismiss: () -> Unit,
 * ) {
 *     var enabled by remember { mutableStateOf(true) }
 *
 *     InputChipMMD(
 *         onClick = {
 *             onDismiss()
 *             enabled = !enabled
 *         },
 *         label = { Text(text, fontWeight = FontWeight.Bold) },
 *         selected = enabled,
 *         avatar = {
 *             Icon(
 *                 Icons.Filled.Person,
 *                 contentDescription = "Localized description",
 *                 Modifier.size(InputChipDefaultsMMD.AvatarSize)
 *             )
 *         },
 *         trailingIcon = {
 *             Icon(
 *                 Icons.Default.Close,
 *                 contentDescription = "Localized description",
 *                 Modifier.size(InputChipDefaultsMMD.IconSize)
 *             )
 *         },
 *         enabled = isEnabled
 *     )
 * }
 * ```
 *
 * @param selected whether this chip is selected or not
 * @param onClick called when this chip is clicked
 * @param label text label for this chip
 * @param modifier the [Modifier] to be applied to this chip
 * @param enabled controls the enabled state of this chip. When `false`, this component will not
 *   respond to user input, and it will appear visually disabled and disabled to accessibility
 *   services.
 * @param leadingIcon optional icon at the start of the chip, preceding the [label] text
 * @param avatar optional avatar at the start of the chip, preceding the [label] text
 * @param trailingIcon optional icon at the end of the chip
 * @param shape defines the shape of this chip's container, border (when [border] is not null), and
 *   shadow (when using [elevation])
 * @param colors [ChipColorsMMD] that will be used to resolve the colors used for this chip in
 *   different states. See [InputChipDefaultsMMD.inputChipColors].
 * @param elevation [ChipElevationMMD] used to resolve the elevation for this chip in different states.
 *   This controls the size of the shadow below the chip. Additionally, when the container color is
 *   [ColorScheme.surface], this controls the amount of primary color applied as an overlay. See
 *   [InputChipDefaultsMMD.inputChipElevation].
 * @param border the border to draw around the container of this chip. Pass `null` for no border.
 *   See [InputChipDefaultsMMD.inputChipBorder].
 * @param interactionSource an optional hoisted [MutableInteractionSource] for observing and
 *   emitting [Interaction]s for this chip. You can use this to change the chip's appearance or
 *   preview the chip in different states. Note that if `null` is provided, interactions will still
 *   happen internally.
 */
@Composable
fun InputChipMMD(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    avatar: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    shape: Shape = InputChipDefaultsMMD.shape,
    colors: SelectableChipColorsMMD = InputChipDefaultsMMD.inputChipColors(),
    elevation: SelectableChipElevationMMD? = InputChipDefaultsMMD.inputChipElevation(),
    border: BorderStroke? = InputChipDefaultsMMD.inputChipBorder(enabled, selected),
    interactionSource: MutableInteractionSource? = null,
) {
    // If given, place the avatar in an InputChipTokens.AvatarShape shape before passing it into the
    // Chip function.
    val shapedAvatar: @Composable (() -> Unit)? = avatar?.let {
        {
            Box(
                modifier = Modifier.graphicsLayer {
                    this.alpha = if (enabled) 1f else .38f
                    this.shape = CircleShape
                    this.clip = true
                },
                contentAlignment = Alignment.Center,
            ) {
                avatar()
            }
        }
    }
    SelectableChip(
        selected = selected,
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        label = label,
        labelTextStyle = MaterialTheme.typography.labelLarge,
        leadingIcon = leadingIcon,
        avatar = shapedAvatar,
        trailingIcon = trailingIcon,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        minHeight = InputChipDefaultsMMD.Height,
        paddingValues = inputChipPadding(
            hasAvatar = shapedAvatar != null,
            hasLeadingIcon = leadingIcon != null,
            hasTrailingIcon = trailingIcon != null,
        ),
        interactionSource = interactionSource,
    )
}

/**
 * SuggestionChip MMD
 *
 * Chips help people enter information, make selections, filter content, or trigger actions. Chips
 * can show multiple interactive elements together in the same area, such as a list of selectable
 * movie times, or a series of email contacts.
 *
 * Suggestion chips help narrow a user's intent by presenting dynamically generated suggestions,
 * such as possible responses or search filters.
 *
 * This suggestion chip is applied with a flat style.
 *
 * Example of a flat SuggestionChip with a trailing icon:
 *
 * ```kotlin
 * @Composable
 * fun SuggestionChipExample(isEnabled: Boolean = true) {
 *     SuggestionChipMMD(
 *         onClick = { Log.d("Suggestion chip", "hello world") },
 *         label = { TextMMD("Suggestion chip", fontWeight = FontWeight.Bold) },
 *         enabled = isEnabled,
 *     )
 * }
 * ```
 *
 * @param onClick called when this chip is clicked
 * @param label text label for this chip
 * @param modifier the [Modifier] to be applied to this chip
 * @param enabled controls the enabled state of this chip. When `false`, this component will not
 *   respond to user input, and it will appear visually disabled and disabled to accessibility
 *   services.
 * @param icon optional icon at the start of the chip, preceding the [label] text
 * @param shape defines the shape of this chip's container, border (when [border] is not null), and
 *   shadow (when using [elevation])
 * @param colors [ChipColorsMMD] that will be used to resolve the colors used for this chip in
 *   different states. See [SuggestionChipDefaultsMMD.suggestionChipColors].
 * @param elevation [ChipElevationMMD] used to resolve the elevation for this chip in different states.
 *   This controls the size of the shadow below the chip. Additionally, when the container color is
 *   [ColorScheme.surface], this controls the amount of primary color applied as an overlay. See
 *   [SuggestionChipDefaultsMMD.suggestionChipElevation].
 * @param border the border to draw around the container of this chip. Pass `null` for no border.
 *   See [SuggestionChipDefaultsMMD.suggestionChipBorder].
 * @param interactionSource an optional hoisted [MutableInteractionSource] for observing and
 *   emitting [Interaction]s for this chip. You can use this to change the chip's appearance or
 *   preview the chip in different states. Note that if `null` is provided, interactions will still
 *   happen internally.
 */
@Composable
fun SuggestionChipMMD(
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null,
    shape: Shape = SuggestionChipDefaultsMMD.shape,
    colors: ChipColorsMMD = SuggestionChipDefaultsMMD.suggestionChipColors(),
    elevation: ChipElevationMMD? = SuggestionChipDefaultsMMD.suggestionChipElevation(),
    border: BorderStroke? = SuggestionChipDefaultsMMD.suggestionChipBorder(enabled),
    interactionSource: MutableInteractionSource? = null,
) =
    Chip(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        label = label,
        labelTextStyle = MaterialTheme.typography.labelLarge,
        labelColor = colors.labelColor(enabled),
        leadingIcon = icon,
        trailingIcon = null,
        shape = shape,
        colors = colors,
        elevation = elevation,
        border = border,
        minHeight = SuggestionChipDefaultsMMD.Height,
        paddingValues = SuggestionChipPadding,
        interactionSource = interactionSource,
    )

/** Contains the baseline values used by [AssistChipMMD]. */
object AssistChipDefaultsMMD {
    /**
     * The height applied for an assist chip. Note that you can override it by applying
     * Modifier.height directly on a chip.
     */
    val Height = 32.0.dp

    /** The size of an assist chip icon. */
    val IconSize = 22.dp

    /**
     * Creates a [ChipColorsMMD] that represents the default container , label, and icon colors used in
     * a flat [AssistChipMMD].
     */
    @Composable
    fun assistChipColors() = defaultAssistChipColors

    /**
     * Creates a [ChipColorsMMD] that represents the default container , label, and icon colors used in
     * a flat [AssistChipMMD].
     *
     * @param containerColor the container color of this chip when enabled
     * @param labelColor the label color of this chip when enabled
     * @param leadingIconContentColor the color of this chip's start icon when enabled
     * @param trailingIconContentColor the color of this chip's end icon when enabled
     * @param disabledContainerColor the container color of this chip when not enabled
     * @param disabledLabelColor the label color of this chip when not enabled
     * @param disabledLeadingIconContentColor the color of this chip's start icon when not enabled
     * @param disabledTrailingIconContentColor the color of this chip's end icon when not enabled
     */
    @Composable
    fun assistChipColors(
        containerColor: Color = Color.Unspecified,
        labelColor: Color = Color.Unspecified,
        leadingIconContentColor: Color = Color.Unspecified,
        trailingIconContentColor: Color = Color.Unspecified,
        disabledContainerColor: Color = Color.Unspecified,
        disabledLabelColor: Color = Color.Unspecified,
        disabledLeadingIconContentColor: Color = Color.Unspecified,
        disabledTrailingIconContentColor: Color = Color.Unspecified,
    ): ChipColorsMMD =
        defaultAssistChipColors.copy(
            containerColor = containerColor,
            labelColor = labelColor,
            leadingIconContentColor = leadingIconContentColor,
            trailingIconContentColor = trailingIconContentColor,
            disabledContainerColor = disabledContainerColor,
            disabledLabelColor = disabledLabelColor,
            disabledLeadingIconContentColor = disabledLeadingIconContentColor,
            disabledTrailingIconContentColor = disabledTrailingIconContentColor,
        )

    private val defaultAssistChipColors: ChipColorsMMD
        @Composable
        get() = ChipColorsMMD(
            containerColor = Color.Transparent,
            labelColor = MaterialTheme.colorScheme.onSurface,
            leadingIconContentColor = MaterialTheme.colorScheme.primary,
            trailingIconContentColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = Color.Transparent,
            disabledLabelColor = MaterialTheme.colorScheme.onSurface
                .copy(alpha = .38f),
            disabledLeadingIconContentColor = MaterialTheme.colorScheme.onSurface
                .copy(alpha = .38f),
            disabledTrailingIconContentColor = MaterialTheme.colorScheme.onSurface
                .copy(alpha = .38f),
        )

    /**
     * Creates a [ChipElevationMMD] that will switch between the provided values according to the
     * Material specification for a flat [AssistChipMMD].
     *
     * @param elevation the elevation used when the [AssistChipMMD] is has no other [Interaction]s
     * @param pressedElevation the elevation used when the chip is pressed.
     * @param focusedElevation the elevation used when the chip is focused
     * @param hoveredElevation the elevation used when the chip is hovered
     * @param draggedElevation the elevation used when the chip is dragged
     * @param disabledElevation the elevation used when the chip is not enabled
     */
    @Composable
    fun assistChipElevation(
        elevation: Dp = 0.0.dp,
        pressedElevation: Dp = elevation,
        focusedElevation: Dp = elevation,
        hoveredElevation: Dp = elevation,
        draggedElevation: Dp = 0.dp,
        disabledElevation: Dp = elevation,
    ): ChipElevationMMD =
        ChipElevationMMD(
            elevation = elevation,
            pressedElevation = pressedElevation,
            focusedElevation = focusedElevation,
            hoveredElevation = hoveredElevation,
            draggedElevation = draggedElevation,
            disabledElevation = disabledElevation,
        )

    /**
     * Creates a [BorderStroke] that represents the default border used in a flat [AssistChipMMD].
     *
     * @param enabled whether the chip is enabled
     * @param borderColor the border color of this chip when enabled
     * @param disabledBorderColor the border color of this chip when not enabled
     * @param borderWidth the border stroke width of this chip
     */
    @Composable
    fun assistChipBorder(
        enabled: Boolean,
        borderColor: Color = MaterialTheme.colorScheme.outline,
        disabledBorderColor: Color =
            MaterialTheme.colorScheme.onSurface.copy(
                alpha = .12f,
            ),
        borderWidth: Dp = 2.dp,
    ): BorderStroke = BorderStroke(borderWidth, if (enabled) borderColor else disabledBorderColor)

    /** Default shape of an assist chip. */
    val shape: Shape
        get() = RoundedCornerShape(20.dp)
}

/** Contains the baseline values used by [FilterChipMMD]. */
object FilterChipDefaultsMMD {
    /**
     * The height applied for a filter chip. Note that you can override it by applying
     * Modifier.height directly on a chip.
     */
    val Height = 32.dp

    /** The size of a filter chip leading icon. */
    val IconSize = 22.dp

    /**
     * Creates a [SelectableChipColorsMMD] that represents the default container and content colors
     * used in a flat [FilterChipMMD].
     */
    @Composable
    fun filterChipColors() = defaultFilterChipColors

    /**
     * Creates a [SelectableChipColorsMMD] that represents the default container and content colors
     * used in a flat [FilterChipMMD].
     *
     * @param containerColor the container color of this chip when enabled
     * @param labelColor the label color of this chip when enabled
     * @param iconColor the color of this chip's start and end icons when enabled
     * @param disabledContainerColor the container color of this chip when not enabled
     * @param disabledLabelColor the label color of this chip when not enabled
     * @param disabledLeadingIconColor the color of this chip's start icon when not enabled
     * @param disabledTrailingIconColor the color of this chip's end icon when not enabled
     * @param selectedContainerColor the container color of this chip when selected
     * @param disabledSelectedContainerColor the container color of this chip when not enabled and
     *   selected
     * @param selectedLabelColor the label color of this chip when selected
     * @param selectedLeadingIconColor the color of this chip's start icon when selected
     * @param selectedTrailingIconColor the color of this chip's end icon when selected
     */
    @Composable
    fun filterChipColors(
        containerColor: Color = Color.Unspecified,
        labelColor: Color = Color.Unspecified,
        iconColor: Color = Color.Unspecified,
        disabledContainerColor: Color = Color.Unspecified,
        disabledLabelColor: Color = Color.Unspecified,
        disabledLeadingIconColor: Color = Color.Unspecified,
        disabledTrailingIconColor: Color = Color.Unspecified,
        selectedContainerColor: Color = Color.Unspecified,
        disabledSelectedContainerColor: Color = Color.Unspecified,
        selectedLabelColor: Color = Color.Unspecified,
        selectedLeadingIconColor: Color = Color.Unspecified,
        selectedTrailingIconColor: Color = Color.Unspecified,
    ): SelectableChipColorsMMD =
        defaultFilterChipColors.copy(
            containerColor = containerColor,
            labelColor = labelColor,
            leadingIconColor = iconColor,
            trailingIconColor = iconColor,
            disabledContainerColor = disabledContainerColor,
            disabledLabelColor = disabledLabelColor,
            disabledLeadingIconColor = disabledLeadingIconColor,
            disabledTrailingIconColor = disabledTrailingIconColor,
            selectedContainerColor = selectedContainerColor,
            disabledSelectedContainerColor = disabledSelectedContainerColor,
            selectedLabelColor = selectedLabelColor,
            selectedLeadingIconColor = selectedLeadingIconColor,
            selectedTrailingIconColor = selectedTrailingIconColor,
        )

    private val defaultFilterChipColors: SelectableChipColorsMMD
        @Composable
        get() = SelectableChipColorsMMD(
            containerColor = Color.Transparent,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            leadingIconColor = MaterialTheme.colorScheme.primary,
            trailingIconColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = Color.Transparent,
            disabledLabelColor = MaterialTheme.colorScheme.onSurface
                .copy(alpha = .38f),
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface
                .copy(alpha = .38f),
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface
                .copy(alpha = .38f),
            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            disabledSelectedContainerColor = MaterialTheme.colorScheme.onSurface
                .copy(alpha = .12f),
            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
            selectedTrailingIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
        )

    /**
     * Creates a [SelectableChipElevationMMD] that will switch between the provided values according
     * to the Material specification for a flat [FilterChipMMD].
     *
     * @param elevation the elevation used when the [FilterChipMMD] is has no other [Interaction]s
     * @param pressedElevation the elevation used when the chip is pressed
     * @param focusedElevation the elevation used when the chip is focused
     * @param hoveredElevation the elevation used when the chip is hovered
     * @param draggedElevation the elevation used when the chip is dragged
     * @param disabledElevation the elevation used when the chip is not enabled
     */
    @Composable
    fun filterChipElevation(
        elevation: Dp = 0.dp,
        pressedElevation: Dp = 0.dp,
        focusedElevation: Dp = 0.dp,
        hoveredElevation: Dp = 0.dp,
        draggedElevation: Dp = 0.dp,
        disabledElevation: Dp = elevation,
    ): SelectableChipElevationMMD =
        SelectableChipElevationMMD(
            elevation = elevation,
            pressedElevation = pressedElevation,
            focusedElevation = focusedElevation,
            hoveredElevation = hoveredElevation,
            draggedElevation = draggedElevation,
            disabledElevation = disabledElevation,
        )

    /**
     * Creates a [BorderStroke] that represents the default border used in a flat [FilterChipMMD].
     *
     * @param selected whether this chip is selected or not
     * @param enabled controls the enabled state of this chip. When `false`, this component will not
     *   respond to user input, and it will appear visually disabled and disabled to accessibility
     *   services.
     * @param borderColor the border color of this chip when enabled and not selected
     * @param selectedBorderColor the border color of this chip when enabled and selected
     * @param disabledBorderColor the border color of this chip when not enabled and not selected
     * @param disabledSelectedBorderColor the border color of this chip when not enabled but
     *   selected
     * @param borderWidth the border stroke width of this chip when not selected
     * @param selectedBorderWidth the border stroke width of this chip when selected
     */
    @Composable
    fun filterChipBorder(
        enabled: Boolean,
        selected: Boolean,
        borderColor: Color = MaterialTheme.colorScheme.outline,
        selectedBorderColor: Color = Color.Transparent,
        disabledBorderColor: Color =
            MaterialTheme.colorScheme.onSurface.copy(
                alpha = .12f,
            ),
        disabledSelectedBorderColor: Color = Color.Transparent,
        borderWidth: Dp = 2.dp,
        selectedBorderWidth: Dp = 2.dp,
    ): BorderStroke {
        val color =
            if (enabled) {
                if (selected) selectedBorderColor else borderColor
            } else {
                if (selected) disabledSelectedBorderColor else disabledBorderColor
            }
        return BorderStroke(if (selected) selectedBorderWidth else borderWidth, color)
    }

    /** Default shape of a filter chip. */
    val shape: Shape
        get() = RoundedCornerShape(20.dp)
}

/** Contains the baseline values used by an [InputChipMMD]. */
object InputChipDefaultsMMD {
    /**
     * The height applied for an input chip. Note that you can override it by applying
     * Modifier.height directly on a chip.
     */
    val Height = 32.dp

    /** The size of an input chip icon. */
    val IconSize = 22.dp

    /** The size of an input chip avatar. */
    val AvatarSize = 22.dp

    /**
     * Creates a [SelectableChipColorsMMD] that represents the default container, label, and icon
     * colors used in an [InputChipMMD].
     */
    @Composable
    fun inputChipColors() = defaultInputChipColors

    /**
     * Creates a [SelectableChipColorsMMD] that represents the default container, label, and icon
     * colors used in an [InputChipMMD].
     *
     * @param containerColor the container color of this chip when enabled
     * @param labelColor the label color of this chip when enabled
     * @param leadingIconColor the color of this chip's start icon when enabled
     * @param trailingIconColor the color of this chip's start end icon when enabled
     * @param disabledContainerColor the container color of this chip when not enabled
     * @param disabledLabelColor the label color of this chip when not enabled
     * @param disabledLeadingIconColor the color of this chip's start icon when not enabled
     * @param disabledTrailingIconColor the color of this chip's end icon when not enabled
     * @param selectedContainerColor the container color of this chip when selected
     * @param disabledSelectedContainerColor the container color of this chip when not enabled and
     *   selected
     * @param selectedLabelColor the label color of this chip when selected
     * @param selectedLeadingIconColor the color of this chip's start icon when selected
     * @param selectedTrailingIconColor the color of this chip's end icon when selected
     */
    @Composable
    fun inputChipColors(
        containerColor: Color = Color.Unspecified,
        labelColor: Color = Color.Unspecified,
        leadingIconColor: Color = Color.Unspecified,
        trailingIconColor: Color = Color.Unspecified,
        disabledContainerColor: Color = Color.Unspecified,
        disabledLabelColor: Color = Color.Unspecified,
        disabledLeadingIconColor: Color = Color.Unspecified,
        disabledTrailingIconColor: Color = Color.Unspecified,
        selectedContainerColor: Color = Color.Unspecified,
        disabledSelectedContainerColor: Color = Color.Unspecified,
        selectedLabelColor: Color = Color.Unspecified,
        selectedLeadingIconColor: Color = Color.Unspecified,
        selectedTrailingIconColor: Color = Color.Unspecified,
    ): SelectableChipColorsMMD =
        defaultInputChipColors.copy(
            containerColor = containerColor,
            labelColor = labelColor,
            leadingIconColor = leadingIconColor,
            trailingIconColor = trailingIconColor,
            disabledContainerColor = disabledContainerColor,
            disabledLabelColor = disabledLabelColor,
            disabledLeadingIconColor = disabledLeadingIconColor,
            disabledTrailingIconColor = disabledTrailingIconColor,
            selectedContainerColor = selectedContainerColor,
            disabledSelectedContainerColor = disabledSelectedContainerColor,
            selectedLabelColor = selectedLabelColor,
            selectedLeadingIconColor = selectedLeadingIconColor,
            selectedTrailingIconColor = selectedTrailingIconColor,
        )

    private val defaultInputChipColors: SelectableChipColorsMMD
        @Composable
        get() = SelectableChipColorsMMD(
            containerColor = Color.Transparent,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            leadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            trailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledContainerColor = Color.Transparent,
            disabledLabelColor = MaterialTheme.colorScheme.onSurface
                .copy(alpha = .38f),
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface
                .copy(alpha = .38f),
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface
                .copy(alpha = .38f),
            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            disabledSelectedContainerColor = MaterialTheme.colorScheme.onSurface
                .copy(alpha = .12f),
            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
            selectedTrailingIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
        )

    /**
     * Creates a [SelectableChipElevationMMD] that will switch between the provided values according
     * to the Material specification for an [InputChipMMD].
     *
     * @param elevation the elevation used when the [FilterChipMMD] is has no other [Interaction]s
     * @param pressedElevation the elevation used when the chip is pressed
     * @param focusedElevation the elevation used when the chip is focused
     * @param hoveredElevation the elevation used when the chip is hovered
     * @param draggedElevation the elevation used when the chip is dragged
     * @param disabledElevation the elevation used when the chip is not enabled
     */
    @Composable
    fun inputChipElevation(
        elevation: Dp = 0.dp,
        pressedElevation: Dp = elevation,
        focusedElevation: Dp = elevation,
        hoveredElevation: Dp = elevation,
        draggedElevation: Dp = 0.dp,
        disabledElevation: Dp = elevation,
    ): SelectableChipElevationMMD =
        SelectableChipElevationMMD(
            elevation = elevation,
            pressedElevation = pressedElevation,
            focusedElevation = focusedElevation,
            hoveredElevation = hoveredElevation,
            draggedElevation = draggedElevation,
            disabledElevation = disabledElevation,
        )

    /**
     * Creates a [BorderStroke] that represents the default border used in an [InputChipMMD].
     *
     * @param selected whether this chip is selected or not
     * @param enabled controls the enabled state of this chip. When `false`, this component will not
     *   respond to user input, and it will appear visually disabled and disabled to accessibility
     *   services.
     * @param borderColor the border color of this chip when enabled and not selected
     * @param selectedBorderColor the border color of this chip when enabled and selected
     * @param disabledBorderColor the border color of this chip when not enabled and not selected
     * @param disabledSelectedBorderColor the border color of this chip when not enabled but
     *   selected
     * @param borderWidth the border stroke width of this chip when not selected
     * @param selectedBorderWidth the border stroke width of this chip when selected
     */
    @Composable
    fun inputChipBorder(
        enabled: Boolean,
        selected: Boolean,
        borderColor: Color = MaterialTheme.colorScheme.outline,
        selectedBorderColor: Color = Color.Transparent,
        disabledBorderColor: Color =
            MaterialTheme.colorScheme.onSurface.copy(
                alpha = .12f,
            ),
        disabledSelectedBorderColor: Color = Color.Transparent,
        borderWidth: Dp = 1.dp,
        selectedBorderWidth: Dp = 0.dp,
    ): BorderStroke {
        val color =
            if (enabled) {
                if (selected) selectedBorderColor else borderColor
            } else {
                if (selected) disabledSelectedBorderColor else disabledBorderColor
            }
        return BorderStroke(if (selected) selectedBorderWidth else borderWidth, color)
    }

    /** Default shape of an input chip. */
    val shape: Shape
        get() = RoundedCornerShape(20.dp)
}

/** Contains the baseline values used by [SuggestionChipMMD]. */
object SuggestionChipDefaultsMMD {
    /**
     * The height applied for a suggestion chip. Note that you can override it by applying
     * Modifier.height directly on a chip.
     */
    val Height = 32.dp

    /** The size of a suggestion chip icon. */
    val IconSize = 22.dp

    /**
     * Creates a [ChipColorsMMD] that represents the default container, label, and icon colors used in
     * a flat [SuggestionChipMMD].
     */
    @Composable
    fun suggestionChipColors() = defaultSuggestionChipColors

    /**
     * Creates a [ChipColorsMMD] that represents the default container, label, and icon colors used in
     * a flat [SuggestionChipMMD].
     *
     * @param containerColor the container color of this chip when enabled
     * @param labelColor the label color of this chip when enabled
     * @param iconContentColor the color of this chip's icon when enabled
     * @param disabledContainerColor the container color of this chip when not enabled
     * @param disabledLabelColor the label color of this chip when not enabled
     * @param disabledIconContentColor the color of this chip's icon when not enabled
     */
    @Composable
    fun suggestionChipColors(
        containerColor: Color = Color.Unspecified,
        labelColor: Color = Color.Unspecified,
        iconContentColor: Color = Color.Unspecified,
        disabledContainerColor: Color = Color.Unspecified,
        disabledLabelColor: Color = Color.Unspecified,
        disabledIconContentColor: Color = Color.Unspecified,
    ): ChipColorsMMD =
        defaultSuggestionChipColors.copy(
            containerColor = containerColor,
            labelColor = labelColor,
            leadingIconContentColor = iconContentColor,
            trailingIconContentColor = Color.Unspecified,
            disabledContainerColor = disabledContainerColor,
            disabledLabelColor = disabledLabelColor,
            disabledLeadingIconContentColor = disabledIconContentColor,
            disabledTrailingIconContentColor = Color.Unspecified,
        )

    /**
     * Creates a [ChipElevationMMD] that will switch between the provided values according to the
     * Material specification for a flat [SuggestionChipMMD].
     *
     * @param elevation the elevation used when the chip is has no other [Interaction]s
     * @param pressedElevation the elevation used when the chip is pressed
     * @param focusedElevation the elevation used when the chip is focused
     * @param hoveredElevation the elevation used when the chip is hovered
     * @param draggedElevation the elevation used when the chip is dragged
     * @param disabledElevation the elevation used when the chip is not enabled
     */
    @Composable
    fun suggestionChipElevation(
        elevation: Dp = 0.dp,
        pressedElevation: Dp = elevation,
        focusedElevation: Dp = elevation,
        hoveredElevation: Dp = elevation,
        draggedElevation: Dp = 0.dp,
        disabledElevation: Dp = elevation,
    ): ChipElevationMMD =
        ChipElevationMMD(
            elevation = elevation,
            pressedElevation = pressedElevation,
            focusedElevation = focusedElevation,
            hoveredElevation = hoveredElevation,
            draggedElevation = draggedElevation,
            disabledElevation = disabledElevation,
        )

    /**
     * Creates a [BorderStroke] that represents the default border used in a flat [SuggestionChipMMD].
     *
     * @param enabled whether the chip is enabled
     * @param borderColor the border color of this chip when enabled
     * @param disabledBorderColor the border color of this chip when not enabled
     * @param borderWidth the border stroke width of this chip
     */
    @Composable
    fun suggestionChipBorder(
        enabled: Boolean,
        borderColor: Color = MaterialTheme.colorScheme.outline,
        disabledBorderColor: Color =
            MaterialTheme.colorScheme.onSurface.copy(
                alpha = .12f,
            ),
        borderWidth: Dp = 2.dp,
    ): BorderStroke = BorderStroke(borderWidth, if (enabled) borderColor else disabledBorderColor)

    /** Default shape of a suggestion chip. */
    val shape: Shape
        get() = RoundedCornerShape(20.dp)
}

@Composable
private fun Chip(
    modifier: Modifier,
    onClick: () -> Unit,
    enabled: Boolean,
    label: @Composable () -> Unit,
    labelTextStyle: TextStyle,
    labelColor: Color,
    leadingIcon: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    shape: Shape,
    colors: ChipColorsMMD,
    elevation: ChipElevationMMD?,
    border: BorderStroke?,
    minHeight: Dp,
    paddingValues: PaddingValues,
    interactionSource: MutableInteractionSource?,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    Surface(
        onClick = onClick,
        modifier = modifier.semantics { role = Role.Button },
        enabled = enabled,
        shape = shape,
        color = colors.containerColor(enabled),
        shadowElevation = elevation?.shadowElevation(enabled, interactionSource)?.value ?: 0.dp,
        border = border,
        interactionSource = interactionSource,
    ) {
        ChipContent(
            label = label,
            labelTextStyle = labelTextStyle,
            labelColor = labelColor,
            leadingIcon = leadingIcon,
            avatar = null,
            trailingIcon = trailingIcon,
            leadingIconColor = colors.leadingIconContentColor(enabled),
            trailingIconColor = colors.trailingIconContentColor(enabled),
            minHeight = minHeight,
            paddingValues = paddingValues,
        )
    }
}

@Composable
private fun SelectableChip(
    selected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
    enabled: Boolean,
    label: @Composable () -> Unit,
    labelTextStyle: TextStyle,
    leadingIcon: @Composable (() -> Unit)?,
    avatar: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    shape: Shape,
    colors: SelectableChipColorsMMD,
    elevation: SelectableChipElevationMMD?,
    border: BorderStroke?,
    minHeight: Dp,
    paddingValues: PaddingValues,
    interactionSource: MutableInteractionSource?,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    Surface(
        selected = selected,
        onClick = onClick,
        modifier = modifier.semantics { role = Role.Checkbox },
        enabled = enabled,
        shape = shape,
        color = colors.containerColor(enabled, selected),
        shadowElevation = elevation?.shadowElevation(enabled, interactionSource)?.value ?: 0.dp,
        border = border,
        interactionSource = interactionSource,
    ) {
        ChipContent(
            label = label,
            labelTextStyle = labelTextStyle,
            leadingIcon = leadingIcon,
            avatar = avatar,
            labelColor = colors.labelColor(enabled, selected),
            trailingIcon = trailingIcon,
            leadingIconColor = colors.leadingIconContentColor(enabled, selected),
            trailingIconColor = colors.trailingIconContentColor(enabled, selected),
            minHeight = minHeight,
            paddingValues = paddingValues,
        )
    }
}

@Composable
private fun ChipContent(
    label: @Composable () -> Unit,
    labelTextStyle: TextStyle,
    labelColor: Color,
    leadingIcon: @Composable (() -> Unit)?,
    avatar: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    leadingIconColor: Color,
    trailingIconColor: Color,
    minHeight: Dp,
    paddingValues: PaddingValues,
) {
    CompositionLocalProvider(
        LocalContentColor provides labelColor,
        LocalTextStyle provides labelTextStyle,
    ) {
        Layout(
            modifier = Modifier
                .defaultMinSize(minHeight = minHeight)
                .padding(paddingValues),
            content = {
                if (avatar != null || leadingIcon != null) {
                    Box(
                        modifier = Modifier.layoutId(LeadingIconLayoutId),
                        contentAlignment = Alignment.Center,
                        content = {
                            if (avatar != null) {
                                avatar()
                            } else if (leadingIcon != null) {
                                CompositionLocalProvider(
                                    LocalContentColor provides leadingIconColor,
                                    content = leadingIcon,
                                )
                            }
                        },
                    )
                }
                Row(
                    modifier = Modifier
                        .layoutId(LabelLayoutId)
                        .padding(HorizontalElementsPadding, 0.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    content = { label() },
                )
                if (trailingIcon != null) {
                    Box(
                        modifier = Modifier.layoutId(TrailingIconLayoutId),
                        contentAlignment = Alignment.Center,
                        content = {
                            CompositionLocalProvider(
                                LocalContentColor provides trailingIconColor,
                                content = trailingIcon,
                            )
                        },
                    )
                }
            },
        ) { measurables, constraints ->
            val leadingIconPlaceable: Placeable? =
                measurables
                    .fastFirstOrNull { it.layoutId == LeadingIconLayoutId }
                    ?.measure(constraints.copy(minWidth = 0, minHeight = 0))
            val leadingIconWidth = widthOrZero(leadingIconPlaceable)
            val leadingIconHeight = heightOrZero(leadingIconPlaceable)

            val trailingIconPlaceable: Placeable? =
                measurables
                    .fastFirstOrNull { it.layoutId == TrailingIconLayoutId }
                    ?.measure(constraints.copy(minWidth = 0, minHeight = 0))
            val trailingIconWidth = widthOrZero(trailingIconPlaceable)
            val trailingIconHeight = heightOrZero(trailingIconPlaceable)

            val labelPlaceable =
                measurables
                    .fastFirst { it.layoutId == LabelLayoutId }
                    .measure(
                        constraints.offset(horizontal = -(leadingIconWidth + trailingIconWidth)),
                    )

            val width = leadingIconWidth + labelPlaceable.width + trailingIconWidth
            val height = maxOf(leadingIconHeight, labelPlaceable.height, trailingIconHeight)

            layout(width, height) {
                leadingIconPlaceable?.placeRelative(
                    0,
                    Alignment.CenterVertically.align(leadingIconHeight, height),
                )
                labelPlaceable.placeRelative(leadingIconWidth, 0)
                trailingIconPlaceable?.placeRelative(
                    leadingIconWidth + labelPlaceable.width,
                    Alignment.CenterVertically.align(trailingIconHeight, height),
                )
            }
        }
    }
}

/**
 * Represents the elevation used in a selectable chip in different states.
 *
 * Note that this default implementation does not take into consideration the `selectable` state
 * passed into its [shadowElevation]. If you wish to apply that state, use a different
 * [SelectableChipElevationMMD].
 *
 * @param elevation the elevation used when the chip is enabled.
 * @param pressedElevation the elevation used when the chip is pressed.
 * @param focusedElevation the elevation used when the chip is focused
 * @param hoveredElevation the elevation used when the chip is hovered
 * @param draggedElevation the elevation used when the chip is dragged
 * @param disabledElevation the elevation used when the chip is not enabled
 */
@Immutable
class ChipElevationMMD(
    val elevation: Dp,
    val pressedElevation: Dp,
    val focusedElevation: Dp,
    val hoveredElevation: Dp,
    val draggedElevation: Dp,
    val disabledElevation: Dp,
) {

    @Composable
    internal fun shadowElevation(
        enabled: Boolean,
        interactionSource: InteractionSource,
    ): State<Dp> {
        val interactions = remember { mutableStateListOf<Interaction>() }

        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is HoverInteraction.Enter -> interactions.add(interaction)
                    is HoverInteraction.Exit -> interactions.remove(interaction.enter)
                    is FocusInteraction.Focus -> interactions.add(interaction)
                    is FocusInteraction.Unfocus -> interactions.remove(interaction.focus)
                    is PressInteraction.Press -> interactions.add(interaction)
                    is PressInteraction.Release -> interactions.remove(interaction.press)
                    is PressInteraction.Cancel -> interactions.remove(interaction.press)
                    is DragInteraction.Start -> interactions.add(interaction)
                    is DragInteraction.Stop -> interactions.remove(interaction.start)
                    is DragInteraction.Cancel -> interactions.remove(interaction.start)
                }
            }
        }

        val interaction = interactions.lastOrNull()

        val target = if (!enabled) {
            disabledElevation
        } else {
            when (interaction) {
                is PressInteraction.Press -> pressedElevation
                is HoverInteraction.Enter -> hoveredElevation
                is FocusInteraction.Focus -> focusedElevation
                is DragInteraction.Start -> draggedElevation
                else -> elevation
            }
        }

        return rememberUpdatedState(target)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChipElevationMMD) return false

        return elevation == other.elevation &&
            pressedElevation == other.pressedElevation &&
            focusedElevation == other.focusedElevation &&
            hoveredElevation == other.hoveredElevation &&
            draggedElevation == other.draggedElevation &&
            disabledElevation == other.disabledElevation
    }

    override fun hashCode(): Int {
        var result = elevation.hashCode()
        result = 31 * result + pressedElevation.hashCode()
        result = 31 * result + focusedElevation.hashCode()
        result = 31 * result + hoveredElevation.hashCode()
        result = 31 * result + draggedElevation.hashCode()
        result = 31 * result + disabledElevation.hashCode()
        return result
    }
}

/**
 * Represents the elevation used in a selectable chip in different states.
 *
 * @param elevation the elevation used when the chip is enabled.
 * @param pressedElevation the elevation used when the chip is pressed.
 * @param focusedElevation the elevation used when the chip is focused
 * @param hoveredElevation the elevation used when the chip is hovered.
 * @param draggedElevation the elevation used when the chip is dragged
 * @param disabledElevation the elevation used when the chip is not enabled
 */
@Immutable
class SelectableChipElevationMMD(
    val elevation: Dp,
    val pressedElevation: Dp,
    val focusedElevation: Dp,
    val hoveredElevation: Dp,
    val draggedElevation: Dp,
    val disabledElevation: Dp,
) {

    @Composable
    internal fun shadowElevation(
        enabled: Boolean,
        interactionSource: InteractionSource,
    ): State<Dp> {
        val interactions = remember { mutableStateListOf<Interaction>() }

        LaunchedEffect(interactionSource) {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is HoverInteraction.Enter -> interactions.add(interaction)
                    is HoverInteraction.Exit -> interactions.remove(interaction.enter)
                    is FocusInteraction.Focus -> interactions.add(interaction)
                    is FocusInteraction.Unfocus -> interactions.remove(interaction.focus)
                    is PressInteraction.Press -> interactions.add(interaction)
                    is PressInteraction.Release -> interactions.remove(interaction.press)
                    is PressInteraction.Cancel -> interactions.remove(interaction.press)
                    is DragInteraction.Start -> interactions.add(interaction)
                    is DragInteraction.Stop -> interactions.remove(interaction.start)
                    is DragInteraction.Cancel -> interactions.remove(interaction.start)
                }
            }
        }

        val interaction = interactions.lastOrNull()

        val target = if (!enabled) {
            disabledElevation
        } else {
            when (interaction) {
                is PressInteraction.Press -> pressedElevation
                is HoverInteraction.Enter -> hoveredElevation
                is FocusInteraction.Focus -> focusedElevation
                is DragInteraction.Start -> draggedElevation
                else -> elevation
            }
        }

        return rememberUpdatedState(target)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SelectableChipElevationMMD) return false

        return elevation == other.elevation &&
            pressedElevation == other.pressedElevation &&
            focusedElevation == other.focusedElevation &&
            hoveredElevation == other.hoveredElevation &&
            draggedElevation == other.draggedElevation &&
            disabledElevation == other.disabledElevation
    }

    override fun hashCode(): Int {
        var result = elevation.hashCode()
        result = 31 * result + pressedElevation.hashCode()
        result = 31 * result + focusedElevation.hashCode()
        result = 31 * result + hoveredElevation.hashCode()
        result = 31 * result + draggedElevation.hashCode()
        result = 31 * result + disabledElevation.hashCode()
        return result
    }
}

/**
 * Represents the container and content colors used in a clickable chip in different states.
 *
 * @param containerColor the container color of this chip when enabled
 * @param labelColor the label color of this chip when enabled
 * @param leadingIconContentColor the color of this chip's start icon when enabled
 * @param trailingIconContentColor the color of this chip's end icon when enabled
 * @param disabledContainerColor the container color of this chip when not enabled
 * @param disabledLabelColor the label color of this chip when not enabled
 * @param disabledLeadingIconContentColor the color of this chip's start icon when not enabled
 * @param disabledTrailingIconContentColor the color of this chip's end icon when not enabled
 * @constructor create an instance with arbitrary colors, see [AssistChipDefaultsMMD],
 *   [InputChipDefaultsMMD], and [SuggestionChipDefaultsMMD] for the default colors used in the various
 *   Chip configurations.
 */
@Immutable
class ChipColorsMMD(
    val containerColor: Color,
    val labelColor: Color,
    val leadingIconContentColor: Color,
    val trailingIconContentColor: Color,
    val disabledContainerColor: Color,
    val disabledLabelColor: Color,
    val disabledLeadingIconContentColor: Color,
    val disabledTrailingIconContentColor: Color,
) {
    /**
     * Returns a copy of this ChipColors, optionally overriding some of the values. This uses the
     * Color.Unspecified to mean “use the value from the source”
     */
    fun copy(
        containerColor: Color = this.containerColor,
        labelColor: Color = this.labelColor,
        leadingIconContentColor: Color = this.leadingIconContentColor,
        trailingIconContentColor: Color = this.trailingIconContentColor,
        disabledContainerColor: Color = this.disabledContainerColor,
        disabledLabelColor: Color = this.disabledLabelColor,
        disabledLeadingIconContentColor: Color = this.disabledLeadingIconContentColor,
        disabledTrailingIconContentColor: Color = this.disabledTrailingIconContentColor,
    ) =
        ChipColorsMMD(
            containerColor.takeOrElse { this.containerColor },
            labelColor.takeOrElse { this.labelColor },
            leadingIconContentColor.takeOrElse { this.leadingIconContentColor },
            trailingIconContentColor.takeOrElse { this.trailingIconContentColor },
            disabledContainerColor.takeOrElse { this.disabledContainerColor },
            disabledLabelColor.takeOrElse { this.disabledLabelColor },
            disabledLeadingIconContentColor.takeOrElse { this.disabledLeadingIconContentColor },
            disabledTrailingIconContentColor.takeOrElse { this.disabledTrailingIconContentColor },
        )

    /**
     * Represents the container color for this chip, depending on [enabled].
     *
     * @param enabled whether the chip is enabled
     */
    @Stable
    internal fun containerColor(enabled: Boolean): Color =
        if (enabled) containerColor else disabledContainerColor

    /**
     * Represents the label color for this chip, depending on [enabled].
     *
     * @param enabled whether the chip is enabled
     */
    @Stable
    internal fun labelColor(enabled: Boolean): Color =
        if (enabled) labelColor else disabledLabelColor

    /**
     * Represents the leading icon's content color for this chip, depending on [enabled].
     *
     * @param enabled whether the chip is enabled
     */
    @Stable
    internal fun leadingIconContentColor(enabled: Boolean): Color =
        if (enabled) leadingIconContentColor else disabledLeadingIconContentColor

    /**
     * Represents the trailing icon's content color for this chip, depending on [enabled].
     *
     * @param enabled whether the chip is enabled
     */
    @Stable
    internal fun trailingIconContentColor(enabled: Boolean): Color =
        if (enabled) trailingIconContentColor else disabledTrailingIconContentColor

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is ChipColorsMMD) return false

        if (containerColor != other.containerColor) return false
        if (labelColor != other.labelColor) return false
        if (leadingIconContentColor != other.leadingIconContentColor) return false
        if (trailingIconContentColor != other.trailingIconContentColor) return false
        if (disabledContainerColor != other.disabledContainerColor) return false
        if (disabledLabelColor != other.disabledLabelColor) return false
        if (disabledLeadingIconContentColor != other.disabledLeadingIconContentColor) return false
        if (disabledTrailingIconContentColor != other.disabledTrailingIconContentColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = containerColor.hashCode()
        result = 31 * result + labelColor.hashCode()
        result = 31 * result + leadingIconContentColor.hashCode()
        result = 31 * result + trailingIconContentColor.hashCode()
        result = 31 * result + disabledContainerColor.hashCode()
        result = 31 * result + disabledLabelColor.hashCode()
        result = 31 * result + disabledLeadingIconContentColor.hashCode()
        result = 31 * result + disabledTrailingIconContentColor.hashCode()

        return result
    }
}

internal val defaultSuggestionChipColors: ChipColorsMMD
    @Composable
    get() = ChipColorsMMD(
        containerColor = Color.Transparent,
        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        leadingIconContentColor = MaterialTheme.colorScheme.primary,
        trailingIconContentColor = Color.Unspecified,
        disabledContainerColor = Color.Transparent,
        disabledLabelColor = MaterialTheme.colorScheme.onSurface
            .copy(alpha = .38f),
        disabledLeadingIconContentColor = MaterialTheme.colorScheme.onSurface
            .copy(alpha = .38f),
        disabledTrailingIconContentColor = Color.Unspecified,
    )

/**
 * Represents the container and content colors used in a selectable chip in different states.
 *
 * See [FilterChipDefaultsMMD.filterChipColors] for the default colors used in [FilterChipMMD].
 */
@Immutable
class SelectableChipColorsMMD(
    private val containerColor: Color,
    private val labelColor: Color,
    private val leadingIconColor: Color,
    private val trailingIconColor: Color,
    private val disabledContainerColor: Color,
    private val disabledLabelColor: Color,
    private val disabledLeadingIconColor: Color,
    private val disabledTrailingIconColor: Color,
    private val selectedContainerColor: Color,
    private val disabledSelectedContainerColor: Color,
    private val selectedLabelColor: Color,
    private val selectedLeadingIconColor: Color,
    private val selectedTrailingIconColor: Color,
) {
    /**
     * Returns a copy of this SelectableChipColors, optionally overriding some of the values. This
     * uses the Color.Unspecified to mean “use the value from the source”
     */
    fun copy(
        containerColor: Color = this.containerColor,
        labelColor: Color = this.labelColor,
        leadingIconColor: Color = this.leadingIconColor,
        trailingIconColor: Color = this.trailingIconColor,
        disabledContainerColor: Color = this.disabledContainerColor,
        disabledLabelColor: Color = this.disabledLabelColor,
        disabledLeadingIconColor: Color = this.disabledLeadingIconColor,
        disabledTrailingIconColor: Color = this.disabledTrailingIconColor,
        selectedContainerColor: Color = this.selectedContainerColor,
        disabledSelectedContainerColor: Color = this.disabledSelectedContainerColor,
        selectedLabelColor: Color = this.selectedLabelColor,
        selectedLeadingIconColor: Color = this.selectedLeadingIconColor,
        selectedTrailingIconColor: Color = this.selectedTrailingIconColor,
    ) =
        SelectableChipColorsMMD(
            containerColor.takeOrElse { this.containerColor },
            labelColor.takeOrElse { this.labelColor },
            leadingIconColor.takeOrElse { this.leadingIconColor },
            trailingIconColor.takeOrElse { this.trailingIconColor },
            disabledContainerColor.takeOrElse { this.disabledContainerColor },
            disabledLabelColor.takeOrElse { this.disabledLabelColor },
            disabledLeadingIconColor.takeOrElse { this.disabledLeadingIconColor },
            disabledTrailingIconColor.takeOrElse { this.disabledTrailingIconColor },
            selectedContainerColor.takeOrElse { this.selectedContainerColor },
            disabledSelectedContainerColor.takeOrElse { this.disabledSelectedContainerColor },
            selectedLabelColor.takeOrElse { this.selectedLabelColor },
            selectedLeadingIconColor.takeOrElse { this.selectedLeadingIconColor },
            selectedTrailingIconColor.takeOrElse { this.selectedTrailingIconColor },
        )

    /**
     * Represents the container color for this chip, depending on [enabled] and [selected].
     *
     * @param enabled whether the chip is enabled
     * @param selected whether the chip is selected
     */
    @Stable
    internal fun containerColor(enabled: Boolean, selected: Boolean): Color {
        return when {
            !enabled -> if (selected) disabledSelectedContainerColor else disabledContainerColor
            !selected -> containerColor
            else -> selectedContainerColor
        }
    }

    /**
     * Represents the label color for this chip, depending on [enabled] and [selected].
     *
     * @param enabled whether the chip is enabled
     * @param selected whether the chip is selected
     */
    @Stable
    internal fun labelColor(enabled: Boolean, selected: Boolean): Color {
        return when {
            !enabled -> disabledLabelColor
            !selected -> labelColor
            else -> selectedLabelColor
        }
    }

    /**
     * Represents the leading icon color for this chip, depending on [enabled] and [selected].
     *
     * @param enabled whether the chip is enabled
     * @param selected whether the chip is selected
     */
    @Stable
    internal fun leadingIconContentColor(enabled: Boolean, selected: Boolean): Color {
        return when {
            !enabled -> disabledLeadingIconColor
            !selected -> leadingIconColor
            else -> selectedLeadingIconColor
        }
    }

    /**
     * Represents the trailing icon color for this chip, depending on [enabled] and [selected].
     *
     * @param enabled whether the chip is enabled
     * @param selected whether the chip is selected
     */
    @Stable
    internal fun trailingIconContentColor(enabled: Boolean, selected: Boolean): Color {
        return when {
            !enabled -> disabledTrailingIconColor
            !selected -> trailingIconColor
            else -> selectedTrailingIconColor
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is SelectableChipColorsMMD) return false

        if (containerColor != other.containerColor) return false
        if (labelColor != other.labelColor) return false
        if (leadingIconColor != other.leadingIconColor) return false
        if (trailingIconColor != other.trailingIconColor) return false
        if (disabledContainerColor != other.disabledContainerColor) return false
        if (disabledLabelColor != other.disabledLabelColor) return false
        if (disabledLeadingIconColor != other.disabledLeadingIconColor) return false
        if (disabledTrailingIconColor != other.disabledTrailingIconColor) return false
        if (selectedContainerColor != other.selectedContainerColor) return false
        if (disabledSelectedContainerColor != other.disabledSelectedContainerColor) return false
        if (selectedLabelColor != other.selectedLabelColor) return false
        if (selectedLeadingIconColor != other.selectedLeadingIconColor) return false
        if (selectedTrailingIconColor != other.selectedTrailingIconColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = containerColor.hashCode()
        result = 31 * result + labelColor.hashCode()
        result = 31 * result + leadingIconColor.hashCode()
        result = 31 * result + trailingIconColor.hashCode()
        result = 31 * result + disabledContainerColor.hashCode()
        result = 31 * result + disabledLabelColor.hashCode()
        result = 31 * result + disabledLeadingIconColor.hashCode()
        result = 31 * result + disabledTrailingIconColor.hashCode()
        result = 31 * result + selectedContainerColor.hashCode()
        result = 31 * result + disabledSelectedContainerColor.hashCode()
        result = 31 * result + selectedLabelColor.hashCode()
        result = 31 * result + selectedLeadingIconColor.hashCode()
        result = 31 * result + selectedTrailingIconColor.hashCode()

        return result
    }
}

/** Returns the [PaddingValues] for the input chip. */
private fun inputChipPadding(
    hasAvatar: Boolean = false,
    hasLeadingIcon: Boolean = false,
    hasTrailingIcon: Boolean = false,
): PaddingValues {
    val start = if (hasAvatar || !hasLeadingIcon) 8.dp else 12.dp
    val end = if (hasTrailingIcon) 12.dp else 8.dp
    return PaddingValues(start = start, end = end)
}

/** The padding between the elements in the chip. */
private val HorizontalElementsPadding = 8.dp

/** Returns the [PaddingValues] for the assist chip. */
private val AssistChipPadding = PaddingValues(horizontal = HorizontalElementsPadding)

/** [PaddingValues] for the filter chip. */
private val FilterChipPadding = PaddingValues(horizontal = HorizontalElementsPadding)

/** Returns the [PaddingValues] for the suggestion chip. */
private val SuggestionChipPadding = PaddingValues(horizontal = HorizontalElementsPadding)

private const val LeadingIconLayoutId = "leadingIcon"
private const val LabelLayoutId = "label"
private const val TrailingIconLayoutId = "trailingIcon"
