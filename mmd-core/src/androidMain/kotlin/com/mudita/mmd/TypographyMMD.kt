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

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import androidx.compose.ui.text.font.FontWeight.Companion.Black
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Light
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.text.font.FontWeight.Companion.Thin
import androidx.compose.ui.unit.sp

private val lato =
    FontFamily(
        Font(
            resId = R.font.lato_regular,
        ),
        Font(
            resId = R.font.lato_italic,
            style = Italic,
        ),
        Font(
            resId = R.font.lato_thin,
            weight = Thin,
        ),
        Font(
            resId = R.font.lato_thin_italic,
            weight = Thin,
            style = Italic,
        ),
        Font(
            resId = R.font.lato_bold,
            weight = Bold,
        ),
        Font(
            resId = R.font.lato_bold_italic,
            weight = Bold,
            style = Italic,
        ),
        Font(
            resId = R.font.lato_light,
            weight = Light,
        ),
        Font(
            resId = R.font.lato_light_italic,
            weight = Light,
            style = Italic,
        ),
        Font(
            resId = R.font.lato_black,
            weight = Black,
        ),
        Font(
            resId = R.font.lato_black_italic,
            weight = Black,
            style = Italic,
        ),
    )

val eInkTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = lato,
        fontWeight = Medium,
        fontSize = 28.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = lato,
        fontWeight = Medium,
        fontSize = 24.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = lato,
        fontWeight = Medium,
        fontSize = 20.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = lato,
        fontWeight = Medium,
        fontSize = 16.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = lato,
        fontWeight = Medium,
        fontSize = 20.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = lato,
        fontWeight = Medium,
        fontSize = 18.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = lato,
        fontWeight = Medium,
        fontSize = 15.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = lato,
        fontWeight = Medium,
        fontSize = 18.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = lato,
        fontWeight = Medium,
        fontSize = 15.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = lato,
        fontWeight = Medium,
        fontSize = 14.sp,
    ),
)
