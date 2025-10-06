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

import android.icu.text.DateFormat
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import androidx.compose.material3.CalendarLocale
import androidx.compose.runtime.Immutable

/**
 * Creates a [CalendarModelMMD] to be used by the date picker.
 *
 * @param locale a [CalendarLocale] that will be used by the created model
 */
internal fun createCalendarModel(locale: CalendarLocale): CalendarModelMMD =
    CalendarModelMMDImpl(locale)

/**
 * Formats a UTC timestamp into a string with a given date format skeleton.
 *
 * A skeleton is similar to, and uses the same format characters as described in
 * [Unicode Technical Standard #35](https://unicode.org/reports/tr35/tr35-dates.html#Date_Field_Symbol_Table)
 *
 * One difference is that order is irrelevant. For example, "MMMMd" will return "MMMM d" in the
 * en_US locale, but "d. MMMM" in the de_CH locale.
 *
 * @param utcTimeMillis a UTC timestamp to format (milliseconds from epoch)
 * @param skeleton a date format skeleton
 * @param locale the [CalendarLocale] to use when formatting the given timestamp
 */
fun formatDateWithSkeleton(
    utcTimeMillis: Long,
    skeleton: DateSkeleton,
    locale: CalendarLocale
): String {
    val calendar = Calendar.getInstance().apply { timeInMillis = utcTimeMillis }
    val formatter = DateFormat.getInstanceForSkeleton(skeleton.pattern, locale) as SimpleDateFormat
    return formatter.format(calendar.time)
}

/**
 * A calendar model.
 *
 * @param locale a [CalendarLocale] to be used by this model
 */
internal abstract class CalendarModelMMD(val locale: CalendarLocale) {

    // A map for caching formatter related results for better performance
    internal val formatterCache = mutableMapOf<String, Any>()

    /** A [CalendarDateMMD] representing the current day. */
    abstract val today: CalendarDateMMD

    /**
     * Hold the first day of the week at the current `Locale` as an integer. The integer value
     * follows the ISO-8601 standard and refer to Monday as 1, and Sunday as 7.
     */
    abstract val firstDayOfWeek: Int

    /**
     * Holds a list of weekday names, starting from Monday as the first day in the list.
     *
     * Each item in this list is a [Pair] that holds the full name of the day, and its short
     * abbreviation letter(s).
     *
     * Newer APIs (i.e. API 26+), a [Pair] will hold a full name and the first letter of the day.
     * Older APIs that predate API 26 will hold a full name and the first three letters of the day.
     */
    abstract val weekdayNames: List<Pair<String, String>>

    /**
     * Returns a [DateInputFormat] for the given [CalendarLocale].
     *
     * The input format represents the date with two digits for the day and the month, and four
     * digits for the year.
     *
     * For example, the input format patterns, including delimiters, will hold 10-characters strings
     * in one of the following variations:
     * - yyyy/MM/dd
     * - yyyy-MM-dd
     * - yyyy.MM.dd
     * - dd/MM/yyyy
     * - dd-MM-yyyy
     * - dd.MM.yyyy
     * - MM/dd/yyyy
     */
    abstract fun getDateInputFormat(locale: CalendarLocale = this.locale): DateInputFormat

    /**
     * Returns a [CalendarDateMMD] from a given _UTC_ time in milliseconds.
     *
     * The returned date will hold milliseconds value that represent the start of the day, which may
     * be different than the one provided to this function.
     *
     * @param timeInMillis UTC milliseconds from the epoch
     */
    abstract fun getCanonicalDate(timeInMillis: Long): CalendarDateMMD

    /**
     * Returns a [CalendarMonthMMD] from a given _UTC_ time in milliseconds.
     *
     * @param timeInMillis UTC milliseconds from the epoch for the first day the month
     */
    abstract fun getMonth(timeInMillis: Long): CalendarMonthMMD

    /**
     * Returns a [CalendarMonthMMD] from a given [CalendarDateMMD].
     *
     * Note: This function ignores the [CalendarDateMMD.dayOfMonth] value and just uses the date's year
     * and month to resolve a [CalendarMonthMMD].
     *
     * @param date a [CalendarDateMMD] to resolve into a month
     */
    abstract fun getMonth(date: CalendarDateMMD): CalendarMonthMMD

    /**
     * Returns a [CalendarMonthMMD] from a given [year] and [month].
     *
     * @param year the month's year
     * @param month an integer representing a month (e.g. JANUARY as 1, December as 12)
     */
    abstract fun getMonth(
        year: Int, /* @IntRange(from = 1, to = 12) */
        month: Int
    ): CalendarMonthMMD

    /**
     * Returns a day of week from a given [CalendarDateMMD].
     *
     * @param date a [CalendarDateMMD] to resolve
     */
    abstract fun getDayOfWeek(date: CalendarDateMMD): Int

    /**
     * Returns a [CalendarMonthMMD] that is computed by adding a number of months, given as
     * [addedMonthsCount], to a given month.
     *
     * @param from the [CalendarMonthMMD] to add to
     * @param addedMonthsCount the number of months to add
     */
    abstract fun plusMonths(from: CalendarMonthMMD, addedMonthsCount: Int): CalendarMonthMMD

    /**
     * Returns a [CalendarMonthMMD] that is computed by subtracting a number of months, given as
     * [subtractedMonthsCount], from a given month.
     *
     * @param from the [CalendarMonthMMD] to subtract from
     * @param subtractedMonthsCount the number of months to subtract
     */
    abstract fun minusMonths(from: CalendarMonthMMD, subtractedMonthsCount: Int): CalendarMonthMMD

    /**
     * Formats a UTC timestamp into a string with a given date format pattern.
     *
     * @param utcTimeMillis a UTC timestamp to format (milliseconds from epoch)
     * @param pattern a date format pattern
     * @param locale the [CalendarLocale] to use when formatting the given timestamp
     */
    abstract fun formatWithPattern(
        utcTimeMillis: Long,
        pattern: String,
        locale: CalendarLocale
    ): String

    /**
     * Parses a date string into a [CalendarDateMMD].
     *
     * @param date a date string
     * @param pattern the expected date pattern to be used for parsing the date string
     * @return a [CalendarDateMMD], or a `null` in case the parsing failed
     */
    abstract fun parse(date: String, pattern: String): CalendarDateMMD?
}

/**
 * Represents a calendar date.
 *
 * @param year the date's year
 * @param month the date's month
 * @param dayOfMonth the date's day of month
 * @param utcTimeMillis the date representation in _UTC_ milliseconds from the epoch
 */
internal data class CalendarDateMMD(
    val year: Int,
    val month: Int,
    val dayOfMonth: Int,
    val utcTimeMillis: Long
) : Comparable<CalendarDateMMD> {
    override operator fun compareTo(other: CalendarDateMMD): Int =
        this.utcTimeMillis.compareTo(other.utcTimeMillis)
}

/**
 * Represents a calendar month.
 *
 * @param year the month's year
 * @param month the calendar month as an integer (e.g. JANUARY as 1, December as 12)
 * @param numberOfDays the number of days in the month
 * @param daysFromStartOfWeekToFirstOfMonth the number of days from the start of the week to the
 *   first day of the month
 * @param startUtcTimeMillis the first day of the month in _UTC_ milliseconds from the epoch
 */
internal data class CalendarMonthMMD(
    val year: Int,
    val month: Int,
    val numberOfDays: Int,
    val daysFromStartOfWeekToFirstOfMonth: Int,
    val startUtcTimeMillis: Long
) {

    /** Returns the position of a [CalendarMonthMMD] within given years range. */
    fun indexIn(years: IntRange): Int {
        return (year - years.first) * 12 + month - 1
    }
}

/**
 * Holds the date input format pattern information.
 *
 * This data class hold the delimiter that is used by the current [CalendarLocale] when representing
 * dates in a short format, as well as a date pattern with and without a delimiter.
 */
@Immutable
internal data class DateInputFormat(val patternWithDelimiters: String, val delimiter: Char) {
    val patternWithoutDelimiters: String = patternWithDelimiters.replace(delimiter.toString(), "")
}

/**
 * Receives a given local date format string and returns a string that can be displayed to the user
 * and parsed by the date parser.
 *
 * This function:
 * - Removes all characters that don't match `d`, `M` and `y`, or any of the date format delimiters
 *   `.`, `/` and `-`.
 * - Ensures that the format is for two digits day and month, and four digits year.
 *
 * The output of this cleanup is always a 10 characters string in one of the following variations:
 * - yyyy/MM/dd
 * - yyyy-MM-dd
 * - yyyy.MM.dd
 * - dd/MM/yyyy
 * - dd-MM-yyyy
 * - dd.MM.yyyy
 * - MM/dd/yyyy
 */
internal fun datePatternAsInputFormat(localeFormat: String): DateInputFormat {
    val patternWithDelimiters =
        localeFormat
            .replace(Regex("[^dMy/\\-.]"), "")
            .replace(Regex("d{1,2}"), "dd")
            .replace(Regex("M{1,2}"), "MM")
            .replace(Regex("y{1,4}"), "yyyy")
            .replace("My", "M/y") // Edge case for the Kako locale
            .removeSuffix(".") // Removes a dot suffix that appears in some formats

    val delimiterRegex = Regex("[/\\-.]")
    val delimiterMatchResult = delimiterRegex.find(patternWithDelimiters)
    val delimiterIndex = delimiterMatchResult!!.groups[0]!!.range.first
    val delimiter = patternWithDelimiters.substring(delimiterIndex, delimiterIndex + 1)
    return DateInputFormat(patternWithDelimiters = patternWithDelimiters, delimiter = delimiter[0])
}

internal const val DaysInWeek: Int = 7
internal const val MillisecondsIn24Hours = 86400000L
