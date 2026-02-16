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

package com.mudita.mmd.components.nav_bar

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.fastFirstOrNull
import com.mudita.mmd.internal.ProvideContentColorTextStyleMMD
import kotlin.math.roundToInt

/**
 * A MMD navigation bar.
 *
 * Navigation bars offer a persistent and convenient way to switch between primary destinations in an app.
 *
 * This composable is a wrapper around the `NavigationBar` composable from Material 3, providing
 * a more convenient way to define a navigation bar with common defaults and allowing for better
 * readability in code.
 *
 * Example usage:
 *
 * ```kotlin
 *  NavigationBarMMD {
 *      NavigationBarItemMMD(
 *          icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
 *          label = { Text("Home") },
 *          selected = selectedItem == 0,
 *          onClick = { selectedItem = 0 }
 *      )
 *      NavigationBarItemMMD(
 *          icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
 *          label = { Text("Settings") },
 *          selected = selectedItem == 1,
 *          onClick = { selectedItem = 1 }
 *      )
 *  }
 * ```
 *
 * @param modifier The [Modifier] to be applied to this navigation bar.
 * @param containerColor The background color for the navigation bar.
 * @param contentColor The preferred color for content inside this navigation bar.
 * @param windowInsets The window insets that this navigation bar will respect.
 * @param showTopDivider Whether to show a divider at the top of the navigation bar.
 * @param content The content of the navigation bar, which typically consists of [NavigationBarItem]s.
 */
@Composable
fun NavigationBarMMD(
    modifier: Modifier = Modifier,
    containerColor: Color = NavigationBarDefaultsMMD.containerColor,
    contentColor: Color = MaterialTheme.colorScheme.contentColorFor(containerColor),
    windowInsets: WindowInsets = NavigationBarDefaultsMMD.windowInsets,
    showTopDivider: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    Column {
        if (showTopDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(NavigationBarDefaultsMMD.topDividerHeight)
                    .background(NavigationBarDefaultsMMD.topDividerColor),
            )
        }
        NavigationBar(
            modifier = modifier,
            containerColor = containerColor,
            contentColor = contentColor,
            tonalElevation = NavigationBarDefaultsMMD.elevation,
            windowInsets = windowInsets,
            content = {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    content()
                }
            },
        )
    }
}

/**
 * Material Design navigation bar item.
 *
 * Navigation bars offer a persistent and convenient way to switch between primary destinations in
 * an app.
 *
 * The recommended configuration for a [NavigationBarItemMMD] depends on how many items there are
 * inside a [NavigationBarMMD]:
 * - Three destinations: Display icons and text labels for all destinations.
 * - Four destinations: Active destinations display an icon and text label. Inactive destinations
 *   display icons, and text labels are recommended.
 * - Five destinations: Active destinations display an icon and text label. Inactive destinations
 *   use icons, and use text labels if space permits.
 *
 * A [NavigationBarItemMMD] always shows text labels (if it exists) when selected. Showing text labels
 * if not selected is controlled by [alwaysShowLabel].
 *
 * @param selected whether this item is selected
 * @param onClick called when this item is clicked
 * @param icon icon for this item, typically an [Icon]
 * @param modifier the [Modifier] to be applied to this item
 * @param enabled controls the enabled state of this item. When `false`, this component will not
 *   respond to user input, and it will appear visually disabled and disabled to accessibility
 *   services.
 * @param label optional text label for this item
 * @param alwaysShowLabel whether to always show the label for this item. If `false`, the label will
 *   only be shown when this item is selected.
 * @param colors [NavigationBarItemColors] that will be used to resolve the colors used for this
 *   item in different states. See [NavigationBarItemDefaults.colors].
 * @param interactionSource an optional hoisted [MutableInteractionSource] for observing and
 *   emitting [Interaction]s for this item. You can use this to change the item's appearance or
 *   preview the item in different states. Note that if `null` is provided, interactions will still
 *   happen internally.
 */
@Composable
fun RowScope.NavigationBarItemMMD(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    alwaysShowLabel: Boolean = true,
    colors: NavigationBarItemColors = NavigationBarItemDefaultsMMD.colors(),
    interactionSource: MutableInteractionSource? = null,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val styledIcon =
        @Composable
        {
            val iconColor = NavigationBarItemDefaultsMMD.iconColor(
                colors = colors, selected = selected, enabled = enabled,
            )
            // If there's a label, don't have a11y services repeat the icon description.
            val clearSemantics = label != null && (alwaysShowLabel || selected)
            Box(modifier = if (clearSemantics) Modifier.clearAndSetSemantics {} else Modifier) {
                CompositionLocalProvider(LocalContentColor provides iconColor, content = icon)
            }
        }

    val styledLabel: @Composable (() -> Unit)? =
        label?.let {
            @Composable {
                val style = NavigationBarItemDefaultsMMD.labelTextFont
                val textColor = NavigationBarItemDefaultsMMD.textColors(
                    colors = colors,
                    selected = selected,
                    enabled = enabled,
                )
                ProvideContentColorTextStyleMMD(
                    contentColor = textColor,
                    textStyle = style,
                    content = label,
                )
            }
        }

    var itemWidth by remember { mutableIntStateOf(0) }

    Box(
        modifier
            .selectable(
                selected = selected,
                onClick = onClick,
                enabled = enabled,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = null,
            )
            .indication(remember { MutableInteractionSource() }, LocalIndication.current)
            .defaultMinSize(minHeight = NavigationBarHeight)
            .weight(1f)
            .onSizeChanged { itemWidth = it.width },
        contentAlignment = Alignment.Center,
        propagateMinConstraints = true,
    ) {
        val selectProgress: Float = if (selected) 1f else 0f

        val indicator =
            @Composable {
                Box(
                    Modifier
                        .layoutId(IndicatorLayoutIdTag)
                        .fillMaxWidth()
                        .background(
                            color = NavigationBarItemDefaultsMMD.indicatorColor,
                        ),
                )
            }

        NavigationBarItemLayoutMMD(
            indicator = indicator,
            icon = styledIcon,
            label = styledLabel,
            alwaysShowLabel = alwaysShowLabel,
            selectProgress = { selectProgress },
        )
    }
}

/**
 * Base layout for a [NavigationBarItemMMD].
 *
 * @param indicator indicator for this item when it is selected
 * @param icon icon for this item
 * @param label text label for this item
 * @param alwaysShowLabel whether to always show the label for this item. If false, the label will
 *   only be shown when this item is selected.
 * @param selectProgress progress, where 0 represents the unselected state of
 *   this item and 1 represents the selected state. This value controls other values such as
 *   indicator size, icon and label positions, etc.
 */
@Composable
private fun NavigationBarItemLayoutMMD(
    indicator: @Composable () -> Unit,
    icon: @Composable () -> Unit,
    label: @Composable (() -> Unit)?,
    alwaysShowLabel: Boolean,
    selectProgress: () -> Float,
) {
    Layout(
        content = {
            indicator()

            Box(
                Modifier
                    .layoutId(IconLayoutIdTag)
                    .heightIn(max = NavigationBarItemDefaultsMMD.iconContainerMaxSize)
                    .widthIn(max = NavigationBarItemDefaultsMMD.iconContainerMaxSize),
            ) {
                icon()
            }

            if (label != null) {
                Box(
                    Modifier
                        .layoutId(LabelLayoutIdTag)
                        .heightIn(max = NavigationBarItemDefaultsMMD.labelContainerMaxHeight)
                        .graphicsLayer { alpha = if (alwaysShowLabel) 1f else selectProgress() }
                        .padding(horizontal = NavigationBarItemHorizontalPadding / 2),
                ) {
                    label() // Assuming label is defined and not null
                }
            }
        },
    ) { measurables, constraints ->
        @Suppress("NAME_SHADOWING") val stateProgress = selectProgress()
        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val iconPlaceable =
            measurables.fastFirst { it.layoutId == IconLayoutIdTag }.measure(looseConstraints)

        val totalIndicatorWidth = constraints.maxWidth
        val animatedIndicatorWidth = (totalIndicatorWidth * stateProgress).roundToInt()
        val indicatorHeight = NavigationBarItemDefaultsMMD.indicatorHeight.roundToPx()
        val indicatorPlaceable =
            measurables
                .fastFirstOrNull { it.layoutId == IndicatorLayoutIdTag }
                ?.measure(
                    Constraints.fixed(width = animatedIndicatorWidth, height = indicatorHeight),
                )

        val labelPlaceable =
            label?.let {
                measurables.fastFirst { it.layoutId == LabelLayoutIdTag }.measure(looseConstraints)
            }

        if (label == null) {
            placeIconMMD(iconPlaceable, indicatorPlaceable, constraints)
        } else {
            placeLabelAndIconMMD(
                labelPlaceable!!,
                iconPlaceable,
                indicatorPlaceable,
                constraints,
                alwaysShowLabel,
                stateProgress,
            )
        }
    }
}

/**
 * Places the provided [Placeable]s in the correct position, depending on [alwaysShowLabel] and
 * [stateProgress].
 *
 * When [alwaysShowLabel] is true, the positions do not move. The [iconPlaceable] and
 * [labelPlaceable] will be placed together in the center with padding between them, according to
 * the spec.
 *
 * When [stateProgress] is 1 (representing the selected state), the positions will be the same
 * as above.
 *
 * Otherwise, when [stateProgress] is 0, [iconPlaceable] will be placed in the center, like in
 * [placeIconMMD], and [labelPlaceable] will not be shown.
 *
 * When [stateProgress] is animating between these values, [iconPlaceable] and [labelPlaceable]
 * will be placed at a corresponding interpolated position.
 *
 * [indicatorPlaceable] will always be placed in such a way that to
 * share the same center as [iconPlaceable].
 *
 * @param labelPlaceable text label placeable inside this item
 * @param iconPlaceable icon placeable inside this item
 * @param indicatorPlaceable indicator placeable inside this item, if it exists
 * @param constraints constraints of the item
 * @param alwaysShowLabel whether to always show the label for this item. If true, icon and label
 *   positions will not change. If false, positions transition between 'centered icon with no label'
 *   and 'top aligned icon with label'.
 * @param stateProgress progress, where 0 represents the unselected state of
 *   this item and 1 represents the selected state. Values between 0 and 1 interpolate positions of
 *   the icon and label.
 */
private fun MeasureScope.placeLabelAndIconMMD(
    labelPlaceable: Placeable,
    iconPlaceable: Placeable,
    indicatorPlaceable: Placeable?,
    constraints: Constraints,
    alwaysShowLabel: Boolean,
    stateProgress: Float,
): MeasureResult {
    val contentHeight =
        iconPlaceable.height + IndicatorVerticalPadding.toPx() + NavigationBarIndicatorToLabelPadding.toPx() + labelPlaceable.height
    val contentVerticalPadding =
        ((constraints.minHeight - contentHeight) / 2).coerceAtLeast(IndicatorVerticalPadding.toPx())
    val height = contentHeight + contentVerticalPadding * 2

    // Icon (when selected) should be `contentVerticalPadding` from top
    val selectedIconY = contentVerticalPadding
    val unselectedIconY =
        if (alwaysShowLabel) selectedIconY else (height - iconPlaceable.height) / 2

    // How far the icon needs to move between unselected and selected states.
    val iconDistance = unselectedIconY - selectedIconY

    // The interpolated fraction of iconDistance that all placeables need to move based on
    // animationProgress.
    val offset = iconDistance * (1 - stateProgress)

    // Label should be fixed padding below icon
    val labelY =
        selectedIconY + iconPlaceable.height + IndicatorVerticalPadding.toPx() + NavigationBarIndicatorToLabelPadding.toPx()

    val containerWidth = constraints.maxWidth
    val containerHeight = constraints.minHeight

    val labelX = (containerWidth - labelPlaceable.width) / 2
    val iconX = (containerWidth - iconPlaceable.width) / 2

    return layout(containerWidth, height.roundToInt()) {
        indicatorPlaceable?.let {
            val indicatorX = (containerWidth - it.width) / 2
            val indicatorY = containerHeight - it.height
            it.placeRelative(indicatorX, (indicatorY + offset).roundToInt())
        }
        if (alwaysShowLabel || stateProgress != 0f) {
            labelPlaceable.placeRelative(labelX, (labelY + offset).roundToInt())
        }
        iconPlaceable.placeRelative(iconX, (selectedIconY + offset).roundToInt())
    }
}

/** Places the provided [Placeable]s in the center of the provided [constraints]. */
private fun MeasureScope.placeIconMMD(
    iconPlaceable: Placeable,
    indicatorPlaceable: Placeable?,
    constraints: Constraints,
): MeasureResult {
    val width = constraints.maxWidth
    val height =
        constraints.constrainHeight(NavigationBarItemDefaultsMMD.containerHeight.roundToPx())

    val iconX = (width - iconPlaceable.width) / 2
    val iconY = (height - iconPlaceable.height) / 2

    return layout(width, height) {
        indicatorPlaceable?.let {
            val indicatorX = (width - it.width) / 2
            val indicatorY = height - it.height
            it.placeRelative(indicatorX, indicatorY)
        }
        iconPlaceable.placeRelative(iconX, iconY)
    }
}

private const val IndicatorLayoutIdTag: String = "indicator"

private const val IconLayoutIdTag: String = "icon"

private const val LabelLayoutIdTag: String = "label"

private val NavigationBarHeight: Dp = NavigationBarItemDefaultsMMD.containerHeight

internal val NavigationBarItemHorizontalPadding: Dp = 8.dp

internal val NavigationBarIndicatorToLabelPadding: Dp = 4.dp

internal val IndicatorVerticalPadding: Dp =
    (NavigationBarItemDefaultsMMD.activeIndicatorHeight - NavigationBarItemDefaultsMMD.iconSize) / 2

object NavigationBarDefaultsMMD {
    /** Default elevation for a navigation bar. */
    val elevation: Dp = 0.dp

    /**
     * Default height for a navigation bar.
     * */
    val topDividerHeight: Dp = 2.dp

    /**
     * Default color for a navigation bar divider.
     * */
    val topDividerColor: Color
        @Composable get() = MaterialTheme.colorScheme.onSurface

    /** Default color for a navigation bar. */
    val containerColor: Color
        @Composable get() = MaterialTheme.colorScheme.surface

    /** Default window insets to be used and consumed by navigation bar */
    val windowInsets: WindowInsets
        @Composable
        get() = NavigationBarDefaults.windowInsets
}

object NavigationBarItemDefaultsMMD {
    /**
     * Default height for a navigation bar item.
     * */
    val indicatorHeight: Dp = 4.dp

    /**
     * Default icon size for a navigation bar item.
     * */
    val iconSize: Dp = 24.dp

    /**
     * Default active indicator height for a navigation bar item.
     * */
    val activeIndicatorHeight: Dp = 32.dp

    /**
     * Default container height for a navigation bar item.
     * */
    val containerHeight: Dp = 80.dp

    /**
     * Maximum height for a navigation bar item label.
     * */
    val labelContainerMaxHeight: Dp = 38.dp

    /**
     * Maximum height for a navigation bar item icon.
     * */
    val iconContainerMaxSize: Dp = 24.dp

    /**
     * Default text style for a navigation bar item label.
     * */
    val labelTextFont: TextStyle
        @Composable get() = MaterialTheme.typography.labelMedium

    /**
     * Default indicator color for a navigation bar item.
     */
    val indicatorColor: Color
        @Composable get() = MaterialTheme.colorScheme.onSurface

    /**
     * Creates a [NavigationBarItemColors] with the provided colors according to the MMD
     * specification.
     */
    @Composable
    fun colors(
        selectedIconColor: Color = MaterialTheme.colorScheme.onSurface,
        selectedTextColor: Color = MaterialTheme.colorScheme.onSurface,
        selectedIndicatorColor: Color = MaterialTheme.colorScheme.onSurface,
        unselectedIconColor: Color = MaterialTheme.colorScheme.onSurface,
        unselectedTextColor: Color = MaterialTheme.colorScheme.onSurface,
        disabledIconColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
        disabledTextColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
    ) = NavigationBarItemColors(
        selectedIconColor = selectedIconColor,
        selectedTextColor = selectedTextColor,
        selectedIndicatorColor = selectedIndicatorColor,
        unselectedIconColor = unselectedIconColor,
        unselectedTextColor = unselectedTextColor,
        disabledIconColor = disabledIconColor,
        disabledTextColor = disabledTextColor
    )

    /**
     * Determines the text color for a navigation bar item based on the provided [colors], [selected],
     * */
    fun textColors(colors: NavigationBarItemColors, selected: Boolean, enabled: Boolean): Color {
        return when {
            !enabled -> colors.disabledTextColor
            selected -> colors.selectedTextColor
            else -> colors.unselectedTextColor
        }
    }

    /**
     * Determines the icon color for a navigation bar item based on the provided [colors], [selected]
     **/
    fun iconColor(colors: NavigationBarItemColors, selected: Boolean, enabled: Boolean): Color {
        return when {
            !enabled -> colors.disabledIconColor
            selected -> colors.selectedIconColor
            else -> colors.unselectedIconColor
        }
    }
}
