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

package com.mudita.mmd.internal.date_picker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import java.text.NumberFormat
import java.util.Locale
import java.util.WeakHashMap

typealias CalendarLocale = Locale

/** Returns a string representation of an integer for the current Locale. */
internal fun Int.toLocalString(
    minDigits: Int = 1,
    maxDigits: Int = 40,
    isGroupingUsed: Boolean = false
): String {
    return getCachedDateTimeFormatter(
        minDigits = minDigits,
        maxDigits = maxDigits,
        isGroupingUsed = isGroupingUsed
    )
        .format(this)
}

private val cachedFormatters = WeakHashMap<String, NumberFormat>()

private fun getCachedDateTimeFormatter(
    minDigits: Int,
    maxDigits: Int,
    isGroupingUsed: Boolean
): NumberFormat {
    // Note: Using Locale.getDefault() as a best effort to obtain a unique key and keeping this
    // function non-composable.
    val key = "$minDigits.$maxDigits.$isGroupingUsed.${Locale.getDefault().toLanguageTag()}"
    return cachedFormatters.getOrPut(key) {
        NumberFormat.getIntegerInstance().apply {
            this.isGroupingUsed = isGroupingUsed
            this.minimumIntegerDigits = minDigits
            this.maximumIntegerDigits = maxDigits
        }
    }
}

/** Returns the default [CalendarLocale]. */
@Composable
@ReadOnlyComposable
internal fun defaultLocale(): CalendarLocale {
    return Locale24.defaultLocale()
}

private class Locale24 {
    companion object {
        @Composable
        @ReadOnlyComposable
        fun defaultLocale(): CalendarLocale {
            return LocalConfiguration.current.locales[0]
        }
    }
}
