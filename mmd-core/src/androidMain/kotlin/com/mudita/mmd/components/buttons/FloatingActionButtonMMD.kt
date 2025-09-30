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

package com.mudita.mmd.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The FAB represents the most important action on a screen. It puts key actions within reach.
 *
 * @param onClick called when this FAB is clicked
 * @param modifier the [Modifier] to be applied to this FAB
 * @param shape defines the shape of this FAB's container
 * @param containerColor the color used for the background of this FAB. Use [Color.Transparent] to
 *   have no color.
 * @param contentColor the preferred color for content inside this FAB. Defaults to either the
 *   matching content color for [containerColor], or to the current [LocalContentColor] if
 *   [containerColor] is not a color from the theme.
 * @param interactionSource an optional hoisted [MutableInteractionSource] for observing and
 *   emitting [Interaction]s for this FAB. You can use this to change the FAB's appearance or
 *   preview the FAB in different states. Note that if `null` is provided, interactions will still
 *   happen internally.
 * @param content the content of this FAB, typically an [Icon]
 */
@Composable
fun FloatingActionButtonMMD(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = FloatingActionButtonDefaultsMMD.shape,
    containerColor: Color = FloatingActionButtonDefaultsMMD.containerColor,
    contentColor: Color = contentColorFor(containerColor),
    interactionSource: MutableInteractionSource? = null,
    border: BorderStroke? = FloatingActionButtonDefaultsMMD.borderStroke,
    content: @Composable () -> Unit,
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = border?.let {
            modifier.border(
                border = border,
                shape = shape
            )
        } ?: modifier,
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        elevation = FloatingActionButtonDefaultsMMD.elevation,
        interactionSource = interactionSource,
    ) {
        content()
    }
}

/**
 *
 * The FAB represents the most important action on a screen. It puts key actions within reach.
 *
 * @param onClick called when this FAB is clicked
 * @param modifier the [Modifier] to be applied to this FAB
 * @param shape defines the shape of this FAB's container
 * @param containerColor the color used for the background of this FAB. Use [Color.Transparent] to
 *   have no color.
 * @param contentColor the preferred color for content inside this FAB. Defaults to either the
 *   matching content color for [containerColor], or to the current [LocalContentColor] if
 *   [containerColor] is not a color from the theme.
 * @param interactionSource an optional hoisted [MutableInteractionSource] for observing and
 *   emitting [Interaction]s for this FAB. You can use this to change the FAB's appearance or
 *   preview the FAB in different states. Note that if `null` is provided, interactions will still
 *   happen internally.
 * @param content the content of this FAB, typically an [Icon]
 */
@Composable
fun SmallFloatingActionButtonMMD(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = SmallFloatingActionButtonDefaultsMMD.smallShape,
    containerColor: Color = SmallFloatingActionButtonDefaultsMMD.smallContainerColor,
    contentColor: Color = contentColorFor(containerColor),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable () -> Unit,
) {
    FloatingActionButtonMMD(
        onClick = onClick,
        modifier =
        modifier.sizeIn(
            minWidth = SmallFloatingActionButtonDefaultsMMD.smallContainerSize,
            minHeight = SmallFloatingActionButtonDefaultsMMD.smallContainerSize,
        ),
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        interactionSource = interactionSource,
        content = content,
    )
}

private object FloatingActionButtonDefaultsMMD {
    /**
     * Default border color for a floating action button.
     */
    private val borderColor = Color.Black

    /**
     * Default border size for a floating action button.
     * */
    private val borderSize = 2.dp

    /**
     * Default shape for a floating action button.
     * */
    val shape: Shape
        @Composable get() = RoundedCornerShape(18.dp)

    /**
     * No elevation for a floating action button. E-ink displays do not support elevation.
     * */
    val elevation: FloatingActionButtonElevation
        @Composable get() = FloatingActionButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp
        ) // no elevation

    /**
     * Default container color for a floating action button.
     * */
    val containerColor: Color
        @Composable get() = MaterialTheme.colorScheme.surface

    /**
     * Default border stroke for a floating action button.
     */
    val borderStroke = BorderStroke(
        width = borderSize,
        color = borderColor,
    )
}

private object SmallFloatingActionButtonDefaultsMMD {
    /**
     * Default size for a small floating action button.
     * */
    val smallContainerSize: Dp = 48.dp

    /**
     * Default shape for a floating action button.
     * */
    val smallShape: Shape
        @Composable get() = RoundedCornerShape(14.dp)

    /**
     * Default container color for a floating action button.
     * */
    val smallContainerColor: Color
        @Composable get() = MaterialTheme.colorScheme.surface
}
