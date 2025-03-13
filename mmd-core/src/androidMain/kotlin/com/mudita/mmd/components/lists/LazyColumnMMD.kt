/*
 * Copyright 2021 The Android Open Source Project
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
 */

package com.mudita.mmd.components.lists

import android.view.ViewTreeObserver
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.mudita.mmd.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

/**
 * The vertically scrolling list that only composes and lays out the currently visible items.
 * The [content] block defines a DSL which allows you to emit items of different types. For
 * example you can use [LazyListScope.item] to add a single item and [LazyListScope.items] to add
 * a list of items.
 *
 *```kotlin
 * @Sampled
 * @Composable
 * fun LazyColumnSample() {
 *     val itemsList = (0..5).toList()
 *     val itemsIndexedList = listOf("A", "B", "C")
 *
 *     LazyColumnMMD {
 *         items(itemsList) {
 *             Text("Item is $it")
 *         }
 *
 *         item {
 *             Text("Single item")
 *         }
 *
 *         itemsIndexed(itemsIndexedList) { index, item ->
 *             Text("Item at index $index is $item")
 *         }
 *     }
 * }
 * ```
 *
 * @param modifier the modifier to apply to this layout.
 * @param state the state object to be used to control or observe the list's state.
 * @param contentPadding a padding around the whole content. This will add padding for the.
 * content after it has been clipped, which is not possible via [modifier] param. You can use it
 * to add a padding before the first item or after the last one. If you want to add a spacing
 * between each item use [verticalArrangement].
 * @param reverseLayout reverse the direction of scrolling and layout. When `true`, items are
 * laid out in the reverse order and [LazyListState.firstVisibleItemIndex] == 0 means
 * that column is scrolled to the bottom. Note that [reverseLayout] does not change the behavior of
 * [verticalArrangement],
 * e.g. with [Arrangement.Top] (top) 123### (bottom) becomes (top) 321### (bottom).
 * @param verticalArrangement The vertical arrangement of the layout's children. This allows
 * to add a spacing between items and specify the arrangement of the items when we have not enough
 * of them to fill the whole minimum size.
 * @param horizontalAlignment the horizontal alignment applied to the items.
 * @param flingBehavior logic describing fling behavior.
 * @param scrollStep the number of items to scroll when the user drags the list.
 * @param content a block which describes the content. Inside this block you can use methods like
 * [LazyListScope.item] to add a single item or [LazyListScope.items] to add a list of items.
 */
@Composable
fun LazyColumnMMD(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = LazyColumnMMDDefaults.contentPadding,
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        LazyColumnMMDDefaults.verticalArrangement(reverseLayout),
    horizontalAlignment: Alignment.Horizontal = LazyColumnMMDDefaults.horizontalAlignment,
    flingBehavior: FlingBehavior = LazyColumnMMDDefaults.flignBehavior,
    scrollStep: Int = LazyColumnMMDDefaults.SCROLL_STEP,
    content: LazyListScope.() -> Unit,
) {

    val scope = rememberCoroutineScope()
    val layoutInfo by remember { derivedStateOf { state.layoutInfo } }
    var isDragging by remember { mutableStateOf(false) }
    val isScrollable by remember {
        derivedStateOf {
            val totalHeight =
                layoutInfo.totalItemsCount * (layoutInfo.visibleItemsInfo.firstOrNull()?.size ?: 0)
            val viewportHeight = layoutInfo.viewportEndOffset - layoutInfo.viewportStartOffset
            totalHeight > viewportHeight
        }
    }

    Row(
        modifier = modifier
            /**
             * To enhance the user experience and prevent visual flickering,
             * the list and scrollbar are rendered only after the list content has fully loaded.
             * This ensures a smoother and more stable initial display.
             */
            .alpha(if (layoutInfo.totalItemsCount > 0) 1f else 0f),
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragEnd = { isDragging = false },
                    ) { _, dragAmount ->
                        if (!isDragging && isScrollable) {
                            isDragging = true
                            val direction = if (dragAmount > 0) -1 else 1
                            val newIdx = (state.firstVisibleItemIndex + direction * scrollStep)
                                .coerceIn(0, layoutInfo.totalItemsCount - 1)
                            scope.launch { state.scrollToItem(newIdx) }
                        }
                    }
                },
            state = state,
            contentPadding = contentPadding,
            reverseLayout = reverseLayout,
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            flingBehavior = flingBehavior,
            // false by default, because we handle scrolling ourselves
            userScrollEnabled = false,
        ) {
            content()
        }

        if (isScrollable) {
            Scrollbar(
                layoutInfo = layoutInfo,
                listState = state,
                scope = scope,
                scrollOnce = { direction ->
                    val newIdx = (state.firstVisibleItemIndex - direction * scrollStep)
                        .coerceIn(0, state.layoutInfo.totalItemsCount - 1)
                    scope.launch { state.scrollToItem(newIdx) }
                },
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Scrollbar(
    modifier: Modifier = Modifier,
    scope: CoroutineScope,
    layoutInfo: LazyListLayoutInfo,
    listState: LazyListState,
    scrollOnce: (direction: Int) -> Unit,
) {
    if (layoutInfo.totalItemsCount == 0)
        return

    val containerColor = LazyColumnMMDDefaults.containerSliderColor
    val sliderColor = LazyColumnMMDDefaults.sliderColor

    val view = LocalView.current
    var isKeyboardOpen by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val keyboardListener = ViewTreeObserver.OnGlobalLayoutListener {
            isKeyboardOpen =
                ViewCompat.getRootWindowInsets(view)?.isVisible(WindowInsetsCompat.Type.ime()) ?: true
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(keyboardListener)
        onDispose { view.viewTreeObserver.removeOnGlobalLayoutListener(keyboardListener) }
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val firstVisibleIdx = layoutInfo.visibleItemsInfo.firstOrNull()?.index ?: 0
        val lastVisibleIdx = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        val viewportHeight = layoutInfo.viewportEndOffset + layoutInfo.viewportStartOffset
        val isFirstItemFullyVisible = layoutInfo.visibleItemsInfo.first().offset >= 0
        val isAtListStart = firstVisibleIdx == 0 && isFirstItemFullyVisible
        val isLastItemFullyVisible =
            layoutInfo.visibleItemsInfo.last().offset + layoutInfo.visibleItemsInfo.last().size <= viewportHeight
        val isAtListEnd = lastVisibleIdx == layoutInfo.totalItemsCount - 1 && isLastItemFullyVisible

        Icon(
            painter = painterResource(if (isAtListStart) R.drawable.chevron_dotted_up else R.drawable.chevron_filled_up),
            contentDescription = null,
            modifier = Modifier
                .padding(vertical = 16.dp)
                .size(LazyColumnMMDDefaults.navigateIconSize)
                .combinedClickable(
                    onClick = { scrollOnce(1) },
                    onLongClick = { scope.launch { listState.scrollToItem(0) } },
                ),
        )

        Canvas(
            modifier = Modifier
                .width(LazyColumnMMDDefaults.sliderBackgroundWidth)
                .weight(1f)
                .border(
                    width = LazyColumnMMDDefaults.sliderBackgroundBorderWidth,
                    color = sliderColor,
                    shape = LazyColumnMMDDefaults.sliderBackgroundCorners,
                )
                .pointerInput(listState.maxValue) {
                    detectTapGestures { offset ->
                        val visibleCount = layoutInfo.visibleItemsInfo.size
                        val sliderHeight = size.height * visibleCount / layoutInfo.totalItemsCount
                        val adjustedOffset =
                            offset.y - (if (isKeyboardOpen) sliderHeight / 4 else sliderHeight / 2)
                        val targetIndex =
                            (adjustedOffset / size.height * layoutInfo.totalItemsCount).toInt()
                                .coerceIn(0, layoutInfo.totalItemsCount - 1)
                        scope.launch { listState.scrollToItem(targetIndex) }
                    }
                },
        ) {
            var visibleCount = lastVisibleIdx - firstVisibleIdx + 1

            if (!isLastItemFullyVisible || !isFirstItemFullyVisible) visibleCount--

            val sliderHeight =
                size.height * (visibleCount.toFloat() / layoutInfo.totalItemsCount.toFloat())
            val minSliderHeight = max(size.height * 0.05f, LazyColumnMMDDefaults.SLIDER_MIN_HEIGHT)
            val adjustedSliderHeight = sliderHeight.coerceAtLeast(minSliderHeight)

            val firstVisible = if (isFirstItemFullyVisible) firstVisibleIdx else min(
                firstVisibleIdx + 1,
                layoutInfo.totalItemsCount - visibleCount,
            )

            val maxOffset = size.height - adjustedSliderHeight
            val sliderOffset =
                ((firstVisible.toFloat() / (layoutInfo.totalItemsCount - visibleCount)) * maxOffset)
                    .coerceIn(
                        0f,
                        maxOffset,
                    )

            // Draw the container for the slider
            drawRoundRect(
                color = containerColor,
                topLeft = Offset.Zero,
                size = Size(size.width, size.height),
                cornerRadius = LazyColumnMMDDefaults.cornerRadius(size),
            )

            // Draw the slider
            drawRoundRect(
                color = if (visibleCount >= layoutInfo.totalItemsCount) containerColor else sliderColor,
                topLeft = Offset(0f, sliderOffset),
                size = Size(size.width, adjustedSliderHeight),
                cornerRadius = LazyColumnMMDDefaults.cornerRadius(size),
            )
        }

        Icon(
            painter = painterResource(if (isAtListEnd) R.drawable.chevron_dotted_down else R.drawable.chevron_filled_down),
            contentDescription = null,
            modifier = Modifier
                .padding(vertical = 16.dp)
                .size(LazyColumnMMDDefaults.navigateIconSize)
                .combinedClickable(
                    onClick = { scrollOnce(-1) },
                    onLongClick = { scope.launch { listState.scrollToItem(layoutInfo.totalItemsCount - 1) } },
                ),
        )
    }
}

object LazyColumnMMDDefaults {
    /**
     * The minimum height of the slider in the scrollbar.
     */
    const val SLIDER_MIN_HEIGHT = 16f

    /**
     * The number of items to scroll when the user drags the list.
     */
    const val SCROLL_STEP = 4

    /**
     * The default fling behavior for the [LazyColumnMMD].
     */
    val flignBehavior: FlingBehavior
        @Composable get() = ScrollableDefaults.flingBehavior()

    /**
     * The default padding around the whole content.
     */
    val contentPadding: PaddingValues = PaddingValues(0.dp)

    /**
     * The default horizontal alignment applied to the items.
     */
    val horizontalAlignment: Alignment.Horizontal = Alignment.Start

    /**
     * The default vertical arrangement of the layout's children.
     */
    fun verticalArrangement(reverseLayout: Boolean): Arrangement.Vertical =
        if (reverseLayout) Arrangement.Bottom else Arrangement.Top

    /**
     * The default corner radius for the slider.
     */
    internal fun cornerRadius(size: Size): CornerRadius =
        CornerRadius(size.width / 2, size.width / 2)

    /**
     * The default color for the container of the slider.
     */
    internal val containerSliderColor: Color
        @Composable get() = MaterialTheme.colorScheme.onPrimary

    /**
     * The default color for the slider.
     */
    internal val sliderColor: Color
        @Composable get() = MaterialTheme.colorScheme.primary

    /**
     * The default size of the navigate icon.
     */
    internal val navigateIconSize: Dp = 24.dp

    /**
     * The default width of the slider background.
     */
    internal val sliderBackgroundWidth: Dp = 8.dp

    /**
     * The default border width of the slider background.
     */
    internal val sliderBackgroundBorderWidth: Dp = 1.dp

    /**
     * The default corners of the slider background.
     */
    internal val sliderBackgroundCorners: Shape = RoundedCornerShape(4.dp)
}

internal val LazyListState.maxValue
    get() = layoutInfo.totalItemsCount - 1
