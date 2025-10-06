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

enum class DateSkeleton(val pattern: String) {
    /**
     * A date format skeleton used to format the date picker's year selection menu button
     * Example output:
     * - English (US): "March 2021"
     * - German (DE): "März 2021"
     */
    YearMonthSkeleton("yMMMM"),

    /**
     * A date format skeleton used to format a selected date
     * Example output:
     * - English (US): "Mar 27, 2021"
     * - German (DE): "27. März 2021"
     */
    YearAbbrMonthDaySkeleton("yMMMd"),

    /**
     * A date format skeleton used for screen reader accessibility
     * Example output:
     * - English (US): "Saturday, March 27, 2021"
     * - German (DE): "Samstag, 27. März 2021"
     */
    YearMonthWeekdayDaySkeleton("yMMMMEEEEd");
}
