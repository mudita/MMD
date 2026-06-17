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

package com.mudita.mmd.components.search_bar

import androidx.activity.BackEventCompat
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.MutatorMutex
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.onConsumedWindowInsetsChanged
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import com.mudita.mmd.components.divider.HorizontalDividerMMD
import com.mudita.mmd.components.search_bar.SearchBarDefaultsMMD.SearchBarColorsMMD
import com.mudita.mmd.components.search_bar.SearchBarDefaultsMMD.SearchBarImpl
import com.mudita.mmd.components.search_bar.SearchBarDefaultsMMD.inputFieldShape
import com.mudita.mmd.components.text_field.TextFieldColorsMMD
import com.mudita.mmd.components.text_field.TextFieldDefaultsMMD
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sign

/**
 * Search Bar Mudita Mindful Design
 *
 * A search bar represents a floating search field that allows users to enter a keyword or phrase
 * and get relevant information. It can be used as a way to navigate through an app via search
 * queries.
 *
 * A search bar expands into a search "view" and can be used to display dynamic suggestions or
 * search results.
 *
 * An example looks like:
 *
 * ```kotlin
 * var text by rememberSaveable { mutableStateOf("") }
 * var expanded by rememberSaveable { mutableStateOf(false) }
 *
 * Box(
 *     Modifier
 *         .fillMaxSize()
 *         .padding(innerPadding)
 *         .semantics { isTraversalGroup = true }) {
 *     SearchBarMMD(
 *         modifier = Modifier
 *             .padding(all = 16.dp)
 *             .align(Alignment.TopCenter)
 *             .semantics { traversalIndex = 0f },
 *         inputField = {
 *             SearchBarDefaultsMMD.InputField(
 *                 query = text,
 *                 onQueryChange = { text = it },
 *                 onSearch = { expanded = false },
 *                 expanded = expanded,
 *                 onExpandedChange = { expanded = it },
 *                 placeholder = { TextMMD("Hinted search text") },
 *                 leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
 *                 trailingIcon = {
 *                     if (text.isNotEmpty()) {
 *                         IconButton(onClick = { text = "" }) {
 *                             Icon(Icons.Sharp.Clear, contentDescription = null)
 *                         }
 *                     }
 *                 },
 *             )
 *         },
 *         border = inputFieldBorder.takeIf { !expanded },
 *         expanded = expanded,
 *         onExpandedChange = { expanded = it },
 *     ) {
 *         Column(Modifier.verticalScroll(rememberScrollState())) {
 *             repeat(4) { idx ->
 *                 val resultText = "Suggestion $idx"
 *                 ListItem(
 *                     headlineContent = { Text(resultText) },
 *                     supportingContent = { Text("Additional info") },
 *                     leadingContent = { Icon(Icons.Filled.Star, contentDescription = null) },
 *                     colors = ListItemDefaults.colors(containerColor = Color.Transparent),
 *                     modifier =
 *                         Modifier
 *                             .clickable {
 *                                 text = resultText
 *                                 expanded = false
 *                             }
 *                             .fillMaxWidth()
 *                             .padding(horizontal = 16.dp, vertical = 4.dp)
 *                 )
 *             }
 *         }
 *     }
 * }
 * ```
 *
 * @param inputField the input field of this search bar that allows entering a query, typically a
 *   [SearchBarDefaultsMMD.InputField].
 * @param expanded whether this search bar is expanded and showing search results.
 * @param onExpandedChange the callback to be invoked when this search bar's expanded state is
 *   changed.
 * @param modifier the [Modifier] to be applied to this search bar.
 * @param shape the shape of this search bar when it is not [expanded]. When [expanded], the shape
 *   will always be [SearchBarDefaultsMMD.fullScreenShape].
 * @param border the border to draw around the container of this search bar's [inputField].
 * @param colors [SearchBarColorsMMD] that will be used to resolve the colors used for this search bar
 *   in different states. See [SearchBarDefaultsMMD.colors].
 * @param tonalElevation when [SearchBarColorsMMD.containerColor] is [ColorScheme.surface], a
 *   translucent primary color overlay is applied on top of the container. A higher tonal elevation
 *   value will result in a darker color in light theme and lighter color in dark theme. See also:
 *   [Surface].
 * @param shadowElevation the elevation for the shadow below this search bar
 * @param windowInsets the window insets that this search bar will respect
 * @param content the content of this search bar to display search results below the [inputField].
 */
@ExperimentalMaterial3Api
@Composable
fun SearchBarMMD(
    inputField: @Composable () -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = inputFieldShape,
    border: BorderStroke? = SearchBarDefaultsMMD.inputFieldBorder,
    colors: SearchBarColorsMMD = SearchBarDefaultsMMD.colors(),
    tonalElevation: Dp = SearchBarDefaultsMMD.TonalElevation,
    shadowElevation: Dp = SearchBarDefaultsMMD.ShadowElevation,
    windowInsets: WindowInsets = SearchBarDefaultsMMD.windowInsets,
    content: @Composable ColumnScope.() -> Unit,
) {
    val progress = if (expanded) 1f else 0f
    val finalBackProgress = remember { mutableFloatStateOf(Float.NaN) }
    val firstBackEvent = remember { mutableStateOf<BackEventCompat?>(null) }
    val currentBackEvent = remember { mutableStateOf<BackEventCompat?>(null) }

    LaunchedEffect(expanded) {
        if (!expanded) {
            finalBackProgress.floatValue = Float.NaN
            firstBackEvent.value = null
            currentBackEvent.value = null
        }
    }

    val mutatorMutex = remember { MutatorMutex() }
    PredictiveBackHandler(enabled = expanded) { currentProgress ->
        mutatorMutex.mutate {
            try {
                finalBackProgress.floatValue = Float.NaN
                currentProgress.collect { backEvent ->
                    if (firstBackEvent.value == null) {
                        firstBackEvent.value = backEvent
                    }
                    currentBackEvent.value = backEvent
                    finalBackProgress.floatValue = 1 - PredictiveBack.transform(backEvent.progress)
                }
                onExpandedChange(false)
            } catch (e: CancellationException) {
                finalBackProgress.floatValue = Float.NaN
                firstBackEvent.value = null
                currentBackEvent.value = null
            }
        }
    }

    SearchBarImpl(
        progress = progress,
        finalBackProgress = finalBackProgress,
        firstBackEvent = firstBackEvent,
        currentBackEvent = currentBackEvent,
        modifier = modifier,
        inputField = inputField,
        shape = shape,
        border = border,
        colors = colors,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        windowInsets = windowInsets,
        content = content,
    )
}

/** Defaults used in [SearchBarMMD]. */
@ExperimentalMaterial3Api
object SearchBarDefaultsMMD {
    /** Default tonal elevation for a search bar. */
    val TonalElevation: Dp = 0.0.dp

    /** Default shadow elevation for a search bar. */
    val ShadowElevation: Dp = 0.0.dp

    /** Default height for a search bar's input field, or a search bar in the unexpanded state. */
    val InputFieldHeight: Dp = 48.0.dp

    /** Default shape for a search bar's input field, or a search bar in the unexpanded state. */
    val inputFieldShape: Shape
        @Composable get() = CircleShape

    /** Default border for a search bar's input field. */
    val inputFieldBorder: BorderStroke
        @Composable get() = BorderStroke(2.dp, MaterialTheme.colorScheme.onSurface)

    /** Default shape for a [SearchBarMMD] in the expanded state. */
    val fullScreenShape: Shape
        @Composable get() = RectangleShape

    /** Default window insets for a [SearchBarMMD]. */
    val windowInsets: WindowInsets
        @Composable get() = WindowInsets.statusBars

    /**
     * Creates a [SearchBarColorsMMD] that represents the different colors used in parts of the search
     * bar in different states.
     *
     * For colors used in the input field, see [SearchBarDefaultsMMD.inputFieldColors].
     *
     * @param containerColor the container color of the search bar
     * @param dividerColor the color of the divider between the input field and the search results
     */
    @Suppress("DEPRECATION")
    @Composable
    fun colors(
        containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
        dividerColor: Color = MaterialTheme.colorScheme.outline,
    ): SearchBarColorsMMD =
        SearchBarColorsMMD(
            containerColor = containerColor,
            dividerColor = dividerColor,
            inputFieldColors = inputFieldColors(),
        )

    /**
     * Creates a [TextFieldColorsMMD] that represents the different colors used in the search bar input
     * field in different states.
     *
     * Only a subset of the full list of [TextFieldColorsMMD] parameters are used in the input field.
     * All other parameters have no effect.
     *
     * @param focusedTextColor the color used for the input text of this input field when focused
     * @param unfocusedTextColor the color used for the input text of this input field when not
     *   focused
     * @param disabledTextColor the color used for the input text of this input field when disabled
     * @param cursorColor the cursor color for this input field
     * @param selectionColors the colors used when the input text of this input field is selected
     * @param focusedLeadingIconColor the leading icon color for this input field when focused
     * @param unfocusedLeadingIconColor the leading icon color for this input field when not focused
     * @param disabledLeadingIconColor the leading icon color for this input field when disabled
     * @param focusedTrailingIconColor the trailing icon color for this input field when focused
     * @param unfocusedTrailingIconColor the trailing icon color for this input field when not
     *   focused
     * @param disabledTrailingIconColor the trailing icon color for this input field when disabled
     * @param focusedPlaceholderColor the placeholder color for this input field when focused
     * @param unfocusedPlaceholderColor the placeholder color for this input field when not focused
     * @param disabledPlaceholderColor the placeholder color for this input field when disabled
     */
    @Composable
    fun inputFieldColors(
        focusedTextColor: Color = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor: Color = MaterialTheme.colorScheme.onSurface,
        disabledTextColor: Color =
            MaterialTheme.colorScheme.onSurface.copy(
                alpha = 0.38f,
            ),
        cursorColor: Color = MaterialTheme.colorScheme.primary,
        selectionColors: TextSelectionColors = LocalTextSelectionColors.current,
        focusedLeadingIconColor: Color = MaterialTheme.colorScheme.onSurface,
        unfocusedLeadingIconColor: Color = MaterialTheme.colorScheme.onSurface,
        disabledLeadingIconColor: Color =
            MaterialTheme.colorScheme.onSurface.copy(
                alpha = 0.38f,
            ),
        focusedTrailingIconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedTrailingIconColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledTrailingIconColor: Color =
            MaterialTheme.colorScheme.onSurface.copy(
                alpha = 0.38f,
            ),
        focusedPlaceholderColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedPlaceholderColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledPlaceholderColor: Color =
            MaterialTheme.colorScheme.onSurface.copy(
                alpha = 0.38f,
            ),
    ): TextFieldColorsMMD =
        TextFieldDefaultsMMD.colors(
            focusedTextColor = focusedTextColor,
            unfocusedTextColor = unfocusedTextColor,
            disabledTextColor = disabledTextColor,
            cursorColor = cursorColor,
            selectionColors = selectionColors,
            focusedLeadingIconColor = focusedLeadingIconColor,
            unfocusedLeadingIconColor = unfocusedLeadingIconColor,
            disabledLeadingIconColor = disabledLeadingIconColor,
            focusedTrailingIconColor = focusedTrailingIconColor,
            unfocusedTrailingIconColor = unfocusedTrailingIconColor,
            disabledTrailingIconColor = disabledTrailingIconColor,
            focusedPlaceholderColor = focusedPlaceholderColor,
            unfocusedPlaceholderColor = unfocusedPlaceholderColor,
            disabledPlaceholderColor = disabledPlaceholderColor,
        )

    /**
     * A text field to input a query in a search bar
     *
     * @param query the query text to be shown in the input field.
     * @param onQueryChange the callback to be invoked when the input service updates the query. An
     *   updated text comes as a parameter of the callback.
     * @param onSearch the callback to be invoked when the input service triggers the
     *   [ImeAction.Search] action. The current [query] comes as a parameter of the callback.
     * @param expanded whether the search bar is expanded and showing search results.
     * @param onExpandedChange the callback to be invoked when the search bar's expanded state is
     *   changed.
     * @param modifier the [Modifier] to be applied to this input field.
     * @param enabled the enabled state of this input field. When `false`, this component will not
     *   respond to user input, and it will appear visually disabled and disabled to accessibility
     *   services.
     * @param placeholder the placeholder to be displayed when the [query] is empty.
     * @param leadingIcon the leading icon to be displayed at the start of the input field.
     * @param trailingIcon the trailing icon to be displayed at the end of the input field.
     * @param colors [TextFieldColorsMMD] that will be used to resolve the colors used for this input
     *   field in different states. See [SearchBarDefaultsMMD.inputFieldColors].
     * @param interactionSource an optional hoisted [MutableInteractionSource] for observing and
     *   emitting [Interaction]s for this input field. You can use this to change the search bar's
     *   appearance or preview the search bar in different states. Note that if `null` is provided,
     *   interactions will still happen internally.
     * @param textFieldContentPadding the padding applied between the internal elements of the input
     *   field (query text, placeholder, icons) and the edge of its container. Defaults to
     *   [TextFieldDefaults.contentPaddingWithoutLabel].
     */
    @ExperimentalMaterial3Api
    @Composable
    fun InputField(
        query: String,
        onQueryChange: (String) -> Unit,
        onSearch: (String) -> Unit,
        expanded: Boolean,
        onExpandedChange: (Boolean) -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        placeholder: @Composable (() -> Unit)? = null,
        leadingIcon: @Composable (() -> Unit)? = null,
        trailingIcon: @Composable (() -> Unit)? = null,
        colors: TextFieldColorsMMD = inputFieldColors(),
        interactionSource: MutableInteractionSource? = null,
        textFieldContentPadding: PaddingValues = TextFieldDefaults.contentPaddingWithoutLabel(),
    ) {
        @Suppress("NAME_SHADOWING")
        val interactionSource = interactionSource ?: remember { MutableInteractionSource() }

        val focused = interactionSource.collectIsFocusedAsState().value
        val focusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current

        val textColor =
            LocalTextStyle.current.color.takeOrElse {
                colors.textColor(enabled, isError = false, focused = focused)
            }

        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = modifier
                .sizeIn(
                    minWidth = SearchBarMinWidth,
                    maxWidth = SearchBarMaxWidth,
                    minHeight = InputFieldHeight,
                )
                .focusRequester(focusRequester)
                .onFocusChanged { if (it.isFocused) onExpandedChange(true) }
                .semantics {
                    onClick {
                        focusRequester.requestFocus()
                        true
                    }
                },
            enabled = enabled,
            singleLine = true,
            textStyle = LocalTextStyle.current.merge(TextStyle(color = textColor)),
            cursorBrush = SolidColor(colors.cursorColor(isError = false)),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch(query) }),
            interactionSource = interactionSource,
            decorationBox = @Composable { innerTextField ->
                TextFieldDefaultsMMD.DecorationBox(
                    value = query,
                    innerTextField = innerTextField,
                    enabled = enabled,
                    singleLine = true,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interactionSource,
                    placeholder = placeholder,
                    leadingIcon = leadingIcon?.let { leading ->
                        { Box(Modifier.offset(x = SearchBarIconOffsetX)) { leading() } }
                    },
                    trailingIcon = trailingIcon?.let { trailing ->
                        { Box(Modifier.offset(x = -SearchBarIconOffsetX)) { trailing() } }
                    },
                    shape = inputFieldShape,
                    colors = colors,
                    contentPadding = textFieldContentPadding,
                    container = {},
                )
            },
        )

        val shouldClearFocus = !expanded && focused
        LaunchedEffect(expanded) {
            if (shouldClearFocus) {
                focusManager.clearFocus()
            }
        }
    }

    /**
     * Represents the colors used by a search bar in different states.
     *
     * See [SearchBarDefaultsMMD.colors] for the default implementation that follows Material
     * specifications.
     */
    @Suppress("DEPRECATION")
    @ExperimentalMaterial3Api
    @Immutable
    class SearchBarColorsMMD
    @Deprecated(
        "Search bars now take the input field as a parameter. " +
            "TextFieldColors should be passed explicitly to the input field. " +
            "The `inputFieldColors` parameter will be removed in a future version of the library.",
    )
    constructor(
        val containerColor: Color,
        val dividerColor: Color,
        @Deprecated(
            "Search bars now take the input field as a parameter. " +
                "TextFieldColors should be passed explicitly to the input field. " +
                "The `inputFieldColors` property will be removed in a future version of the library.",
        )
        val inputFieldColors: TextFieldColorsMMD,
    ) {
        constructor(
            containerColor: Color,
            dividerColor: Color,
        ) : this(containerColor, dividerColor, UnspecifiedTextFieldColors)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is SearchBarColorsMMD) return false

            if (containerColor != other.containerColor) return false
            if (dividerColor != other.dividerColor) return false
            if (inputFieldColors != other.inputFieldColors) return false

            return true
        }

        override fun hashCode(): Int {
            var result = containerColor.hashCode()
            result = 31 * result + dividerColor.hashCode()
            result = 31 * result + inputFieldColors.hashCode()
            return result
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    internal fun SearchBarImpl(
        progress: Float,
        finalBackProgress: MutableFloatState,
        firstBackEvent: MutableState<BackEventCompat?>,
        currentBackEvent: MutableState<BackEventCompat?>,
        modifier: Modifier = Modifier,
        inputField: @Composable () -> Unit,
        shape: Shape = inputFieldShape,
        border: BorderStroke? = inputFieldBorder,
        colors: SearchBarColorsMMD = colors(),
        tonalElevation: Dp = TonalElevation,
        shadowElevation: Dp = ShadowElevation,
        windowInsets: WindowInsets = SearchBarDefaultsMMD.windowInsets,
        content: @Composable ColumnScope.() -> Unit,
    ) {
        val defaultInputFieldShape = inputFieldShape
        val defaultFullScreenShape = fullScreenShape
        val useFullScreenShape = progress == 1f

        val staticShape =
            when {
                shape == defaultInputFieldShape && !useFullScreenShape -> defaultInputFieldShape
                useFullScreenShape -> defaultFullScreenShape
                else -> shape
            }

        val surface =
            @Composable {
                Surface(
                    shape = staticShape,
                    color = colors.containerColor,
                    contentColor = contentColorFor(colors.containerColor),
                    tonalElevation = tonalElevation,
                    shadowElevation = shadowElevation,
                    border = border,
                    content = {},
                )
            }

        val showContent = progress > 0f
        val wrappedContent: (@Composable () -> Unit)? =
            if (showContent) {
                {
                    Column(Modifier.graphicsLayer { alpha = progress }) {
                        HorizontalDividerMMD(color = colors.dividerColor)
                        content()
                    }
                }
            } else null

        SearchBarLayout(
            progress = progress,
            finalBackProgress = finalBackProgress,
            firstBackEvent = firstBackEvent,
            currentBackEvent = currentBackEvent,
            modifier = modifier,
            windowInsets = windowInsets,
            inputField = inputField,
            surface = surface,
            content = wrappedContent,
        )
    }

    @Composable
    private fun SearchBarLayout(
        progress: Float,
        finalBackProgress: MutableFloatState,
        firstBackEvent: MutableState<BackEventCompat?>,
        currentBackEvent: MutableState<BackEventCompat?>,
        modifier: Modifier,
        windowInsets: WindowInsets,
        inputField: @Composable () -> Unit,
        surface: @Composable () -> Unit,
        content: (@Composable () -> Unit)?,
    ) {
        val unconsumedInsets = remember { MutableWindowInsets() }
        Layout(
            modifier = modifier
                .zIndex(1f)
                .onConsumedWindowInsetsChanged { consumedInsets ->
                    unconsumedInsets.insets = windowInsets.exclude(consumedInsets)
                }
                .consumeWindowInsets(windowInsets),
            content = {
                Box(
                    Modifier.layoutId(LayoutIdSurface),
                    propagateMinConstraints = true,
                ) { surface() }
                Box(Modifier.layoutId(LayoutIdInputField), propagateMinConstraints = true) {
                    inputField()
                }
                content?.let { content ->
                    Box(Modifier.layoutId(LayoutIdSearchContent), propagateMinConstraints = true) {
                        content()
                    }
                }
            },
        ) { measurables, constraints ->
            val inputFieldMeasurable = measurables.fastFirst { it.layoutId == LayoutIdInputField }
            val surfaceMeasurable = measurables.fastFirst { it.layoutId == LayoutIdSurface }
            val contentMeasurable =
                measurables.fastFirstOrNull { it.layoutId == LayoutIdSearchContent }

            val topPadding = unconsumedInsets.getTop(this) + SearchBarVerticalPadding.roundToPx()
            val bottomPadding = SearchBarVerticalPadding.roundToPx()

            val defaultStartHeight =
                constraints.constrainHeight(
                    inputFieldMeasurable.minIntrinsicHeight(constraints.maxWidth),
                )

            val predictiveBackMultiplier =
                calculatePredictiveBackMultiplier(
                    currentBackEvent.value,
                    progress,
                    finalBackProgress.floatValue,
                )

            val width = constraints.maxWidth
            val height =
                if (progress == 1f)
                    constraints.maxHeight
                else
                    topPadding + defaultStartHeight + bottomPadding

            val inputFieldPlaceable =
                inputFieldMeasurable.measure(
                    Constraints(
                        minWidth = width,
                        maxWidth = width,
                        minHeight = defaultStartHeight,
                        maxHeight = defaultStartHeight,
                    ),
                )

            val surfacePlaceable =
                surfaceMeasurable.measure(Constraints.fixed(width, height - topPadding))
            val contentPlaceable =
                contentMeasurable?.measure(
                    Constraints.fixed(
                        width,
                        height - (topPadding + defaultStartHeight + bottomPadding),
                    ),
                )

            layout(width, height) {
                val minOffsetMargin = SearchBarPredictiveBackMinMargin.roundToPx()
                val predictiveBackOffsetX =
                    calculatePredictiveBackOffsetX(
                        constraints = constraints,
                        minMargin = minOffsetMargin,
                        currentBackEvent = currentBackEvent.value,
                        layoutDirection = layoutDirection,
                        progress = progress,
                        predictiveBackMultiplier = predictiveBackMultiplier,
                    )
                val predictiveBackOffsetY =
                    calculatePredictiveBackOffsetY(
                        constraints = constraints,
                        minMargin = minOffsetMargin,
                        currentBackEvent = currentBackEvent.value,
                        firstBackEvent = firstBackEvent.value,
                        height = height,
                        maxOffsetY = SearchBarPredictiveBackMaxOffsetY.roundToPx(),
                        predictiveBackMultiplier = predictiveBackMultiplier,
                    )

                surfacePlaceable.placeRelative(
                    predictiveBackOffsetX,
                    predictiveBackOffsetY,
                )
                inputFieldPlaceable.placeRelative(
                    predictiveBackOffsetX,
                    predictiveBackOffsetY,
                )
                contentPlaceable?.placeRelative(
                    predictiveBackOffsetX,
                    predictiveBackOffsetY + inputFieldPlaceable.height,
                )
            }
        }
    }

    private fun calculatePredictiveBackMultiplier(
        currentBackEvent: BackEventCompat?,
        progress: Float,
        finalBackProgress: Float,
    ) =
        when {
            currentBackEvent == null -> 0f // Not in predictive back at all.
            finalBackProgress.isNaN() -> 1f // User is currently swiping predictive back.
            finalBackProgress <= 0 -> 0f // Safety check for divide by zero.
            else -> progress / finalBackProgress // User has released predictive back swipe.
        }

    private fun calculatePredictiveBackOffsetX(
        constraints: Constraints,
        minMargin: Int,
        currentBackEvent: BackEventCompat?,
        layoutDirection: LayoutDirection,
        progress: Float,
        predictiveBackMultiplier: Float,
    ): Int {
        if (currentBackEvent == null || predictiveBackMultiplier == 0f) {
            return 0
        }
        val directionMultiplier =
            if (currentBackEvent.swipeEdge == BackEventCompat.EDGE_LEFT) 1 else -1
        val rtlMultiplier = if (layoutDirection == LayoutDirection.Ltr) 1 else -1
        val maxOffsetX = (constraints.maxWidth * SearchBarPredictiveBackMaxOffsetXRatio) - minMargin
        val interpolatedOffsetX = maxOffsetX * (1 - progress)
        return (interpolatedOffsetX * predictiveBackMultiplier * directionMultiplier * rtlMultiplier)
            .roundToInt()
    }

    private fun calculatePredictiveBackOffsetY(
        constraints: Constraints,
        minMargin: Int,
        currentBackEvent: BackEventCompat?,
        firstBackEvent: BackEventCompat?,
        height: Int,
        maxOffsetY: Int,
        predictiveBackMultiplier: Float,
    ): Int {
        if (firstBackEvent == null || currentBackEvent == null || predictiveBackMultiplier == 0f) {
            return 0
        }
        val availableVerticalSpace = max(0, (constraints.maxHeight - height) / 2 - minMargin)
        val adjustedMaxOffsetY = min(availableVerticalSpace, maxOffsetY)
        val yDelta = currentBackEvent.touchY - firstBackEvent.touchY
        val yProgress = abs(yDelta) / constraints.maxHeight
        val directionMultiplier = sign(yDelta)
        val interpolatedOffsetY = lerp(0, adjustedMaxOffsetY, yProgress)
        return (interpolatedOffsetY * predictiveBackMultiplier * directionMultiplier).roundToInt()
    }

    private val UnspecifiedTextFieldColors: TextFieldColorsMMD =
        TextFieldColorsMMD(
            focusedTextColor = Color.Unspecified,
            unfocusedTextColor = Color.Unspecified,
            disabledTextColor = Color.Unspecified,
            errorTextColor = Color.Unspecified,
            focusedContainerColor = Color.Unspecified,
            unfocusedContainerColor = Color.Unspecified,
            disabledContainerColor = Color.Unspecified,
            errorContainerColor = Color.Unspecified,
            cursorColor = Color.Unspecified,
            errorCursorColor = Color.Unspecified,
            textSelectionColors = TextSelectionColors(Color.Unspecified, Color.Unspecified),
            focusedIndicatorColor = Color.Unspecified,
            unfocusedIndicatorColor = Color.Unspecified,
            disabledIndicatorColor = Color.Unspecified,
            errorIndicatorColor = Color.Unspecified,
            focusedLeadingIconColor = Color.Unspecified,
            unfocusedLeadingIconColor = Color.Unspecified,
            disabledLeadingIconColor = Color.Unspecified,
            errorLeadingIconColor = Color.Unspecified,
            focusedTrailingIconColor = Color.Unspecified,
            unfocusedTrailingIconColor = Color.Unspecified,
            disabledTrailingIconColor = Color.Unspecified,
            errorTrailingIconColor = Color.Unspecified,
            focusedLabelColor = Color.Unspecified,
            unfocusedLabelColor = Color.Unspecified,
            disabledLabelColor = Color.Unspecified,
            errorLabelColor = Color.Unspecified,
            focusedPlaceholderColor = Color.Unspecified,
            unfocusedPlaceholderColor = Color.Unspecified,
            disabledPlaceholderColor = Color.Unspecified,
            errorPlaceholderColor = Color.Unspecified,
            focusedSupportingTextColor = Color.Unspecified,
            unfocusedSupportingTextColor = Color.Unspecified,
            disabledSupportingTextColor = Color.Unspecified,
            errorSupportingTextColor = Color.Unspecified,
            focusedPrefixColor = Color.Unspecified,
            unfocusedPrefixColor = Color.Unspecified,
            disabledPrefixColor = Color.Unspecified,
            errorPrefixColor = Color.Unspecified,
            focusedSuffixColor = Color.Unspecified,
            unfocusedSuffixColor = Color.Unspecified,
            disabledSuffixColor = Color.Unspecified,
            errorSuffixColor = Color.Unspecified,
        )

    private const val LayoutIdInputField = "InputField"
    private const val LayoutIdSurface = "Surface"
    private const val LayoutIdSearchContent = "Content"

    // Measurement specs
    private val SearchBarMinWidth: Dp = 360.dp
    private val SearchBarMaxWidth: Dp = 720.dp
    private val SearchBarVerticalPadding: Dp = 0.dp

    // Search bar has 16dp padding between icons and start/end, while by default text field has 12dp.
    private val SearchBarIconOffsetX: Dp = 4.dp
    private val SearchBarPredictiveBackMinMargin: Dp = 8.dp
    private val SearchBarPredictiveBackMaxOffsetXRatio: Float = 1f / 20f
    private val SearchBarPredictiveBackMaxOffsetY: Dp = 24.dp
}
