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

package com.mudita.mmd.components.progress_indicator

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/**
 *
 * Linear Progress indicators display the duration of a process.
 *
 *```kotlin
 * @Preview
 * @Composable
 * fun LinearProgressIndicatorSample() {
 *     var progress by remember { mutableFloatStateOf(0.1f) }
 *
 *     Column(horizontalAlignment = Alignment.CenterHorizontally) {
 *         LinearProgressIndicator(
 *             progress = { progress },
 *         )
 *         Spacer(Modifier.requiredHeight(30.dp))
 *         Text("Set progress:")
 *         Slider(
 *             modifier = Modifier.width(300.dp),
 *             value = progress,
 *             valueRange = 0f..1f,
 *             onValueChange = { progress = it },
 *         )
 *     }
 * }
 * ```
 *
 * @param progress the progress of this progress indicator, where 0.0 represents no progress and 1.0
 *   represents full progress. Values outside of this range are coerced into the range.
 * @param modifier the [Modifier] to be applied to this progress indicator
 * @param color color of this progress indicator
 * @param borderColor color of the border behind the indicator
 * @param borderWidth width of the track behind the indicator
 * @param strokeCap stroke cap to use for the ends of this progress indicator
 */
@Composable
fun LinearProgressIndicatorMMD(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    color: Color = LinearProgressIndicatorDefaultsMMD.linearColor,
    borderColor: Color = LinearProgressIndicatorDefaultsMMD.linearBorderColor,
    borderWidth: Dp = LinearProgressIndicatorDefaultsMMD.linearBorderWidth,
    strokeCap: StrokeCap = LinearProgressIndicatorDefaultsMMD.LinearStrokeCap,
) {
    val coercedProgress = { progress().coerceIn(0f, 1f) }
    Canvas(
        modifier
            .size(width = LinearIndicatorWidth, height = LinearIndicatorHeight),
    ) {
        val strokeWidth = size.height
        val currentProgress = coercedProgress()

        when {
            strokeCap == StrokeCap.Butt || strokeCap == StrokeCap.Square || size.height > size.width -> drawRect(
                color = borderColor,
                topLeft = Offset(0f, 0f),
                size = Size(size.width, size.height),
                style = Stroke(width = borderWidth.toPx()),
            )

            else -> drawRoundRect(
                color = borderColor, size = Size(size.width, size.height),
                cornerRadius = CornerRadius(size.height / 2, size.height / 2),
                style = Stroke(width = borderWidth.toPx()),
            )
        }

        drawLinearIndicator(0f, currentProgress, color, strokeWidth, strokeCap)
    }
}

private fun DrawScope.drawLinearIndicator(
    startFraction: Float,
    endFraction: Float,
    color: Color,
    strokeWidth: Float,
    strokeCap: StrokeCap,
) {
    val width = size.width
    val height = size.height
    val yOffset = height / 2

    val isLtr = layoutDirection == LayoutDirection.Ltr
    val barStart = (if (isLtr) startFraction else 1f - endFraction) * width
    val barEnd = (if (isLtr) endFraction else 1f - startFraction) * width

    when {
        strokeCap == StrokeCap.Butt || strokeCap == StrokeCap.Square || height > width -> {
            drawLine(color, Offset(barStart, yOffset), Offset(barEnd, yOffset), strokeWidth)
        }

        else -> {
            val strokeCapOffset = strokeWidth / 2
            val coerceRange = strokeCapOffset..(width - strokeCapOffset)
            val adjustedBarStart = barStart.coerceIn(coerceRange)
            val adjustedBarEnd = barEnd.coerceIn(coerceRange)

            if (abs(endFraction - startFraction) > 0) {
                drawLine(
                    color,
                    Offset(adjustedBarStart, yOffset),
                    Offset(adjustedBarEnd, yOffset),
                    strokeWidth,
                    strokeCap,
                )
            }
        }
    }
}

object LinearProgressIndicatorDefaultsMMD {
    /** Default color for a linear progress indicator. */
    val linearColor: Color
        @Composable get() = MaterialTheme.colorScheme.primary

    /** Default border color for a linear progress indicator. */
    val linearBorderColor: Color
        @Composable get() = MaterialTheme.colorScheme.secondaryContainer

    /** Default stroke cap for a linear progress indicator. */
    val LinearStrokeCap: StrokeCap = StrokeCap.Round

    /** Default stroke width for a linear progress indicator. */
    val linearBorderWidth: Dp = 1.dp
}

internal val LinearIndicatorWidth = 240.dp
internal val LinearIndicatorHeight = 8.dp
