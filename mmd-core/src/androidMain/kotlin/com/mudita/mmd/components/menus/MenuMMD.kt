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

package com.mudita.mmd.components.menus

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

/**
 * DropdownMenu MMD
 *
 * Menus display a list of choices on a temporary surface. They appear when users interact with a
 * button, action, or other control.
 *
 * A [DropdownMenuMMD] behaves similarly to a [Popup], and will use the position of the parent layout
 * to position itself on screen. Commonly a [DropdownMenuMMD] will be placed in a [Box] with a sibling
 * that will be used as the 'anchor'. Note that a [DropdownMenuMMD] by itself will not take up any
 * space in a layout, as the menu is displayed in a separate window, on top of other content.
 *
 * The [content] of a [DropdownMenuMMD] will typically be [DropdownMenuItemMMD]s, as well as custom
 * content. Using [DropdownMenuItemMMD]s will result in a menu that matches the Material specification
 * for menus. Also note that the [content] is placed inside a scrollable [Column].
 *
 * [onDismissRequest] will be called when the menu should close - for example when there is a tap
 * outside the menu, or when the back key is pressed.
 *
 * [DropdownMenuMMD] changes its positioning depending on the available space, always trying to be
 * fully visible. Depending on layout direction, first it will try to align its start to the start
 * of its parent, then its end to the end of its parent, and then to the edge of the window.
 * Vertically, it will try to align its top to the bottom of its parent, then its bottom to top of
 * its parent, and then to the edge of the window.
 *
 * An [offset] can be provided to adjust the positioning of the menu for cases when the layout
 * bounds of its parent do not coincide with its visual bounds.
 *
 * Example usage:
 *
 * ```kotlin
 * @Composable
 * fun MenuSample() {
 *     var expanded by remember { mutableStateOf(false) }
 *
 *     Box {
 *         IconButton(onClick = { expanded = true }) {
 *             Icon(Icons.Default.MoreVert, contentDescription = "More options")
 *         }
 *
 *         DropdownMenuMMD(
 *             expanded = expanded,
 *             onDismissRequest = { expanded = false },
 *             offset = DpOffset(x = (-8).dp, y = 0.dp)
 *         ) {
 *             DropdownMenuItemMMD(
 *                 text = { Text("Option 1") },
 *                 onClick = { expanded = false }
 *             )
 *             DashedDivider()
 *             DropdownMenuItemMMD(
 *                 enabled = false,
 *                 text = { Text("Option 2") },
 *                 onClick = { expanded = false }
 *             )
 *             DashedDivider()
 *             DropdownMenuItemMMD(
 *                 text = { Text("Option 3") },
 *                 onClick = { expanded = false },
 *                 leadingIcon = {
 *                     Icon(Icons.Default.Call, contentDescription = "Option 2 Icon")
 *                 }
 *             )
 *             DashedDivider()
 *             DropdownMenuItemMMD(
 *                 text = { Text("Option 4") },
 *                 onClick = { expanded = false },
 *                 leadingIcon = {
 *                     Icon(Icons.Default.Home, contentDescription = "Option 3 Icon")
 *                 },
 *                 trailingIcon = {
 *                     Icon(
 *                         imageVector = Icons.Default.KeyboardArrowRight,
 *                         contentDescription = "Option 3 Trailing Icon"
 *                     )
 *                 }
 *             )
 *         }
 *     }
 * }
 * ```
 *
 * @param expanded whether the menu is expanded or not
 * @param onDismissRequest called when the user requests to dismiss the menu, such as by tapping
 *   outside the menu's bounds
 * @param modifier [Modifier] to be applied to the menu's content
 * @param offset [DpOffset] from the original position of the menu. The offset respects the
 *   [LayoutDirection], so the offset's x position will be added in LTR and subtracted in RTL.
 * @param scrollState a [ScrollState] to used by the menu's content for items vertical scrolling
 * @param properties [PopupProperties] for further customization of this popup's behavior
 * @param shape the shape of the menu
 * @param containerColor the container color of the menu
 * @param tonalElevation when [containerColor] is [ColorScheme.surface], a translucent primary color
 *   overlay is applied on top of the container. A higher tonal elevation value will result in a
 *   darker color in light theme and lighter color in dark theme. See also: [Surface].
 * @param shadowElevation the elevation for the shadow below the menu
 * @param border the border to draw around the container of the menu. Pass `null` for no border.
 * @param content the content of this dropdown menu, typically a [DropdownMenuItemMMD]
 */
@Composable
fun DropdownMenuMMD(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
    scrollState: ScrollState = rememberScrollState(),
    properties: PopupProperties = DefaultMenuProperties,
    shape: Shape = MenuDefaultsMMD.shape,
    containerColor: Color = MenuDefaultsMMD.containerColor,
    tonalElevation: Dp = MenuDefaultsMMD.TonalElevation,
    shadowElevation: Dp = MenuDefaultsMMD.ShadowElevation,
    border: BorderStroke? = MenuDefaultsMMD.border,
    content: @Composable ColumnScope.() -> Unit
) {
    if (expanded) {
        val density = LocalDensity.current
        val popupPositionProvider = remember(offset, density) {
            DropdownMenuPositionProvider(
                contentOffset = offset,
                density = density
            )
        }

        Popup(
            onDismissRequest = onDismissRequest,
            popupPositionProvider = popupPositionProvider,
            properties = properties
        ) {
            DropdownMenuContent(
                scrollState = scrollState,
                shape = shape,
                containerColor = containerColor,
                tonalElevation = tonalElevation,
                shadowElevation = shadowElevation,
                border = border,
                modifier = modifier,
                content = content,
            )
        }
    }
}

/**
 * DropdownMenuItem MMD
 *
 * Menus display a list of choices on a temporary surface. They appear when users interact with a
 * button, action, or other control.
 *
 * Example usage:
 *
 * ```kotlin
 * @Composable
 * fun MenuSample() {
 *     var expanded by remember { mutableStateOf(false) }
 *
 *     Box {
 *         IconButton(onClick = { expanded = true }) {
 *             Icon(Icons.Default.MoreVert, contentDescription = "More options")
 *         }
 *
 *         DropdownMenuMMD(
 *             expanded = expanded,
 *             onDismissRequest = { expanded = false },
 *             offset = DpOffset(x = (-8).dp, y = 0.dp)
 *         ) {
 *             DropdownMenuItemMMD(
 *                 text = { Text("Option 1") },
 *                 onClick = { expanded = false }
 *             )
 *             DashedDivider()
 *             DropdownMenuItemMMD(
 *                 enabled = false,
 *                 text = { Text("Option 2") },
 *                 onClick = { expanded = false }
 *             )
 *             DashedDivider()
 *             DropdownMenuItemMMD(
 *                 text = { Text("Option 3") },
 *                 onClick = { expanded = false },
 *                 leadingIcon = {
 *                     Icon(Icons.Default.Call, contentDescription = "Option 2 Icon")
 *                 }
 *             )
 *             DashedDivider()
 *             DropdownMenuItemMMD(
 *                 text = { Text("Option 4") },
 *                 onClick = { expanded = false },
 *                 leadingIcon = {
 *                     Icon(Icons.Default.Home, contentDescription = "Option 3 Icon")
 *                 },
 *                 trailingIcon = {
 *                     Icon(
 *                         imageVector = Icons.Default.KeyboardArrowRight,
 *                         contentDescription = "Option 3 Trailing Icon"
 *                     )
 *                 }
 *             )
 *         }
 *     }
 * }
 * ```
 *
 * @param text text of the menu item
 * @param onClick called when this menu item is clicked
 * @param modifier the [Modifier] to be applied to this menu item
 * @param leadingIcon optional leading icon to be displayed at the beginning of the item's text
 * @param trailingIcon optional trailing icon to be displayed at the end of the item's text. This
 *   trailing icon slot can also accept [Text] to indicate a keyboard shortcut.
 * @param enabled controls the enabled state of this menu item. When `false`, this component will
 *   not respond to user input, and it will appear visually disabled and disabled to accessibility
 *   services.
 * @param colors [MenuItemColorsMMD] that will be used to resolve the colors used for this menu item in
 *   different states. See [MenuDefaultsMMD.itemColors].
 * @param contentPadding the padding applied to the content of this menu item
 * @param interactionSource an optional hoisted [MutableInteractionSource] for observing and
 *   emitting [Interaction]s for this menu item. You can use this to change the menu item's
 *   appearance or preview the menu item in different states. Note that if `null` is provided,
 *   interactions will still happen internally.
 */
@Composable
fun DropdownMenuItemMMD(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    colors: MenuItemColorsMMD = MenuDefaultsMMD.itemColors(),
    contentPadding: PaddingValues = MenuDefaultsMMD.DropdownMenuItemContentPadding,
    interactionSource: MutableInteractionSource? = null,
) {
    DropdownMenuItemContent(
        text = text,
        onClick = onClick,
        modifier = modifier,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        enabled = enabled,
        colors = colors,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
    )
}

/** Contains default values used for [DropdownMenuMMD] and [DropdownMenuItemMMD]. */
object MenuDefaultsMMD {
    /** The default tonal elevation for a menu. */
    val TonalElevation = 0.dp

    /** The default shadow elevation for a menu. */
    val ShadowElevation = 0.0.dp

    /** The default shape for a menu. */
    val shape
        @Composable get() = RoundedCornerShape(20.dp)

    /** The default container color for a menu. */
    val containerColor
        @Composable get() = MaterialTheme.colorScheme.surface

    /** The default border for a menu. */
    val border: BorderStroke
        @Composable get() = BorderStroke(2.dp, MaterialTheme.colorScheme.outline)

    /**
     * Creates a [MenuItemColorsMMD] that represents the default text and icon colors used in a
     * [DropdownMenuItemContent].
     */
    @Composable
    fun itemColors() = defaultMenuItemColors

    /**
     * Creates a [MenuItemColorsMMD] that represents the default text and icon colors used in a
     * [DropdownMenuItemContent].
     *
     * @param textColor the text color of this [DropdownMenuItemContent] when enabled
     * @param leadingIconColor the leading icon color of this [DropdownMenuItemContent] when enabled
     * @param trailingIconColor the trailing icon color of this [DropdownMenuItemContent] when
     *   enabled
     * @param disabledTextColor the text color of this [DropdownMenuItemContent] when not enabled
     * @param disabledLeadingIconColor the leading icon color of this [DropdownMenuItemContent] when
     *   not enabled
     * @param disabledTrailingIconColor the trailing icon color of this [DropdownMenuItemContent]
     *   when not enabled
     */
    @Composable
    fun itemColors(
        textColor: Color = Color.Unspecified,
        leadingIconColor: Color = Color.Unspecified,
        trailingIconColor: Color = Color.Unspecified,
        disabledTextColor: Color = Color.Unspecified,
        disabledLeadingIconColor: Color = Color.Unspecified,
        disabledTrailingIconColor: Color = Color.Unspecified,
    ): MenuItemColorsMMD =
        defaultMenuItemColors.copy(
            textColor = textColor,
            leadingIconColor = leadingIconColor,
            trailingIconColor = trailingIconColor,
            disabledTextColor = disabledTextColor,
            disabledLeadingIconColor = disabledLeadingIconColor,
            disabledTrailingIconColor = disabledTrailingIconColor,
        )

    private val defaultMenuItemColors: MenuItemColorsMMD
        @Composable get() = MenuItemColorsMMD(
            textColor = MaterialTheme.colorScheme.onSurface,
            leadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            trailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTextColor = MaterialTheme.colorScheme.onSurface
                .copy(alpha = .38f),
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface
                .copy(alpha = .38f),
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface
                .copy(alpha = .38f),
        )

    /** Default padding used for [DropdownMenuItemMMD]. */
    val DropdownMenuItemContentPadding =
        PaddingValues(horizontal = DropdownMenuItemHorizontalPadding, vertical = 0.dp)
}

internal val DefaultMenuProperties: PopupProperties = PopupProperties(focusable = true)

/**
 * Represents the text and icon colors used in a menu item at different states.
 *
 * @param textColor the text color of this [DropdownMenuItemContent] when enabled
 * @param leadingIconColor the leading icon color of this [DropdownMenuItemContent] when enabled
 * @param trailingIconColor the trailing icon color of this [DropdownMenuItemContent] when enabled
 * @param disabledTextColor the text color of this [DropdownMenuItemContent] when not enabled
 * @param disabledLeadingIconColor the leading icon color of this [DropdownMenuItemContent] when not
 *   enabled
 * @param disabledTrailingIconColor the trailing icon color of this [DropdownMenuItemContent] when
 *   not enabled
 * @constructor create an instance with arbitrary colors. See [MenuDefaultsMMD.itemColors] for the
 *   default colors used in a [DropdownMenuItemContent].
 */
@Immutable
class MenuItemColorsMMD(
    val textColor: Color,
    val leadingIconColor: Color,
    val trailingIconColor: Color,
    val disabledTextColor: Color,
    val disabledLeadingIconColor: Color,
    val disabledTrailingIconColor: Color,
) {

    /**
     * Returns a copy of this MenuItemColors, optionally overriding some of the values. This uses
     * the Color.Unspecified to mean “use the value from the source”
     */
    fun copy(
        textColor: Color = this.textColor,
        leadingIconColor: Color = this.leadingIconColor,
        trailingIconColor: Color = this.trailingIconColor,
        disabledTextColor: Color = this.disabledTextColor,
        disabledLeadingIconColor: Color = this.disabledLeadingIconColor,
        disabledTrailingIconColor: Color = this.disabledTrailingIconColor,
    ) =
        MenuItemColorsMMD(
            textColor.takeOrElse { this.textColor },
            leadingIconColor.takeOrElse { this.leadingIconColor },
            trailingIconColor.takeOrElse { this.trailingIconColor },
            disabledTextColor.takeOrElse { this.disabledTextColor },
            disabledLeadingIconColor.takeOrElse { this.disabledLeadingIconColor },
            disabledTrailingIconColor.takeOrElse { this.disabledTrailingIconColor },
        )

    /**
     * Represents the text color for a menu item, depending on its [enabled] state.
     *
     * @param enabled whether the menu item is enabled
     */
    @Stable
    internal fun textColor(enabled: Boolean): Color = if (enabled) textColor else disabledTextColor

    /**
     * Represents the leading icon color for a menu item, depending on its [enabled] state.
     *
     * @param enabled whether the menu item is enabled
     */
    @Stable
    internal fun leadingIconColor(enabled: Boolean): Color =
        if (enabled) leadingIconColor else disabledLeadingIconColor

    /**
     * Represents the trailing icon color for a menu item, depending on its [enabled] state.
     *
     * @param enabled whether the menu item is enabled
     */
    @Stable
    internal fun trailingIconColor(enabled: Boolean): Color =
        if (enabled) trailingIconColor else disabledTrailingIconColor

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is MenuItemColorsMMD) return false

        if (textColor != other.textColor) return false
        if (leadingIconColor != other.leadingIconColor) return false
        if (trailingIconColor != other.trailingIconColor) return false
        if (disabledTextColor != other.disabledTextColor) return false
        if (disabledLeadingIconColor != other.disabledLeadingIconColor) return false
        if (disabledTrailingIconColor != other.disabledTrailingIconColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = textColor.hashCode()
        result = 31 * result + leadingIconColor.hashCode()
        result = 31 * result + trailingIconColor.hashCode()
        result = 31 * result + disabledTextColor.hashCode()
        result = 31 * result + disabledLeadingIconColor.hashCode()
        result = 31 * result + disabledTrailingIconColor.hashCode()
        return result
    }
}

@Composable
internal fun DropdownMenuContent(
    modifier: Modifier,
    scrollState: ScrollState,
    shape: Shape,
    containerColor: Color,
    tonalElevation: Dp,
    shadowElevation: Dp,
    border: BorderStroke?,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        shape = shape,
        color = containerColor,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        border = border,
    ) {
        Column(
            modifier = modifier
                .padding(vertical = DropdownMenuVerticalPadding)
                .width(IntrinsicSize.Max)
                .verticalScroll(scrollState),
            content = content,
        )
    }
}

@Composable
internal fun DropdownMenuItemContent(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier,
    leadingIcon: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    enabled: Boolean,
    colors: MenuItemColorsMMD,
    contentPadding: PaddingValues,
    interactionSource: MutableInteractionSource?
) {
    Row(
        modifier = modifier
            .clickable(
                enabled = enabled,
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null,
            )
            .fillMaxWidth()
            .sizeIn(
                minWidth = DropdownMenuItemDefaultMinWidth,
                maxWidth = DropdownMenuItemDefaultMaxWidth,
                minHeight = MenuListItemContainerHeight,
            )
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ProvideTextStyle(MaterialTheme.typography.labelLarge) {
            if (leadingIcon != null) {
                CompositionLocalProvider(
                    LocalContentColor provides colors.leadingIconColor(enabled),
                ) {
                    Box(Modifier.defaultMinSize(minWidth = 24.dp)) {
                        leadingIcon()
                    }
                }
            }
            CompositionLocalProvider(LocalContentColor provides colors.textColor(enabled)) {
                Box(
                    Modifier
                        .weight(1f)
                        .padding(
                            start = if (leadingIcon != null) {
                                DropdownMenuItemHorizontalPadding
                            } else {
                                0.dp
                            },
                            end = if (trailingIcon != null) {
                                DropdownMenuItemHorizontalPadding
                            } else {
                                0.dp
                            },
                        ),
                ) {
                    text()
                }
            }
            if (trailingIcon != null) {
                CompositionLocalProvider(
                    LocalContentColor provides colors.trailingIconColor(enabled)
                ) {
                    Box(Modifier.defaultMinSize(minWidth = 24.dp)) {
                        trailingIcon()
                    }
                }
            }
        }
    }
}

internal val MenuVerticalMargin = 48.dp
private val MenuListItemContainerHeight = 48.dp
private val DropdownMenuItemHorizontalPadding = 12.dp
internal val DropdownMenuVerticalPadding = 8.dp
private val DropdownMenuItemDefaultMinWidth = 112.dp
private val DropdownMenuItemDefaultMaxWidth = 280.dp
