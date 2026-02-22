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

package com.mudita.mmd.components.text_field

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.LayoutIdParentData
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.lerp
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mudita.mmd.internal.ProvideContentColorTextStyleMMD

/** Implementation of the [TextFieldMMD] */
@Composable
internal fun CommonDecorationBox(
    value: String,
    innerTextField: @Composable () -> Unit,
    visualTransformation: VisualTransformation,
    label: @Composable (() -> Unit)?,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    singleLine: Boolean = false,
    enabled: Boolean = true,
    isError: Boolean = false,
    interactionSource: InteractionSource,
    contentPadding: PaddingValues,
    colors: TextFieldColorsMMD,
    container: @Composable () -> Unit,
) {
    val transformedText = remember(value, visualTransformation) {
        visualTransformation.filter(AnnotatedString(value))
    }.text.text

    val isFocused = interactionSource.collectIsFocusedAsState().value
    val inputState =
        when {
            isFocused -> InputPhase.Focused
            transformedText.isEmpty() -> InputPhase.UnfocusedEmpty
            else -> InputPhase.UnfocusedNotEmpty
        }

    val labelColor = colors.labelColor(enabled, isError, isFocused)

    val typography = MaterialTheme.typography
    val bodyLarge = typography.bodyLarge
    val bodySmall = typography.bodySmall
    val overrideLabelTextStyleColor =
        (bodyLarge.color == Color.Unspecified && bodySmall.color != Color.Unspecified) ||
            (bodyLarge.color != Color.Unspecified && bodySmall.color == Color.Unspecified)

    TextFieldTransitionScope(
        inputState = inputState,
        focusedLabelTextStyleColor =
        with(bodySmall.color) {
            if (overrideLabelTextStyleColor) this.takeOrElse { labelColor } else this
        },
        unfocusedLabelTextStyleColor =
        with(bodyLarge.color) {
            if (overrideLabelTextStyleColor) this.takeOrElse { labelColor } else this
        },
        labelColor = labelColor,
        showLabel = label != null,
    ) { labelProgress, labelTextStyleColor, labelContentColor, placeholderAlpha, prefixSuffixAlpha ->
        val labelProgressValue = labelProgress.value
        val decoratedLabel: @Composable (() -> Unit)? =
            label?.let {
                @Composable {
                    val labelTextStyle =
                        lerp(bodyLarge, bodySmall, labelProgressValue).let { textStyle ->
                            if (overrideLabelTextStyleColor) {
                                textStyle.copy(color = labelTextStyleColor.value)
                            } else {
                                textStyle
                            }
                        }
                    Decoration(labelContentColor.value, labelTextStyle, it)
                }
            }

        // Transparent components interfere with Talkback (b/261061240), so if any components below
        // have alpha == 0, we set the component to null instead.

        val placeholderColor = colors.placeholderColor(enabled, isError, isFocused)
        val showPlaceholder by remember {
            derivedStateOf(structuralEqualityPolicy()) { placeholderAlpha.value > 0f }
        }
        val decoratedPlaceholder: @Composable ((Modifier) -> Unit)? =
            if (placeholder != null && transformedText.isEmpty() && showPlaceholder) {
                @Composable { modifier ->
                    Box(modifier.graphicsLayer { alpha = placeholderAlpha.value }) {
                        Decoration(
                            contentColor = placeholderColor,
                            textStyle = bodyLarge,
                            content = placeholder,
                        )
                    }
                }
            } else null

        val prefixColor = colors.prefixColor(enabled, isError, isFocused)
        val showPrefixSuffix by remember {
            derivedStateOf(structuralEqualityPolicy()) { prefixSuffixAlpha.value > 0f }
        }
        val decoratedPrefix: @Composable (() -> Unit)? =
            if (prefix != null && showPrefixSuffix) {
                @Composable {
                    Box(Modifier.graphicsLayer { alpha = prefixSuffixAlpha.value }) {
                        Decoration(
                            contentColor = prefixColor,
                            textStyle = bodyLarge,
                            content = prefix,
                        )
                    }
                }
            } else null

        val suffixColor = colors.suffixColor(enabled, isError, isFocused)
        val decoratedSuffix: @Composable (() -> Unit)? =
            if (suffix != null && showPrefixSuffix) {
                @Composable {
                    Box(Modifier.graphicsLayer { alpha = prefixSuffixAlpha.value }) {
                        Decoration(
                            contentColor = suffixColor,
                            textStyle = bodyLarge,
                            content = suffix,
                        )
                    }
                }
            } else null

        val leadingIconColor = colors.leadingIconColor(enabled, isError, isFocused)
        val decoratedLeading: @Composable (() -> Unit)? =
            leadingIcon?.let {
                @Composable { Decoration(contentColor = leadingIconColor, content = it) }
            }

        val trailingIconColor = colors.trailingIconColor(enabled, isError, isFocused)
        val decoratedTrailing: @Composable (() -> Unit)? =
            trailingIcon?.let {
                @Composable { Decoration(contentColor = trailingIconColor, content = it) }
            }

        val supportingTextColor = colors.supportingTextColor(enabled, isError, isFocused)
        val decoratedSupporting: @Composable (() -> Unit)? =
            supportingText?.let {
                @Composable {
                    Decoration(
                        contentColor = supportingTextColor,
                        textStyle = bodySmall,
                        content = it,
                    )
                }
            }

        val containerWithId: @Composable () -> Unit = {
            Box(Modifier.layoutId(ContainerId), propagateMinConstraints = true) {
                container()
            }
        }

        TextFieldLayout(
            modifier = Modifier,
            textField = innerTextField,
            placeholder = decoratedPlaceholder,
            label = decoratedLabel,
            leading = decoratedLeading,
            trailing = decoratedTrailing,
            prefix = decoratedPrefix,
            suffix = decoratedSuffix,
            container = containerWithId,
            supporting = decoratedSupporting,
            singleLine = singleLine,
            animationProgress = labelProgressValue,
            paddingValues = contentPadding,
        )
    }
}

/** Decorates [content] with [contentColor] and [textStyle]. */
@Composable
private fun Decoration(contentColor: Color, textStyle: TextStyle, content: @Composable () -> Unit) =
    ProvideContentColorTextStyleMMD(contentColor, textStyle, content)

/** Decorates [content] with [contentColor]. */
@Composable
private fun Decoration(contentColor: Color, content: @Composable () -> Unit) =
    CompositionLocalProvider(LocalContentColor provides contentColor, content = content)

// Developers need to handle invalid input manually. But since we don't provide an error message
// slot API, we can set the default error message in case developers forget about it.
internal fun Modifier.defaultErrorSemantics(
    isError: Boolean,
    defaultErrorMessage: String,
): Modifier = if (isError) semantics { error(defaultErrorMessage) } else this

/**
 * Replacement for Modifier.background which takes color as a State to avoid recomposition while
 * animating.
 */
internal fun Modifier.textFieldBackground(
    color: ColorProducer,
    shape: Shape,
): Modifier =
    this.drawWithCache {
        val outline = shape.createOutline(size, layoutDirection, this)
        onDrawBehind { drawOutline(outline, color = color()) }
    }

internal fun widthOrZero(placeable: Placeable?) = placeable?.width ?: 0

internal fun heightOrZero(placeable: Placeable?) = placeable?.height ?: 0

@Composable
private inline fun TextFieldTransitionScope(
    inputState: InputPhase,
    focusedLabelTextStyleColor: Color,
    unfocusedLabelTextStyleColor: Color,
    labelColor: Color,
    showLabel: Boolean,
    content: @Composable (
        labelProgress: State<Float>,
        labelTextStyleColor: State<Color>,
        labelContentColor: State<Color>,
        placeholderOpacity: State<Float>,
        prefixSuffixOpacity: State<Float>,
    ) -> Unit,
) {
    val labelProgress = rememberUpdatedState(
        when (inputState) {
            InputPhase.Focused -> 1f
            InputPhase.UnfocusedEmpty -> 0f
            InputPhase.UnfocusedNotEmpty -> 1f
        },
    )

    val placeholderOpacity = rememberUpdatedState(
        when (inputState) {
            InputPhase.Focused -> 1f
            InputPhase.UnfocusedEmpty -> if (showLabel) 0f else 1f
            InputPhase.UnfocusedNotEmpty -> 0f
        },
    )

    val prefixSuffixOpacity = rememberUpdatedState(
        when (inputState) {
            InputPhase.Focused -> 1f
            InputPhase.UnfocusedEmpty -> if (showLabel) 0f else 1f
            InputPhase.UnfocusedNotEmpty -> 1f
        },
    )

    val labelTextStyleColor = rememberUpdatedState(
        when (inputState) {
            InputPhase.Focused -> focusedLabelTextStyleColor
            else -> unfocusedLabelTextStyleColor
        },
    )

    val labelContentColor = rememberUpdatedState(labelColor)

    content(
        labelProgress,
        labelTextStyleColor,
        labelContentColor,
        placeholderOpacity,
        prefixSuffixOpacity,
    )
}

@Composable
internal fun getBorderStroke(
    enabled: Boolean,
    isError: Boolean,
    focused: Boolean,
    colors: TextFieldColorsMMD,
    focusedBorderThickness: Dp,
    unfocusedBorderThickness: Dp,
): State<BorderStroke> {
    val targetColor = colors.indicatorColor(enabled, isError, focused)
    val indicatorColor = rememberUpdatedState(targetColor)

    val targetThickness = if (focused) focusedBorderThickness else unfocusedBorderThickness
    val thickness = rememberUpdatedState(targetThickness)

    return rememberUpdatedState(BorderStroke(thickness.value, indicatorColor.value))
}

/** An internal state used to animate a label and an indicator. */
private enum class InputPhase {
    // Text field is focused
    Focused,

    // Text field is not focused and input text is empty
    UnfocusedEmpty,

    // Text field is not focused but input text is not empty
    UnfocusedNotEmpty
}

internal val IntrinsicMeasurable.layoutId: Any?
    get() = (parentData as? LayoutIdParentData)?.layoutId

internal const val TextFieldId = "TextField"
internal const val PlaceholderId = "Hint"
internal const val LabelId = "Label"
internal const val LeadingId = "Leading"
internal const val TrailingId = "Trailing"
internal const val PrefixId = "Prefix"
internal const val SuffixId = "Suffix"
internal const val SupportingId = "Supporting"
internal const val ContainerId = "Container"
internal val ZeroConstraints = Constraints(0, 0, 0, 0)

internal val TextFieldPadding = 16.dp
internal val HorizontalIconPadding = 12.dp
internal val SupportingTopPadding = 4.dp
internal val PrefixSuffixTextPadding = 2.dp
internal val MinTextLineHeight = 24.dp
internal val MinFocusedLabelLineHeight = 16.dp
internal val MinSupportingTextLineHeight = 16.dp

internal val IconDefaultSizeModifier = Modifier.defaultMinSize(48.dp, 48.dp)
