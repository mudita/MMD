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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mudita.mmd.R
import com.mudita.mmd.components.progress_indicator.ProgressIndicatorDefaultsMMD.ANIMATION_STEP_DEGREES
import com.mudita.mmd.components.progress_indicator.ProgressIndicatorDefaultsMMD.STROKE_DEGREES
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/**
 *
 * Progress indicators express an unspecified wait time or display the duration of a process.
 *
 *```kotlin
 * @Preview
 * @Composable
 * fun IndeterminateCircularProgressIndicatorSample() {
 *     Column(horizontalAlignment = Alignment.CenterHorizontally) { CircularProgressIndicatorMMD() }
 * }
 * ```
 *
 * @param modifier the [Modifier] to be applied to this progress indicator
 * @param color color of this progress indicator
 * @param size size of this progress indicator
 * @param trackColor color of the track behind the indicator, visible when the progress has not
 *   reached the area of the overall indicator yet
 * @param frameDelay duration of each step of the animation
 */
@Composable
fun CircularProgressIndicatorMMD(
    modifier: Modifier = Modifier,
    color: Color = ProgressIndicatorDefaultsMMD.circularColor,
    size: Dp = ProgressIndicatorDefaultsMMD.circularSize,
    trackColor: Color = ProgressIndicatorDefaultsMMD.circularIndeterminateTrackColor,
    frameDelay: Long = ProgressIndicatorDefaultsMMD.frameDelay,
) {
    var rotationDegree by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        while (currentCoroutineContext().isActive) {
            rotationDegree =
                (rotationDegree + ANIMATION_STEP_DEGREES) % STROKE_DEGREES
            delay(frameDelay)
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_loader_background),
            contentDescription = null,
            modifier = Modifier.size(size),
            tint = color,
        )
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_loader_foreground),
            contentDescription = null,
            tint = trackColor,
            modifier = Modifier
                .size(size)
                .graphicsLayer { rotationZ = rotationDegree },
        )
    }
}

object ProgressIndicatorDefaultsMMD {
    const val ANIMATION_STEP_DEGREES = 45f
    const val STROKE_DEGREES = 360

    val frameDelay: Long
        get() = 250L

    val circularSize: Dp = 32.dp

    val circularColor: Color
        @Composable get() = MaterialTheme.colorScheme.primary

    val circularIndeterminateTrackColor: Color
        @Composable get() = MaterialTheme.colorScheme.primary
}
