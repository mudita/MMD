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

package com.mudita.mmd.components.snackbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.fastFirstOrNull
import com.mudita.mmd.components.buttons.OutlinedButtonMMD
import kotlin.math.max
import kotlin.math.min

/**
 *
 * SnackbarsMMD provide brief messages about app processes at the bottom of the screen.
 *
 * SnackbarsMMD inform users of a process that an app has performed or will perform. They appear
 * temporarily, towards the bottom of the screen. They shouldn’t interrupt the user experience, and
 * they don’t require user input to disappear.
 *
 * A SnackbarMMD can contain a single action. "Dismiss" or "cancel" actions are optional.
 *
 * SnackbarsMMD with an action should not timeout or self-dismiss until the user performs another
 * action. Here, moving the keyboard focus indicator to navigate through interactive elements in a
 * page is not considered an action.
 *
 * This component provides only the visuals of the SnackbarMMD. If you need to show a SnackbarMMD with
 * defaults on the screen, use [SnackbarHostStateMMD.showSnackbar]:
 *
 * ```kotlin
 * @Preview
 * @Composable
 * fun ScaffoldWithSimpleSnackbar() {
 *     val snackbarHostState = remember { SnackbarHostStateMMD() }
 *     val scope = rememberCoroutineScope()
 *     Scaffold(
 *         snackbarHost = { SnackbarHostMMD(snackbarHostState) },
 *         floatingActionButton = {
 *             var clickCount by remember { mutableStateOf(0) }
 *             FloatingActionButtonMMD(
 *                 onClick = {
 *                     // show snackbar as a suspend function
 *                     scope.launch { snackbarHostState.showSnackbar("Snackbar # ${++clickCount}") }
 *                 }
 *             ) {
 *                 Text("Show snackbar")
 *             }
 *         },
 *         content = { innerPadding ->
 *             Text(
 *                 text = "Body content",
 *                 modifier = Modifier.padding(innerPadding).fillMaxSize().wrapContentSize()
 *             )
 *         }
 *     )
 * }
 * ```
 *
 * If you want to customize appearance of the SnackbarMMD, you can pass your own version as a child of
 * the [SnackbarHostMMD] to the [Scaffold]:
 *
 * @param modifier the [Modifier] to be applied to this snackbar
 * @param action action / button component to add as an action to the snackbar. Consider using
 *   [ColorScheme.inversePrimary] as the color for the action, if you do not have a predefined color
 *   you wish to use instead.
 * @param dismissAction action / button component to add as an additional close affordance action
 *   when a snackbar is non self-dismissive. Consider using [ColorScheme.inverseOnSurface] as the
 *   color for the action, if you do not have a predefined color you wish to use instead.
 * @param actionOnNewLine whether or not action should be put on a separate line. Recommended for
 *   action with long action text.
 * @param shape defines the shape of this snackbar's container
 * @param containerColor the color used for the background of this snackbar. Use [Color.Transparent]
 *   to have no color.
 * @param contentColor the preferred color for content inside this snackbar
 * @param actionContentColor the preferred content color for the optional [action] inside this
 *   snackbar
 * @param dismissActionContentColor the preferred content color for the optional [dismissAction]
 *   inside this snackbar
 * @param dividerColor the color of the divider line
 * @param showDivider whether or not to show the divider line
 * @param content content to show information about a process that an app has performed or will
 *   perform
 */
@Composable
fun SnackbarMMD(
    modifier: Modifier = Modifier,
    action: @Composable (() -> Unit)? = null,
    dismissAction: @Composable (() -> Unit)? = null,
    actionOnNewLine: Boolean = false,
    shape: Shape = SnackbarDefaultsMMD.shape,
    containerColor: Color = SnackbarDefaultsMMD.color,
    contentColor: Color = SnackbarDefaultsMMD.contentColor,
    actionContentColor: Color = SnackbarDefaultsMMD.actionContentColor,
    dismissActionContentColor: Color = SnackbarDefaultsMMD.dismissActionContentColor,
    dividerColor: Color = SnackbarDefaultsMMD.dividerColor,
    showDivider: Boolean = true,
    content: @Composable () -> Unit
) {
    Column {
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .width(SnackbarDefaultsMMD.dividerLineHeight),
                color = dividerColor
            )
        }
        Surface(
            modifier = modifier,
            shape = shape,
            color = containerColor,
            contentColor = contentColor,
            shadowElevation = SnackbarDefaultsMMD.elevation
        ) {
            val textStyle = SnackbarDefaultsMMD.supportingTextFont
            val actionTextStyle = SnackbarDefaultsMMD.actionLabelTextFont
            CompositionLocalProvider(LocalTextStyle provides textStyle) {
                when {
                    actionOnNewLine && action != null ->
                        NewLineButtonSnackbar(
                            text = content,
                            action = action,
                            dismissAction = dismissAction,
                            actionTextStyle = actionTextStyle,
                            actionContentColor = actionContentColor,
                            dismissActionContentColor = dismissActionContentColor,
                        )

                    else ->
                        OneRowSnackbar(
                            text = content,
                            action = action,
                            dismissAction = dismissAction,
                            actionTextStyle = actionTextStyle,
                            actionTextColor = actionContentColor,
                            dismissActionColor = dismissActionContentColor,
                        )
                }
            }
        }
    }
}

/**
 * SnackbarsMMD provide brief messages about app processes at the bottom of the screen.
 *
 * SnackbarsMMD inform users of a process that an app has performed or will perform. They appear
 * temporarily, towards the bottom of the screen. They shouldn’t interrupt the user experience, and
 * they don’t require user input to disappear.
 *
 * A SnackbarMMD can contain a single action. "Dismiss" or "cancel" actions are optional.
 *
 * SnackbarsMMD with an action should not timeout or self-dismiss until the user performs another
 * action. Here, moving the keyboard focus indicator to navigate through interactive elements in a
 * page is not considered an action.
 *
 * This version of snackbar is designed to work with [SnackbarDataMMD] provided by the [SnackbarHostMMD],
 * which is usually used inside of the [Scaffold].
 *
 * This components provides only the visuals of the Snackbar. If you need to show a Snackbar with
 * defaults on the screen, use [SnackbarHostStateMMD.showSnackbar]:
 *
 *```kotlin
 * @Preview
 * @Composable
 * fun ScaffoldWithSimpleSnackbar() {
 *     val snackbarHostState = remember { SnackbarHostStateMMD() }
 *     val scope = rememberCoroutineScope()
 *     Scaffold(
 *         snackbarHost = { SnackbarHostMMD(snackbarHostState) },
 *         floatingActionButton = {
 *             var clickCount by remember { mutableStateOf(0) }
 *             FloatingActionButtonMMD(
 *                 onClick = {
 *                     // show snackbar as a suspend function
 *                     scope.launch { snackbarHostState.showSnackbar("Snackbar # ${++clickCount}") }
 *                 }
 *             ) {
 *                 Text("Show snackbar")
 *             }
 *         },
 *         content = { innerPadding ->
 *             Text(
 *                 text = "Body content",
 *                 modifier = Modifier.padding(innerPadding).fillMaxSize().wrapContentSize()
 *             )
 *         }
 *     )
 * }
 *
 * ```
 *
 * If you want to customize appearance of the Snackbar, you can pass your own version as a child of
 * the [SnackbarHost] to the [Scaffold]:
 *
 * ```kotlin
 * @Preview
 * @Composable
 * fun ScaffoldWithCustomSnackbar() {
 *     class SnackbarVisualsWithError(override val message: String, val isError: Boolean) :
 *         SnackbarVisualsMMD {
 *         override val actionLabel: String
 *             get() = if (isError) "Error" else "OK"
 *
 *         override val withDismissAction: Boolean
 *             get() = false
 *
 *         override val duration: SnackbarDurationMMD
 *             get() = SnackbarDurationMMD.Indefinite
 *     }
 *
 *     val snackbarHostState = remember { SnackbarHostStateMMD() }
 *     val scope = rememberCoroutineScope()
 *     Scaffold(
 *         snackbarHost = {
 *             // reuse default SnackbarHost to have default animation and timing handling
 *             SnackbarHostMMD(snackbarHostState) { data ->
 *                 // custom snackbar with the custom action button color and border
 *                 val isError = (data.visuals as? SnackbarVisualsWithError)?.isError ?: false
 *                 val buttonColor =
 *                     if (isError) {
 *                         ButtonDefaults.textButtonColors(
 *                             containerColor = MaterialTheme.colorScheme.errorContainer,
 *                             contentColor = MaterialTheme.colorScheme.error
 *                         )
 *                     } else {
 *                         ButtonDefaults.textButtonColors(
 *                             contentColor = MaterialTheme.colorScheme.inversePrimary
 *                         )
 *                     }
 *
 *                 SnackbarMMD(
 *                     modifier =
 *                     Modifier.border(2.dp, MaterialTheme.colorScheme.secondary).padding(12.dp),
 *                     action = {
 *                         TextButton(
 *                             onClick = { if (isError) data.dismiss() else data.performAction() },
 *                             colors = buttonColor
 *                         ) {
 *                             Text(data.visuals.actionLabel ?: "")
 *                         }
 *                     }
 *                 ) {
 *                     Text(data.visuals.message)
 *                 }
 *             }
 *         },
 *         floatingActionButton = {
 *             var clickCount by remember { mutableStateOf(0) }
 *             FloatingActionButtonMMD(
 *                 onClick = {
 *                     scope.launch {
 *                         snackbarHostState.showSnackbar(
 *                             SnackbarVisualsWithError(
 *                                 "Snackbar # ${++clickCount}",
 *                                 isError = clickCount % 2 != 0
 *                             )
 *                         )
 *                     }
 *                 }
 *             ) {
 *                 Text("Show snackbar")
 *             }
 *         },
 *         content = { innerPadding ->
 *             Text(
 *                 text = "Custom Snackbar Demo",
 *                 modifier = Modifier.padding(innerPadding).fillMaxSize().wrapContentSize()
 *             )
 *         }
 *     )
 * }
 * ```
 *
 * When a [SnackbarDataMMD.visuals] sets the Snackbar's duration as [SnackbarDurationMMD.Indefinite], it's
 * recommended to display an additional close affordance action. See
 * [SnackbarVisualsMMD.withDismissAction]:
 *
 *```kotlin
 * @Preview
 * @Composable
 * fun ScaffoldWithSimpleSnackbar() {
 *     val snackbarHostState = remember { SnackbarHostStateMMD() }
 *     val scope = rememberCoroutineScope()
 *     Scaffold(
 *         snackbarHost = { SnackbarHostMMD(snackbarHostState) },
 *         floatingActionButton = {
 *             var clickCount by remember { mutableStateOf(0) }
 *             FloatingActionButtonMMD(
 *                 onClick = {
 *                     // show snackbar as a suspend function
 *                     scope.launch { snackbarHostState.showSnackbar("Snackbar # ${++clickCount}") }
 *                 }
 *             ) {
 *                 Text("Show snackbar")
 *             }
 *         },
 *         content = { innerPadding ->
 *             Text(
 *                 text = "Body content",
 *                 modifier = Modifier.padding(innerPadding).fillMaxSize().wrapContentSize()
 *             )
 *         }
 *     )
 * }
 * ```
 *
 * @param snackbarData data about the current snackbar showing via [SnackbarHostStateMMD]
 * @param modifier the [Modifier] to be applied to this snackbar
 * @param actionOnNewLine whether or not action should be put on a separate line. Recommended for
 *   action with long action text.
 * @param shape defines the shape of this snackbar's container
 * @param containerColor the color used for the background of this snackbar. Use [Color.Transparent]
 *   to have no color.
 * @param contentColor the preferred color for content inside this snackbar
 * @param actionContentColor the preferred content color for the optional action inside this
 *   snackbar. See [SnackbarVisualsMMD.actionLabel].
 * @param dismissActionContentColor the preferred content color for the optional dismiss action
 *   inside this snackbar. See [SnackbarVisualsMMD.withDismissAction].
 * @param dividerColor the color of the divider line
 * @param showDivider whether or not to show the divider line
 */
@Composable
fun SnackbarMMD(
    snackbarData: SnackbarDataMMD,
    modifier: Modifier = Modifier,
    actionOnNewLine: Boolean = false,
    shape: Shape = SnackbarDefaultsMMD.shape,
    containerColor: Color = SnackbarDefaultsMMD.color,
    contentColor: Color = SnackbarDefaultsMMD.contentColor,
    actionContentColor: Color = SnackbarDefaultsMMD.actionContentColor,
    dismissActionContentColor: Color = SnackbarDefaultsMMD.dismissActionContentColor,
    dividerColor: Color = SnackbarDefaultsMMD.dividerColor,
    showDivider: Boolean = true,
) {
    SnackbarMMD(
        modifier = modifier.padding(vertical = 8.dp),
        action = snackbarData.visuals.actionLabel?.let { label ->
            { OutlinedButtonMMD(onClick = snackbarData::performAction) { Text(label) } }
        },
        dismissAction = snackbarData.visuals.withDismissAction.takeIf { it }?.let {
            {
                IconButton(onClick = snackbarData::dismiss) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = null,
                    )
                }
            }
        },
        actionOnNewLine = actionOnNewLine,
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        actionContentColor = actionContentColor,
        dismissActionContentColor = dismissActionContentColor,
        dividerColor = dividerColor,
        showDivider = showDivider,
    ) {
        Text(snackbarData.visuals.message)
    }
}

@Composable
private fun NewLineButtonSnackbar(
    text: @Composable () -> Unit,
    action: @Composable () -> Unit,
    dismissAction: @Composable (() -> Unit)?,
    actionTextStyle: TextStyle,
    actionContentColor: Color,
    dismissActionContentColor: Color
) {
    Column(
        modifier =
        Modifier
            // Fill max width, up to ContainerMaxWidth.
            .widthIn(max = ContainerMaxWidth)
            .fillMaxWidth()
            .padding(start = HorizontalSpacing, bottom = SeparateButtonExtraY)
    ) {
        Box(
            Modifier
                .paddingFromBaseline(HeightToFirstLine, LongButtonVerticalOffset)
                .padding(end = HorizontalSpacingButtonSide)
        ) {
            text()
        }

        Box(
            Modifier
                .align(Alignment.End)
                .padding(end = if (dismissAction == null) HorizontalSpacingButtonSide else 0.dp)
        ) {
            Row {
                CompositionLocalProvider(
                    LocalContentColor provides actionContentColor,
                    LocalTextStyle provides actionTextStyle,
                    content = action
                )
                if (dismissAction != null) {
                    CompositionLocalProvider(
                        LocalContentColor provides dismissActionContentColor,
                        content = dismissAction
                    )
                }
            }
        }
    }
}

@Composable
private fun OneRowSnackbar(
    text: @Composable () -> Unit,
    action: @Composable (() -> Unit)?,
    dismissAction: @Composable (() -> Unit)?,
    actionTextStyle: TextStyle,
    actionTextColor: Color,
    dismissActionColor: Color
) {
    val textTag = "text"
    val actionTag = "action"
    val dismissActionTag = "dismissAction"
    Layout(
        {
            Box(
                Modifier
                    .layoutId(textTag)
                    .padding(vertical = SnackbarVerticalPadding)
            ) { text() }
            if (action != null) {
                Box(Modifier.layoutId(actionTag)) {
                    CompositionLocalProvider(
                        LocalContentColor provides actionTextColor,
                        LocalTextStyle provides actionTextStyle,
                        content = action
                    )
                }
            }
            if (dismissAction != null) {
                Box(Modifier.layoutId(dismissActionTag)) {
                    CompositionLocalProvider(
                        LocalContentColor provides dismissActionColor,
                        content = dismissAction
                    )
                }
            }
        },
        modifier =
        Modifier.padding(
            start = HorizontalSpacing,
            end = if (dismissAction == null) HorizontalSpacingButtonSide else 0.dp
        )
    ) { measurables, constraints ->
        val containerWidth = min(constraints.maxWidth, ContainerMaxWidth.roundToPx())
        val actionButtonPlaceable =
            measurables.fastFirstOrNull { it.layoutId == actionTag }?.measure(constraints)
        val dismissButtonPlaceable =
            measurables.fastFirstOrNull { it.layoutId == dismissActionTag }?.measure(constraints)
        val actionButtonWidth = actionButtonPlaceable?.width ?: 0
        val actionButtonHeight = actionButtonPlaceable?.height ?: 0
        val dismissButtonWidth = dismissButtonPlaceable?.width ?: 0
        val dismissButtonHeight = dismissButtonPlaceable?.height ?: 0
        val extraSpacingWidth = if (dismissButtonWidth == 0) TextEndExtraSpacing.roundToPx() else 0
        val textMaxWidth =
            (containerWidth - actionButtonWidth - dismissButtonWidth - extraSpacingWidth)
                .coerceAtLeast(constraints.minWidth)
        val textPlaceable =
            measurables
                .fastFirst { it.layoutId == textTag }
                .measure(constraints.copy(minHeight = 0, maxWidth = textMaxWidth))

        val firstTextBaseline = textPlaceable[FirstBaseline]
        val lastTextBaseline = textPlaceable[LastBaseline]
        val hasText =
            firstTextBaseline != AlignmentLine.Unspecified && lastTextBaseline != AlignmentLine.Unspecified
        val isOneLine = firstTextBaseline == lastTextBaseline || !hasText
        val dismissButtonPlaceX = containerWidth - dismissButtonWidth
        val actionButtonPlaceX = dismissButtonPlaceX - actionButtonWidth

        val textPlaceY: Int
        val containerHeight: Int
        val actionButtonPlaceY: Int
        if (isOneLine) {
            val minContainerHeight = SnackbarDefaultsMMD.singleLineContainerHeight.roundToPx()
            val contentHeight = max(actionButtonHeight, dismissButtonHeight)
            containerHeight = max(minContainerHeight, contentHeight)
            textPlaceY = (containerHeight - textPlaceable.height) / 2
            actionButtonPlaceY = actionButtonPlaceable?.get(FirstBaseline)
                ?.takeIf { it != AlignmentLine.Unspecified }
                ?.let { textPlaceY + firstTextBaseline - it } ?: 0
        } else {
            val baselineOffset = HeightToFirstLine.roundToPx()
            textPlaceY = baselineOffset - firstTextBaseline
            val minContainerHeight = SnackbarDefaultsMMD.twoLinesContainerHeight.roundToPx()
            val contentHeight = textPlaceY + textPlaceable.height
            containerHeight = max(minContainerHeight, contentHeight)
            actionButtonPlaceY =
                actionButtonPlaceable?.let { (containerHeight - it.height) / 2 } ?: 0
        }
        val dismissButtonPlaceY =
            dismissButtonPlaceable?.let { (containerHeight - it.height) / 2 } ?: 0

        layout(containerWidth, containerHeight) {
            textPlaceable.placeRelative(0, textPlaceY)
            dismissButtonPlaceable?.placeRelative(dismissButtonPlaceX, dismissButtonPlaceY)
            actionButtonPlaceable?.placeRelative(actionButtonPlaceX, actionButtonPlaceY)
        }
    }
}

/** Contains the default values used for [Snackbar]. */
object SnackbarDefaultsMMD {
    /** Default height of a snackbar. */
    val dividerLineHeight = 3.dp

    /** Default elevation of a snackbar. */
    val elevation = 0.dp // no elevation for snackbar

    /** Default height of a snackbar for single line. */
    val singleLineContainerHeight = 48.dp

    /** Default height of a snackbar for two lines. */
    val twoLinesContainerHeight = 68.dp

    val actionLabelTextFont: TextStyle
        @Composable get() = MaterialTheme.typography.bodyMedium

    val supportingTextFont: TextStyle
        @Composable get() = MaterialTheme.typography.bodyLarge

    /** Default shape of a snackbar. */
    val shape: Shape
        @Composable get() = RoundedCornerShape(0.dp)

    /** Default color of a snackbar. */
    val color: Color
        @Composable get() = MaterialTheme.colorScheme.inverseSurface

    val dividerColor: Color
        @Composable get() = MaterialTheme.colorScheme.inverseOnSurface

    /** Default content color of a snackbar. */
    val contentColor: Color
        @Composable get() = MaterialTheme.colorScheme.inverseOnSurface

    /** Default action content color of a snackbar. */
    val actionContentColor: Color
        @Composable get() = MaterialTheme.colorScheme.inversePrimary

    /** Default dismiss action content color of a snackbar. */
    val dismissActionContentColor: Color
        @Composable get() = MaterialTheme.colorScheme.inverseOnSurface
}

private val ContainerMaxWidth = 600.dp
private val HeightToFirstLine = 30.dp
private val HorizontalSpacing = 12.dp
private val HorizontalSpacingButtonSide = 8.dp
private val SeparateButtonExtraY = 2.dp
private val SnackbarVerticalPadding = 6.dp
private val TextEndExtraSpacing = 8.dp
private val LongButtonVerticalOffset = 12.dp
