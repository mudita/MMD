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

package com.mudita.mmd

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

val black = Color(0xFF000000)
val white = Color(0xFFFFFFFF)

val eInkColorScheme = ColorScheme(
    primary = black,
    onPrimary = white,
    primaryContainer = black,
    onPrimaryContainer = white,
    inversePrimary = white,
    secondary = white,
    onSecondary = black,
    secondaryContainer = black,
    onSecondaryContainer = white,
    tertiary = white,
    onTertiary = black,
    tertiaryContainer = black,
    onTertiaryContainer = white,
    background = white,
    onBackground = black,
    surface = white,
    onSurface = black,
    surfaceVariant = white,
    onSurfaceVariant = black,
    surfaceTint = white,
    inverseSurface = white,
    inverseOnSurface = black,
    error = black,
    onError = white,
    errorContainer = white,
    onErrorContainer = black,
    outline = black,
    outlineVariant = black,
    scrim = black,
    surfaceBright = Color.Unspecified,
    surfaceDim = Color.Unspecified,
    surfaceContainer = Color.Unspecified,
    surfaceContainerHigh = Color.Unspecified,
    surfaceContainerHighest = Color.Unspecified,
    surfaceContainerLow = white,
    surfaceContainerLowest = Color.Unspecified,
)
