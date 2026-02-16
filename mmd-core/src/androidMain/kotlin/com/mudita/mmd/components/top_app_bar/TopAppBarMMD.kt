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

package com.mudita.mmd.components.top_app_bar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Top App Barr MMD
 *
 * Top app bars display information and actions at the top of a screen.
 *
 * This TopAppBarMMD has slots for a title, navigation icon, and actions.
 *
 * A simple top app bar looks like:
 *
 *```kotlin
 * @OptIn(ExperimentalMaterial3Api::class)
 * @Preview
 * @Composable
 * fun SimpleTopAppBar() {
 *     Scaffold(
 *         topBar = {
 *             TopAppBarMMD(
 *                 title = {
 *                     Text("Simple TopAppBar", maxLines = 1, overflow = TextOverflow.Ellipsis)
 *                 },
 *                 navigationIcon = {
 *                     IconButton(onClick = { /* doSomething() */ }) {
 *                         Icon(
 *                             imageVector = Icons.Filled.Menu,
 *                             contentDescription = "Localized description"
 *                         )
 *                     }
 *                 },
 *                 actions = {
 *                     IconButton(onClick = { /* doSomething() */ }) {
 *                         Icon(
 *                             imageVector = Icons.Filled.Favorite,
 *                             contentDescription = "Localized description"
 *                         )
 *                     }
 *                 }
 *             )
 *         },
 *         content = { innerPadding ->
 *             LazyColumn(
 *                 contentPadding = innerPadding,
 *                 verticalArrangement = Arrangement.spacedBy(8.dp)
 *             ) {
 *                 val list = (0..75).map { it.toString() }
 *                 items(count = list.size) {
 *                     Text(
 *                         text = list[it],
 *                         style = MaterialTheme.typography.bodyLarge,
 *                         modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
 *                     )
 *                 }
 *             }
 *         }
 *     )
 * }
 * ```
 *
 * @param title the title to be displayed in the top app bar
 * @param modifier the [Modifier] to be applied to this top app bar
 * @param navigationIcon the navigation icon displayed at the start of the top app bar. This should
 *   typically be an [IconButton] or [IconToggleButton].
 * @param actions the actions displayed at the end of the top app bar. This should typically be
 *   [IconButton]s. The default layout here is a [Row], so icons inside will be placed horizontally.
 * @param expandedHeight this app bar's height. When a specified [scrollBehavior] causes the app bar
 *   to collapse or expand, this value will represent the maximum height that the bar will be
 *   allowed to expand. This value must be specified and finite, otherwise it will be ignored and
 *   replaced with [TopAppBarDefaults.TopAppBarExpandedHeight].
 * @param windowInsets a window insets that app bar will respect.
 * @param colors [TopAppBarColors] that will be used to resolve the colors used for this top app bar
 *   in different states. See [TopAppBarDefaultsMMD.topAppBarColors].
 * @param scrollBehavior a [TopAppBarScrollBehavior] which holds various offset values that will be
 *   applied by this top app bar to set up its height and colors. A scroll behavior is designed to
 *   work in conjunction with a scrolled content to change the top app bar appearance as the content
 *   scrolls. See [TopAppBarScrollBehavior.nestedScrollConnection].
 * @param dividerColor the color of the divider line that will be drawn at the bottom of the app bar
 * @param showDivider whether to show the divider line at the bottom of the app bar
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarMMD(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    expandedHeight: Dp = TopAppBarDefaultsMMD.TopAppBarExpandedHeight,
    windowInsets: WindowInsets = TopAppBarDefaultsMMD.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaultsMMD.topAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    dividerColor: Color = TopAppBarDefaultsMMD.dividerColor,
    showDivider: Boolean = true,
) {

    Column {
        TopAppBar(
            title = title,
            modifier = modifier,
            navigationIcon = navigationIcon,
            actions = actions,
            expandedHeight = expandedHeight,
            windowInsets = windowInsets,
            colors = colors,
            scrollBehavior = scrollBehavior,
        )
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .width(TopAppBarDefaultsMMD.dividerLineHeight),
                color = dividerColor,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
object TopAppBarDefaultsMMD {
    /** Default divider height used by a [TopAppBarMMD] */
    val dividerLineHeight = 3.dp

    /**
     * Creates a [TopAppBarColors] for [TopAppBarMMD]. The default implementation animates
     * between the provided colors according to the Material Design specification.
     */
    @Composable
    fun topAppBarColors(
        containerColor: Color = MaterialTheme.colorScheme.surface,
        scrolledContainerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
        navigationIconContentColor: Color = MaterialTheme.colorScheme.onSurface,
        titleContentColor: Color = MaterialTheme.colorScheme.onSurface
    ) =
        TopAppBarColors(
            containerColor = containerColor,
            scrolledContainerColor = scrolledContainerColor,
            navigationIconContentColor = navigationIconContentColor,
            titleContentColor = navigationIconContentColor,
            actionIconContentColor = titleContentColor,
        )

    /** Default divider colors used by a [TopAppBarMMD] */
    val dividerColor: Color
        @Composable get() = MaterialTheme.colorScheme.onSurface

    /** Default insets to be used and consumed by the top app bars */
    val windowInsets: WindowInsets
        @Composable get() = TopAppBarDefaults.windowInsets

    /** The default expanded height of a [TopAppBarMMD]. */
    val TopAppBarExpandedHeight: Dp = 64.dp
}
