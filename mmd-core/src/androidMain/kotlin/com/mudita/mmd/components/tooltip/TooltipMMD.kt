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

package com.mudita.mmd.components.tooltip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupPositionProvider
import com.mudita.mmd.components.tooltip.CaretDirectionMMD.Auto
import com.mudita.mmd.components.tooltip.CaretDirectionMMD.Down
import com.mudita.mmd.components.tooltip.CaretDirectionMMD.Up

/**
 * A custom tooltip component that displays a message when the user hovers over or focuses on a specific UI element.
 * This tooltip supports a caret (arrow) pointing to the target element and can be customized with various properties.
 *
 * ```kotlin
 * @Composable
 * fun TooltipSample() {
 *  val state = remember { TooltipState() }
 *  val scope = rememberCoroutineScope()
 *
 * 	Column(
 *         horizontalAlignment = Alignment.CenterHorizontally,
 *         verticalArrangement = Arrangement.Top,
 *         modifier = Modifier
 *             .padding(innerPadding)
 *             .fillMaxSize()
 *     ) {
 *         TooltipBoxMMD(
 *             tooltip = {
 *                 TooltipMMD(
 *                     caretDirection = CaretDirection.Auto
 *                 ) {
 *                     Text(
 *                         "Add to favorites",
 *                         fontWeight = FontWeight.Bold,
 *                         fontSize = MaterialTheme.typography.labelLarge.fontSize
 *                     )
 *                 }
 *             },
 *             state = state
 *         ) {
 *             Icon(
 *                 imageVector = Icons.Filled.AddCircle,
 *                 contentDescription = "Localized Description",
 *             )
 *         }
 *         Spacer(Modifier.requiredHeight(30.dp))
 *         ButtonMMD(onClick = { scope.launch { state.show() } }) {
 *             Text("Display plain tooltip")
 *         }
 *     }
 * }
 * ```
 *
 * @param modifier Modifier to be applied to the tooltip.
 * @param caretSize The size of the caret (arrow) pointing to the target element.
 * @param caretDirection The direction in which the caret should point. Defaults to [Auto].
 * @param containerColor The background color of the tooltip.
 * @param contentColor The color of the tooltip content.
 * @param borderColor The color of the tooltip border.
 * @param borderWidth The width of the tooltip border.
 * @param cornerRadius The corner radius of the tooltip.
 * @param contentPadding The padding around the tooltip content.
 * @param content The content to be displayed inside the tooltip.
 */
@Composable
fun TooltipMMD(
    modifier: Modifier = Modifier,
    caretSize: DpSize = TooltipDefaultsMMD.CaretSize,
    caretDirection: CaretDirectionMMD = Auto,
    containerColor: Color = TooltipDefaultsMMD.ContainerColor,
    contentColor: Color = TooltipDefaultsMMD.ContentColor,
    borderColor: Color = TooltipDefaultsMMD.BorderColor,
    borderWidth: Dp = TooltipDefaultsMMD.BorderWidth,
    cornerRadius: Dp = TooltipDefaultsMMD.CornerRadius,
    contentPadding: PaddingValues = TooltipDefaultsMMD.ContentPadding,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val borderWidthPx = with(density) { borderWidth.toPx() }
    val caretWidthPx = with(density) { caretSize.width.toPx() }
    val caretHeightPx = with(density) { caretSize.height.toPx() }
    val radiusPx = with(density) { cornerRadius.toPx() }

    val autoCaretDirection = TooltipDefaultsMMD.LocalCaretDirection.current
    val effectiveCaretDirection = when (caretDirection) {
        Auto -> autoCaretDirection
        else -> caretDirection
    }

    Box(
        modifier = modifier
            .sizeIn(minWidth = MinWidth, maxWidth = MaxWidth, minHeight = MinHeight)
            .drawBehind {
                val fullW = size.width
                val hasCaret = caretSize != DpSize.Unspecified

                val rectTop =
                    if (hasCaret && effectiveCaretDirection == Up) caretHeightPx else 0f
                val rectBottom =
                    if (hasCaret && effectiveCaretDirection == Down) size.height - caretHeightPx else size.height

                val roundRect = RoundRect(
                    left = 0f,
                    top = rectTop,
                    right = fullW,
                    bottom = rectBottom,
                    cornerRadius = CornerRadius(radiusPx, radiusPx),
                )

                val roundRectPath = Path().apply {
                    addRoundRect(roundRect)
                }
                val caretPath = Path().apply {
                    val cx = fullW / 2f
                    if (effectiveCaretDirection == Down) {
                        moveTo(cx - caretWidthPx / 2f, rectBottom)
                        lineTo(cx, rectBottom + caretHeightPx)
                        lineTo(cx + caretWidthPx / 2f, rectBottom)
                        close()
                    } else {
                        moveTo(cx - caretWidthPx / 2f, rectTop)
                        lineTo(cx, rectTop - caretHeightPx)
                        lineTo(cx + caretWidthPx / 2f, rectTop)
                        close()
                    }
                }
                val combined = Path.combine(PathOperation.Union, roundRectPath, caretPath)

                drawPath(path = combined, color = containerColor)
                drawPath(
                    path = combined,
                    color = borderColor,
                    style = Stroke(width = borderWidthPx),
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        val textStyle = MaterialTheme.typography.labelLarge

        CompositionLocalProvider(
            LocalContentColor provides contentColor,
            LocalTextStyle provides textStyle,
            content = {
                val topPadding = if (effectiveCaretDirection == Up)
                    contentPadding.calculateTopPadding() + caretSize.height
                else contentPadding.calculateTopPadding()

                val bottomPadding = if (effectiveCaretDirection == Down)
                    contentPadding.calculateBottomPadding() + caretSize.height
                else contentPadding.calculateBottomPadding()

                Box(
                    modifier = Modifier.padding(
                        start = contentPadding.calculateStartPadding(LayoutDirection.Ltr),
                        end = contentPadding.calculateEndPadding(LayoutDirection.Ltr),
                        top = topPadding,
                        bottom = bottomPadding,
                    ),
                ) {
                    content()
                }
            },
        )
    }
}

enum class CaretDirectionMMD { Up, Down, Auto }

/**
 * TooltipDefaultsMMD provides default values and configurations for tooltips in a Composable UI.
 * These defaults include spacing, caret size, padding, corner radius, border width, and colors.
 * The object also provides a mechanism to calculate the position of the tooltip relative to its anchor,
 * handling caret direction changes automatically.
 */
object TooltipDefaultsMMD {
    /**
     * Default spacing between the tooltip and its anchor in dp.
     */
    private val SpacingBetweenTooltipAndAnchor = 4.dp

    /**
     * CompositionLocal to provide the current caret direction (e.g., Up, Down, Auto).
     * Defaults to Down.
     */
    internal val LocalCaretDirection = compositionLocalOf { Down }

    /**
     * Size of the tooltip's caret.
     */
    val CaretSize = DpSize(width = 16.dp, height = 8.dp)

    /**
     * Padding inside the tooltip content.
     */
    val ContentPadding = PaddingValues(8.dp)

    /**
     * Corner radius for the tooltip's container.
     */
    val CornerRadius = 8.dp

    /**
     * Width of the tooltip's border.
     */
    val BorderWidth = 3.dp

    /**
     * Color of the tooltip container.
     */
    val ContainerColor
        @Composable
        get() = MaterialTheme.colorScheme.background

    /**
     * Color of the tooltip content.
     */
    val ContentColor
        @Composable
        get() = MaterialTheme.colorScheme.onBackground

    /**
     * Color of the tooltip border.
     */
    val BorderColor
        @Composable
        get() = MaterialTheme.colorScheme.primary

    /**
     * Provides a [PopupPositionProvider] for the tooltip.
     * Calculates the position of the tooltip relative to its anchor.
     * Automatically handles caret direction changes when CaretDirection.Auto is used.
     *
     * @param spacingBetweenTooltipAndAnchor Space in dp between the tooltip and its anchor.
     * @param onCaretDirectionChange Callback invoked when caret direction changes.
     * @return [PopupPositionProvider] instance for positioning the tooltip.
     */
    @Composable
    fun rememberPlainTooltipPositionProvider(
        spacingBetweenTooltipAndAnchor: Dp = SpacingBetweenTooltipAndAnchor,
        onCaretDirectionChange: (CaretDirectionMMD) -> Unit,
    ): PopupPositionProvider {
        val tooltipAnchorSpacing =
            with(LocalDensity.current) { spacingBetweenTooltipAndAnchor.roundToPx() }

        return remember(tooltipAnchorSpacing, onCaretDirectionChange) {
            PlainTooltipPositionProvider(tooltipAnchorSpacing, onCaretDirectionChange)
        }
    }
}

/**
 * A [PopupPositionProvider] implementation for positioning a tooltip relative to its anchor.
 * This class calculates the optimal position for the tooltip, ensuring it does not overlap with the anchor.
 * It also handles caret direction changes when the caret direction is set to [CaretDirectionMMD.Auto].
 *
 * @param tooltipAnchorSpacing The space in pixels between the tooltip and its anchor.
 * @param onCaretDirectionChange A callback invoked when the caret direction changes.
 * */
private class PlainTooltipPositionProvider(
    private val tooltipAnchorSpacing: Int,
    private val onCaretDirectionChange: (CaretDirectionMMD) -> Unit,
) : PopupPositionProvider {

    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset {
        val x = anchorBounds.left + (anchorBounds.width - popupContentSize.width) / 2

        // Tooltip prefers to be above the anchor,
        // but if this causes the tooltip to overlap with the anchor
        // then we place it below the anchor
        var y = anchorBounds.top - popupContentSize.height - tooltipAnchorSpacing
        val calculatedCaretDirection = if (y < 0) {
            y = anchorBounds.bottom + tooltipAnchorSpacing
            Up
        } else {
            Down
        }

        onCaretDirectionChange(calculatedCaretDirection)

        return IntOffset(x, y)
    }
}

internal val MinWidth = 48.dp
internal val MaxWidth = 280.dp
internal val MinHeight = 40.dp
