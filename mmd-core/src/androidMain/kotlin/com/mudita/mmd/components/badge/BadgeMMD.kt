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

package com.mudita.mmd.components.badge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirst
import com.mudita.mmd.internal.ProvideContentColorTextStyleMMD
import kotlin.math.roundToInt

/**
 * MMD Badge Box.
 *
 * A badge represents dynamic information such as a number of pending requests in a navigation bar.
 *
 * A common use case is to display a badge with navigation bar items. For more information, see
 * [Navigation Bar](https://m3.material.io/components/navigation-bar/overview)
 *
 * A simple icon with badge example looks like:
 *
 *```kotlin
 * BadgedBoxMMD(
 *     badge = {
 *         BadgeMMD()
 *     }
 * ) {
 *     Icon(
 *         imageVector = Icons.Filled.Email,
 *         modifier = Modifier.size(48.dp),
 *         contentDescription = "Email"
 *     )
 * }
 *```
 *
 * @param badge the badge to be displayed - typically a [BadgeMMD]
 * @param modifier the [Modifier] to be applied to this BadgedBox
 * @param content the anchor to which this badge will be positioned
 */
@Composable
fun BadgedBoxMMD(
    badge: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    var layoutAbsoluteLeft by remember { mutableFloatStateOf(0f) }
    var layoutAbsoluteTop by remember { mutableFloatStateOf(0f) }
    // We use Float.POSITIVE_INFINITY and Float.NEGATIVE_INFINITY to represent the case
    // when there isn't a great grand parent layout.
    var greatGrandParentAbsoluteRight by remember { mutableFloatStateOf(Float.POSITIVE_INFINITY) }
    var greatGrandParentAbsoluteTop by remember { mutableFloatStateOf(Float.NEGATIVE_INFINITY) }

    Layout(
        {
            Box(
                modifier = Modifier.layoutId("anchor"),
                contentAlignment = Alignment.Center,
                content = content,
            )
            Box(modifier = Modifier.layoutId("badge"), content = badge)
        },
        modifier = modifier.onGloballyPositioned { coordinates ->
            val windowBoundsRect = coordinates.boundsInWindow()
            layoutAbsoluteLeft = windowBoundsRect.left
            layoutAbsoluteTop = windowBoundsRect.top
            val layoutGreatGrandParent =
                coordinates.parentLayoutCoordinates?.parentLayoutCoordinates?.parentCoordinates
            layoutGreatGrandParent?.let {
                val greatGrandParentWindowBoundsRect = it.boundsInWindow()
                greatGrandParentAbsoluteRight = greatGrandParentWindowBoundsRect.right
                greatGrandParentAbsoluteTop = greatGrandParentWindowBoundsRect.top
            }
        },
    ) { measurables, constraints ->
        val badgePlaceable =
            measurables
                .fastFirst { it.layoutId == "badge" }
                .measure(
                    // Measure with loose constraints for height as we don't want the text to take
                    // up more
                    // space than it needs.
                    constraints.copy(minHeight = 0),
                )

        val anchorPlaceable = measurables.fastFirst { it.layoutId == "anchor" }.measure(constraints)

        val firstBaseline = anchorPlaceable[FirstBaseline]
        val lastBaseline = anchorPlaceable[LastBaseline]
        val totalWidth = anchorPlaceable.width
        val totalHeight = anchorPlaceable.height

        layout(
            totalWidth,
            totalHeight,
            // Provide custom baselines based only on the anchor content to avoid default baseline
            // calculations from including by any badge content.
            mapOf(FirstBaseline to firstBaseline, LastBaseline to lastBaseline),
        ) {
            // Use the width of the badge to infer whether it has any content (based on radius used
            // in [Badge]) and determine its horizontal offset.
            val hasContent = badgePlaceable.width > (Size.roundToPx())
            val badgeHorizontalOffset =
                if (hasContent) BadgeWithContentHorizontalOffset else BadgeOffset
            val badgeVerticalOffset =
                if (hasContent) BadgeWithContentVerticalOffset else BadgeOffset

            anchorPlaceable.placeRelative(0, 0)

            // Desired Badge placement
            var badgeX = anchorPlaceable.width - badgeHorizontalOffset.roundToPx()
            var badgeY = -badgePlaceable.height + badgeVerticalOffset.roundToPx()
            // Badge correction logic if the badge will be cut off by the grandparent bounds.
            val badgeAbsoluteTop = layoutAbsoluteTop + badgeY
            val badgeAbsoluteRight = layoutAbsoluteLeft + badgeX + badgePlaceable.width.toFloat()
            val badgeGreatGrandParentHorizontalDiff =
                greatGrandParentAbsoluteRight - badgeAbsoluteRight
            val badgeGreatGrandParentVerticalDiff = badgeAbsoluteTop - greatGrandParentAbsoluteTop
            // Adjust badgeX and badgeY if the desired placement would cause it to clip.
            if (badgeGreatGrandParentHorizontalDiff < 0) {
                badgeX += badgeGreatGrandParentHorizontalDiff.roundToInt()
            }
            if (badgeGreatGrandParentVerticalDiff < 0) {
                badgeY -= badgeGreatGrandParentVerticalDiff.roundToInt()
            }

            badgePlaceable.placeRelative(badgeX, badgeY)
        }
    }
}

/**
 * A badge represents dynamic information such as a number of pending requests in a navigation bar.
 *
 * See [BadgedBoxMMD] for a top level layout that will properly place the badge relative to content
 * such as text or an icon.
 *
 * @param modifier the [Modifier] to be applied to this badge
 * @param containerColor the color used for the background of this badge
 * @param contentColor the preferred color for content inside this badge. Defaults to either the
 *   matching content color for [containerColor], or to the current [LocalContentColor] if
 *   [containerColor] is not a color from the theme.
 * @param content optional content to be rendered inside this badge
 */
@Composable
fun BadgeMMD(
    modifier: Modifier = Modifier,
    containerColor: Color = BadgeDefaultsMMD.containerColor,
    contentColor: Color = contentColorFor(containerColor),
    content: @Composable (RowScope.() -> Unit)? = null,
) {
    val size = if (content != null) LargeSize else Size
    val shape =
        if (content != null) {
            LargeShape
        } else {
            Shape
        }

    Row(
        modifier = modifier
            .defaultMinSize(minWidth = size, minHeight = size)
            .background(color = containerColor, shape = shape)
            .then(
                if (content != null)
                    Modifier.padding(horizontal = BadgeWithContentHorizontalPadding)
                else Modifier,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (content != null) {
            // Not using Surface composable because it blocks touch propagation behind it.
            val style = LargeLabelTextFont
            ProvideContentColorTextStyleMMD(
                contentColor = contentColor,
                textStyle = style,
                content = { content() },
            )
        }
    }
}

/** Default values used for [BadgeMMD] implementations. */
object BadgeDefaultsMMD {
    /** Default container color for a badge. */
    val containerColor: Color
        @Composable get() = MaterialTheme.colorScheme.primaryContainer
}

private val LargeLabelTextFont
    @Composable get() = MaterialTheme.typography.labelSmall
private val LargeShape = CircleShape
private val LargeSize = 28.0.dp
private val Shape = CircleShape
private val Size = 8.0.dp
private val BadgeWithContentHorizontalPadding = 4.dp
private val BadgeWithContentHorizontalOffset = 12.dp
private val BadgeWithContentVerticalOffset = 14.dp
private val BadgeOffset = 6.dp
