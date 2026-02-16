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

package com.mudita.mmd.components.bottom_sheet

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.collapse
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mudita.mmd.R
import com.mudita.mmd.components.bottom_sheet.SheetValueMMD.Expanded
import com.mudita.mmd.components.bottom_sheet.SheetValueMMD.Hidden
import com.mudita.mmd.components.bottom_sheet.SheetValueMMD.PartiallyExpanded
import com.mudita.mmd.internal.bottom_sheet.DraggableAnchors
import com.mudita.mmd.internal.bottom_sheet.draggableAnchors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.max

/**
 *
 * Modal bottom sheets are used as an alternative to inline menus or simple dialogs on mobile,
 * especially when offering a long list of action items, or when items require longer descriptions
 * and icons. Like dialogs, modal bottom sheets appear in front of app content, disabling all other
 * app functionality when they appear, and remaining on screen until confirmed, dismissed, or a
 * required action has been taken.
 *
 * A simple example of a modal bottom sheet(without animations) looks like this:
 *
 *```kotlin
 * @OptIn(ExperimentalMaterial3Api::class)
 * @Composable
 * fun ModalBottomSheetCustom() {
 *     var openBottomSheet by rememberSaveable { mutableStateOf(false) }
 *     var skipPartiallyExpanded by rememberSaveable { mutableStateOf(false) }
 *     val scope = rememberCoroutineScope()
 *     val bottomSheetState =
 *         rememberModalBottomSheetMMDState(skipPartiallyExpanded = skipPartiallyExpanded)
 *
 *     Column(
 *         horizontalAlignment = Alignment.Start,
 *         verticalArrangement = Arrangement.spacedBy(4.dp)
 *     ) {
 *         Row(
 *             Modifier.toggleable(
 *                 value = skipPartiallyExpanded,
 *                 role = Role.Checkbox,
 *                 onValueChange = { checked -> skipPartiallyExpanded = checked }
 *             )
 *         ) {
 *             Checkbox(checked = skipPartiallyExpanded, onCheckedChange = null)
 *             Spacer(Modifier.width(16.dp))
 *             Text("Skip partially expanded State")
 *         }
 *         ButtonMMD(
 *             onClick = { openBottomSheet = !openBottomSheet },
 *             modifier = Modifier.align(Alignment.CenterHorizontally)
 *         ) {
 *             Text(text = "Show Bottom Sheet")
 *         }
 *     }
 *
 *     if (openBottomSheet) {
 *         ModalBottomSheetMMD(
 *             onDismissRequest = { openBottomSheet = false },
 *             sheetState = bottomSheetState,
 *         ) {
 *             Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
 *                 Button(
 *                     // Note: If you provide logic outside of onDismissRequest to remove the sheet,
 *                     // you must additionally handle intended state cleanup, if any.
 *                     onClick = {
 *                         scope
 *                             .launch { bottomSheetState.hide() }
 *                             .invokeOnCompletion {
 *                                 if (!bottomSheetState.isVisible) {
 *                                     openBottomSheet = false
 *                                 }
 *                             }
 *                     }
 *                 ) {
 *                     Text("Hide Bottom Sheet")
 *                 }
 *             }
 *             LazyColumnMMD {
 *                 items(25) {
 *                     ListItem(
 *                         headlineContent = { Text("Item $it") },
 *                         leadingContent = {
 *                             Icon(
 *                                 Icons.Default.Favorite,
 *                                 contentDescription = "Localized description"
 *                             )
 *                         },
 *                         colors = ListItemDefaults.colors(
 *                             containerColor = MaterialTheme.colorScheme.surfaceContainerLow
 *                         ),
 *                     )
 *                 }
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * @param onDismissRequest Executes when the user clicks outside of the bottom sheet, after sheet
 *   animates to [Hidden].
 * @param modifier Optional [Modifier] for the bottom sheet.
 * @param sheetState The state of the bottom sheet.
 * @param sheetMaxWidth [Dp] that defines what the maximum width the sheet will take. Pass in
 *   [Dp.Unspecified] for a sheet that spans the entire screen width.
 * @param shape The shape of the bottom sheet.
 * @param containerColor The color used for the background of this bottom sheet
 * @param contentColor The preferred color for content inside this bottom sheet. Defaults to either
 *   the matching content color for [containerColor], or to the current [LocalContentColor] if
 *   [containerColor] is not a color from the theme.
 * @param scrimColor Color of the scrim that obscures content when the bottom sheet is open.
 * @param dragHandle Optional visual marker to swipe the bottom sheet.
 * @param contentWindowInsets window insets to be passed to the bottom sheet content via
 *   [PaddingValues] params.
 * @param properties [ModalBottomSheetPropertiesMMD] for further customization of this modal bottom
 *   sheet's window behavior.
 * @param content The content to be displayed inside the bottom sheet.
 */
@Composable
@ExperimentalMaterial3Api
fun ModalBottomSheetMMD(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetStateMMD = rememberModalBottomSheetMMDState(),
    sheetMaxWidth: Dp = BottomSheetDefaultsMMD.SheetMaxWidth,
    shape: Shape = BottomSheetDefaultsMMD.ExpandedShape,
    containerColor: Color = BottomSheetDefaultsMMD.ContainerColor,
    contentColor: Color = contentColorFor(containerColor),
    scrimColor: Color = BottomSheetDefaultsMMD.ScrimColor,
    dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaultsMMD.DragHandle() },
    contentWindowInsets: @Composable () -> WindowInsets = { BottomSheetDefaultsMMD.windowInsets },
    properties: ModalBottomSheetPropertiesMMD = ModalBottomSheetDefaultsMMD.properties,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scope = rememberCoroutineScope()
    val performDismiss: () -> Unit = {
        if (sheetState.anchoredDraggableState.confirmValueChange(Hidden)) {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    onDismissRequest()
                }
            }
        }
    }
    val settleToDismiss: (velocity: Float) -> Unit = {
        scope.launch { sheetState.settle(it) }.invokeOnCompletion {
            if (!sheetState.isVisible) onDismissRequest()
        }
    }

    ModalBottomSheetDialog(
        properties = properties,
        onDismissRequest = {
            if (sheetState.currentValue == Expanded && sheetState.hasPartiallyExpandedState) {
                scope.launch { sheetState.partialExpand() }
            } else {
                scope.launch { sheetState.hide() }.invokeOnCompletion { onDismissRequest() }
            }
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .semantics { isTraversalGroup = true },
        ) {
            Scrim(
                color = scrimColor,
                onDismissRequest = performDismiss,
                visible = sheetState.targetValue != Hidden,
            )
            ModalBottomSheetContent(
                scope,
                performDismiss,
                settleToDismiss,
                modifier,
                sheetState,
                sheetMaxWidth,
                shape,
                containerColor,
                contentColor,
                TonalElevation,
                dragHandle,
                contentWindowInsets,
                content,
            )
        }
    }
    if (sheetState.hasExpandedState) {
        LaunchedEffect(sheetState) { sheetState.show() }
    }
}

@Composable
@ExperimentalMaterial3Api
internal fun BoxScope.ModalBottomSheetContent(
    scope: CoroutineScope,
    animateToDismiss: () -> Unit,
    settleToDismiss: (velocity: Float) -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetStateMMD = rememberModalBottomSheetMMDState(),
    sheetMaxWidth: Dp = BottomSheetDefaults.SheetMaxWidth,
    shape: Shape = BottomSheetDefaults.ExpandedShape,
    containerColor: Color = BottomSheetDefaults.ContainerColor,
    contentColor: Color = contentColorFor(containerColor),
    tonalElevation: Dp = BottomSheetDefaults.Elevation,
    dragHandle: @Composable (() -> Unit)? = { BottomSheetDefaults.DragHandle() },
    contentWindowInsets: @Composable () -> WindowInsets = { BottomSheetDefaults.windowInsets },
    content: @Composable ColumnScope.() -> Unit,
) {
    val context = LocalContext.current

    Surface(
        modifier =
        modifier
            .align(Alignment.TopCenter)
            .widthIn(max = sheetMaxWidth)
            .fillMaxWidth()
            .nestedScroll(
                remember(sheetState) {
                    consumeSwipeWithinBottomSheetBoundsNestedScrollConnection(
                        sheetState = sheetState,
                        orientation = Orientation.Vertical,
                        onFling = settleToDismiss,
                    )
                },
            )
            .draggableAnchors(
                sheetState.anchoredDraggableState,
                Orientation.Vertical,
            ) { sheetSize, constraints ->
                val fullHeight = constraints.maxHeight.toFloat()
                val newAnchors = DraggableAnchors {
                    Hidden at fullHeight
                    if (
                        sheetSize.height > (fullHeight / 2) && !sheetState.skipPartiallyExpanded
                    ) {
                        PartiallyExpanded at fullHeight / 2f
                    }
                    if (sheetSize.height != 0) {
                        Expanded at max(0f, fullHeight - sheetSize.height)
                    }
                }
                val newTarget =
                    when (sheetState.anchoredDraggableState.targetValue) {
                        Hidden -> Hidden
                        PartiallyExpanded, Expanded -> {
                            val hasPartiallyExpandedState =
                                newAnchors.hasAnchorFor(PartiallyExpanded)
                            if (hasPartiallyExpandedState) PartiallyExpanded
                            else if (newAnchors.hasAnchorFor(Expanded)) Expanded else Hidden
                        }
                    }
                return@draggableAnchors newAnchors to newTarget
            }
            .draggable(
                state = sheetState.anchoredDraggableState.draggableState,
                orientation = Orientation.Vertical,
                enabled = sheetState.isVisible,
                startDragImmediately = sheetState.anchoredDraggableState.isAnimationRunning,
                onDragStopped = { settleToDismiss(it) },
            )
            .semantics { traversalIndex = 0f },
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = tonalElevation,
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .windowInsetsPadding(contentWindowInsets()),
        ) {
            if (dragHandle != null) {
                val collapseActionLabel =
                    context.getString(R.string.bottom_sheet_collapse_description)
                val dismissActionLabel =
                    context.getString(R.string.bottom_sheet_dismiss_description)
                val expandActionLabel = context.getString(R.string.bottom_sheet_expand_description)
                Box(
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .semantics(
                            mergeDescendants = true,
                        ) {
                            with(sheetState) {
                                dismiss(dismissActionLabel) {
                                    animateToDismiss()
                                    true
                                }
                                if (currentValue == PartiallyExpanded) {
                                    expand(expandActionLabel) {
                                        if (anchoredDraggableState.confirmValueChange(Expanded)) {
                                            scope.launch { sheetState.expand() }
                                        }
                                        true
                                    }
                                } else if (hasPartiallyExpandedState) {
                                    collapse(collapseActionLabel) {
                                        if (
                                            anchoredDraggableState.confirmValueChange(
                                                PartiallyExpanded,
                                            )
                                        ) {
                                            scope.launch { partialExpand() }
                                        }
                                        true
                                    }
                                }
                            }
                        },
                ) {
                    dragHandle()
                }
            }
            content()
        }
    }
}

/**
 * Create and [remember] a [SheetStateMMD] for [ModalBottomSheetMMD].
 *
 * @param skipPartiallyExpanded Whether the partially expanded state, if the sheet is tall enough,
 *   should be skipped. If true, the sheet will always expand to the [Expanded] state and move to
 *   the [Hidden] state when hiding the sheet, either programmatically or by user interaction.
 * @param confirmValueChange Optional callback invoked to confirm or veto a pending state change.
 */
@Composable
@ExperimentalMaterial3Api
fun rememberModalBottomSheetMMDState(
    skipPartiallyExpanded: Boolean = false,
    confirmValueChange: (SheetValueMMD) -> Boolean = { true },
) =
    rememberSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded,
        confirmValueChange = confirmValueChange,
        initialValue = Hidden,
    )

@Composable
private fun Scrim(color: Color, onDismissRequest: () -> Unit, visible: Boolean) {
    if (color.isSpecified) {
        val alpha = if (visible) 1f else 0f

        val dismissSheet =
            if (visible) {
                Modifier
                    .pointerInput(onDismissRequest) { detectTapGestures { onDismissRequest() } }
                    .semantics(mergeDescendants = true) {
                        traversalIndex = 1f
                        onClick {
                            onDismissRequest()
                            true
                        }
                    }
            } else {
                Modifier
            }

        Canvas(
            Modifier
                .fillMaxSize()
                .then(dismissSheet),
        ) {
            drawRect(color = color, alpha = alpha)
        }
    }
}

private val TonalElevation = 0.dp
